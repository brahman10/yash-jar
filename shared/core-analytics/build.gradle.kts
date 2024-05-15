plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-kapt")
    id("com.codingfeline.buildkonfig")
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

                //Core Analytics
                api("com.jar.internal.library.core-analytics:shared:${Dependencies.Common.core_analytics_version}")

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {

                //Firebase BOM
                implementation(project.dependencies.platform("com.google.firebase:firebase-bom:${Dependencies.Android.firebase_version}"))

                //Firebase Analytics
                api("com.google.firebase:firebase-analytics-ktx")

                //PlotLine
                api("com.gitlab.plotline:plotline-android-sdk:${Dependencies.Android.plotline_version}")

                //Dagger DI
                implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
                configurations.getByName("kapt").dependencies.add(
                    org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                        "com.google.dagger",
                        "hilt-compiler",
                        Dependencies.Android.hilt_version
                    )
                )

                //CleverTap SDK
                //Made it API as it is being used in [com.jar.app.feature_homepage.impl.ui.homepage.HomeFragment]
                api("com.clevertap.android:clevertap-android-sdk:${Dependencies.Android.clevertap_version}")

                //AppsFlyer SDK
                api("com.appsflyer:af-android-sdk:${Dependencies.Android.appsflyer_version}")

                // Facebook Analytics SDK
                api("com.facebook.android:facebook-android-sdk:${Dependencies.Android.facebook_sdk_version}")

                // UserExperior Analytics SDK
                api("com.userexperior:userexperior-android:${Dependencies.Android.userexperior_version}")

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

val modulePackageName = "com.jar.app.core_analytics"

buildkonfig {
    packageName = modulePackageName
    exposeObjectWithName = "CoreAnalyticsBuildKonfig"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "AMPLITUDE_PROJECT_KEY",
            "76039b1784347a69f0b0e0fbbd7bf698"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "USER_EXPERIOR_PROJECT_KEY",
            "d1cc79d1-2233-4037-a11f-ac6247185738"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "APPSFLYER_DEV_KEY",
            "XVuH5ijKxLodedSdnqUh8f"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PLOT_LINE_KEY",
            "NGU3OGI5ZjktNjJlMy00MGJlLWEyNjctYmQ5OTVhNGYyODI4"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "XIAOMI_APP_KEY",
            "5551992779429"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "XIAOMI_APP_ID",
            "2882303761519927429"
        )
    }

    defaultConfigs("staging") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "AMPLITUDE_PROJECT_KEY",
            "76039b1784347a69f0b0e0fbbd7bf698"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "USER_EXPERIOR_PROJECT_KEY",
            "d1cc79d1-2233-4037-a11f-ac6247185738"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "APPSFLYER_DEV_KEY",
            "XVuH5ijKxLodedSdnqUh8f"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PLOT_LINE_KEY",
            "NGU3OGI5ZjktNjJlMy00MGJlLWEyNjctYmQ5OTVhNGYyODI4"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "XIAOMI_APP_KEY",
            "5551992779429"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "XIAOMI_APP_ID",
            "2882303761519927429"
        )
    }
    defaultConfigs("prodReplica") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "AMPLITUDE_PROJECT_KEY",
            "0a544cdf4a3addafef25222d3b35536a"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "USER_EXPERIOR_PROJECT_KEY",
            "ee06fb08-7564-46e6-a6ac-4eac0f21ee44"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "APPSFLYER_DEV_KEY",
            "XVuH5ijKxLodedSdnqUh8f"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PLOT_LINE_KEY",
            "MzFiMjFkZjUtMjVmOC00NmQwLThkNmQtOWJjNzJlZjcxNzNl"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "XIAOMI_APP_KEY",
            "5551992779429"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "XIAOMI_APP_ID",
            "2882303761519927429"
        )
    }
    defaultConfigs("prod") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "AMPLITUDE_PROJECT_KEY",
            "0a544cdf4a3addafef25222d3b35536a"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "USER_EXPERIOR_PROJECT_KEY",
            "ee06fb08-7564-46e6-a6ac-4eac0f21ee44"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "APPSFLYER_DEV_KEY",
            "XVuH5ijKxLodedSdnqUh8f"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PLOT_LINE_KEY",
            "MzFiMjFkZjUtMjVmOC00NmQwLThkNmQtOWJjNzJlZjcxNzNl"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "XIAOMI_APP_KEY",
            "5551992779429"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "XIAOMI_APP_ID",
            "2882303761519927429"
        )
    }
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