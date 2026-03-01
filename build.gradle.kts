plugins {
    kotlin("jvm") version "2.0.21" apply false
    kotlin("android") version "2.0.21" apply false
    kotlin("multiplatform") version "2.0.21" apply false
    kotlin("plugin.serialization") version "2.0.21" apply false
    id("com.android.library") version "9.0.1" apply false
    id("app.cash.sqldelight") version "2.0.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.android.application") version "8.7.3" apply false
}

allprojects {
    group = "com.barcodebite"
    version = "0.1.0"
}
