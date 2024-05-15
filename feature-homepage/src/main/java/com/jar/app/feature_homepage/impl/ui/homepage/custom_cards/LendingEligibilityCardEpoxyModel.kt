package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageLendingEligibilityCardBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.LendingEligibilityCardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class LendingEligibilityCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val lendingEligibilityCardData: LendingEligibilityCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageLendingEligibilityCardBinding>(
        R.layout.feature_homepage_lending_eligibility_card
    ) {

    private var visibilityState: Int? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to lendingEligibilityCardData.cardType,
                DynamicCardEventKey.FeatureType to lendingEligibilityCardData.featureType,
            )
        )
    }

    override fun getBinding(view: View): FeatureHomepageLendingEligibilityCardBinding {
        return FeatureHomepageLendingEligibilityCardBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageLendingEligibilityCardBinding) {
        binding.root.setPlotlineViewTag(tag = lendingEligibilityCardData.featureType)
        binding.tvTitle.text = lendingEligibilityCardData.title
        binding.tvDescription.text = lendingEligibilityCardData.description

        binding.root.setDebounceClickListener {
            onCtaClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.LENDING_ONBOARDING,
                    order = lendingEligibilityCardData.getSortKey(),
                    cardType = lendingEligibilityCardData.getCardType(),
                    featureType = lendingEligibilityCardData.featureType
                ),
                cardEventData
            )
        }
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