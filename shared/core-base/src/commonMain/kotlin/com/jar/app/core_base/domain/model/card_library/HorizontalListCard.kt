package com.jar.app.core_base.domain.model.card_library

data class HorizontalListCard(
    val cards: List<DynamicCard>,

    val order: Int,

    override var uniqueId: String? = cards.joinToString { (it as? LibraryCardData)?.featureType.orEmpty() },

    override var verticalPosition: Int? = cards.getOrNull(0)?.verticalPosition,

    override var horizontalPosition: Int? = null,

    override var shouldShowLabelTop: Boolean? = cards.any { (it as? LibraryCardData)?.cardMeta?.labelTop != null }
) : DynamicCard {

    override fun getSortKey(): Int {
        return order
    }

    override fun getCardType(): DynamicCardType {
        return DynamicCardType.HOMEFEED_TYPE_HORIZONTAL_LIST
    }

    override fun getCardHeader(): TextList? {
        return (cards.getOrNull(0) as? LibraryCardData)?.header
    }

    override fun getCardDescription(): TextList? {
        return (cards.getOrNull(0) as? LibraryCardData)?.description
    }
}