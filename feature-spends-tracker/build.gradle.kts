plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
}

android {
    namespace = "com.jar.app.feature_spends_tracker"
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
        getByName("debug") {}
        create("mock") {}
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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "${Dependencies.Android.compose_kotlin_compiler_version}"
    }
    sourceSets.getByName("main").resources.srcDirs("src/main/res").includes.addAll(arrayOf("**/*.*"))
}

dependencies {

    //Base Module
    implementation(project(":base"))

    //Shared Core Base Module
    implementation(project(":shared:core-base"))

    //Network Module
    implementation(project(":shared:core-network"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Core UI Module
    implementation(project(":core-utils"))

    //Core Web Pdf Viewer
    implementation(project(":core-web-pdf-viewer"))

    //Feature Gold Price
    implementation(project(":shared:feature-gold-price"))

    //Core Analytics
    implementation(project(":shared:core-analytics"))

    //Feature Payment
    implementation(project(":feature-payment"))
    implementation(project(":shared:feature-one-time-payments"))
    implementation(project(":shared:feature-one-time-payments-common"))

    //Core remote config
    implementation(project(":shared:core-remote-config"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //Feature User API
    implementation(project(":shared:feature-user-api"))

    //Weekly Magic Common Feature
    implementation(project(":shared:feature-weekly-magic-common"))

    //Shared Feature Spends
    implementation(project(":shared:feature-spends-tracker"))

    //Paging
    implementation("androidx.paging:paging-common-ktx:${Dependencies.Android.paging_version}")
    implementation("androidx.paging:paging-runtime-ktx:${Dependencies.Android.paging_version}")
    implementation(project(":feature-in-app-review"))
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:${Dependencies.Android.swipe_refresh_version}")
    implementation(project(mapOf("path" to ":shared:feature-spends-tracker")))

    //Testing
    testImplementation("junit:junit:${Dependencies.Android.junit_version}")
    androidTestImplementation("androidx.test.ext:junit:${Dependencies.Android.android_junit_version}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Dependencies.Android.esperesso_version}")

    //Material Component
    implementation("com.google.android.material:material:${Dependencies.Android.material_version}")

    //Fragment KTX
    implementation("androidx.fragment:fragment-ktx:${Dependencies.Android.fragment_ktx_version}")

    //AppCompat + KTX
    implementation("androidx.core:core-ktx:${Dependencies.Android.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:${Dependencies.Android.app_compat_version}")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Logger
    implementation("com.jakewharton.timber:timber:${Dependencies.Android.timber_version}")

    //Lottie
    implementation("com.airbnb.android:lottie:${Dependencies.Android.lottie_version}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")

    //LiveData + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Dependencies.Android.lifecycle_version}")

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")

    implementation("androidx.hilt:hilt-navigation-fragment:${Dependencies.Android.hilt_navigation}")

    //Epoxy
    implementation("com.airbnb.android:epoxy:${Dependencies.Android.epoxy_version}")
    kapt("com.airbnb.android:epoxy-processor:${Dependencies.Android.epoxy_version}")

    //Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:${Dependencies.Android.compose_bom_version}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    //Material Design
    implementation("androidx.compose.material:material")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")

    //Integration with activities
    implementation("androidx.activity:activity-compose")
    //Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    //Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    //play-services-base
    implementation("com.google.android.gms:play-services-basement:${Dependencies.Android.play_services_basement}")

    //downloadable fonts
    implementation("androidx.compose.ui:ui-text-google-fonts")
}