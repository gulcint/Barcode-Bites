package com.barcodebite.android

import androidx.compose.runtime.Composable
import com.barcodebite.android.ui.navigation.AppNavigation
import com.barcodebite.android.ui.theme.BarcodeBiteTheme

@Composable
fun BarcodeBiteApp() {
    BarcodeBiteTheme {
        AppNavigation()
    }
}
