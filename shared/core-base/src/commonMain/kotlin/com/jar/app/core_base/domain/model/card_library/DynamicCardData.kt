package com.jar.app.core_base.domain.model.card_library

import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonElement

@kotlinx.serialization.Serializable
data class LibraryCardData(
    @SerialName("cardType")
    var cardType: String,

    @SerialName("order")
    val order: Int,

    @SerialName("showCard")
    val showCard: Boolean,

    @SerialName("groupId")
    val groupId: Int? = null,

    @SerialName("featureType")
    val featureType: String,

    @SerialName("state")
    val state: String? = null,

    @SerialName("header")
    val header: TextList? = null,

    @SerialName("subHeader")
    val description: TextList? = null,

    @SerialName("cardMeta")
    val cardMeta: CardData? = null,

    @SerialName("staticInfoData")
    val staticInfoData: StaticInfoData? = null,

    @SerialName("shouldShowAsSingleCard")
    var shouldShowAsSingleCard: Boolean? = null,

    @SerialName("data")
    var data: JsonElement? = null,

    override var uniqueId: String? = featureType
        .plus(header?.convertToRawString())
        .plus(description?.convertToRawString())
        .plus(order)
        .plus(cardMeta?.labelTop?.text?.convertToRawString())
        .plus(cardMeta?.labelBottom?.text?.convertToRawString()),

    override var verticalPosition: Int? = null,

    override var horizontalPosition: Int? = null,

    override var shouldShowLabelTop: Boolean? = null
) : DynamicCard {

    override fun getSortKey(): Int {
        return order
    }

    override fun getCardType(): DynamicCardType {
        return DynamicCardType.values().find { it.name == cardType } ?: DynamicCardType.NONE
    }

    override fun getCardHeader(): TextList? {
        return header
    }

    override fun getCardDescription(): TextList? {
        return description
    }

    override fun shouldShowAsSingleCard(): Boolean {
        return shouldShowAsSingleCard.orFalse()
    }
}

@kotlinx.serialization.Serializable
data class TextList(
    @SerialName("textList")
    val textList: List<TextData>,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("iconList")
    val iconList: List<String>? = null,
) {

    fun convertToRawString(): String {
        return textList.joinToString { it.text }
    }
}

@kotlinx.serialization.Serializable
data class TextData(
    @SerialName("text") val text: String,

    @SerialName("textColor") val textColor: String? = null,

    @SerialName("textIcon") val textIcon: String? = null,

    @SerialName("textSize") val textSize: Int,

    @SerialName("hyperlink") val hyperlink: String? = null,

    @SerialName("fontType") val fontType: List<String>,

    @SerialName("deepLink") val deepLink: String? = null
)

@kotlinx.serialization.Serializable
data class Infographic(
    @SerialName("type")
    private val type: String,

    @SerialName("url")
    val url: String,

    /** Special case for [InfographicType.VIDEO]] where we show Thumbnail While video is being played*/
    @SerialName("thumbnail")
    val thumbnail: String? = null,
) {
    fun getInfographicType(): InfographicType {
        return InfographicType.valueOf(type)
    }
}

@kotlinx.serialization.Serializable
data class CTA(

    @SerialName("deepLink")
    val deepLink: String,

    @SerialName("text")
    val text: TextList? = null,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("startColor")
    val startColor: String? = null,

    @SerialName("endColor")
    val endColor: String? = null,

    @SerialName("actionType")
    val actionType: String? = null,

    @SerialName("actionUrl")
    val actionUrl: String? = null
) {
    fun getPrimaryActionType(): PrimaryActionType {
        return actionType?.let { PrimaryActionType.valueOf(it) }
            ?: kotlin.run { PrimaryActionType.DEEPLINK }
    }
}


@kotlinx.serialization.Serializable
data class FooterCarousel(
    @SerialName("backgroundColor")
    val backgroundColor: String? = null,
    @SerialName("slides")
    val slides: List<TextList>? = null
)

