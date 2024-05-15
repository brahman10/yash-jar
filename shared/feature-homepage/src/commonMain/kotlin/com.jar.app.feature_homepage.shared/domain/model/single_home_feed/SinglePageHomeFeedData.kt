package com.jar.app.feature_homepage.shared.domain.model.single_home_feed

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.TextList
import com.jar.app.core_base.util.orFalse

data class SinglePageHomeFeedData(
    val order: Int,
    val cardType: String,
    val featureType: String,
    val header: TextList?,
    val singleHomeFeedCardMetaData: SingleHomeFeedCardMetaData?,

    override var uniqueId: String? = featureType.plus(singleHomeFeedCardMetaData?.showMoreButton)
        .plus(singleHomeFeedCardMetaData?.isExpanded),

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
        return null
    }

    override fun getCardDescription(): TextList? {
        return null
    }

    override fun shouldShowAsSingleCard(): Boolean {
        return singleHomeFeedCardMetaData?.shouldShowAsSingleCard.orFalse()
    }
}