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
    namespace = "com.jar.app.feature_daily_investment_tempering"
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
}

dependencies {
    implementation(project(":shared"))

    //Base Module
    implementation(project(":base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Network Module
    implementation(project(":shared:core-network"))

    //Core UI Module
    implementation(project(":core-compose-ui"))

    // Compose UI
    implementation("androidx.compose.ui:ui")

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Shared Base Module
    implementation(project(":shared:core-base"))

    //Utils SDK
    implementation(project(":core-utils"))

    //Feature Payment
    implementation(project(":feature-payment"))

    //Kyc
    implementation(project(":feature-kyc"))

    //Feature User API
    implementation(project(":shared:feature-user-api"))

    //Feature Gold Price
    implementation(project(":shared:feature-gold-price"))

    //Shared Daily Investment Cancellation
    implementation(project(":shared:feature-daily-investment-cancellation"))

    //Feature Feature-One-Time-Payments
    implementation(project(":shared:feature-one-time-payments"))
    implementation(project(":shared:feature-one-time-payments-common"))

    //Buy Gold V2 SDK
    implementation(project(":feature-buy-gold-v2"))

    //Feature Mandate Payment
    implementation(project(":feature-mandate-payment"))

    //Core Web Pdf Viewer
    implementation(project(":core-web-pdf-viewer"))

    //Weekly Magic Common Feature
    implementation(project(":feature-weekly-magic-common"))
    implementation(project(":shared:feature-weekly-magic-common"))

    //Savings Common Module
    implementation(project(":shared:feature-savings-common"))

    //Daily Investment
    implementation(project(":feature-daily-investment"))
    implementation(project(":shared:feature-daily-investment"))

    // Post Setup
    implementation(project(":feature-post-setup"))

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")
    implementation(project(mapOf("path" to ":shared:feature-gold-redemption")))
    implementation(project(mapOf("path" to ":feature-mandate-payment-common")))

    //Testing
    testImplementation("junit:junit:${Dependencies.Android.junit_version}")
    androidTestImplementation("androidx.test.ext:junit:${Dependencies.Android.android_junit_version}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Dependencies.Android.esperesso_version}")

    //AppCompat + KTX
    implementation("androidx.core:core-ktx:${Dependencies.Android.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:${Dependencies.Android.app_compat_version}")
    implementation("androidx.legacy:legacy-support-v4:${Dependencies.Android.androidx_legacy_support}")

    //Material Component
    implementation("com.google.android.material:material:${Dependencies.Android.material_version}")

    //Constraint Layout
    implementation("androidx.constraintlayout:constraintlayout:${Dependencies.Android.constraint_layout_version}")

    //Fragment KTX
    implementation("androidx.fragment:fragment-ktx:${Dependencies.Android.fragment_ktx_version}")

    //Bounce Effect
    implementation("io.github.everythingme:overscroll-decor-android:${Dependencies.Android.overscroll_version}")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")
    implementation("androidx.hilt:hilt-navigation-fragment:${Dependencies.Android.hilt_navigation}")

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Lottie
    implementation("com.airbnb.android:lottie:${Dependencies.Android.lottie_version}")

    //Epoxy
    implementation("com.airbnb.android:epoxy:${Dependencies.Android.epoxy_version}")
    kapt("com.airbnb.android:epoxy-processor:${Dependencies.Android.epoxy_version}")

    //Circle Progress Bar
    implementation("com.mikhaellopez:circularprogressbar:${Dependencies.Android.circular_progress_bar_version}")

    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:${Dependencies.Android.compose_bom_version}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")

    implementation("me.onebone:toolbar-compose:${Dependencies.Android.compose_onebone_toolbar}")
    //Material Design
    implementation("androidx.compose.material:material")

    //Integration with activities
    implementation("androidx.activity:activity-compose")
    //Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    //Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("com.github.bumptech.glide:compose:${Dependencies.Android.compose_glide_version}")

    implementation ("com.airbnb.android:lottie-compose:${Dependencies.Android.compose_lottie}")
}