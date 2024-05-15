plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-kapt")
    id("kotlin-parcelize")
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

                //Shared Feature Gold Price Module
                implementation(project(":shared:feature-gold-price"))

                //Shared Feature User Api Module
                implementation(project(":shared:feature-user-api"))

                //Feature Transaction Common Module
                implementation(project(":shared:feature-transaction-common"))

                //Feature Sell Gold Common Module
                implementation(project(":shared:feature-sell-gold-common"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                // Moko Parcelize
                implementation("dev.icerock.moko:parcelize:${Dependencies.Common.moko_parcelize}")

                //KMM Paging
                api("com.jar.internal.library.paging:paging:${Dependencies.Common.core_paging_version}")

                //Common Flow
                api("com.jar.internal.library.core-kmm-flow:shared:${Dependencies.Common.core_kmm_flow}")

                //Kotlin Date-Time
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Dependencies.Common.kotlin_datetime_version}")
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
        val iosMain by getting {
            dependencies {

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

val modulePackageName = "com.jar.app.feature_transactions.shared"

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