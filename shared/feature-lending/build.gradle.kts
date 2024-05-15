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

                //Lending kyc
                implementation(project(":shared:feature-kyc"))

                //Network Module
                implementation(project(":shared:core-network"))

                //Analytics SDK
                implementation(project(":shared:core-analytics"))

                //Network Module
                implementation(project(":shared:core-network"))

                //Preferences
                implementation(project(":shared:core-preferences"))

                //Shared Base Module
                implementation (project(":shared:core-base"))

                //Remote Config SDK
                implementation (project(":shared:core-remote-config"))

                //User API
                implementation(project(":shared:feature-user-api"))

                implementation(project(":shared:feature-one-time-payments"))
                implementation(project(":shared:feature-one-time-payments-common"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                // Moko Parcelize
                implementation("dev.icerock.moko:parcelize:${Dependencies.Common.moko_parcelize}")

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

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

val modulePackageName = "com.jar.app.feature_lending.shared"

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