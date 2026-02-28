package com.barcodebite.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.barcodebite.android.data.history.HistoryContainer
import com.barcodebite.android.ui.navigation.AppNavigation
import com.barcodebite.android.ui.theme.BarcodeBiteTheme

@Composable
fun BarcodeBiteApp() {
    val context = LocalContext.current
    val historyContainer = remember {
        HistoryContainer(context.applicationContext)
    }

    BarcodeBiteTheme {
        AppNavigation(scanHistoryRepository = historyContainer.scanHistoryRepository)
    }
}
