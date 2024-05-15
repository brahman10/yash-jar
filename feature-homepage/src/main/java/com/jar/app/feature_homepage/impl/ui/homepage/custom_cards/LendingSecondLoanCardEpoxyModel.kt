package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageSecondLoanCardBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.LendingSecondLoanCardData
import kotlinx.coroutines.*

internal class LendingSecondLoanCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val cardData: LendingSecondLoanCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageSecondLoanCardBinding>(
        R.layout.feature_homepage_second_loan_card
    ) {

    private var visibilityState: Int? = null


    private var job: Job? = null

    private var shimmerLayout: ShimmerFrameLayout? = null
    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to cardData.cardType,
                DynamicCardEventKey.FeatureType to cardData.featureType,
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageSecondLoanCardBinding) {
        shimmerLayout = binding.shimmerPlaceholder
        binding.tvAmount.text = binding.root.context.getString(
            R.string.feature_homepage_rupee_prefix_string,
            cardData.availableLimit.getFormattedAmount()
        )

        binding.root.setDebounceClickListener {
            onCtaClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.LENDING_ONBOARDING,
                    order = cardData.getSortKey(),
                    cardType = cardData.getCardType(),
                    featureType = cardData.featureType
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

            if (cardData.shouldRunShimmer)
                startShimmer()
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            stopShimmer()
        } else if (visibilityState == VisibilityState.PARTIAL_IMPRESSION_INVISIBLE) {
            stopShimmer()
        }
    }

    override fun getBinding(view: View): FeatureHomepageSecondLoanCardBinding {
        return FeatureHomepageSecondLoanCardBinding.bind(view)
    }

    private fun startShimmer() {
        job?.cancel()
        job = uiScope.launch {
            delay(1000)
            if (isActive) {
                shimmerLayout?.showShimmer(false)
                shimmerLayout?.startShimmer()
            }
        }
    }

    private fun stopShimmer() {
        job?.cancel()
        shimmerLayout?.stopShimmer()
        shimmerLayout?.hideShimmer()
        shimmerLayout?.clearAnimation()
    }

}