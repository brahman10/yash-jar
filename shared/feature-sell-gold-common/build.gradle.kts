plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    android {
        publishAllLibraryVariants()
    }

    ios()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {

                // Moko Parcelize
                implementation("dev.icerock.moko:parcelize:${Dependencies.Common.moko_parcelize}")

                //Kotlinx-Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.Common.kotlin_serialization}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

val modulePackageName = "com.jar.app.feature_sell_gold_common.shared"

android {
    namespace = modulePackageName
    compileSdk = rootProject.extra.get("compileSdk") as Int
    defaultConfig {
        minSdk = rootProject.extra.get("minSdk") as Int
        targetSdk = rootProject.extra.get("targetSdk") as Int
    }

    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
    }
}