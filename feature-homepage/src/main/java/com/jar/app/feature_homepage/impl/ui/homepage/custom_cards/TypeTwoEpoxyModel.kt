package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orFalse
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.*
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeTwoBinding
import com.jar.app.core_ui.util.convertToString
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

internal class TypeTwoEpoxyModel(
    internal val libraryCardViewData: LibraryCardData,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : CustomViewBindingEpoxyModel<FeatureHomepageCellTypeTwoBinding>(R.layout.feature_homepage_cell_type_two),
    HorizontalCard {

    private val eventData by lazy {
        CardEventData(
            map = mutableMapOf(
                DynamicCardEventKey.CardType to libraryCardViewData.cardType,
                DynamicCardEventKey.FeatureType to libraryCardViewData.featureType,
                DynamicCardEventKey.Data to libraryCardViewData.state.toString(),
                DynamicCardEventKey.CardVerticalPosition to libraryCardViewData.verticalPosition.toString(),
                DynamicCardEventKey.CardHorizontalPosition to libraryCardViewData.horizontalPosition.toString(),
                DynamicCardEventKey.CardTitle to libraryCardViewData.cardMeta?.title?.textList?.getOrNull(0)?.text.orEmpty(),
                DynamicCardEventKey.CardDescription to libraryCardViewData.cardMeta?.description?.textList?.getOrNull(0)?.text.orEmpty(),
                DynamicCardEventKey.TopLabelTitle to libraryCardViewData.cardMeta?.labelTop?.text?.convertToRawString()
                    .toString(),
                DynamicCardEventKey.BottomLabelTitle to libraryCardViewData.cardMeta?.labelBottom?.text?.convertToRawString()
                    .toString(),
            ),
            fromSection = libraryCardViewData.header?.convertToRawString(),
            fromCard = libraryCardViewData.featureType
        )
    }

    private var visibilityState: Int? = null

    private var job: Job? = null

    private var shimmerLayout: ShimmerFrameLayout? = null

    override fun getBinding(view: View): FeatureHomepageCellTypeTwoBinding {
        return FeatureHomepageCellTypeTwoBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellTypeTwoBinding) {
        binding.root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
        shimmerLayout = binding.shimmerLayout

        when (libraryCardViewData.cardMeta?.infographic?.getInfographicType()) {
            InfographicType.GIF,
            InfographicType.IMAGE -> {
                Glide.with(binding.root).load(libraryCardViewData.cardMeta?.infographic!!.url)
                    .into(binding.ivStartIcon)
                binding.ivStartIcon.isVisible = true
                binding.ivStartIconLottie.isVisible = false
            }
            InfographicType.LOTTIE -> {
                binding.ivStartIconLottie.playLottieWithUrlAndExceptionHandling(
                    binding.root.context,
                    libraryCardViewData.cardMeta?.infographic!!.url
                )
                binding.ivStartIcon.isVisible = false
                binding.ivStartIconLottie.isVisible = true
            }
            else -> {
                //Do Nothing
            }
        }
        Glide.with(binding.root)
            .load(libraryCardViewData.cardMeta?.endIcon)
            .into(binding.ivIconEnd)
        val contextRef = WeakReference(binding.root.context)
        binding.tvHeader.text = libraryCardViewData.cardMeta?.title?.convertToString(contextRef)
        binding.tvDescription.text =
            libraryCardViewData.cardMeta?.description?.convertToString(contextRef)

        val data = PrimaryActionData(
            type = libraryCardViewData.cardMeta?.cta?.getPrimaryActionType()!!,
            value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
            order = libraryCardViewData.getSortKey(),
            cardType = libraryCardViewData.getCardType(),
            featureType = libraryCardViewData.featureType
        )

        val maxLines = if (libraryCardViewData.cardMeta?.labelBottom != null) 2 else 3

        binding.tvDescription.maxLines = maxLines

        updateLabelState(
            label = libraryCardViewData.cardMeta?.labelBottom,
            shouldShowLabel = true, //always show bottom label in all cards unless label is null
            tvLabel = binding.tvLabelBottom,
            card = binding.clCard,
            shouldTranslateCard = false, //No need to translate card for bottom label
            marginTop = 8.dp,
            rootView = binding.root,
            contextRef = contextRef
        )

        updateLabelState(
            label = libraryCardViewData.cardMeta?.labelTop,
            shouldShowLabel = libraryCardViewData.shouldShowLabelTop.orFalse(),
            tvLabel = binding.tvLabelTop,
            card = binding.clCard,
            shouldTranslateCard = libraryCardViewData.cardMeta?.hasTranslatedOnce?.not().orFalse(), // Translate in case of top label only
            marginTop = 8.dp,
            rootView = binding.root,
            contextRef = contextRef
        )

        libraryCardViewData.cardMeta?.hasTranslatedOnce = true

        binding.root.setDebounceClickListener {
            onActionClick.invoke(data, eventData)
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)

        this.visibilityState = visibilityState

        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            startShowEventJob(
                uiScope,
                isCardFullyVisible = {
                    this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                },
                onCardShownEvent = {
                    onCardShown.invoke(eventData)
                }
            )

            if (libraryCardViewData.cardMeta?.shouldRunShimmer.orFalse())
                startShimmer()
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            stopShimmer()
        } else if (visibilityState == VisibilityState.PARTIAL_IMPRESSION_INVISIBLE) {
            stopShimmer()
        }
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