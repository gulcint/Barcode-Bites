package com.barcodebite.android.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.barcodebite.android.data.profile.ProfileRepository
import com.barcodebite.android.data.profile.SubscriptionSyncRepository
import com.barcodebite.android.data.compare.CompareRepository
import com.barcodebite.android.ui.screen.history.HistoryScreen
import com.barcodebite.android.ui.screen.history.HistoryViewModel
import com.barcodebite.android.ui.screen.home.HomeScreen
import com.barcodebite.android.ui.screen.compare.CompareScreen
import com.barcodebite.android.ui.screen.compare.CompareViewModel
import com.barcodebite.android.ui.screen.onboarding.OnboardingScreen
import com.barcodebite.android.ui.screen.paywall.PaywallScreen
import com.barcodebite.android.ui.screen.paywall.PaywallViewModel
import com.barcodebite.android.ui.screen.profile.ProfileScreen
import com.barcodebite.android.ui.screen.profile.ProfileViewModel
import com.barcodebite.android.ui.screen.result.ProductResultScreen
import com.barcodebite.android.ui.screen.scanner.ScannerScreen
import com.barcodebite.android.ui.screen.splash.SplashScreen
import com.barcodebite.android.premium.PremiumGate
import com.barcodebite.shared.domain.repository.ScanHistoryRepository

object AppRoute {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Home = "home"
    const val Scanner = "scanner"
    const val History = "history"
    const val Profile = "profile"
    const val Paywall = "paywall"
    const val Compare = "compare"
    const val Result = "result/{barcode}"

    fun result(barcode: String): String = "result/${Uri.encode(barcode)}"
}

@Composable
fun AppNavigation(
    scanHistoryRepository: ScanHistoryRepository,
    profileRepository: ProfileRepository,
    subscriptionSyncRepository: SubscriptionSyncRepository,
    compareRepository: CompareRepository,
    premiumGate: PremiumGate,
    isOnboardingCompleted: () -> Boolean,
    setOnboardingCompleted: () -> Unit,
    accessTokenProvider: () -> String?,
    navController: NavHostController = rememberNavController(),
) {
    val historyViewModel = remember(scanHistoryRepository) {
        HistoryViewModel(scanHistoryRepository = scanHistoryRepository)
    }
    val profileViewModel = remember(profileRepository) {
        ProfileViewModel(
            profileRepository = profileRepository,
            accessTokenProvider = accessTokenProvider,
        )
    }
    val paywallViewModel = remember(premiumGate, subscriptionSyncRepository) {
        PaywallViewModel(
            premiumGate = premiumGate,
            subscriptionSyncRepository = subscriptionSyncRepository,
            accessTokenProvider = accessTokenProvider,
        )
    }
    val compareViewModel = remember(compareRepository) {
        CompareViewModel(repository = compareRepository)
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash,
    ) {
        composable(AppRoute.Splash) {
            SplashScreen(
                onFinished = {
                    val target = if (isOnboardingCompleted()) AppRoute.Home else AppRoute.Onboarding
                    navController.navigate(target) {
                        popUpTo(AppRoute.Splash) { inclusive = true }
                    }
                },
            )
        }
        composable(AppRoute.Onboarding) {
            OnboardingScreen(
                onContinue = {
                    setOnboardingCompleted()
                    navController.navigate(AppRoute.Home) {
                        popUpTo(AppRoute.Onboarding) { inclusive = true }
                    }
                },
            )
        }
        composable(AppRoute.Home) {
            HomeScreen(
                onScanClick = { navController.navigate(AppRoute.Scanner) },
                onHistoryClick = { navController.navigate(AppRoute.History) },
                onProfileClick = { navController.navigate(AppRoute.Profile) },
                onGoPremiumClick = { navController.navigate(AppRoute.Paywall) },
                onAdvancedAnalysisClick = {
                    navController.navigate(resolveAdvancedAnalysisRoute(premiumGate.isPremiumEnabled()))
                },
            )
        }
        composable(AppRoute.Scanner) {
            ScannerScreen(
                onBarcodeDetected = { barcode -> navController.navigate(AppRoute.result(barcode)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.History) {
            HistoryScreen(
                viewModel = historyViewModel,
                onBack = { navController.popBackStack() },
                onOpenResult = { barcode ->
                    navController.navigate(AppRoute.result(barcode))
                },
            )
        }
        composable(
            route = AppRoute.Result,
            arguments = listOf(navArgument("barcode") { type = NavType.StringType }),
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            ProductResultScreen(
                barcode = barcode,
                scanHistoryRepository = scanHistoryRepository,
                onBackToHome = {
                    navController.navigate(AppRoute.Home) {
                        popUpTo(AppRoute.Home) { inclusive = true }
                    }
                },
            )
        }
        composable(AppRoute.Profile) {
            ProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() },
                onGoPremium = { navController.navigate(AppRoute.Paywall) },
            )
        }
        composable(AppRoute.Paywall) {
            PaywallScreen(
                viewModel = paywallViewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.Compare) {
            CompareScreen(
                viewModel = compareViewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

internal fun resolveAdvancedAnalysisRoute(isPremium: Boolean): String {
    return if (isPremium) AppRoute.Compare else AppRoute.Paywall
}
