package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.VisibilityState
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_base.domain.model.card_library.StaticInfoType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellHelpVideoCardBinding
import com.jar.app.feature_homepage.shared.domain.model.HelpVideosData
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.impl.ui.help_videos.HelpVideosRecyclerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class HelpVideosCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val helpVideoData: HelpVideosData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (staticInfoData: StaticInfoData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellHelpVideoCardBinding>(R.layout.feature_homepage_cell_help_video_card) {

    private val eventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to helpVideoData.cardType,
                DynamicCardEventKey.FeatureType to helpVideoData.featureType
            )
        )
    }
    private var visibilityState: Int? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun bindItem(binding: FeatureHomepageCellHelpVideoCardBinding) {
        binding.root.setPlotlineViewTag(tag = helpVideoData.featureType)
        val adapter = HelpVideosRecyclerAdapter(true) {
            val data = StaticInfoData(
                type = StaticInfoType.CUSTOM_WEB_VIEW.name,
                value = it.link,
                deeplink = null
            )
            onActionClick.invoke(data, eventData)
        }
        binding.rvVideos.layoutManager =
            GridLayoutManager(binding.root.context, 1, GridLayoutManager.HORIZONTAL, false)
        binding.rvVideos.adapter = adapter
        adapter.submitList(helpVideoData.data)
        binding.btnViewAll.setDebounceClickListener {
            val data = StaticInfoData(
                type = StaticInfoType.DEEPLINK.name,
                value = BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.HELP_VIDEOS_LISTING,
                deeplink = null
            )
            onActionClick.invoke(data, eventData)
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
                    onCardShown.invoke(eventData)
                }
            )
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellHelpVideoCardBinding {
        return FeatureHomepageCellHelpVideoCardBinding.bind(view)
    }
}