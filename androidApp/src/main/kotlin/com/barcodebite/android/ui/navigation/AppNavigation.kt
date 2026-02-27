package com.barcodebite.android.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.barcodebite.android.ui.screen.home.HomeScreen
import com.barcodebite.android.ui.screen.result.ProductResultScreen
import com.barcodebite.android.ui.screen.scanner.ScannerScreen

object AppRoute {
    const val Home = "home"
    const val Scanner = "scanner"
    const val Result = "result/{barcode}"

    fun result(barcode: String): String = "result/${Uri.encode(barcode)}"
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
                onBarcodeDetected = { barcode -> navController.navigate(AppRoute.result(barcode)) },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AppRoute.Result,
            arguments = listOf(navArgument("barcode") { type = NavType.StringType }),
        ) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            ProductResultScreen(
                barcode = barcode,
                onBackToHome = {
                    navController.navigate(AppRoute.Home) {
                        popUpTo(AppRoute.Home) { inclusive = true }
                    }
                },
            )
        }
    }
}
