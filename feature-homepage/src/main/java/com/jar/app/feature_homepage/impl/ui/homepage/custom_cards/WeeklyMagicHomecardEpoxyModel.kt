package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellWeeklyMagicBinding
import com.jar.app.feature_homepage.shared.domain.model.WeeklyMagicHomecardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference

class WeeklyMagicHomecardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val weeklyMagicHomecardData: WeeklyMagicHomecardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
): HomeFeedCard, CustomViewBindingEpoxyModel<FeatureHomepageCellWeeklyMagicBinding>(R.layout.feature_homepage_cell_weekly_magic) {

    private var visibilityState: Int? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to weeklyMagicHomecardData.cardType,
                DynamicCardEventKey.FeatureType to weeklyMagicHomecardData.featureType,
                DynamicCardEventKey.Data to weeklyMagicHomecardData.cardsLeft.toString()
            )
        )
    }

    override fun getBinding(view: View): FeatureHomepageCellWeeklyMagicBinding {
        return FeatureHomepageCellWeeklyMagicBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellWeeklyMagicBinding) {
        val contextRef = WeakReference(binding.root.context)
        val cardMeta = weeklyMagicHomecardData.cardMeta
        binding.root.setPlotlineViewTag(weeklyMagicHomecardData.featureType)

        binding.tvTitle.text = cardMeta?.title?.convertToString(contextRef)

        cardMeta?.infographic?.url?.let {
            Glide.with(binding.root).load(it).into(binding.ivInfoGraphic)
        }
        cardMeta?.endIcon?.let {
            Glide.with(binding.root).load(it).into(binding.ivEndIcon)
        }
        cardMeta?.footer?.getOrNull(0)?.let {
            Glide.with(binding.root).load(it).into(binding.ivFooterIcon)
        }

        binding.tvFooter.text = cardMeta?.textListFooter?.convertToString(contextRef)

        val cardImageViews = listOf(binding.ivCard1, binding.ivCard2, binding.ivCard3, binding.ivCard4)
        val cardsWon = cardImageViews.size - weeklyMagicHomecardData.cardsLeft
        for (i in 0 until cardsWon) {
            cardImageViews[i].setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context, R.drawable.feature_homepage_ic_weekly_magic_card
                )
            )
        }
        for (i in cardsWon until cardImageViews.size) {
            cardImageViews[i].setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context, R.drawable.feature_homepage_ic_weekly_magic_card_empty
                )
            )
        }

        binding.root.setDebounceClickListener {
            cardMeta?.cta?.deepLink?.let { deepLink ->
                val data = PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = deepLink,
                    order = weeklyMagicHomecardData.getSortKey(),
                    cardType = weeklyMagicHomecardData.getCardType(),
                    featureType = weeklyMagicHomecardData.featureType
                )
                onActionClick.invoke(data, cardEventData)
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
}