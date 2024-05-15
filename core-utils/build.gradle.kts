plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
}


android {
    compileSdk = rootProject.extra.get("compileSdk") as Int

    defaultConfig {
        minSdk = rootProject.extra.get("minSdk") as Int
        targetSdk = rootProject.extra.get("targetSdk") as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {

        }
        create("mock") {

        }
    }

    flavorDimensions.add("config")
    productFlavors {
        create("prod") {
            buildConfigField("String", "APPS_FLYER_TEMPLATE_ID", "\"6H9Q\"")
        }

        create("prodReplica") {
            buildConfigField("String", "APPS_FLYER_TEMPLATE_ID", "\"6H9Q\"")
        }

        create("staging") {
            buildConfigField("String", "APPS_FLYER_TEMPLATE_ID", "\"iZH6\"")
        }
    }

    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
    }
    kotlinOptions {
        jvmTarget = rootProject.extra.get("jvmTarget") as String
    }
    namespace = "com.jar.app.core_utils"
}

dependencies {

    //Base
    implementation(project(":base"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //Shared Core Base
    implementation(project(":shared:core-base"))

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

    // Play Services Ads
    implementation("com.google.android.gms:play-services-ads-lite:${Dependencies.Android.play_services_ads}")

    //Logger
    implementation("com.jakewharton.timber:timber:${Dependencies.Android.timber_version}")

    //Play Core
    implementation ("com.google.android.play:review:2.0.1")
    implementation ("com.google.android.play:review-ktx:2.0.1")

    // Biometric
    implementation("androidx.biometric:biometric:${Dependencies.Android.androidx_biometric_version}")

    //AppsFlyer SDK
    implementation("com.appsflyer:af-android-sdk:${Dependencies.Android.appsflyer_version}")

    //AppsFlyer LVL SDK
    implementation("com.appsflyer:lvl:${Dependencies.Android.appsflyer_version}")

    // Kotlinx-Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.Common.kotlin_serialization}")
}