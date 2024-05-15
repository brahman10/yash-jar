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
            buildConfigField("String", "GOOGLE_PLACES_API_KEY_ENCRYPTED", "\"cYcLYisAdS1hHb1q8RVoCchEAfGOBYrpOBp3tyHt3JamGiTmPC0yqKXkeyoob8qv\"")
        }

        create("prodReplica") {
            buildConfigField("String", "GOOGLE_PLACES_API_KEY_ENCRYPTED", "\"cYcLYisAdS1hHb1q8RVoCchEAfGOBYrpOBp3tyHt3JamGiTmPC0yqKXkeyoob8qv\"")
        }

        create("staging") {
            buildConfigField("String", "GOOGLE_PLACES_API_KEY_ENCRYPTED", "\"cYcLYisAdS1hHb1q8RVoCchEAfGOBYrpOBp3tyHt3JamGiTmPC0yqKXkeyoob8qv\"")
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
    namespace = "com.jar.app.feature_lending"
}

dependencies {
    //Base Module
    implementation(project(":base"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Network Module
    implementation(project(":shared:core-network"))

    //Preferences
    implementation(project(":shared:core-preferences"))

    //Shared Feature Lending
    implementation(project(":shared:feature-lending"))
    //Shared Feature Lending
    implementation(project(":shared:feature-lending-kyc"))

    //Shared Base Module
    implementation (project(":shared:core-base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Core Utils
    implementation(project(":core-utils"))

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //User API
    implementation(project(":shared:feature-user-api"))

    //Lending KYC
    implementation(project(":feature-lending-kyc"))

    //PDF viewer for BASE64
    implementation(project(":core-web-pdf-viewer"))

    //Feature Payment
    implementation(project(":feature-payment"))

    implementation(project(":shared:feature-one-time-payments"))
    implementation(project(":shared:feature-one-time-payments-common"))

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

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")

    //Phone Number Formatting
    implementation("io.michaelrocks:libphonenumber-android:${Dependencies.Android.phone_number_utils_version}")

    //OTP View
    implementation("com.github.mukeshsolanki:android-otpview-pinview:${Dependencies.Android.otp_pin_view}")

    //Google Places
    implementation("com.google.android.libraries.places:places:${Dependencies.Android.google_places}")

    //Dynamic Permission
    implementation("com.github.permissions-dispatcher:permissionsdispatcher:${Dependencies.Android.permission_version}")
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:${Dependencies.Android.permission_version}")

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //Flexbox
    implementation("com.google.android.flexbox:flexbox:${Dependencies.Android.flex_box_version}")

    // User Experior Analytics SDK
    implementation("com.userexperior:userexperior-android:${Dependencies.Android.userexperior_version}")

    //Core Compose UI
    implementation(project(":core-compose-ui"))

    //Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:${Dependencies.Android.compose_bom_version}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    //Material Design, added beta to fix a bottomsheet crash
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

    //LiveData + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-process:${Dependencies.Android.lifecycle_version}")


    //For Hilt View Model
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")

    //Constraint layout for compose
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.0")

    //Glide Image for compose
    implementation("com.github.bumptech.glide:compose:${Dependencies.Android.compose_glide_version}")

    //lottie animation for compose
    implementation ("com.airbnb.android:lottie-compose:${Dependencies.Android.compose_lottie}")
}
configurations {
    all {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
}
