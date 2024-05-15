plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
    id("kotlin-kapt")
    id("com.codingfeline.buildkonfig")
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

                //Network Module
                implementation(project(":shared:core-network"))

                //Shared Base Module
                implementation(project(":shared:core-base"))

                //Shared Core Remote Config Module
                implementation(project(":shared:core-remote-config"))

                // Shared Coupon API
                implementation(project(":shared:feature-coupon-api"))

                implementation(project(":shared:feature-exit-survey"))

                //Shared Core Analytics Module
                implementation(project(":shared:core-analytics"))

                //Feature Gold Price
                implementation(project(":shared:feature-gold-price"))

                //Kotlin-Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")

                // Moko Parcelize
                implementation("dev.icerock.moko:parcelize:${Dependencies.Common.moko_parcelize}")

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")

                //Common Flow
                api("com.jar.internal.library.core-kmm-flow:shared:${Dependencies.Common.core_kmm_flow}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
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

val modulePackageName = "com.jar.app.feature_mandate_payments_common.shared"

multiplatformResources {
    multiplatformResourcesPackage = modulePackageName
}

buildkonfig {
    packageName = modulePackageName
    exposeObjectWithName = "MandatePaymentBuildKonfig"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_PACKAGE",
            "net.one97.paytm"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PHONEPE_PACKAGE",
            "com.phonepe.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "GPAY_PACKAGE",
            "com.google.android.apps.nbu.paisa.user"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_MID",
            "CHANGE32137276398543"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_CALLBACK_URL",
            "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_PAYMENT_URL",
            "https://securegw.paytm.in/theia/api/v1/showPaymentPage"
        )
    }

    defaultConfigs("staging") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_PACKAGE",
            "net.one97.paytm"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PHONEPE_PACKAGE",
            "com.phonepe.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "GPAY_PACKAGE",
            "com.google.android.apps.nbu.paisa.user"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_MID",
            "CHANGE11588127832171"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_CALLBACK_URL",
            "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_PAYMENT_URL",
            "https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage"
        )
    }
    defaultConfigs("prodReplica") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_PACKAGE",
            "net.one97.paytm"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PHONEPE_PACKAGE",
            "com.phonepe.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "GPAY_PACKAGE",
            "com.google.android.apps.nbu.paisa.user"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_MID",
            "CHANGE32137276398543"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_CALLBACK_URL",
            "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_PAYMENT_URL",
            "https://securegw.paytm.in/theia/api/v1/showPaymentPage"
        )
    }
    defaultConfigs("prod") {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_PACKAGE",
            "net.one97.paytm"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PHONEPE_PACKAGE",
            "com.phonepe.app"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "GPAY_PACKAGE",
            "com.google.android.apps.nbu.paisa.user"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_MID",
            "CHANGE32137276398543"
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_CALLBACK_URL",
            "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "PAYTM_PAYMENT_URL",
            "https://securegw.paytm.in/theia/api/v1/showPaymentPage"
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