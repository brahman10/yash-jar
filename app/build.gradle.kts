import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.ReadOnlyProductFlavor
import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.appdistribution")
    id("com.google.firebase.crashlytics")
    id("hypersdk-asset-plugin")
    id("com.google.firebase.firebase-perf") version Dependencies.Common.firebase_pref apply false
    kotlin("plugin.serialization") version Dependencies.Common.kotlinVersion
}


android {
    compileSdk = rootProject.extra.get("compileSdk") as Int

    signingConfigs {
        register("release") {
            storeFile = file("../cert")
            storePassword = "ChangeJar@Save$"
            keyAlias = "changejar"
            keyPassword = "ChangeJar"
        }
        named("debug") {
            storeFile = file("../debugcert")
            storePassword = "ChangeJar@Save$"
            keyAlias = "changejardebug"
            keyPassword = "ChangeJar"
        }
        register("mock") {
            storeFile = file("../mockcert")
            storePassword = "ChangeJar@Mock"
            keyAlias = "changejarmock"
            keyPassword = "ChangeJar@Mock"
        }
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    defaultConfig {
        applicationId = "com.jar.app"

        minSdk = rootProject.extra.get("minSdk") as Int
        targetSdk = rootProject.extra.get("targetSdk") as Int

        versionCode = rootProject.extra.get("versionCode") as Int
        versionName = rootProject.extra.get("versionName") as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "UX_CAM_KEY", "\"cshbbuxtt8aip09\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("Boolean", "IS_PLAY_STORE", "true")
            buildConfigField("Boolean", "MOCK", "false")
            lintOptions {
                disable("MissingTranslation")
                disable("ExtraTranslation")
            }
            isTestCoverageEnabled = false
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("Boolean", "IS_PLAY_STORE", "false")
            buildConfigField("Boolean", "MOCK", "false")
            buildConfigField("Boolean", "DEBUG", "true")
            isDebuggable = true
            isTestCoverageEnabled = true
        }
        create("mock") {
            signingConfig = signingConfigs.getByName("mock")
            buildConfigField("Boolean", "IS_PLAY_STORE", "false")
            buildConfigField("Boolean", "MOCK", "true")
            buildConfigField("Boolean", "DEBUG", "true")
            isDebuggable = true
            isTestCoverageEnabled = false
        }
    }

    flavorDimensions.add("config")
    productFlavors {
        create("prod") {
            dimension = "config"
            applicationId = "com.jar.app"
            buildConfigField("String", "PAYTM_MID", "\"CHANGE32137276398543\"")
            buildConfigField(
                "String",
                "PAYTM_CALLBACK_URL",
                "\"https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=\""
            )
            buildConfigField(
                "String",
                "PAYTM_PAYMENT_URL",
                "\"https://securegw.paytm.in/theia/api/v1/showPaymentPage\""
            )
            buildConfigField(
                "String",
                "BASE_URL_SMS_PARSER",
                "\"https://webhook.myjar.app/production/\""
            )
            buildConfigField(
                "String",
                "BUREAU_AUTH_CLIENT_ID",
                "\"4e44dbc4-a7b7-4979-abac-64ea9db511e2\""
            )
            buildConfigField(
                "String",
                "BUREAU_CALLBACK_URL",
                "\"https://prod.myjar.app/v1/callback/otl/status\""
            )
            buildConfigField("String", "PHONEPE_PACKAGE", "\"com.phonepe.app\"")
            buildConfigField("String", "AUSPICIOUS_TIME_TOPIC", "\"auspiciousTimeAlertsTopic\"")
            resValue("string", "CLEVERTAP_ACCOUNT_ID", "W84-655-7R6Z")
            resValue("string", "CLEVERTAP_ACCOUNT_TOKEN", "556-4b0")
            resValue("string", "CLEVERTAP_XIAOMI_APP_KEY", "5551992779429")
            resValue("string", "CLEVERTAP_XIAOMI_APP_ID", "2882303761519927429")
            val trueCallerKey by extra {
                mapOf(
                    "debug" to "tWvZT89efd251ca844b68a1557b28fbaf9594",
                    "mock" to "tWvZT89efd251ca844b68a1557b28fbaf9594",
                    "release" to "ZD4wr666c4798b2584a169c8350ce861c9a39"
                )
            }
            manifestPlaceholders["templateId"] = "/6H9Q"
            manifestPlaceholders["oneLinkUrl1"] = "jar.onelink.me"
            manifestPlaceholders["oneLinkUrl2"] = "click.myjar.app"
            manifestPlaceholders["oneLinkUrl3"] = "start.myjar.app"

            firebaseAppDistribution {
                releaseNotes = rootProject.extra.get("appDistributionReleaseNotes") as String
                serviceCredentialsFile = "$rootDir/changejarprod_app_distribution.json"
                groups = "teamjar"
            }
        }
        create("prodReplica") {
            dimension = "config"
            applicationId = "com.jar.app.replica"
            buildConfigField("String", "PAYTM_MID", "\"CHANGE32137276398543\"")
            buildConfigField(
                "String",
                "PAYTM_CALLBACK_URL",
                "\"https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=\""
            )
            buildConfigField(
                "String",
                "PAYTM_PAYMENT_URL",
                "\"https://securegw.paytm.in/theia/api/v1/showPaymentPage\""
            )
            buildConfigField(
                "String",
                "BASE_URL_SMS_PARSER",
                "\"https://webhook.myjar.app/prod-replica/\""
            )
            buildConfigField(
                "String",
                "BUREAU_AUTH_CLIENT_ID",
                "\"4e44dbc4-a7b7-4979-abac-64ea9db511e2\""
            )
            buildConfigField(
                "String",
                "BUREAU_CALLBACK_URL",
                "\"https://prod-replica.myjar.app/v1/callback/otl/status\""
            )
            buildConfigField("String", "PHONEPE_PACKAGE", "\"com.phonepe.app.preprod\"")
            buildConfigField("String", "AUSPICIOUS_TIME_TOPIC", "\"auspiciousTimeAlertsReplica\"")
            resValue("string", "CLEVERTAP_ACCOUNT_ID", "W84-655-7R6Z")
            resValue("string", "CLEVERTAP_ACCOUNT_TOKEN", "556-4b0")
            resValue("string", "CLEVERTAP_XIAOMI_APP_KEY", "5551992779429")
            resValue("string", "CLEVERTAP_XIAOMI_APP_ID", "2882303761519927429")
            val trueCallerKey by extra {
                mapOf(
                    "debug" to "QpSVJ64e66ad11abf4eda9754399e82845bf4",
                    "mock" to "r4WZGa844413a431b4c8d92e298c031e52f43",
                    "release" to "jxnzTf3b23103e890411c8252fd2dbeaea1b5"
                )
            }

            manifestPlaceholders["templateId"] = "/6H9Q"
            manifestPlaceholders["oneLinkUrl1"] = "jar.onelink.me"
            manifestPlaceholders["oneLinkUrl2"] = "click.myjar.app"
            manifestPlaceholders["oneLinkUrl3"] = "start.myjar.app"

            firebaseAppDistribution {
                releaseNotes = rootProject.extra.get("appDistributionReleaseNotes") as String
                serviceCredentialsFile = "$rootDir/changejarprod_app_distribution.json"
                groups = "teamjar"
            }
        }
        create("staging") {
            dimension = "config"
            applicationId = "com.aso_centric.jar.staging"
            buildConfigField("String", "PAYTM_MID", "\"CHANGE11588127832171\"")
            buildConfigField(
                "String",
                "PAYTM_CALLBACK_URL",
                "\"https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=\""
            )
            buildConfigField(
                "String",
                "PAYTM_PAYMENT_URL",
                "\"https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage\""
            )
            buildConfigField(
                "String",
                "BASE_URL_SMS_PARSER",
                "\"https://webhook.myjar.app/staging/\""
            )
            buildConfigField(
                "String",
                "BUREAU_AUTH_CLIENT_ID",
                "\"4e44dbc4-a7b7-4979-abac-64ea9db511e2\""
            )
            buildConfigField(
                "String",
                "BUREAU_CALLBACK_URL",
                "\"https://dev.myjar.app/v1/callback/otl/status\""
            )
            buildConfigField("String", "PHONEPE_PACKAGE", "\"com.phonepe.app.preprod\"")
            buildConfigField("String", "AUSPICIOUS_TIME_TOPIC", "\"auspiciousTimeAlertsReplica\"")
            resValue("string", "CLEVERTAP_ACCOUNT_ID", "TEST-Z84-655-7R6Z")
            resValue("string", "CLEVERTAP_ACCOUNT_TOKEN", "TEST-556-4b1")
            resValue("string", "CLEVERTAP_XIAOMI_APP_KEY", "5551992779429")
            resValue("string", "CLEVERTAP_XIAOMI_APP_ID", "2882303761519927429")
            val trueCallerKey by extra {
                mapOf(
                    "debug" to "X5HEd6bb15d250c9440e39b123c174b467c84",
                    "mock" to "oj02uf8c0288afe7449d89930f2f3bca433df",
                    "release" to "VmdX6b52cab8a5b93487ba7977b0914df63f4"
                )
            }

            firebaseAppDistribution {
                releaseNotes = rootProject.extra.get("appDistributionReleaseNotes") as String
                serviceCredentialsFile = "$rootDir/changejarstaging_app_distribution.json"
                groups = "teamjar"
            }
            manifestPlaceholders["templateId"] = "/iZH6"
            manifestPlaceholders["oneLinkUrl1"] = "jar-staging.onelink.me"
            manifestPlaceholders["oneLinkUrl2"] = "jar-staging.onelink.me"
            manifestPlaceholders["oneLinkUrl3"] = "jar-staging.onelink.me"
        }
    }


    applicationVariants.all(object : Action<ApplicationVariant> {
        override fun execute(variant: ApplicationVariant) {
            val flavor = variant.productFlavors[0] as ReadOnlyProductFlavor
            val extra = flavor.getProperty("ext") as DefaultExtraPropertiesExtension
            val map = extra.get("trueCallerKey") as Map<*, *>
            val value = map[variant.buildType.name] as String
            variant.resValue("string", "TRUECALLER_APP_KEY", value)
        }

    })


    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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


    sourceSets {
        getByName("mock") {
            assets {
                srcDirs("src/main/assetsDebug")
            }
        }
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }

    packagingOptions {
        resources {
            merges.add("values-**/*")
            merges.add("values/*")
            merges.add("drawable/ic_arrow_back.xml")
        }
    }
    namespace = "com.jar.app"
}

