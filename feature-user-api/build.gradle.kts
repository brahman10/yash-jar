plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("kotlin-android")
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

        }

        create("prodReplica") {

        }

        create("staging") {

        }
    }

    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = rootProject.extra.get("jvmTarget") as String
    }
    sourceSets.getByName("main").resources.srcDirs("src/main/res").includes.addAll(arrayOf("**/*.*"))
    sourceSets.getByName("test").resources.srcDirs("src/test/resources").includes.addAll(arrayOf("**/*.*"))
    namespace = "com.jar.app.feature_user_api"
}

dependencies {

    //Network Module
    implementation(project(":shared:core-network"))

    //Shared Base Module
    implementation(project(":shared:core-base"))

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Preferences
    implementation(project(":shared:core-preferences"))

    //Preferences
    implementation(project(":shared:feature-user-api"))

    //Base Module
    implementation(project(":base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Utils Module
    implementation(project(":core-utils"))

    //AppCompat + KTX
    implementation("androidx.core:core-ktx:${Dependencies.Android.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:${Dependencies.Android.app_compat_version}")

    //Fragment KTX
    implementation("androidx.fragment:fragment-ktx:${Dependencies.Android.fragment_ktx_version}")

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //Phone Number Formatting
    implementation("io.michaelrocks:libphonenumber-android:${Dependencies.Android.phone_number_utils_version}")

    //Room SQLite ORM
    implementation("androidx.room:room-runtime:${Dependencies.Android.room_version}")
    kapt("androidx.room:room-compiler:${Dependencies.Android.room_version}")
    implementation("androidx.room:room-ktx:${Dependencies.Android.room_version}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

}