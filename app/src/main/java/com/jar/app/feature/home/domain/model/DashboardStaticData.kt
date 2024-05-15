package com.jar.app.feature.home.domain.model

import com.jar.app.core_base.domain.model.FaqList
import com.jar.app.feature_homepage.shared.domain.model.hamburger.HamburgerItems
import com.jar.app.feature_user_api.domain.model.SuggestedAmountOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class DashboardStaticData(

    @SerialName("param")
    val param: Int = 0,

    @SerialName("showReferralCard")
    val showReferralCard: Boolean = false,

    @SerialName("buyGoldOptions")
    val buyGoldOptions: SuggestedAmountOptions? = null,

    @SerialName("giftGoldOptions")
    val giftGoldOptions: SuggestedAmountOptions? = null,

    @SerialName("generalFaqs")
    val faqList: FaqList? = null,

    @SerialName("goldFAQs")
    val goldFaq: FaqList? = null,

    @SerialName("privacyPolicy")
    val privacyPolicy: String? = null,

    @SerialName("tnC")
    val tnC: String? = null,

    @SerialName("pauseSavingOptions")
    val pauseSavingOptions: List<String>? = null,

    @SerialName("withDrawReasons")
    val withdrawReasons: List<String>? = null,

    @SerialName("avatarInfo")
    val avatarInfo: com.jar.app.feature_onboarding.shared.domain.model.AvatarInfoResponse? = null,

    @SerialName("smsFAQs")
    val smsFAQs: FaqList? = null,

    @SerialName("hamburgerItems")
    val hamburgerItems: HamburgerItems? = null,

    @SerialName("isPromoCodeAvailable")
    val isPromoCodeAvailable: Boolean? = null,

    @SerialName("activeAnalyticsProvider")
    val activeAnalyticsProvider: List<String>? = null,

    @SerialName("onboardingData")
    val onboardingData: OnboardingData? = null,

    @SerialName("deepLink")
    val deepLink: String? = null,

    @SerialName("campaign")
    val campaign: String? = null,

    @SerialName("version")
    val version: String? = null,

    @SerialName("infographic")
    val infographic: Infographics? = null,
)
@Serializable
data class Infographics(
    @SerialName("type")
    val type: String? = null,

    @SerialName("url")
    val url: String? = null,

    @SerialName("thumbnail")
    val thumbnail: String? = null,
)