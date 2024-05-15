plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("dagger.hilt.android.plugin")
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
        create("prod") {}

        create("prodReplica") {}

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
    namespace = "com.jar.app.core_ui"
}

dependencies {

    //Base Module
    implementation(project(":base"))

    //Network Module
    implementation(project(":shared:core-network"))

    //Utils Module
    implementation(project(":core-utils"))

    //Shared Base Module
    implementation (project(":shared:core-base"))

    //Preferences SDK
    implementation(project(":shared:core-preferences"))

    //User API
    implementation(project(":shared:feature-user-api"))

    //Preferences SDK
    implementation(project(":shared:feature-one-time-payments-common"))

    //Moko Resources
    implementation("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

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

    //Constraint Layout
    implementation("androidx.constraintlayout:constraintlayout:${Dependencies.Android.constraint_layout_version}")

    //Material Component
    implementation("com.google.android.material:material:${Dependencies.Android.material_version}")

    //Bounce Effect
    implementation("io.github.everythingme:overscroll-decor-android:${Dependencies.Android.overscroll_version}")

    //Paging
    implementation("androidx.paging:paging-runtime-ktx:${Dependencies.Android.paging_version}")

    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")

    //Lottie
    implementation("com.airbnb.android:lottie:${Dependencies.Android.lottie_version}")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")

    //Flip View
    implementation("com.wajahatkarim:EasyFlipView:${Dependencies.Android.flip_view_version}")

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Epoxy
    implementation("com.airbnb.android:epoxy:${Dependencies.Android.epoxy_version}")
    kapt("com.airbnb.android:epoxy-processor:${Dependencies.Android.epoxy_version}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //ExoPlayer
    implementation("com.google.android.exoplayer:exoplayer-core:${Dependencies.Android.exo_player_version}")
    implementation("com.google.android.exoplayer:exoplayer-ui:${Dependencies.Android.exo_player_version}")

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.Common.kotlin_serialization}")

    //Adapter Delegate
    implementation("com.hannesdorfmann:adapterdelegates4:${Dependencies.Android.adapter_delegate_version}")
}

configurations {
    all {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
}
