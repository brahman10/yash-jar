package com.jar.app.feature_homepage.shared.domain.model

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.TextList
import com.jar.app.feature_jar_duo.shared.domain.model.DuoContactsMetaData

data class JarDuoData(
    val order: Int,
    val cardType: String,
    val featureType: String,
    val header: TextList?,
    val duoContactsMetaData: DuoContactsMetaData?,

    override var uniqueId: String? = featureType
        .plus(duoContactsMetaData?.totalContactsToBeInvited)
        .plus(duoContactsMetaData?.totalPendingInvites)
        .plus(duoContactsMetaData?.totalGroupCount),

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
