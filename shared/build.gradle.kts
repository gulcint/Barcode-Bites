plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("app.cash.sqldelight")
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:runtime:2.0.2")
                implementation("io.ktor:ktor-client-core:2.3.12")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-mock:2.3.12")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("app.cash.sqldelight:android-driver:2.0.2")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:2.3.12")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
            }
        }
    }
}

kotlin.jvmToolchain(17)

android {
    namespace = "com.barcodebite.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}

sqldelight {
    databases {
        create("BarcodeBiteDatabase") {
            packageName.set("com.barcodebite.shared.local")
        }
    }
}
