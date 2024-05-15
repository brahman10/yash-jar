package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellDailySavingsV2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class DailySavingsV2EpoxyModel(
    private val uiScope: CoroutineScope,
    private val dailySavingsCardData: com.jar.app.feature_homepage.shared.domain.model.DailySavingCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit = {},
    private val onPrimaryCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellDailySavingsV2Binding>(
        R.layout.feature_homepage_cell_daily_savings_v2
    ) {
    private var isPaytmBannerVisible = false
    private var visibilityState: Int? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to if(!isPaytmBannerVisible) dailySavingsCardData.cardType else "paytmcashback_banner",
                DynamicCardEventKey.FeatureType to dailySavingsCardData.featureType,
                DynamicCardEventKey.Data to "Start saving"
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageCellDailySavingsV2Binding) {
        val deeplink =
            BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.DAILY_SAVINGS
        binding.cvDailySaving.setDebounceClickListener {
            onPrimaryCtaClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = deeplink,
                    order = dailySavingsCardData.getSortKey(),
                    cardType = dailySavingsCardData.getCardType(),
                    featureType = dailySavingsCardData.featureType
                ), cardEventData
            )
        }
        binding.btnStartDailySavings.setDebounceClickListener {
            onPrimaryCtaClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = deeplink,
                    order = dailySavingsCardData.getSortKey(),
                    cardType = dailySavingsCardData.getCardType(),
                    featureType = dailySavingsCardData.featureType
                ), cardEventData
            )
        }
        binding.tvHeading.text = dailySavingsCardData.dailySavingV2CardData?.header
        binding.tvBody.text = dailySavingsCardData.dailySavingV2CardData?.title
        binding.tvCaption.text = dailySavingsCardData.dailySavingV2CardData?.desc
        binding.btnStartDailySavings.setText(dailySavingsCardData.dailySavingV2CardData?.buttonText.toString())
    }

    override fun getBinding(view: View): FeatureHomepageCellDailySavingsV2Binding {
        return FeatureHomepageCellDailySavingsV2Binding.bind(view)
    }


    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            this.visibilityState = visibilityState
            startShowEventJob(
                uiScope,
                isCardFullyVisible = {
                    this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                },
                onCardShownEvent = {
                    onCardShown.invoke(cardEventData)
                }
            )
        }
    }

}