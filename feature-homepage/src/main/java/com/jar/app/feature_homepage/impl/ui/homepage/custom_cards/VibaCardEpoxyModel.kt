package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.VisibilityState
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellVibaCardBinding
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCard
import com.jar.app.feature_homepage.impl.ui.viba.VibaCardRecyclerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class VibaCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val vibaHorizontalCard: VibaHorizontalCard,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellVibaCardBinding>(R.layout.feature_homepage_cell_viba_card) {
    private var visibilityState: Int? = null
    private val eventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to vibaHorizontalCard.cardType,
                DynamicCardEventKey.FeatureType to vibaHorizontalCard.featureType,
                DynamicCardEventKey.VibaCardType to DynamicCardEventKey.VibaCardType,
                DynamicCardEventKey.Position to vibaHorizontalCard.order.toString(),
                DynamicCardEventKey.CardTitle to vibaHorizontalCard.header?.textList?.get(0)?.text.orEmpty(),
            )
        )
    }

    private val spaceItemDecoration = SpaceItemDecoration(6.dp, 0.dp)

    var adapter: VibaCardRecyclerAdapter? = null

    override fun bindItem(binding: FeatureHomepageCellVibaCardBinding) {
        binding.root.setPlotlineViewTag(tag = vibaHorizontalCard.featureType)

        adapter = VibaCardRecyclerAdapter(uiScope) {
            val data = PrimaryActionData(
                type = it.cta.getPrimaryActionType(),
                value = it.cta.deepLink,
                order = vibaHorizontalCard.order,
                cardType = vibaHorizontalCard.getCardType(),
                featureType = vibaHorizontalCard.featureType
            )

            eventData.map[DynamicCardEventKey.CardHorizontalPosition] = it.priority.orZero().toString()
            eventData.map[DynamicCardEventKey.Images] = it.background.overlayImage.orEmpty()
            eventData.map[DynamicCardEventKey.CardSubText] = it.description?.getOrNull(0)?.title.orEmpty()
            onActionClick.invoke(data, eventData)
        }
        binding.rvVibaList.layoutManager =
            LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
        binding.rvVibaList.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvVibaList.adapter = adapter
        adapter?.submitList(vibaHorizontalCard.vibaCardData)
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
                    eventData.map[DynamicCardEventKey.SubTextTop] = vibaHorizontalCard.vibaCardData.getOrNull(0)?.title.orEmpty()
                    eventData.map[DynamicCardEventKey.Images] = vibaHorizontalCard.vibaCardData.map { it.background.overlayImage.orEmpty() }.toString()
                    eventData.map[DynamicCardEventKey.SubText] = vibaHorizontalCard.vibaCardData.map { it.description?.map { it.title + " " + it.description } }.toString()
                    onCardShown.invoke(eventData)
                }
            )
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellVibaCardBinding {
        return FeatureHomepageCellVibaCardBinding.bind(view)
    }
}