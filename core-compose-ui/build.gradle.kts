plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    namespace = "com.jar.app.core_compose_ui"
}
dependencies {

    //Base Module
    implementation(project(":base"))

    // Core Base
    implementation(project(":shared:core-base"))

    //Utils Module
    implementation(project(":core-utils"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Remote Config SDK
    implementation(project(":shared:core-remote-config"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //Core Ui
    implementation(project(":core-ui"))

    //AppCompat + KTX
    implementation("androidx.core:core-ktx:${Dependencies.Android.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:${Dependencies.Android.app_compat_version}")

    implementation("com.google.android.material:material:1.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")

    //Jetpack Compose
    val composeBom =
        platform("androidx.compose:compose-bom:${Dependencies.Android.compose_bom_version}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    //Material Design
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3:${Dependencies.Android.material_design}")
    implementation("androidx.compose.material:material-icons-extended")

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

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Showkase Library Component
    implementation("com.airbnb.android:showkase:${Dependencies.Android.compose_showkase}")

    // glide image loader
    implementation("com.github.bumptech.glide:compose:${Dependencies.Android.compose_glide_version}")

    kapt("com.airbnb.android:showkase-processor:${Dependencies.Android.compose_showkase}")

    api("com.airbnb.android:lottie-compose:${Dependencies.Android.compose_lottie}")
}

configurations {
    all {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
}