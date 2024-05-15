package com.jar.app.feature_homepage.shared.domain.model.current_investment

import com.jar.app.core_base.domain.model.GoldBalance

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.TextList
import com.jar.app.feature_homepage.shared.domain.model.QuickActionData
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData

class CurrentGoldInvestmentCardData(
    val goldBalance: GoldBalance,
    val quickActionsButtonData: List<QuickActionData>?,
    val firstCoinData: com.jar.app.feature_homepage.shared.domain.model.FirstCoinHomeScreenData?,
    val weeklyChallengeMetaData: WeeklyChallengeMetaData?,
    val order: Int,
    val cardType: String,
    val header: TextList?,
    val featureType: String,
    val shouldRunShimmer: Boolean,
    var isFirstTimeAnimationCompleted: Boolean = false,

    override var uniqueId: String? = featureType
        .plus("_")
        .plus(goldBalance.getGoldVolumeWithUnit())
        .plus("_")
        .plus(firstCoinData?.percentageCompleted)
        .plus("_")
        .plus(firstCoinData?.deliveryStatus)
        .plus("_")
        .plus(weeklyChallengeMetaData?.cardsLeft),

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
}