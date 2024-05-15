package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellPostSetupGoldSipCardBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.gold_sip.GoldSipCard
import com.jar.app.feature_homepage.shared.domain.model.gold_sip.GoldSipData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class PostSetupGoldSipCardEpoxyModel(
    private val goldSipCard: GoldSipCard,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit = {},
    private val onGoldSipManualPaymentClicked: (cardEventData: CardEventData, goldSipData: GoldSipData) -> Unit = { _, _ -> },
    override var cardShownEventJob: Job? = null
) : HomeFeedCard, CustomViewBindingEpoxyModel<FeatureHomepageCellPostSetupGoldSipCardBinding>(
    R.layout.feature_homepage_cell_post_setup_gold_sip_card
) {

    private var job: Job? = null

    private var visibilityState: Int? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to goldSipCard.cardType,
                DynamicCardEventKey.FeatureType to goldSipCard.featureType,
                DynamicCardEventKey.Data to goldSipCard.goldSipData?.amount.toString()
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageCellPostSetupGoldSipCardBinding) {
        binding.root.setPlotlineViewTag(tag = goldSipCard.featureType)
        binding.tvPendingAmount.text =
            binding.root.context.getString(
                com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                goldSipCard.goldSipData?.amount.orZero()
            )

        binding.btnPayNow.setDebounceClickListener {
            goldSipCard.goldSipData?.let {
                onGoldSipManualPaymentClicked.invoke(cardEventData, it)
            }
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

    override fun getBinding(view: View): FeatureHomepageCellPostSetupGoldSipCardBinding {
        return FeatureHomepageCellPostSetupGoldSipCardBinding.bind(view)
    }
}