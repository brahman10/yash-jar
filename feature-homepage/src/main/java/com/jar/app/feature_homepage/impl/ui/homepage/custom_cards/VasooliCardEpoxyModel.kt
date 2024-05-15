package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellVasooliCardBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.VasooliCardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class VasooliCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val vasooliCardData: VasooliCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellVasooliCardBinding>(
        R.layout.feature_homepage_cell_vasooli_card
    ) {

    private var visibilityState: Int? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to vasooliCardData.cardType,
                DynamicCardEventKey.FeatureType to vasooliCardData.featureType,
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageCellVasooliCardBinding) {
        binding.root.setPlotlineViewTag(tag = vasooliCardData.featureType)
        Glide.with(binding.root)
            .load(BaseConstants.IllustrationUrls.VASOOLI_ONBOARDING)
            .into(binding.ivImage)

//        binding.btnAction.setDebounceClickListener {
//            onCtaClick.invoke(
//                PrimaryActionData(
//                    type = PrimaryActionType.DEEPLINK,
//                    value = BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.VASOOLI,
//                    order = vasooliCardData.getSortKey(),
//                    cardType = vasooliCardData.getCardType(),
//                    featureType = vasooliCardData.featureType
//                ),
//                cardEventData
//            )
//        }
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

    override fun getBinding(view: View): FeatureHomepageCellVasooliCardBinding {
        return FeatureHomepageCellVasooliCardBinding.bind(view)
    }
}