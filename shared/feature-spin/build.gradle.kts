plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dev.icerock.mobile.multiplatform-resources")
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

                //Shared Core Remote Config
                implementation(project(":shared:core-remote-config"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

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

val modulePackageName = "com.jar.app.feature_spin.shared"

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