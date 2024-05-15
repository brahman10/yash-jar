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
        jvmTarget = "1.8"
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

    //Core Analytics
    implementation(project(":shared:core-analytics"))

    //Core remote config
    implementation(project(":shared:core-remote-config"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //Core UI Module
    implementation(project(":core-compose-ui"))

    implementation(project(":shared:feature-goal-based-saving"))
    implementation(project(":shared:feature-savings-common"))
    implementation(project(":feature-mandate-payment"))
    implementation(project(":core-web-pdf-viewer"))
    implementation(project(":feature-daily-investment"))
    implementation(project(":shared:feature-daily-investment"))
    implementation(project(":feature-mandate-payment-common"))
    implementation(project(":feature-mandate-payment-common"))

    //Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:${Dependencies.Android.compose_bom_version}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    //Material Design
    implementation("androidx.compose.material:material")
    implementation ("androidx.compose.material3:material3")
    implementation( "androidx.compose.material:material-icons-extended")


    //Buy Gold V2 SDK
    implementation(project(":feature-buy-gold-v2"))
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("androidx.hilt:hilt-common:1.0.0")

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
    implementation ("com.airbnb.android:lottie-compose:${Dependencies.Android.compose_lottie}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")


    //LiveData + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Dependencies.Android.lifecycle_version}")


    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")
    implementation("com.github.bumptech.glide:compose:${Dependencies.Android.compose_glide_version}")


    implementation("androidx.hilt:hilt-navigation-fragment:${Dependencies.Android.hilt_navigation}")

    //Epoxy
    implementation("com.airbnb.android:epoxy:${Dependencies.Android.epoxy_version}")
    kapt("com.airbnb.android:epoxy-processor:${Dependencies.Android.epoxy_version}")

    //Material Design
    implementation("androidx.compose.material:material")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")

    // Compose UI
    implementation("androidx.compose.ui:ui")

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

    implementation("com.robinhood.ticker:ticker:2.0.4")
}