package com.jar.app.feature_homepage.shared.domain.model.partner_banner

import com.jar.app.core_base.domain.model.card_library.CTA

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.TextList

data class BannersData(
    val banners: List<Banner>,
    val order: Int,
    val cardType: String,
    val header: TextList?,
    val cta: CTA?,
    val featureType: String,

    override var uniqueId: String? = featureType.plus(banners.size),

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