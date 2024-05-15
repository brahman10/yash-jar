// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
}

buildscript {

    extra.apply {
        set("versionName", "6.6.7")
        set("versionCode", 457)

        set("minSdk", 21)
        set("targetSdk", 33)
        set("compileSdk", 33)
        set("javaVersion", JavaVersion.VERSION_1_8)
        set("jvmTarget", "1.8")

        set(
            "appDistributionReleaseNotes",
            "Handling UPI States changes"
        )
    }

    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.juspay.in/jp-build-packages/hypersdk-asset-download/releases/")
        maven("https://maven.juspay.in/jp-build-packages/hyper-sdk/")
        maven("https://plotline-android-sdk.s3.amazonaws.com")
        maven( "https://packages.bureau.id/api/packages/Bureau/maven")
        maven("https://storage.googleapis.com/r8-releases/raw")
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Dependencies.Common.gradleVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Common.kotlinVersion}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Dependencies.Android.nav_version}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Dependencies.Android.hilt_version}")
        classpath("com.google.gms:google-services:${Dependencies.Android.googleServices}")
        classpath("com.google.firebase:firebase-appdistribution-gradle:${Dependencies.Android.firebaseAppDistributionVersion}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Dependencies.Android.crashlyticsVersion}")
        classpath("in.juspay:hypersdk-asset-plugin:${Dependencies.Android.hyperSdkVersion}")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:${Dependencies.Common.build_config}")
        classpath("dev.icerock.moko:resources-generator:${Dependencies.Common.moko_resource}")
        classpath("com.squareup.sqldelight:gradle-plugin:${Dependencies.Common.sqldelight_version}")
        classpath("com.android.tools:r8:${Dependencies.Android.r8_version}")
    }
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}