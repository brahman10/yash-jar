plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
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
    namespace = "com.jar.app.feature_calculator"
}


dependencies {

    //Shared Feature 
    implementation(project(":shared:feature-calculator"))

    //Base Module
    implementation(project(":base"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Network Module
    implementation(project(":shared:core-network"))

    //Preferences
    implementation(project(":shared:core-preferences"))

    //Shared Base Module
    implementation (project(":shared:core-base"))

    //Core UI Module
    implementation(project(":core-ui"))

    //Core Utils
    implementation(project(":core-utils"))

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //Lending
    implementation (project(":feature-lending"))
    implementation (project(":shared:feature-lending"))

    //Core Compose UI
    implementation(project(":core-compose-ui"))

    //Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:${Dependencies.Android.compose_bom_version}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.material:material")
    implementation ("androidx.compose.material3:material3")

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

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Glide Image for compose
    implementation("com.github.bumptech.glide:compose:${Dependencies.Android.compose_glide_version}")

    //lottie animation for compose
    implementation ("com.airbnb.android:lottie-compose:${Dependencies.Android.compose_lottie}")
}