plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
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
            buildConfigField("String", "AUSPICIOUS_TIME_TOPIC", "\"auspiciousTimeAlertsTopic\"")
        }

        create("prodReplica") {
            buildConfigField("String", "AUSPICIOUS_TIME_TOPIC", "\"auspiciousTimeAlertsReplica\"")
        }

        create("staging") {
            buildConfigField("String", "AUSPICIOUS_TIME_TOPIC", "\"auspiciousTimeAlertsReplica\"")
        }
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
    namespace = "com.jar.app.feature_settings"
}

dependencies {

    //Base Module
    implementation(project(":base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Utils Module
    implementation(project(":core-utils"))

    //Network Module
    implementation(project(":shared:core-network"))

    //Shared Core Base
    implementation(project(":shared:core-base"))

    //Shared Feature-Round-Off
    implementation(project(":shared:feature-round-off"))

    //Feature Daily-Investment
    implementation(project(":feature-daily-investment"))
    implementation(project(":shared:feature-daily-investment"))
    implementation(project(":feature-daily-investment-cancellation"))

    //Feature Daily-Investment Tempering

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //Preferences
    implementation(project(":shared:core-preferences"))

    //User API's
    implementation(project(":shared:feature-user-api"))

    //Core Analytics
    implementation(project(":shared:core-analytics"))

    //Shared Feature Settings
    implementation(project(":shared:feature-settings"))

    //Shared Feature Settings
    implementation(project(":shared:feature-settings"))

    //Feature Round off
    implementation(project(":feature-round-off"))

    //Feature Gold SIP
    implementation(project(":feature-gold-sip"))
    implementation(project(":shared:feature-gold-sip"))

    //Feature Round off
    implementation(project(":feature-round-off"))

    //Savings Common Module
    implementation(project(":shared:feature-savings-common"))

    //Testing
    testImplementation("junit:junit:${Dependencies.Android.junit_version}")
    androidTestImplementation("androidx.test.ext:junit:${Dependencies.Android.android_junit_version}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Dependencies.Android.esperesso_version}")

    //AppCompat + KTX
    implementation("androidx.core:core-ktx:${Dependencies.Android.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:${Dependencies.Android.app_compat_version}")

    //Material Component
    implementation("com.google.android.material:material:${Dependencies.Android.material_version}")

    //Fragment KTX
    implementation("androidx.fragment:fragment-ktx:${Dependencies.Android.fragment_ktx_version}")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")

    //Logger
    implementation("com.jakewharton.timber:timber:${Dependencies.Android.timber_version}")

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Lottie
    implementation("com.airbnb.android:lottie:${Dependencies.Android.lottie_version}")

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //Firebase Messaging
    implementation("com.google.firebase:firebase-messaging-ktx")

    //Adapter Delegate
    implementation("com.hannesdorfmann:adapterdelegates4:${Dependencies.Android.adapter_delegate_version}")

    //Autostarter - Permission Manager
    implementation("com.github.judemanutd:autostarter:${Dependencies.Android.auto_starter_version}")

}