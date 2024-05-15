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

                //Shared Core Base Module
                implementation(project(":shared:core-base"))

                //Shared feature-buy-gold Module
                implementation(project(":shared:feature-buy-gold-v2"))

                //Shared feature-kyc
                implementation(project(":shared:feature-kyc"))

                //Shared feature-lending-kyc
                implementation(project(":shared:feature-lending-kyc"))

                //Preferences
                implementation (project(":shared:core-preferences"))

                //Shared feature-gold-price Module
                implementation(project(":shared:feature-gold-price"))

                //Shared feature-one-time-payments Module
                implementation(project(":shared:feature-one-time-payments"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                // Moko Parcelize
                implementation("dev.icerock.moko:parcelize:${Dependencies.Common.moko_parcelize}")

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

                //Shared Feature Transaction Common
                implementation(project(":shared:feature-transaction-common"))

                //Shared Feature User Api
                implementation(project(":shared:feature-user-api"))

                //KMM Paging
                api("com.jar.internal.library.paging:paging:${Dependencies.Common.core_paging_version}")

                //Common Flow
                api("com.jar.internal.library.core-kmm-flow:shared:${Dependencies.Common.core_kmm_flow}")
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


val modulePackageName = "com.jar.app.feature_gold_lease.shared"

multiplatformResources {
    multiplatformResourcesPackage = modulePackageName
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