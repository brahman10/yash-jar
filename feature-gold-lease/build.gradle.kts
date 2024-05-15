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
    namespace = "com.jar.app.feature_gold_lease"
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

    //Shared Base Module
    implementation(project(":shared:core-base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Core Utils
    implementation(project(":core-utils"))

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //Shared Feature-One-Time-Payments
    implementation(project(":shared:feature-one-time-payments"))

    //Shared Feature-One-Time-Payments-Common
    implementation(project(":shared:feature-one-time-payments-common"))

    //Feature Transaction
    implementation(project(":feature-transaction"))
    implementation(project(":shared:feature-transaction"))

    //Feature Gold Price
    implementation(project(":shared:feature-gold-price"))

    //Buy Gold V2 SDK
    implementation(project(":feature-buy-gold-v2"))
    implementation(project(":shared:feature-buy-gold-v2"))

    //Shared Feature Gold Lease
    api(project(":shared:feature-gold-lease"))

    //Feature User API
    implementation(project(":shared:feature-user-api"))

    //Feature Lending Kyc
    implementation(project(":feature-lending-kyc"))
    implementation (project(":shared:feature-lending-kyc"))

    //Feature Kyc
    implementation(project(":feature-kyc"))

    //Feature Payment
    implementation(project(":feature-payment"))

    //Feature Transaction Common
    implementation(project(":feature-transaction-common"))
    implementation(project(":shared:feature-transaction-common"))

    //Feature Web PDF Viewer
    implementation(project(":core-web-pdf-viewer"))

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

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //Paging
    implementation("androidx.paging:paging-runtime-ktx:${Dependencies.Android.paging_version}")

    //Flexbox
    implementation("com.google.android.flexbox:flexbox:${Dependencies.Android.flex_box_version}")
}