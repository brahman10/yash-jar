plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
}

kapt {
    correctErrorTypes = true
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

    kotlinOptions {
        jvmTarget = rootProject.extra.get("jvmTarget") as String
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "${Dependencies.Android.compose_kotlin_compiler_version}"
    }

    sourceSets.getByName("main").resources.srcDirs("src/main/res").includes.addAll(arrayOf("**/*.*"))
    namespace = "com.jar.app.feature_gold_price_alerts"
}


dependencies {

    //Base Module
    implementation(project(":base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Core UI Module
    implementation(project(":core-compose-ui"))

    //Utils Module
    implementation(project(":core-utils"))

    // Buy gold V2
    implementation(project(":feature-buy-gold-v2"))

    //Network Module
    implementation(project(":shared:core-network"))

    //Remote Config SDK
    implementation(project(":shared:core-remote-config"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //Shared Core Base Module
    implementation(project(":shared:core-base"))

    //Shared Gold price module
    implementation(project(":shared:feature-gold-price"))

    //Shared Gold price alerts module
    implementation(project(":shared:feature-gold-price-alerts"))

    //Common Flow
    api("com.jar.internal.library.core-kmm-flow:shared:${Dependencies.Common.core_kmm_flow}")

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

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    implementation("androidx.hilt:hilt-navigation-fragment:${Dependencies.Android.hilt_navigation}")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")

    //Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

    // alternatively - without Android dependencies for tests
    testImplementation("androidx.paging:paging-common:${Dependencies.Android.compose_paging}")

    //Graphs
    implementation("com.github.PhilJay:MPAndroidChart:${Dependencies.Android.android_mp_chart}")

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Ripple library
    implementation ("com.skyfishjy.ripplebackground:library:1.0.1")

    implementation("androidx.hilt:hilt-navigation-fragment:${Dependencies.Android.hilt_navigation}")

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")
}