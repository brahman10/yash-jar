plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
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
    }

    sourceSets.getByName("main").resources.srcDirs("src/main/res").includes.addAll(arrayOf("**/*.*"))
    namespace = "com.jar.app.feature_gold_delivery"
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

    //Shared Base Module
    implementation(project(":shared:core-base"))

    // Core Web PDF Viewer
    implementation(project(mapOf("path" to ":core-web-pdf-viewer")))

    //Shared Feature-One-Time-Payments
    implementation(project(":shared:feature-one-time-payments"))

    //Shared Feature-One-Time-Payments-Common
    implementation(project(":shared:feature-one-time-payments-common"))

    //Feature Payment
    implementation(project(":feature-payment"))

    //Feature Gold Price
    implementation(project(":shared:feature-gold-price"))

    //Shared Gold delivery
    implementation(project(":shared:feature-gold-delivery"))

    //Feature Transaction Common
    implementation(project(":feature-transaction-common"))
    implementation(project(":shared:feature-transaction-common"))

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

    //Lottie
    implementation("com.airbnb.android:lottie:${Dependencies.Android.lottie_version}")

    //Dynamic Permission
    implementation("com.github.permissions-dispatcher:permissionsdispatcher:${Dependencies.Android.permission_version}")
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:${Dependencies.Android.permission_version}")

    //Access Contacts
    implementation("com.github.vestrel00.contacts-android:core:0.2.3")
    implementation("com.github.vestrel00:contacts-android:0.2.3")

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")

    //Logger
    implementation("com.jakewharton.timber:timber:${Dependencies.Android.timber_version}")

    //Phone Number Formatting
    implementation("io.michaelrocks:libphonenumber-android:${Dependencies.Android.phone_number_utils_version}")

    //Paging
    implementation("androidx.paging:paging-runtime-ktx:${Dependencies.Android.paging_version}")

    //Epoxy
    implementation("com.airbnb.android:epoxy:${Dependencies.Android.epoxy_version}")
    kapt("com.airbnb.android:epoxy-processor:${Dependencies.Android.epoxy_version}")

    //Text Drawable
//    implementation("com.amulyakhare:com.amulyakhare.textdrawable:1.0.1")

    implementation("androidx.hilt:hilt-navigation-fragment:${Dependencies.Android.hilt_navigation}")
}