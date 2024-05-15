package com.jar.app.core_base.domain.model.card_library

interface DynamicCard {

    var uniqueId: String?

    var verticalPosition: Int?

    var horizontalPosition: Int?

    var shouldShowLabelTop: Boolean?

    fun getSortKey(): Int

    fun getCardType(): DynamicCardType

    fun getCardHeader(): TextList?

    fun getCardDescription(): TextList?

    fun shouldShowAsSingleCard(): Boolean {
        return false
    }
}