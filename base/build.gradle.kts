plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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
        create("prod") {}
        create("prodReplica") {}
        create("staging") {}
    }
    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
    }
    kotlinOptions {
        jvmTarget = rootProject.extra.get("jvmTarget") as String
    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets.getByName("main").resources.srcDirs("src/main/res").includes.addAll(arrayOf("**/*.*"))
    namespace = "com.jar.app.base"
}

dependencies {

    //Preference Module
    implementation(project(":shared:core-preferences"))

    //Shared Base Module
    implementation(project(":shared:core-base"))

    //Core Network Module
    implementation(project(":shared:core-network"))

    //Moko Resources
    implementation("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

    //Testing
    testImplementation("junit:junit:${Dependencies.Android.junit_version}")
    androidTestImplementation("androidx.test.ext:junit:${Dependencies.Android.android_junit_version}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Dependencies.Android.esperesso_version}")

    //AppCompat + KTX
    implementation("androidx.core:core-ktx:${Dependencies.Android.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:${Dependencies.Android.app_compat_version}")

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Progress HUD
    implementation("com.kaopiz:kprogresshud:${Dependencies.Android.progress_hud_version}")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")

    //Logger
    implementation("com.jakewharton.timber:timber:${Dependencies.Android.timber_version}")

    //Custom Chrome Tabs
    implementation("androidx.browser:browser:${Dependencies.Android.androidx_browser}")

    //Compression image
    implementation("top.zibin:Luban:${Dependencies.Android.luban}")

    //Auto Read
    implementation("com.google.android.gms:play-services-auth:${Dependencies.Android.play_services_auth}")
    implementation("com.google.android.gms:play-services-auth-api-phone:${Dependencies.Android.play_services_auth_api_phone}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.Common.kotlin_serialization}")

    //Intuit dynamic dp and sp
    api("com.intuit.ssp:ssp-android:${Dependencies.Android.intuit}")
    api("com.intuit.sdp:sdp-android:${Dependencies.Android.intuit}")
}