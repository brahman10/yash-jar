plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
    id("co.touchlab.faktory.kmmbridge") version Dependencies.Common.kmm_bridge
    `maven-publish`
    kotlin("native.cocoapods")
}

version = "0.1"

kotlin {
    android {
        publishAllLibraryVariants()
    }

    ios()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":shared:core-logger"))
                api(project(":shared:core-analytics"))
                api(project(":shared:core-base"))
                api(project(":shared:core-network"))
                api(project(":shared:core-preferences"))
                api(project(":shared:core-remote-config"))
                api(project(":shared:feature-gifting"))
                api(project(":shared:feature-gold-price"))
                api(project(":shared:feature-gold-price-alerts"))
                api(project(":shared:feature-gold-sip"))
                api(project(":shared:feature-homepage"))
                api(project(":shared:feature-refer-earn-v2"))
                api(project(":shared:feature-onboarding"))
                api(project(":shared:feature-one-time-payments"))
                api(project(":shared:feature-one-time-payments-common"))
                api(project(":shared:feature-post-setup"))
                api(project(":shared:feature-profile"))
                api(project(":shared:feature-round-off"))
                api(project(":shared:feature-settings"))
                api(project(":shared:feature-user-api"))
                api(project(":shared:feature-weekly-magic"))
                api(project(":shared:feature-weekly-magic-common"))
                api(project(":shared:feature-in-app-notification"))
                api(project(":shared:feature-transaction"))
                api(project(":shared:feature-transaction-common"))
                api(project(":shared:feature-sell-gold"))
                api(project(":shared:feature-sell-gold-common"))
                api(project(":shared:feature-buy-gold-v2"))
                api(project(":shared:feature-gold-delivery"))
                api(project(":shared:feature-lending"))
                api(project(":shared:feature-lending-kyc"))
                api(project(":shared:feature-kyc"))
                api(project(":shared:feature-exit-survey"))
                api(project(":shared:feature-mandate-payments-common"))
                api(project(":shared:feature-jar-duo"))
                api(project(":shared:feature-contact-sync-common"))
                api(project(":shared:feature-daily-investment"))
                api(project(":shared:feature-gold-lease"))
                api(project(":shared:feature-homepage"))
                api(project(":shared:feature-spin"))
                api(project(":shared:feature-coupon-api"))
                api(project(":shared:feature-savings-common"))
                api("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
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

    cocoapods {
        summary = "Jar Shared Binary"
        homepage = "https://www.myjar.app/"
        ios.deploymentTarget = "13.1"
        extraSpecAttributes["libraries"] = "'c++', 'sqlite3'"

        framework {
            export(project(":shared:core-logger"))
            export(project(":shared:core-analytics"))
            export(project(":shared:core-base"))
            export(project(":shared:core-network"))
            export(project(":shared:core-preferences"))
            export(project(":shared:core-remote-config"))
            export(project(":shared:feature-gifting"))
            export(project(":shared:feature-exit-survey"))
            export(project(":shared:feature-gold-price"))
            export(project(":shared:feature-gold-price-alerts"))
            export(project(":shared:feature-gold-sip"))
            export(project(":shared:feature-homepage"))
            export(project(":shared:feature-onboarding"))
            export(project(":shared:feature-refer-earn-v2"))
            export(project(":shared:feature-one-time-payments"))
            export(project(":shared:feature-one-time-payments-common"))
            export(project(":shared:feature-post-setup"))
            export(project(":shared:feature-profile"))
            export(project(":shared:feature-round-off"))
            export(project(":shared:feature-settings"))
            export(project(":shared:feature-user-api"))
            export(project(":shared:feature-weekly-magic"))
            export(project(":shared:feature-weekly-magic-common"))
            export(project(":shared:feature-in-app-notification"))
            export(project(":shared:feature-transaction"))
            export(project(":shared:feature-transaction-common"))
            export(project(":shared:feature-sell-gold"))
            export(project(":shared:feature-sell-gold-common"))
            export(project(":shared:feature-buy-gold-v2"))
            export(project(":shared:feature-gold-delivery"))
            export(project(":shared:feature-lending"))
            export(project(":shared:feature-lending-kyc"))
            export(project(":shared:feature-kyc"))
            export(project(":shared:feature-mandate-payments-common"))
            export(project(":shared:feature-jar-duo"))
            export(project(":shared:feature-contact-sync-common"))
            export(project(":shared::feature-daily-investment"))
            export(project(":shared:feature-gold-lease"))
            export(project(":shared:feature-homepage"))
            export(project(":shared:feature-spin"))
            export(project(":shared:feature-coupon-api"))
            export(project(":shared:feature-savings-common"))
            export("com.jar.internal.library.paging:paging:${Dependencies.Common.core_paging_version}")
            export("dev.icerock.moko:resources:${Dependencies.Common.moko_resource}")
            export("com.jar.internal.library.core-network:shared:${Dependencies.Common.core_network_client_version}")
            isStatic = false
        }
    }

    targets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>()
        .forEach {
            it.binaries.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>()
                .forEach { lib ->
                    lib.isStatic = false
                    lib.linkerOpts.add("-lsqlite3")
                }
        }
}

val modulePackageName = "com.jar.app.shared"

multiplatformResources {
    multiplatformResourcesPackage = modulePackageName
    iosBaseLocalizationRegion = "en"
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

addGithubPackagesRepository()
kmmbridge {
    mavenPublishArtifacts()
    githubReleaseVersions()
    spm()
    cocoapods("git@github.com:Changejarapp/IosPodspec.git")
    versionPrefix.set("0.1")
}
