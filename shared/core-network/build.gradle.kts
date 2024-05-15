plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-kapt")
    id("com.codingfeline.buildkonfig")
    id("com.google.firebase.firebase-perf") version Dependencies.Common.firebase_pref apply false
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
}

kotlin {
    android {
        publishAllLibraryVariants()
    }

    ios()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {

                //Preference Module
                implementation(project(":shared:core-preferences"))

                //Config Module
                implementation(project(":shared:core-remote-config"))

                //Base Module
                implementation(project(":shared:core-base"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                api("com.jar.internal.library.core-network:shared:${Dependencies.Common.core_network_client_version}")

                api("io.ktor:ktor-client-core:${Dependencies.Common.ktor_version}")
                api("io.ktor:ktor-client-auth:${Dependencies.Common.ktor_version}")
                api("io.ktor:ktor-serialization-kotlinx-json:${Dependencies.Common.ktor_version}")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.Common.kotlin_serialization}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                //Material Component
                implementation("com.google.android.material:material:${Dependencies.Android.material_version}")

                //Firebase BOM
                implementation(platform("com.google.firebase:firebase-bom:${Dependencies.Android.firebase_version}"))
                implementation("com.google.firebase:firebase-perf-ktx")

                //Dagger DI
                implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
                configurations.getByName("kapt").dependencies.add(
                    org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency(
                        "com.google.dagger",
                        "hilt-compiler",
                        Dependencies.Android.hilt_version
                    )
                )

            }
        }
        val androidUnitTest by getting
        val iosMain by getting
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}

val modulePackageName = "com.jar.app.core_network"

buildkonfig {
    packageName = modulePackageName
    exposeObjectWithName = "CoreNetworkBuildKonfig"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL_KTOR",
            "prod.myjar.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL_SMS_PARSER",
            "production"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT,
            "VERSION_CODE",
            rootProject.extra.get("versionCode")?.toString()
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_STORY_URL_KTOR",
            "edge.myjar.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ENV",
            "prod"
        )
    }

    defaultConfigs("staging") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL_KTOR",
            "dev.myjar.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL_SMS_PARSER",
            "staging"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_STORY_URL_KTOR",
            "dev-edge.myjar.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ENV",
            "staging"
        )
    }
    defaultConfigs("prodReplica") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL_KTOR",
            "prod-replica.myjar.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL_SMS_PARSER",
            "prod-replica"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_STORY_URL_KTOR",
            "uat-edge.myjar.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ENV",
            "prodReplica"
        )
    }
    defaultConfigs("prod") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL_KTOR",
            "prod.myjar.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL_SMS_PARSER",
            "production"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_STORY_URL_KTOR",
            "edge.myjar.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "ENV",
            "prod"
        )
    }
}

android {
    namespace = modulePackageName
    compileSdk = rootProject.extra.get("compileSdk") as Int
    defaultConfig {
        minSdk = rootProject.extra.get("minSdk") as Int
        targetSdk = rootProject.extra.get("targetSdk") as Int
    }

    compileOptions {
        sourceCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
        targetCompatibility = rootProject.extra.get("javaVersion") as JavaVersion
    }
}