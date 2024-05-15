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
            buildConfigField("String", "PAYTM_MID", "\"CHANGE32137276398543\"")
            buildConfigField("String", "PAYTM_CALLBACK_URL", "\"https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=\"")
            buildConfigField("String", "PAYTM_PAYMENT_URL", "\"https://securegw.paytm.in/theia/api/v1/showPaymentPage\"")
            buildConfigField("String", "JUSPAY_CLIENT_ID", "\"jar\"")
            buildConfigField("String", "JUSPAY_MID", "\"jar\"")
            buildConfigField("String", "JUSPAY_SERVICE", "\"in.juspay.hyperapi\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"production\"")
        }

        create("prodReplica") {
            buildConfigField("String", "PAYTM_MID", "\"CHANGE32137276398543\"")
            buildConfigField("String", "PAYTM_CALLBACK_URL", "\"https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=\"")
            buildConfigField("String", "PAYTM_PAYMENT_URL", "\"https://securegw.paytm.in/theia/api/v1/showPaymentPage\"")
            buildConfigField("String", "JUSPAY_CLIENT_ID", "\"jar\"")
            buildConfigField("String", "JUSPAY_MID", "\"jar\"")
            buildConfigField("String", "JUSPAY_SERVICE", "\"in.juspay.hyperapi\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"production\"")
        }

        create("staging") {
            buildConfigField("String", "PAYTM_MID", "\"CHANGE11588127832171\"")
            buildConfigField("String", "PAYTM_CALLBACK_URL", "\"https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=\"")
            buildConfigField("String", "PAYTM_PAYMENT_URL", "\"https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage\"")
            buildConfigField("String", "JUSPAY_CLIENT_ID", "\"jar-sandbox\"")
            buildConfigField("String", "JUSPAY_MID", "\"jar-sandbox\"")
            buildConfigField("String", "JUSPAY_SERVICE", "\"in.juspay.hyperapi\"")
            buildConfigField("String", "JUSPAY_ENVIRONMENT", "\"sandbox\"")
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
    namespace = "com.jar.app.feature_payment"
}

dependencies {
    //Core Logger
    implementation(project(":shared:core-logger"))

    //Base Module
    implementation(project(":base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Utils Module
    implementation(project(":core-utils"))

    //Network Module
    implementation(project(":shared:core-network"))

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //Feature Gold Price
    implementation(project(":shared:feature-gold-price"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Shared Core Preference
    implementation(project(":shared:core-preferences"))

    //Shared Base Module
    implementation(project(":shared:core-base"))

    //Core UI Module
    implementation(project(":core-compose-ui"))

    //Shared Feature-One-Time-Payments
    implementation(project(":shared:feature-one-time-payments"))

    //Shared Feature-One-Time-Payments-Common
    implementation(project(":shared:feature-one-time-payments-common"))

    //Shared Feature-One-Time-Payments-Common
    implementation(project(":feature-mandate-payment-common"))
    implementation(project(mapOf("path" to ":shared:feature-exit-survey")))

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

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //Juspay SDK
    implementation("in.juspay:hypersdk:${Dependencies.Android.juspay_sdk_version}")

    //Paytm SDK
    implementation("com.paytm.appinvokesdk:appinvokesdk:${Dependencies.Android.paytm_sdk_version}")

    //Logger
    implementation("com.jakewharton.timber:timber:${Dependencies.Android.timber_version}")

    //Circle Progress Bar
    implementation("com.mikhaellopez:circularprogressbar:${Dependencies.Android.circular_progress_bar_version}")

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

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")

    //Lottie
    implementation("com.airbnb.android:lottie:${Dependencies.Android.lottie_version}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //Adapter Delegate
    implementation("com.hannesdorfmann:adapterdelegates4:${Dependencies.Android.adapter_delegate_version}")

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

    implementation("androidx.paging:paging-compose:${Dependencies.Android.compose_paging_version}")
    api("androidx.paging:paging-compose:${Dependencies.Android.compose_paging_version}")

    //downloadable fonts
    implementation("androidx.compose.ui:ui-text-google-fonts")

    // glide image loader
    implementation("com.github.bumptech.glide:compose:${Dependencies.Android.compose_glide_version}")

    api ("com.airbnb.android:lottie-compose:${Dependencies.Android.compose_lottie}")

}