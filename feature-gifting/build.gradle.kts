plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
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
    namespace = "com.jar.app.feature_gifting"
}

dependencies {

    //Base Module
    implementation(project(":base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Shared Core Base
    implementation(project(":shared:core-base"))

    //Network Module
    implementation(project(":shared:core-network"))

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

    //Utils SDK
    implementation(project(":shared:feature-gifting"))

    //Feature Payment
    implementation(project(":feature-payment"))

    //Buy Gold V2 SDK
    implementation(project(":feature-buy-gold-v2"))
    implementation(project(":shared:feature-buy-gold-v2"))

    //Shared Feature-Gold-Price
    implementation(project(":shared:feature-gold-price"))

    //Share Feature-User-API
    implementation(project(":shared:feature-user-api"))

    //Shared Feature-One-Time-Payment
    implementation(project(":shared:feature-one-time-payments"))

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

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
    
    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Lottie
    implementation("com.airbnb.android:lottie:${Dependencies.Android.lottie_version}")

    //Country Code Picker
    implementation("com.github.joielechong:countrycodepicker:${Dependencies.Android.country_code_picker_version}")

    //Epoxy
    implementation("com.airbnb.android:epoxy:${Dependencies.Android.epoxy_version}")
    kapt("com.airbnb.android:epoxy-processor:${Dependencies.Android.epoxy_version}")

    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")

    //Dynamic Permission
    implementation("com.github.permissions-dispatcher:permissionsdispatcher:${Dependencies.Android.permission_version}")
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:${Dependencies.Android.permission_version}")

}