@kotlinx.serialization.Serializable
data class CardBackground(
    @SerialName("startColor") val startColor: String? = null,

    @SerialName("endColor") val endColor: String? = null,

    @SerialName("overlayImage") val overlayImage: String? = null,

    @SerialName("cornerRadius") private val cornerRadius: Int? = null,
) {
    fun getCornerRadius(default: Float? = null): Float {
        return cornerRadius?.toFloat() ?: default.orZero()
    }

    fun getCardBackgroundGradient(): List<String> {
        val gradientStartColor = startColor ?: "color_352F4F"
        val gradientEndColor = endColor ?: "color_352F4F"
        return listOf(gradientStartColor, gradientEndColor)
    }

    fun getCardOverlayGradient(): List<String> {
        val gradientStartColor = "#00ffffff"
        val gradientEndColor = endColor ?: "color_352F4F"
        return listOf(gradientStartColor, gradientEndColor)
    }

}

@kotlinx.serialization.Serializable
data class CardData(
    @SerialName("startIcon") val startIcon: String? = null,

    @SerialName("endIcon") val endIcon: String? = null,

    @SerialName("title") val title: TextList? = null,

    @SerialName("description") val description: TextList? = null,

    @SerialName("infographic")
    val infographic: Infographic? = null,

    @SerialName("textListFooter")
    val textListFooter: TextList? = null,

    @SerialName("cta")
    val cta: CTA? = null,

    @SerialName("label_top") val labelTop: Label? = null,

    @SerialName("label_bottom") val labelBottom: Label? = null,

    @SerialName("footer")
    val footer: List<String>? = null,

    @SerialName("footerIconList")
    val footerIconList: List<String>? = null,

    @SerialName("cardBackground")
    val cardBackground: CardBackground? = null,

    @SerialName("footerCarousel")
    val footerCarousel: FooterCarousel? = null,

    @SerialName("shouldRunShimmer") val shouldRunShimmer: Boolean,

    var hasTranslatedOnce: Boolean = false
)

@kotlinx.serialization.Serializable
data class StaticInfoData(
    @SerialName("type")
    private val type: String? = null,

    @SerialName("value")
    val value: String,

    @SerialName("deeplink")
    var deeplink: String? = null
) {
    fun getStaticInfoType() =
        if (type.isNullOrBlank()) StaticInfoType.CUSTOM_ACTION_DISMISS_SAVING else StaticInfoType.valueOf(
            type
        )
}

@kotlinx.serialization.Serializable
data class PrimaryActionData(
    @SerialName("type")
    val type: PrimaryActionType,
    @SerialName("value")
    val value: String,
    @SerialName("order")
    val order: Int,
    @SerialName("cardType")
    val cardType: DynamicCardType,
    @SerialName("featureType")
    val featureType: String,
    @SerialName("data")
    var data: JsonElement? = null,
)

@kotlinx.serialization.Serializable
data class Label(
    @SerialName("text")
    val text: TextList,

    @SerialName("icon")
    val icon: String? = null,

    @SerialName("backgroundColor")
    private val backgroundColor: String? = null,

    @SerialName("alignment")
    private val alignment: String? = null,

    @SerialName("enabled")
    val shouldShowLabelTopView: Boolean? = null
) {
    fun getAlignment(): LabelAlignment =
        LabelAlignment.values().find { it.name == alignment } ?: LabelAlignment.START

    fun getLabelBackgroundColor() = backgroundColor ?: "#1EA787"
}

enum class LabelAlignment {
    START, CENTER, END;

    companion object {
        fun getValueForType(value: String): LabelAlignment {
            return LabelAlignment.values().find { it.name == value } ?: START
        }
    }
}

enum class FontType {
    NORMAL, BOLD, UNDERLINE, STRIKETHROUGH;

    companion object {
        fun getTypeForValue(value: String): FontType {
            return FontType.values().find { it.name == value } ?: NORMAL
        }

    }
}

enum class InfographicType {
    IMAGE, LOTTIE, VIDEO, GIF
}

enum class PrimaryActionType {
    DEEPLINK, IN_APP_BROWSER
}

enum class StaticInfoType {
    POPUP, VIDEO, DEEPLINK, IN_APP_HELP, EXTERNAL_URL, CUSTOM_WEB_VIEW, CUSTOM_ACTION_DISMISS_SAVING, CUSTOM_ACTION_DISMISS_REFER_EARN
}