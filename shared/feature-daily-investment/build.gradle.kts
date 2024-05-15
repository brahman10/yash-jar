plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
}

kotlin {
    android {
        publishAllLibraryVariants()
    }

    ios()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {

                //Network Module
                implementation(project(":shared:core-network"))

                //Shared Base Module
                implementation(project(":shared:core-base"))

                //Remote Config SDK
                implementation(project(":shared:core-remote-config"))

                //Common Flow
                api("com.jar.internal.library.core-kmm-flow:shared:${Dependencies.Common.core_kmm_flow}")

                //Feature User API
                implementation(project(":shared:feature-user-api"))

                //Analytics SDK
                implementation(project(":shared:core-analytics"))

                //Preferences
                implementation(project(":shared:core-preferences"))

                //Shared Feature Daily Investment
                implementation(project(":shared:feature-mandate-payments-common"))

                //Shared Weekly Magic
                implementation(project(":shared:feature-weekly-magic-common"))

                //Shared Feature Gold Price
                implementation(project(":shared:feature-gold-price"))

                //Shared Feature Buy Gold
                implementation(project(":shared:feature-buy-gold-v2"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                // Moko Parcelize
                implementation("dev.icerock.moko:parcelize:${Dependencies.Common.moko_parcelize}")

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

                //Shared Feature-One-Time-Payments
                implementation(project(":shared:feature-one-time-payments-common"))

                //Savings Common Module
                implementation(project(":shared:feature-savings-common"))

                implementation(project(":shared:feature-exit-survey"))

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                //Dagger DI
                implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
                configurations.getByName("kapt").dependencies.add(
                    org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                        "com.google.dagger",
                        "hilt-compiler",
                        Dependencies.Android.hilt_version
                    )
                )
            }
        }
        val androidUnitTest by getting
        val iosMain by getting
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}


val packageName = "com.jar.app.feature_daily_investment.shared"

multiplatformResources {
    multiplatformResourcesPackage = packageName
    multiplatformResourcesClassName = "DailyInvestmentMR"
}


android {
    namespace = packageName
    compileSdk = rootProject.extra.get("compileSdk") as Int
    defaultConfig {
        minSdk = rootProject.extra.get("minSdk") as Int
        targetSdk = rootProject.extra.get("targetSdk") as Int
    }
}
