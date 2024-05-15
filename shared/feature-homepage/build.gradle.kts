plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.squareup.sqldelight")
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

                //Shared Network Module
                implementation(project(":shared:core-network"))

                //Shared Base Module
                implementation(project(":shared:core-base"))

                //Shared Core Preferences
                implementation(project(":shared:core-preferences"))

                //Analytics SDK
                implementation(project(":shared:core-analytics"))

                //Shared Core Remote Config
                implementation(project(":shared:core-remote-config"))

                //Shared Feature Gold Price
                implementation(project(":shared:feature-gold-price"))

                //Shared Feature Contact Sync Common
                api(project(":shared:feature-contact-sync-common"))

                //Shared Feature Jar Duo
                api(project(":shared:feature-jar-duo"))

                //Shared Feature Round-off
                implementation(project(":shared:feature-round-off"))

                //Shared Feature Weekly Magic Common
                implementation(project(":shared:feature-weekly-magic-common"))

                //Shared Feature User Api
                implementation(project(":shared:feature-user-api"))

                //Shared Feature User Api
                implementation(project(":shared:feature-buy-gold-v2"))

                //Shared Feature One Time Payments
                implementation(project(":shared:feature-one-time-payments"))

                //Shared Feature One Time Payments Common
                implementation(project(":shared:feature-one-time-payments-common"))

                //Shared Feature Lending Kyc
                implementation(project(":shared:feature-lending-kyc"))

                //Shared Feature Lending Kyc
                implementation(project(":shared:feature-spin"))

                //Shared Feature Daily Investment
                implementation(project(":shared:feature-daily-investment"))

                //Shared Feature Coupon Api
                implementation(project(":shared:feature-coupon-api"))

                //Shared Feature Transaction
                implementation(project(":shared:feature-transaction"))

                //Shared Feature Savings Common
                api(project(":shared:feature-savings-common"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

                //Common Flow
                api("com.jar.internal.library.core-kmm-flow:shared:${Dependencies.Common.core_kmm_flow}")

                //SqlDelight
                implementation("com.squareup.sqldelight:runtime:${Dependencies.Common.sqldelight_version}")
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

                //SqlDelight
                implementation("com.squareup.sqldelight:android-driver:${Dependencies.Common.sqldelight_version}")
                implementation("com.squareup.sqldelight:coroutines-extensions:${Dependencies.Common.sqldelight_version}")
            }
        }
        val androidUnitTest by getting
        val iosMain by getting {
            dependencies {
                //SqlDelight
                implementation("com.squareup.sqldelight:native-driver:${Dependencies.Common.sqldelight_version}")

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}

val modulePackageName = "com.jar.app.feature_home.shared"

multiplatformResources {
    multiplatformResourcesPackage = modulePackageName
    multiplatformResourcesClassName = "FeatureHomepageMR"
}

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

sqldelight {
    database("HomePageDatabase") {
        packageName = modulePackageName
    }
}