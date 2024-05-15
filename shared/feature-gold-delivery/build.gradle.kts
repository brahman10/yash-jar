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

                //Shared Feature-One-Time-Payments
                implementation(project(":shared:feature-one-time-payments"))

                //Shared Feature-One-Time-Payments-Common
                implementation(project(":shared:feature-one-time-payments-common"))

                //Feature Gold Price
                implementation(project(":shared:feature-gold-price"))

                //Shared Feature Transaction(Common)
                api(project(":shared:feature-transaction"))
                api(project(":shared:feature-transaction-common"))

                //Network Module
                implementation(project(":shared:core-network"))

                //Shared Base Module
                implementation(project(":shared:core-base"))

                //Remote Config SDK
                implementation(project(":shared:core-remote-config"))

                //Common Flow
                api("com.jar.internal.library.core-kmm-flow:shared:${Dependencies.Common.core_kmm_flow}")

                //Feature User API
                api(project(":shared:feature-user-api"))

                implementation(project(":shared:feature-weekly-magic-common"))

                //Analytics SDK
                implementation(project(":shared:core-analytics"))

                //Preferences
                implementation(project(":shared:core-preferences"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                // Moko Parcelize
                implementation("dev.icerock.moko:parcelize:${Dependencies.Common.moko_parcelize}")

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

                //KMM Paging
                api("com.jar.internal.library.paging:paging:${Dependencies.Common.core_paging_version}")
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

val packageName = "com.jar.app.feature_gold_delivery.shared"

multiplatformResources {
    multiplatformResourcesPackage = packageName
}

android {
    namespace = packageName
    compileSdk = rootProject.extra.get("compileSdk") as Int
    defaultConfig {
        minSdk = rootProject.extra.get("minSdk") as Int
        targetSdk = rootProject.extra.get("targetSdk") as Int
    }
}