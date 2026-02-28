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
import com.barcodebite.android.ui.screen.history.HistoryScreen
import com.barcodebite.android.ui.screen.history.HistoryViewModel
import com.barcodebite.android.ui.screen.home.HomeScreen
import com.barcodebite.android.ui.screen.result.ProductResultScreen
import com.barcodebite.android.ui.screen.scanner.ScannerScreen
import com.barcodebite.shared.domain.repository.ScanHistoryRepository

object AppRoute {
    const val Home = "home"
    const val Scanner = "scanner"
    const val History = "history"
    const val Result = "result/{barcode}"

    fun result(barcode: String): String = "result/${Uri.encode(barcode)}"
}

@Composable
fun AppNavigation(
    scanHistoryRepository: ScanHistoryRepository,
    navController: NavHostController = rememberNavController(),
) {
    val historyViewModel = remember(scanHistoryRepository) {
        HistoryViewModel(scanHistoryRepository = scanHistoryRepository)
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
    ) {
        composable(AppRoute.Home) {
            HomeScreen(
                onScanClick = { navController.navigate(AppRoute.Scanner) },
                onHistoryClick = { navController.navigate(AppRoute.History) },
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
    }
}
