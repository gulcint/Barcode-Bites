package com.barcodebite.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.barcodebite.android.data.compare.CompareRepository
import com.barcodebite.android.data.profile.DefaultProfileRepository
import com.barcodebite.android.data.profile.SubscriptionSyncRepository
import com.barcodebite.android.data.history.HistoryContainer
import com.barcodebite.android.launch.AppLaunchStore
import com.barcodebite.android.observability.createAppAnalytics
import com.barcodebite.android.observability.createCrashReporter
import com.barcodebite.android.premium.SharedPrefsPremiumGate
import com.barcodebite.android.session.SessionStore
import com.barcodebite.android.ui.navigation.AppNavigation
import com.barcodebite.android.ui.theme.BarcodeBiteTheme

@Composable
fun BarcodeBiteApp() {
    val context = LocalContext.current
    val historyContainer = remember {
        HistoryContainer(context.applicationContext)
    }
    val profileRepository = remember {
        DefaultProfileRepository(baseUrl = BuildConfig.API_BASE_URL)
    }
    val compareRepository = remember {
        CompareRepository(baseUrl = BuildConfig.API_BASE_URL)
    }
    val subscriptionSyncRepository = remember {
        SubscriptionSyncRepository(baseUrl = BuildConfig.API_BASE_URL)
    }
    val premiumGate = remember {
        SharedPrefsPremiumGate(context.applicationContext)
    }
    val sessionStore = remember {
        SessionStore(context.applicationContext)
    }
    val analytics = remember { createAppAnalytics(context.applicationContext) }
    val crashReporter = remember { createCrashReporter(context.applicationContext) }
    val appLaunchStore = remember {
        AppLaunchStore(context.applicationContext)
    }

    DisposableEffect(Unit) {
        analytics.logEvent("app_open")
        onDispose {
            profileRepository.close()
            compareRepository.close()
            subscriptionSyncRepository.close()
        }
    }

    DisposableEffect(Unit) {
        val handler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            crashReporter.recordException(e, "uncaught_exception:${t.name}")
            handler?.uncaughtException(t, e)
        }
        onDispose {
            Thread.setDefaultUncaughtExceptionHandler(handler)
        }
    }

    BarcodeBiteTheme {
        AppNavigation(
            scanHistoryRepository = historyContainer.scanHistoryRepository,
            profileRepository = profileRepository,
            subscriptionSyncRepository = subscriptionSyncRepository,
            compareRepository = compareRepository,
            premiumGate = premiumGate,
            isOnboardingCompleted = { appLaunchStore.isOnboardingCompleted() },
            setOnboardingCompleted = { appLaunchStore.setOnboardingCompleted(true) },
            accessTokenProvider = { sessionStore.getAccessToken() },
        )
    }
}
