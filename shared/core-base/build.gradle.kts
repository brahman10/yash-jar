plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.codingfeline.buildkonfig")
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
                // Kotlin serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.Common.kotlin_serialization}")

                // Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                // Moko Parcelize
                implementation("dev.icerock.moko:parcelize:${Dependencies.Common.moko_parcelize}")

                // Kotlin BigNum
                implementation("com.ionspin.kotlin:bignum:${Dependencies.Common.kotlin_bignum}")

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
                // Play Services Ads
                implementation("com.google.android.gms:play-services-ads-lite:${Dependencies.Android.play_services_ads}")

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

val modulePackageName = "com.jar.app.core_base.shared"

multiplatformResources {
    multiplatformResourcesPackage = modulePackageName
    multiplatformResourcesClassName = "CoreBaseMR"
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

buildkonfig {
    packageName = modulePackageName
    exposeObjectWithName = "CoreBaseBuildKonfig"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SECRET_KEY",
            "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SALT",
            "QWlGNHNhMTJTQWZ2bGhpV3U="
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "INIT_VECTOR",
            "bVQzNFNhRkQ1Njc4UUFaWA=="
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ENV",
            "prod"
        )
    }

    defaultConfigs("staging") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ENV",
            "staging"
        )
    }
    defaultConfigs("prodReplica") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ENV",
            "prodReplica"
        )
    }
    defaultConfigs("prod") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ENV",
            "prod"
        )
    }
}