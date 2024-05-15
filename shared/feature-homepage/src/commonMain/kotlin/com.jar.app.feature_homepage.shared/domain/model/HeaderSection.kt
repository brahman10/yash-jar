package com.jar.app.feature_homepage.shared.domain.model

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.TextList

data class HeaderSection(
    val title: TextList?,
    val description: TextList?,
    val position: Int,

    override var uniqueId: String? = title?.toString()?.plus(position),

    override var verticalPosition: Int? = null,

    override var horizontalPosition: Int? = null,

    override var shouldShowLabelTop: Boolean? = null,

    ) : DynamicCard {

    override fun getSortKey(): Int {
        return position
    }

    override fun getCardType(): DynamicCardType {
        return DynamicCardType.HEADER
    }

    override fun getCardHeader(): TextList? {
        return title
    }

    override fun getCardDescription(): TextList? {
        return description
    }
}