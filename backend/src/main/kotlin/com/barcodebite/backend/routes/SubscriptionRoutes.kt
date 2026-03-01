package com.barcodebite.backend.routes

import com.barcodebite.backend.repository.SubscriptionRepository
import com.barcodebite.backend.security.CurrentUserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.time.Instant
import java.time.temporal.ChronoUnit
import com.barcodebite.backend.model.SubscriptionRecord
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionEntitlementsResponse(
    val compare: Boolean,
    val advanced_analysis: Boolean,
    val unlimited_scans: Boolean,
)

@Serializable
data class SubscriptionStatusResponse(
    val plan: String,
    val isActive: Boolean,
    val expiresAtEpochMs: Long? = null,
    val entitlements: SubscriptionEntitlementsResponse,
)

@Serializable
data class VerifySubscriptionRequest(
    val plan: String,
    val purchaseToken: String,
)

@Serializable
data class RestoreSubscriptionRequest(
    val purchaseToken: String,
)

fun Route.subscriptionRoutes(
    subscriptionRepository: SubscriptionRepository,
) {
    route("/subscriptions") {
        authenticate("auth-jwt") {
            get("/status") {
                val principal = call.principal<CurrentUserPrincipal>()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                val subscription = subscriptionRepository.findByUserId(principal.id)
                val plan = subscription?.plan ?: "free"
                val isActive = subscription?.isActive ?: false
                val entitlements = buildEntitlements(plan = plan, isActive = isActive)

                call.respond(
                    SubscriptionStatusResponse(
                        plan = plan,
                        isActive = isActive,
                        expiresAtEpochMs = subscription?.expiresAtEpochMs,
                        entitlements = entitlements,
                    ),
                )
            }

            post("/verify") {
                val principal = call.principal<CurrentUserPrincipal>()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val request = call.receive<VerifySubscriptionRequest>()
                if (request.purchaseToken.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "purchaseToken is required")
                    return@post
                }
                if (!request.plan.equals("premium_monthly", ignoreCase = true) &&
                    !request.plan.equals("premium_yearly", ignoreCase = true)
                ) {
                    call.respond(HttpStatusCode.BadRequest, "unsupported plan")
                    return@post
                }
                val expiresAt = if (request.plan.equals("premium_yearly", ignoreCase = true)) {
                    Instant.now().plus(365, ChronoUnit.DAYS).toEpochMilli()
                } else {
                    Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli()
                }
                val saved = subscriptionRepository.upsert(
                    SubscriptionRecord(
                        userId = principal.id,
                        plan = request.plan,
                        isActive = true,
                        expiresAtEpochMs = expiresAt,
                    ),
                )
                call.respond(
                    SubscriptionStatusResponse(
                        plan = saved.plan,
                        isActive = saved.isActive,
                        expiresAtEpochMs = saved.expiresAtEpochMs,
                        entitlements = buildEntitlements(saved.plan, saved.isActive),
                    ),
                )
            }

            post("/restore") {
                val principal = call.principal<CurrentUserPrincipal>()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val request = call.receive<RestoreSubscriptionRequest>()
                if (request.purchaseToken.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "purchaseToken is required")
                    return@post
                }
                val existing = subscriptionRepository.findByUserId(principal.id)
                if (existing == null || !existing.isActive) {
                    call.respond(HttpStatusCode.NotFound, "active subscription not found")
                    return@post
                }
                call.respond(
                    SubscriptionStatusResponse(
                        plan = existing.plan,
                        isActive = existing.isActive,
                        expiresAtEpochMs = existing.expiresAtEpochMs,
                        entitlements = buildEntitlements(existing.plan, existing.isActive),
                    ),
                )
            }
        }
    }
}

private fun buildEntitlements(
    plan: String,
    isActive: Boolean,
): SubscriptionEntitlementsResponse {
    if (!isActive) {
        return SubscriptionEntitlementsResponse(
            compare = false,
            advanced_analysis = false,
            unlimited_scans = false,
        )
    }

    val isPremium = plan.equals("premium_monthly", ignoreCase = true) ||
        plan.equals("premium_yearly", ignoreCase = true)

    return if (isPremium) {
        SubscriptionEntitlementsResponse(
            compare = true,
            advanced_analysis = true,
            unlimited_scans = true,
        )
    } else {
        SubscriptionEntitlementsResponse(
            compare = false,
            advanced_analysis = false,
            unlimited_scans = false,
        )
    }
}
