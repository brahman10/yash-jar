plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
    id("kotlin-kapt")
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
                //Network Module
                implementation(project(":shared:core-network"))

                //Shared Base Module
                implementation(project(":shared:core-base"))

                //Remote Config SDK
                implementation(project(":shared:core-remote-config"))

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

val modulePackageName = "com.jar.app.feature_user_api.shared"

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

sqldelight {
    database("UserMetaDatabase") {
        packageName = modulePackageName
    }
}