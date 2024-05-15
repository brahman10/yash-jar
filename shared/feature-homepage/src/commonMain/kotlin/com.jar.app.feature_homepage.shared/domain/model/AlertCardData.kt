package com.jar.app.feature_homepage.shared.domain.model

import com.jar.app.core_base.domain.model.card_library.CardData
import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.TextList
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AlertCardData(
    @SerialName("cardType")
    val cardType: String,

    @SerialName("order")
    val order: Int,

    @SerialName("showCard")
    val showCard: Boolean,

    @SerialName("groupId")
    val groupId: Int? = null,

    @SerialName("featureType")
    val featureType: String,

    @SerialName("header")
    val header: TextList? = null,

    @SerialName("description")
    val description: TextList? = null,

    @SerialName("cardMeta")
    val cardMeta: CardData,

    @SerialName("savingsType")
    var savingsType: SavingsType? = null,

    override var uniqueId: String? = featureType.plus(savingsType?.name),

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
        return description
    }
}
