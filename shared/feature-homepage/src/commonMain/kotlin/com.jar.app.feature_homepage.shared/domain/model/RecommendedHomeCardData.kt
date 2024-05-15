package com.jar.app.feature_homepage.shared.domain.model

import com.jar.app.core_base.domain.model.card_library.CardData

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.TextList
import com.jar.app.core_base.util.orZero

data class RecommendedHomeCardData(
    val order: Int,
    val cardType: String,
    val featureType: String,
    val header: TextList?,
    val cardData: CardData?,
    val offerAmount: Int?,

    override var uniqueId: String? = featureType.plus("_").plus(offerAmount.orZero())
        .plus(cardData?.title?.convertToRawString().orEmpty()),

    override var verticalPosition: Int? = null,
    override var horizontalPosition: Int? = null,
    override var shouldShowLabelTop: Boolean? = true
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
