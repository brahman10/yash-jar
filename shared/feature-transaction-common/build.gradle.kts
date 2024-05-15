plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
    id("dev.icerock.mobile.multiplatform-resources")
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

                //Shared Core Base Module
                implementation(project(":shared:core-base"))

                //Shared Core Network Module
                implementation(project(":shared:core-network"))

                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {

        }
        val androidUnitTest by getting
        val iosMain by getting {
            dependencies {
                //Moko Resources
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}

val modulePackageName = "com.jar.app.feature_transactions_common.shared"

multiplatformResources {
    multiplatformResourcesPackage = modulePackageName
    multiplatformResourcesClassName = "CommonTransactionMR"
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