plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
}

android {
    namespace = "com.jar.app.feature_sms_sync"
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {

        }
        create("mock") {

        }
    }

    flavorDimensions.add("config")
    productFlavors {
        create("prod") {
            buildConfigField("String", "BASE_URL_SMS_PARSER", "\"production\"")
        }

        create("prodReplica") {
            buildConfigField("String", "BASE_URL_SMS_PARSER", "\"prod-replica\"")
        }

        create("staging") {
            buildConfigField("String", "BASE_URL_SMS_PARSER", "\"staging\"")
        }
    }

    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets.getByName("main").resources.srcDirs("src/main/res").includes.addAll(arrayOf("**/*.*"))
}


dependencies {

    //Network Module
    implementation(project(":shared:core-network"))

    //Core Util Module
    implementation(project(":core-utils"))

    //Core Analytics
    implementation(project(":shared:core-analytics"))

    //Core remote config
    implementation(project(":shared:core-remote-config"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //Base
    implementation(project(":base"))

    implementation("androidx.core:core-ktx:${Dependencies.Android.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")

    //Work Manager
    implementation("androidx.work:work-runtime-ktx:${Dependencies.Android.work_version}")
    implementation("androidx.hilt:hilt-work:${Dependencies.Android.hilt_worker}")
    kapt("androidx.hilt:hilt-compiler:${Dependencies.Android.hilt_worker}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")
}