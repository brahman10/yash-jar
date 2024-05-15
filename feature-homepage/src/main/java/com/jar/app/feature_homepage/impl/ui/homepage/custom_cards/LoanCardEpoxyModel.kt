package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_base.domain.model.card_library.StaticInfoType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellLoanCardBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.LoanCardData
import kotlinx.coroutines.Job

internal class LoanCardEpoxyModel(
    private val loanCardData: LoanCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onCtaClick: (staticInfoData: StaticInfoData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellLoanCardBinding>(
        R.layout.feature_homepage_cell_loan_card
    ) {

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to loanCardData.cardType,
                DynamicCardEventKey.FeatureType to loanCardData.featureType,
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageCellLoanCardBinding) {
        binding.root.setPlotlineViewTag(tag = loanCardData.featureType)
        Glide.with(binding.root).load("${BaseConstants.CDN_BASE_URL}/Generic/loan_card.png")
            .into(binding.ivImage)
        Glide.with(binding.root).load("${BaseConstants.CDN_BASE_URL}/Generic/Limited-Time-Tag.png")
            .into(binding.ivLimitedTimeTag)

        binding.btnAction.setDebounceClickListener {
            onCtaClick.invoke(
                StaticInfoData(
                    type = if (loanCardData.lendingKycProgress) StaticInfoType.CUSTOM_WEB_VIEW.name else StaticInfoType.DEEPLINK.name,
                    value = if (loanCardData.lendingKycProgress) BaseConstants.MICRO_LOAN_DETAILS_URL else BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.LENDING_KYC_RESUME,
                    deeplink = null
                ),
                cardEventData
            )
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            onCardShown.invoke(cardEventData)
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellLoanCardBinding {
        return FeatureHomepageCellLoanCardBinding.bind(view)
    }
}