dependencies {

    implementation(project(mapOf("path" to ":feature-exit-survey")))
    val mockImplementation by configurations

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.9")

    implementation(project(":shared"))

    //Sms SDK
    implementation(project(":feature-sms-sync"))

    //Contacts sync module
    implementation(project(":feature-contacts-sync-common"))

    //Base
    implementation(project(":base"))

    //Core UI
    implementation(project(":core-ui"))

    //Shared Core Base
    implementation(project(":shared:core-base"))

    //Core Logger
    implementation(project(":shared:core-logger"))

    //Core Compose UI
    implementation(project(":core-compose-ui"))

    //Network SDK
    implementation(project(":shared:core-network"))

    //Analytics SDK
    implementation(project(":shared:core-analytics"))

    //Remote Config SDK
    implementation (project(":shared:core-remote-config"))

    //Preferences SDK
    implementation (project(":shared:core-preferences"))

    //Shared Base Module
    implementation (project(":shared:core-base"))

    //Shared Base Module
    implementation (project(":shared:feature-in-app-notification"))

    //Utils SDK
    implementation(project(":core-utils"))

    //Core Image Picker
    implementation(project(":core-image-picker"))

    //Core Web Pdf Viewer
    implementation(project(":core-web-pdf-viewer"))

    //Shared Feature-One-Time-Payments
    implementation(project(":shared:feature-one-time-payments"))

    //Shared Feature-One-Time-Payments-Common
    implementation(project(":shared:feature-one-time-payments-common"))

    //Shared Feature-Gifting
    implementation(project(":shared:feature-gifting"))

    //Payments SDK
    implementation(project(":feature-payment"))

    //Refer earn v2
    implementation(project(":feature-refer-earn-v2"))

    //Buy Gold V2 SDK
    implementation(project(":feature-buy-gold-v2"))
    implementation(project(":shared:feature-buy-gold-v2"))

    //Feature Gold Price
    implementation(project(":shared:feature-gold-price"))

    //Feature Daily-Investment
    implementation(project(":feature-daily-investment"))
    implementation(project(":shared:feature-daily-investment"))

    //Feature Transaction Common
    implementation(project(":feature-transaction-common"))

    //Feature User API
    implementation(project(":feature-user-api"))
    implementation(project(":shared:feature-user-api"))

    //Feature Homepage
    implementation(project(":feature-homepage"))

    //Feature Profile
    implementation(project(":feature-profile"))
    implementation(project(":shared:feature-profile"))

    //Feature Settings
    implementation(project(":feature-settings"))
    implementation(project(":shared:feature-settings"))

    //Feature Sell Gold
    implementation(project(":feature-sell-gold"))

    //Feature Gold delivery
    implementation(project(":feature-gold-delivery"))

    //Feature Gold Redemption
    implementation(project(":feature-gold-redemption"))

    //Feature KYC
    implementation(project(":feature-kyc"))

    //Feature Vasooli
    implementation(project(":feature-vasooli"))

    //Feature Transaction
    implementation(project(":feature-transaction"))

    //Feature Gifting
    implementation(project(":feature-gifting"))

    //Feature Duo
    implementation(project(":feature-jar-duo"))
    implementation(project(":feature-goal-based-saving"))

    //Feature Round Off
    implementation(project(":feature-round-off"))
    implementation(project(":shared:feature-round-off"))

    //Feature Mandate Payment
    implementation(project(":feature-mandate-payment"))
    implementation(project(":feature-mandate-payment-common"))

    //Feature Spin
    implementation(project(":feature-spin"))

    //Feature Lending KYC
    implementation(project(":feature-lending-kyc"))
    implementation(project(":shared:feature-lending-kyc"))

    //Feature Gold SIP
    implementation(project(":feature-gold-sip"))
    implementation(project(":shared:feature-gold-sip"))

    //Feature Quests
    implementation(project(":feature-quests"))

    //Feature Weekly Magic Common
    implementation(project(":feature-weekly-magic-common"))
    implementation(project(":shared:feature-weekly-magic-common"))

    //Feature Weekly Magic
    implementation(project(":feature-weekly-magic"))

    //Feature Lending
    implementation(project(":feature-lending"))
    implementation(project(":shared:feature-lending"))

    //Feature Lending KYC
    implementation(project(":feature-lending-kyc"))

    //Feature Gold Price Alerts
    implementation(project(":feature-gold-price-alerts"))

    //Feature Gold Lease
    implementation(project(":feature-gold-lease"))

    //Feature Post Setup
    implementation(project(":feature-post-setup"))
    implementation(project(":shared:feature-post-setup"))

    //Feature Daily Investment Cancellation
    implementation(project(":feature-daily-investment-cancellation"))

    //Shared Feature Home Page Module
    implementation(project(":shared:feature-homepage"))

    //Feature Daily Investment Tempering

    //Shared Feature Onboarding
    implementation(project(":shared:feature-onboarding"))

    //Shared Feature Refer N Earn V2
    implementation(project(":shared:feature-refer-earn-v2"))

    //Feature spends tracker
    implementation(project(":feature-spends-tracker"))

    //Health Insurance
    implementation(project(":feature-health-insurance"))

    //in app stories
    implementation(project(":feature-in-app-stories"))

    //Promo Code
    implementation(project(":feature-promo-code"))
    implementation(project(":feature-graph-manual-buy"))

    //In App Story
    implementation(project(":shared:feature-story"))
    //Feature Calculator
    implementation(project(":feature-calculator"))
    implementation(project(":shared:feature-calculator"))

    //FlexBox
    implementation("com.google.android.flexbox:flexbox:${Dependencies.Android.flex_box_version}")

    //Testing
    testImplementation("junit:junit:${Dependencies.Android.junit_version}")
    androidTestImplementation("androidx.test.ext:junit:${Dependencies.Android.android_junit_version}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Dependencies.Android.esperesso_version}")
    androidTestImplementation("androidx.work:work-testing:${Dependencies.Android.work_version}")
    androidTestImplementation("androidx.arch.core:core-testing:${Dependencies.Android.android_arch_core_testing}")
    // For instrumented tests.
    androidTestImplementation("com.google.dagger:hilt-android-testing:${Dependencies.Android.hilt_version}")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${Dependencies.Android.hilt_version}")


    //AppCompat + KTX
    implementation("androidx.core:core-ktx:${Dependencies.Android.core_ktx_version}")
    implementation("androidx.appcompat:appcompat:${Dependencies.Android.app_compat_version}")
    implementation("androidx.legacy:legacy-support-v4:${Dependencies.Android.androidx_legacy_support}")

    //Material Component
    implementation("com.google.android.material:material:${Dependencies.Android.material_version}")

    //Constraint Layout
    implementation("androidx.constraintlayout:constraintlayout:${Dependencies.Android.constraint_layout_version}")

    //LiveData + ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Dependencies.Android.lifecycle_version}")
    implementation("androidx.lifecycle:lifecycle-process:${Dependencies.Android.lifecycle_version}")

    //RecyclerView
    implementation("androidx.recyclerview:recyclerview:${Dependencies.Android.recycler_view_version}")

    //Adapter Delegate
    implementation("com.hannesdorfmann:adapterdelegates4:${Dependencies.Android.adapter_delegate_version}")

    //Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:${Dependencies.Android.nav_version}")
    implementation("androidx.navigation:navigation-ui-ktx:${Dependencies.Android.nav_version}")

    //Fragment KTX
    implementation("androidx.fragment:fragment-ktx:${Dependencies.Android.fragment_ktx_version}")

    //Logger
    implementation("com.jakewharton.timber:timber:${Dependencies.Android.timber_version}")

    //Kotlin-Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Common.coroutine_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Common.coroutine_version}")

    //Glide
    implementation("com.github.bumptech.glide:glide:${Dependencies.Android.glide_version}")
    kapt("com.github.bumptech.glide:compiler:${Dependencies.Android.glide_version}")

    //TrueCaller SDK
    implementation("com.truecaller.android.sdk:truecaller-sdk:${Dependencies.Android.truecaller_version}")

    //Dagger DI
    implementation("com.google.dagger:hilt-android:${Dependencies.Android.hilt_version}")
    kapt("com.google.dagger:hilt-compiler:${Dependencies.Android.hilt_version}")
    implementation("androidx.hilt:hilt-navigation-fragment:${Dependencies.Android.hilt_navigation}")

    // LeakCanary
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:${Dependencies.Android.leak_canary_version}")
//    mockImplementation("com.squareup.leakcanary:leakcanary-android:${Dependencies.Android.leak_canary_version}")

    //Country Code Picker
    implementation("com.github.joielechong:countrycodepicker:${Dependencies.Android.country_code_picker_version}")

    //Lottie
    implementation("com.airbnb.android:lottie:${Dependencies.Android.lottie_version}")

    //Preferences
    implementation("androidx.preference:preference-ktx:${Dependencies.Android.preferences_version}")

    //Swipe To Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:${Dependencies.Android.swipe_refresh_version}")

    //Dynamic Permission
    implementation("com.github.permissions-dispatcher:permissionsdispatcher:${Dependencies.Android.permission_version}")
    kapt("com.github.permissions-dispatcher:permissionsdispatcher-processor:${Dependencies.Android.permission_version}")

    //Progress HUD
    implementation("com.kaopiz:kprogresshud:${Dependencies.Android.progress_hud_version}")

    //Bounce Effect
    implementation("io.github.everythingme:overscroll-decor-android:${Dependencies.Android.overscroll_version}")

    //Expandable View
    implementation("com.github.cachapa:ExpandableLayout:${Dependencies.Android.expandable_view_version}")

    //EventBus
    implementation("org.greenrobot:eventbus:${Dependencies.Android.eventbus_version}")

    //Paytm SDK
    implementation("com.paytm.appinvokesdk:appinvokesdk:${Dependencies.Android.paytm_sdk_version}")

    //CleverTap SDK
    implementation("com.clevertap.android:clevertap-android-sdk:${Dependencies.Android.clevertap_version}")
    implementation("com.clevertap.android:push-templates:1.0.3")

    //CleverTap XIAOMI
    implementation ("com.clevertap.android:clevertap-xiaomi-sdk:1.5.3")
    implementation(files("libs/MiPush_SDK_Client_5_1_5-G_3rd.aar"))
//    //https://docs.clevertap.com/docs/rendermax
//    implementation("com.clevertap.android:clevertap-rendermax-sdk:1.0.1")

    //AppsFlyer SDK
    implementation("com.appsflyer:af-android-sdk:${Dependencies.Android.appsflyer_version}")

    //AppsFlyer LVL SDK
    implementation("com.appsflyer:lvl:${Dependencies.Android.appsflyer_version}")

    // Facebook Analytics SDK
    implementation("com.facebook.android:facebook-android-sdk:${Dependencies.Android.facebook_sdk_version}")
    //Facebook AppLinks
    implementation("com.facebook.android:facebook-applinks:${Dependencies.Android.facebook_sdk_version}")

    //Paging
    implementation("androidx.paging:paging-runtime-ktx:${Dependencies.Android.paging_version}")

    //Three Ten ABP - Date Parsing
    implementation("org.threeten:threetenbp:${Dependencies.Android.threetenbp_version}")

    //Custom Chrome Tabs
    implementation("androidx.browser:browser:${Dependencies.Android.androidx_browser}")

    //Graphs
    implementation("com.github.PhilJay:MPAndroidChart:${Dependencies.Android.android_mp_chart}")

    //InstallReferrer
    implementation("com.android.installreferrer:installreferrer:${Dependencies.Android.androidx_install_referrer}")

    //Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:${Dependencies.Android.firebase_version}"))
    implementation("com.google.firebase:firebase-perf-ktx")

    //Firebase Core
    implementation("com.google.firebase:firebase-installations-ktx")

    //Remote Config
    implementation("com.google.firebase:firebase-config-ktx")

    //Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")

    //firebase.iid now this one should be used.
    implementation("com.google.firebase:firebase-installations-ktx")

    //Firebase Messaging
    implementation("com.google.firebase:firebase-messaging-ktx")

    //Firebase Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Play Services Ads
    implementation("com.google.android.gms:play-services-ads-lite:${Dependencies.Android.play_services_ads}")

    //Auto Read
    implementation("com.google.android.gms:play-services-auth:${Dependencies.Android.play_services_auth}")
    implementation("com.google.android.gms:play-services-auth-api-phone:${Dependencies.Android.play_services_auth_api_phone}")

    //Phone Number Formatting
    implementation("io.michaelrocks:libphonenumber-android:${Dependencies.Android.phone_number_utils_version}")

    //OTP View
    implementation("com.github.mukeshsolanki:android-otpview-pinview:${Dependencies.Android.otp_pin_view}")

    //Room SQLite ORM
    implementation("androidx.room:room-runtime:${Dependencies.Android.room_version}")
    kapt("androidx.room:room-compiler:${Dependencies.Android.room_version}")
    implementation("androidx.room:room-ktx:${Dependencies.Android.room_version}")

    //Work Manager
    implementation("androidx.work:work-runtime-ktx:${Dependencies.Android.work_version}")
    implementation("androidx.hilt:hilt-work:${Dependencies.Android.hilt_worker}")
    kapt("androidx.hilt:hilt-compiler:${Dependencies.Android.hilt_worker}")

    //Play In app review
    implementation ("com.google.android.play:app-update-ktx:${Dependencies.Android.play_app_update}")

    //Circular Progress Bar
    implementation("com.mikhaellopez:circularprogressbar:${Dependencies.Android.circular_progress_bar_version}")

    //Mockk
    testImplementation("io.mockk:mockk:${Dependencies.Android.mockk_version}")
    androidTestImplementation("io.mockk:mockk-android:${Dependencies.Android.mockk_version}")

    //Truth assertion
    testImplementation("com.google.truth:truth:${Dependencies.Android.truth_version}")

    // Coroutine support in Play services [Tasks]
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Dependencies.Android.kotlinx_coroutines_play_services}")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:${Dependencies.Android.shimmer_version}")

    //PlotLine
    implementation("com.gitlab.plotline:plotline-android-sdk:${Dependencies.Android.plotline_version}")

    //Rating Bar
    implementation("com.github.ome450901:SimpleRatingBar:1.5.1")

    //Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:${Dependencies.Android.compose_bom_version}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    //Material Design
    implementation("androidx.compose.material:material")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-tooling")

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

    // User Experior Analytics SDK
    implementation("com.userexperior:userexperior-android:${Dependencies.Android.userexperior_version}")

    // library for the ripple effect
    implementation("com.skyfishjy.ripplebackground:library:${Dependencies.Android.ripple_effect_library}")

    //Bureau SDK
    implementation("id.bureau:corelib:${Dependencies.Android.bureau_sdk_version}")
}