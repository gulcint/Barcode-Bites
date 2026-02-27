package com.barcodebite.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.barcodebite.android.ui.screen.home.HomeScreen
import com.barcodebite.android.ui.screen.result.ProductResultScreen
import com.barcodebite.android.ui.screen.scanner.ScannerScreen

object AppRoute {
    const val Home = "home"
    const val Scanner = "scanner"
    const val Result = "result"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
    ) {
        composable(AppRoute.Home) {
            HomeScreen(
                onScanClick = { navController.navigate(AppRoute.Scanner) },
            )
        }
        composable(AppRoute.Scanner) {
            ScannerScreen(
                onBarcodeDetected = { navController.navigate(AppRoute.Result) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.Result) {
            ProductResultScreen(
                onBackToHome = {
                    navController.navigate(AppRoute.Home) {
                        popUpTo(AppRoute.Home) { inclusive = true }
                    }
                },
            )
        }
    }
}
