package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.SinglePageHomefeedBinding
import com.jar.app.feature_homepage.shared.domain.model.single_home_feed.SingleHomeFeedCardMetaData
import com.jar.app.feature_homepage.shared.util.HomeConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import java.lang.ref.WeakReference

internal class SinglePageHomeFeedEpoxyModel(
    private val uiScope: CoroutineScope,
    private val singleHomeFeedCardMetaData: SingleHomeFeedCardMetaData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onPrimaryCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    private val onShowMoreCtaClick: (cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<SinglePageHomefeedBinding>(R.layout.single_page_homefeed) {

    private var visibilityState: Int? = null
    private var timerJob: Job? = null

    override fun getBinding(view: View): SinglePageHomefeedBinding {
        return SinglePageHomefeedBinding.bind(view)
    }

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to singleHomeFeedCardMetaData.cardType,
                DynamicCardEventKey.FeatureType to singleHomeFeedCardMetaData.featureType,
            )
        )
    }

    private var singlePageHomefeedBinding: SinglePageHomefeedBinding? = null
    override fun bindItem(binding: SinglePageHomefeedBinding) {
        singlePageHomefeedBinding = binding
        val contextRef = WeakReference(binding.root.context)
        val singlePageHomeFeedMetaData = singleHomeFeedCardMetaData

        binding.root.setPlotlineViewTag(tag = singleHomeFeedCardMetaData.featureType)

        Glide.with(binding.root)
            .load(singlePageHomeFeedMetaData.header.icon)
            .into(binding.ivHeaderImage)

        binding.tvHeaderSPH.text = singlePageHomeFeedMetaData.header.convertToString(contextRef)
        binding.tvFragmentHeading.text =
            singlePageHomeFeedMetaData.title?.convertToString(contextRef)
        binding.tvFragmentSubHeading.text =
            singlePageHomeFeedMetaData.description.convertToString(contextRef)

        Glide.with(binding.root)
            .load(singlePageHomeFeedMetaData.bgImage)
            .into(binding.ivBgImage)

        binding.leftCTA.setText(
            singlePageHomeFeedMetaData.buttonList.getOrNull(0)?.text?.textList?.getOrNull(
                0
            )?.text.orEmpty()
        )
        binding.rightCTA.setText(
            singlePageHomeFeedMetaData.buttonList.getOrNull(1)?.text?.textList?.getOrNull(
                0
            )?.text.orEmpty()
        )

        if (singlePageHomeFeedMetaData.offerDetails == null) {
            binding.iCouponLayout.couponContainer.visibility = View.INVISIBLE
            binding.clCouponHolder.visibility = View.INVISIBLE
            binding.tvTrustedBy.visibility = View.VISIBLE
            Glide.with(binding.root)
                .load(singlePageHomeFeedMetaData.trustedByImage)
                .into(binding.tvTrustedImage)
        } else {
            binding.clCouponHolder.visibility = View.VISIBLE
            binding.iCouponLayout.couponContainer.visibility = View.VISIBLE
            binding.tvTrustedBy.visibility = View.INVISIBLE
            binding.tvTrustedImage.visibility = View.INVISIBLE
            binding.iCouponLayout.tvCouponHeading.text =
                singlePageHomeFeedMetaData.offerDetails?.textList?.getOrNull(0)?.text.orEmpty()
        }

        binding.leftCTA.setDebounceClickListener {
            cardEventData.map[DynamicCardEventKey.Data] = binding.leftCTA.getText()
            cardEventData.map[DynamicCardEventKey.VariantType] =
                singleHomeFeedCardMetaData.variantType
            cardEventData.map[DynamicCardEventKey.ExploreButtonStatus] =
                singleHomeFeedCardMetaData.showMoreButton.toString()
            onPrimaryCtaClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = BaseConstants.ExternalDeepLinks.SINGLE_HOME_FEED_CTA + "/" + singlePageHomeFeedMetaData.buttonList.getOrNull(
                        0
                    )?.deepLink.orEmpty(),
                    order = singleHomeFeedCardMetaData.order,
                    cardType = DynamicCardType.valueOf(singleHomeFeedCardMetaData.cardType),
                    featureType = singleHomeFeedCardMetaData.featureType
                ),
                cardEventData
            )
        }

        binding.rightCTA.setDebounceClickListener {
            cardEventData.map[DynamicCardEventKey.Data] = binding.rightCTA.getText()
            cardEventData.map[DynamicCardEventKey.VariantType] =
                singleHomeFeedCardMetaData.variantType
            cardEventData.map[DynamicCardEventKey.ExploreButtonStatus] =
                singleHomeFeedCardMetaData.showMoreButton.toString()
            onPrimaryCtaClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = BaseConstants.ExternalDeepLinks.SINGLE_HOME_FEED_CTA + "/" + singlePageHomeFeedMetaData.buttonList.getOrNull(
                        1
                    )?.deepLink.orEmpty(),
                    order = singleHomeFeedCardMetaData.order,
                    cardType = DynamicCardType.valueOf(singleHomeFeedCardMetaData.cardType),
                    featureType = singleHomeFeedCardMetaData.featureType
                ),
                cardEventData
            )
        }

        binding.showMore.visibility =
            if (singlePageHomeFeedMetaData.showMoreButton) View.VISIBLE else View.GONE

        if (singlePageHomeFeedMetaData.isExpanded.orFalse()) {
            binding.showMore.setText(HomeConstants.SinglePageHomeFeed.showLess)
            binding.showMore.setDrawableEnd(R.drawable.ic_top_arrow)
        } else {
            binding.showMore.setText(HomeConstants.SinglePageHomeFeed.showMore)
            binding.showMore.setDrawableEnd(R.drawable.ic_bottom_arrow)
        }
        binding.showMore.setDebounceClickListener {
            cardEventData.map[DynamicCardEventKey.Data] = binding.showMore.getText()
            onShowMoreCtaClick.invoke(cardEventData)
        }

        checkAndCancelTimerJob()
        timerJob = uiScope.countDownTimer(
            totalMillis = diffBetweenEndEpochToCurrentInMillis(singlePageHomeFeedMetaData.offerDetails?.expiresAt.orZero() / 1000),
            onInterval = {
                binding.iCouponLayout.couponTimer.text = it.milliSecondsToCountDown()
            },
            onFinished = {
                binding.iCouponLayout.couponContainer.visibility = View.INVISIBLE
                binding.clCouponHolder.visibility = View.INVISIBLE
                binding.tvTrustedBy.visibility = View.VISIBLE
                binding.tvTrustedImage.visibility = View.VISIBLE
                Glide.with(binding.root)
                    .load(singlePageHomeFeedMetaData.trustedByImage)
                    .into(binding.tvTrustedImage)
            }
        )
        timerJob?.start()
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        when (visibilityState) {
            VisibilityState.FULL_IMPRESSION_VISIBLE -> {
                this.visibilityState = visibilityState
                startShowEventJob(
                    uiScope,
                    isCardFullyVisible = {
                        this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                    },
                    onCardShownEvent = {
                        cardEventData.map[DynamicCardEventKey.VariantType] =
                            singleHomeFeedCardMetaData.variantType
                        cardEventData.map[DynamicCardEventKey.ExploreButtonStatus] =
                            singleHomeFeedCardMetaData.showMoreButton.toString()
                        cardEventData.map[DynamicCardEventKey.transaction_type] =
                            mutableListOf(
                                singlePageHomefeedBinding?.leftCTA?.getText().orEmpty(),
                                singlePageHomefeedBinding?.rightCTA?.getText().orEmpty(),
                                singlePageHomefeedBinding?.showMore?.getText().orEmpty()
                            ).toString()
                        onCardShown.invoke(cardEventData)
                    }
                )
            }

            VisibilityState.INVISIBLE -> {
                checkAndCancelTimerJob()
            }

            VisibilityState.PARTIAL_IMPRESSION_INVISIBLE -> {
                checkAndCancelTimerJob()
            }
        }
    }

    private fun checkAndCancelTimerJob() {
        if (timerJob?.isActive == true) {
            timerJob?.cancel()
            timerJob = null
        }
    }

    private fun diffBetweenEndEpochToCurrentInMillis(endTimeEpochInSeconds: Long): Long {
        val endInstant = Instant.ofEpochSecond(endTimeEpochInSeconds).atOffset(ZoneOffset.UTC)
        val currentInstant = Instant.now().atOffset(ZoneOffset.UTC)
        return Duration.between(currentInstant, endInstant).toMillis()
    }
}