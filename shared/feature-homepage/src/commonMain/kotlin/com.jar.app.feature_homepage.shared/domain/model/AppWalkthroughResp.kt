package com.jar.app.feature_homepage.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppWalkthroughResp(
    @SerialName("allowSkip")
    val allowSkip: Boolean,
    @SerialName("appWalkthroughSections")
    val appWalkthroughSections: List<AppWalkthroughSection>? = null,
    @SerialName("msg")
    val message: String? = null
)

@Serializable
data class AppWalkthroughSection(
    @SerialName("sequence")
    val sequence: Int,
    @SerialName("cardType")
    private val cardType: String? = null,
    @SerialName("sectionType")
    val sectionType: String,
    @SerialName("text")
    val text: String? = null,
    @SerialName("subText")
    val subText: String,
    @SerialName("footer")
    val footer: String? = null,
    @SerialName("tab")
    private val tab: String? = null,
    @SerialName("featureType")
    private val featureTypeList: List<String>? = null,
) {
    fun getFeatureType() =
        featureTypeList?.map { it.prependIndent(BaseConstants.PLOTLINE_CONSTANT) }

    fun getSectionType() = try { SectionType.valueOf(sectionType) } catch (ex:Exception) {
        SectionType.NONE
    }
    fun getTab() = tab?.let { Tab.valueOf(it) } ?: Tab.HOME
}

enum class SectionType {
    INTRO,
    LOCKER,
    REDESIGN_LOCKER,
    WITHDRAW_BUTTON,
    SETUP_AUTOMATIC_SAVINGS,
    QUICK_ACTIONS,
    QUICK_ACTIONS_SINGLE_CARD,
    REWARDS,
    DOCK,
    OUTRO,
    RECOMMENDED_FOR_YOU,
    NONE
}

enum class Tab(val position: Int) {
    HOME(0), TRANSACTIONS(1), ACCOUNT(2)
}
