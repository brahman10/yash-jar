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
        getByName("debug") {}
        create("mock") {}
    }

    flavorDimensions.add("config")
    productFlavors {
        create("prod") {
            buildConfigField("String", "PAYTM_PACKAGE", "\"net.one97.paytm\"")
            buildConfigField("String", "PHONEPE_PACKAGE", "\"com.phonepe.app\"")
            buildConfigField("String", "GPAY_PACKAGE", "\"com.google.android.apps.nbu.paisa.user\"")
            buildConfigField("String", "PAYTM_MID", "\"CHANGE32137276398543\"")
            buildConfigField("String", "PAYTM_CALLBACK_URL", "\"https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=\"")
            buildConfigField("String", "PAYTM_PAYMENT_URL", "\"https://securegw.paytm.in/theia/api/v1/showPaymentPage\"")
        }

        create("prodReplica") {
            buildConfigField("String", "PAYTM_PACKAGE", "\"net.one97.paytm\"")
            buildConfigField("String", "PHONEPE_PACKAGE", "\"com.phonepe.app\"")
            buildConfigField("String", "GPAY_PACKAGE", "\"com.google.android.apps.nbu.paisa.user\"")
            buildConfigField("String", "PAYTM_MID", "\"CHANGE32137276398543\"")
            buildConfigField("String", "PAYTM_CALLBACK_URL", "\"https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=\"")
            buildConfigField("String", "PAYTM_PAYMENT_URL", "\"https://securegw.paytm.in/theia/api/v1/showPaymentPage\"")
        }

        create("staging") {
            buildConfigField("String", "PAYTM_PACKAGE", "\"net.one97.paytm\"")
            buildConfigField("String", "PHONEPE_PACKAGE", "\"com.phonepe.app.preprod\"")
            buildConfigField("String", "GPAY_PACKAGE", "\"com.google.android.apps.nbu.paisa.user\"")
            buildConfigField("String", "PAYTM_MID", "\"CHANGE11588127832171\"")
            buildConfigField("String", "PAYTM_CALLBACK_URL", "\"https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=\"")
            buildConfigField("String", "PAYTM_PAYMENT_URL", "\"https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage\"")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
    }
    kotlinOptions {
        jvmTarget = rootProject.extra.get("jvmTarget") as String
    }
    sourceSets.getByName("main").resources.srcDirs("src/main/res").includes.addAll(arrayOf("**/*.*"))
    namespace = "com.jar.app.feature_mandate_payment"
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

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //Shared Core Base
    implementation(project(":shared:core-base"))

    //Feature Buy Gold V2
    implementation(project(":shared:feature-coupon-api"))

    //Kotlin Date-Time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Dependencies.Common.kotlin_datetime_version}")

    //Common Mandate Payment Module
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

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Logger
    implementation("com.jakewharton.timber:timber:${Dependencies.Android.timber_version}")

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")

    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")

    //Adapter Delegate
    implementation("com.hannesdorfmann:adapterdelegates4:${Dependencies.Android.adapter_delegate_version}")
    implementation("com.hannesdorfmann:adapterdelegates4-kotlin-dsl-viewbinding:${Dependencies.Android.adapter_delegate_version}")

    //Paytm SDK
    implementation("com.paytm.appinvokesdk:appinvokesdk:${Dependencies.Android.paytm_sdk_version}")

    //Circle Progress Bar
    implementation("com.mikhaellopez:circularprogressbar:${Dependencies.Android.circular_progress_bar_version}")

}