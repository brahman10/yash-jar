package com.jar.app.feature_homepage.shared.domain.model.payment_prompt

import com.jar.app.core_base.domain.model.card_library.CTA

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.TextList
import com.jar.app.feature_user_api.domain.model.SuggestedAmount
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PaymentPromptData(
    @SerialName("order")
    val order: Int,

    @SerialName("cardType")
    val cardType: String,

    @SerialName("header")
    val header: TextList?,

    @SerialName("cta")
    val cta: CTA?,

    @SerialName("featureType")
    val featureType: String,

    @SerialName("investPromptSuggestions")
    val investPromptSuggestions: List<SuggestedAmount>?,

    @SerialName("investPromptTitle")
    val investPromptTitle: String?,

    val shouldRunShimmer:Boolean,

    override var uniqueId: String? = featureType,

    override var verticalPosition: Int? = null,

    override var horizontalPosition: Int? = null,

    override var shouldShowLabelTop: Boolean? = null,

    ) : DynamicCard {

    override fun getSortKey(): Int {
        return order
    }

    override fun getCardType(): DynamicCardType {
        return DynamicCardType.valueOf(cardType)
    }

    override fun getCardHeader(): TextList? {
        return header
    }

    override fun getCardDescription(): TextList? {
        return null
    }

}