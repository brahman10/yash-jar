package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.animation.ValueAnimator
import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellGoldSipCardBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.gold_sip.GoldSipCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class GoldSipCardEpoxyModel(
    private val goldSipCard: GoldSipCard,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit = {},
    private val onPrimaryCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    override var cardShownEventJob: Job? = null
) : HomeFeedCard, CustomViewBindingEpoxyModel<FeatureHomepageCellGoldSipCardBinding>(
    R.layout.feature_homepage_cell_gold_sip_card
) {

    private var binding: FeatureHomepageCellGoldSipCardBinding? = null
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

    private val weeklyAnimUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it?.animatedValue.toString().toFloatOrZero() * 100).toInt()
        if (progress == 100 && binding != null) {
            autoSlideToNext(binding!!.lottieWeekly, binding!!.lottieMonthly)
            binding!!.lottieMonthly.playAnimation()
            binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(1), true)
        }
    }

    private val monthlyAnimUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it?.animatedValue.toString().toFloatOrZero() * 100).toInt()
        if (progress == 100 && binding != null) {
            autoSlideToNext(binding!!.lottieMonthly, binding!!.lottieWeekly)
            binding!!.lottieWeekly.playAnimation()
            binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(0), true)
        }
    }


    override fun bindItem(binding: FeatureHomepageCellGoldSipCardBinding) {
        this.binding = binding
        binding.root.setPlotlineViewTag(tag = goldSipCard.featureType)
        binding.btnKnowMore.setText(binding.root.context.getString(R.string.feature_home_page_know_more))

        binding.btnKnowMore.setDebounceClickListener {
            onPrimaryCtaClick(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.GOLD_SIP_INTRO,
                    order = goldSipCard.getSortKey(),
                    cardType = goldSipCard.getCardType(),
                    featureType = goldSipCard.featureType
                ),
                cardEventData
            )
        }
    }

    private fun autoSlideToNext(view1: View, view2: View) {
        view1.slideToRevealNew(view2)
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {

            binding?.lottieWeekly?.removeAllUpdateListeners()
            binding?.lottieWeekly?.addAnimatorUpdateListener(weeklyAnimUpdateListener)

            binding?.lottieMonthly?.removeAllUpdateListeners()
            binding?.lottieMonthly?.addAnimatorUpdateListener(monthlyAnimUpdateListener)

            binding?.lottieWeekly?.playLottieWithUrlAndExceptionHandling(
                binding?.root?.context!!,
                BaseConstants.LottieUrls.GOLD_SIP_WEEKLY
            )
            binding?.lottieMonthly?.playLottieWithUrlAndExceptionHandling(
                binding?.root?.context!!,
                BaseConstants.LottieUrls.GOLD_SIP_MONTHLY
            )
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
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            binding?.lottieWeekly?.removeAllUpdateListeners()
            binding?.lottieMonthly?.removeAllUpdateListeners()
            binding?.lottieWeekly?.cancelAnimation()
            binding?.lottieWeekly?.clearAnimation()
            binding?.lottieMonthly?.cancelAnimation()
            binding?.lottieMonthly?.clearAnimation()
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellGoldSipCardBinding {
        return FeatureHomepageCellGoldSipCardBinding.bind(view)
    }
}