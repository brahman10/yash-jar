package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageLayoutPartnerBannerBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.Banner
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.BannersData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class PartnerBannerEpoxyModel(
    private val uiScope: CoroutineScope,
    private val bannersData: BannersData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onClaimPartnerBonusClick: (banner: Banner) -> Unit,
    private val onShowAllPartnerBonusClick: () -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageLayoutPartnerBannerBinding>(R.layout.feature_homepage_layout_partner_banner) {

    private var visibilityState: Int? = null

    private val eventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to bannersData.cardType,
                DynamicCardEventKey.FeatureType to bannersData.featureType,
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageLayoutPartnerBannerBinding) {
        val firstBanner = bannersData.banners[0]
        binding.root.setPlotlineViewTag(tag = bannersData.featureType)
        binding.include.claimNow.setDebounceClickListener {
            onClaimPartnerBonusClick.invoke(firstBanner)
        }

        binding.btnShowAll.setDebounceClickListener {
            onShowAllPartnerBonusClick.invoke()
        }

        binding.tvDesc.isVisible = bannersData.banners.size > 1
        binding.btnShowAll.isVisible = bannersData.banners.size > 1

        binding.tvDesc.text = binding.root.context.resources.getQuantityString(
            R.plurals.feature_homepage_bonus_available,
            bannersData.banners.size - 1,
            bannersData.banners.size - 1
        )
        binding.include.tvTitle.text = firstBanner.title
        binding.include.tvDescription.text = firstBanner.description
        Glide.with(binding.root).load(firstBanner.partnerLogo).into(binding.include.ivLogo)
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
                    onCardShown.invoke(eventData)
                }
            )
        }
    }

    override fun getBinding(view: View): FeatureHomepageLayoutPartnerBannerBinding {
        return FeatureHomepageLayoutPartnerBannerBinding.bind(view)
    }
}