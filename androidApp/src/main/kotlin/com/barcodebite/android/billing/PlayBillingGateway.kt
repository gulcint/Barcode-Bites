package com.barcodebite.android.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

class PlayBillingGateway(
    context: Context,
    private val onPurchaseGranted: (String, String) -> Unit,
    private val onPurchaseRestored: (String) -> Unit,
    private val onError: (String) -> Unit,
) {
    private val billingClient = BillingClient.newBuilder(context)
        .setListener { result, purchases ->
            if (result.responseCode != BillingResponseCode.OK) {
                onError("Billing error: ${result.debugMessage}")
                return@setListener
            }
            purchases.orEmpty().forEach { purchase ->
                if (purchase.purchaseState == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED) {
                    val plan = when {
                        purchase.products.contains(MONTHLY_PRODUCT_ID) -> "premium_monthly"
                        purchase.products.contains(YEARLY_PRODUCT_ID) -> "premium_yearly"
                        else -> "premium_unknown"
                    }
                    acknowledgeIfNeeded(purchase.purchaseToken)
                    onPurchaseGranted(plan, purchase.purchaseToken)
                }
            }
        }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .build()

    private val detailsByProductId = mutableMapOf<String, ProductDetails>()

    init {
        billingClient.startConnection(
            object : com.android.billingclient.api.BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    onError("Billing service disconnected")
                }

                override fun onBillingSetupFinished(result: com.android.billingclient.api.BillingResult) {
                    if (result.responseCode != BillingResponseCode.OK) {
                        onError("Billing setup failed: ${result.debugMessage}")
                        return
                    }
                    queryProductDetails()
                }
            },
        )
    }

    fun launchMonthly(activity: Activity): Boolean = launch(activity, MONTHLY_PRODUCT_ID)

    fun launchYearly(activity: Activity): Boolean = launch(activity, YEARLY_PRODUCT_ID)

    fun restorePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingResponseCode.OK) {
                onError("Restore failed: ${result.debugMessage}")
                return@queryPurchasesAsync
            }
            val restored = purchases.orEmpty().firstOrNull { it.purchaseState == com.android.billingclient.api.Purchase.PurchaseState.PURCHASED }
            if (restored == null) {
                onError("Restorable purchase not found")
                return@queryPurchasesAsync
            }
            acknowledgeIfNeeded(restored.purchaseToken)
            onPurchaseRestored(restored.purchaseToken)
        }
    }

    fun close() {
        billingClient.endConnection()
    }

    private fun launch(activity: Activity, productId: String): Boolean {
        val details = detailsByProductId[productId]
        if (details == null) {
            onError("Product details unavailable for $productId")
            return false
        }
        val offerToken = details.subscriptionOfferDetails
            ?.firstOrNull()
            ?.offerToken
        if (offerToken.isNullOrBlank()) {
            onError("Offer token unavailable")
            return false
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offerToken)
            .build()

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        val result = billingClient.launchBillingFlow(activity, flowParams)
        if (result.responseCode != BillingResponseCode.OK) {
            onError("Launch billing failed: ${result.debugMessage}")
            return false
        }
        return true
    }

    private fun queryProductDetails() {
        val products = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(MONTHLY_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(YEARLY_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode != BillingResponseCode.OK) {
                onError("Product query failed: ${result.debugMessage}")
                return@queryProductDetailsAsync
            }
            detailsByProductId.clear()
            productDetailsList.orEmpty().forEach { detailsByProductId[it.productId] = it }
        }
    }

    private fun acknowledgeIfNeeded(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { }
    }

    companion object {
        const val MONTHLY_PRODUCT_ID = "barcodebite_premium_monthly"
        const val YEARLY_PRODUCT_ID = "barcodebite_premium_yearly"
    }
}
