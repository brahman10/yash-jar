package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeElevenCardBinding
import java.lang.ref.WeakReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class TypeElevenEpoxyModel(
    internal val libraryCardViewData: LibraryCardData,
    private val isSingleCard: Boolean,
    private val totalCards: Int,
    private val verticalPosition: Int,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellTypeElevenCardBinding>(R.layout.feature_homepage_cell_type_eleven_card) {

    private val eventData by lazy {
        CardEventData(
            map = mutableMapOf(
                DynamicCardEventKey.CardType to libraryCardViewData.cardType,
                DynamicCardEventKey.FeatureType to libraryCardViewData.featureType,
                DynamicCardEventKey.CardVerticalPosition to verticalPosition.toString(),
                DynamicCardEventKey.CardHorizontalPosition to libraryCardViewData.horizontalPosition.toString(),
                DynamicCardEventKey.NumberOfCards to totalCards.toString(),
                DynamicCardEventKey.CardTitle to libraryCardViewData.cardMeta?.description?.textList?.getOrNull(
                    0
                )?.text.orEmpty()
            ),
            fromSection = libraryCardViewData.header?.convertToRawString(),
            fromCard = libraryCardViewData.featureType
        )
    }

    private var job: Job? = null

    private var shimmerLayout: ShimmerFrameLayout? = null

    override fun getBinding(view: View): FeatureHomepageCellTypeElevenCardBinding {
        return FeatureHomepageCellTypeElevenCardBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellTypeElevenCardBinding) {
        val contextRef = WeakReference(binding.root.context)
        shimmerLayout = binding.shimmerPlaceholder

        libraryCardViewData.cardMeta?.cardBackground?.let {
            if (it.startColor != null && it.endColor != null) {
                val background = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(
                        Color.parseColor(it.startColor),
                        Color.parseColor(it.endColor),
                    )
                )
                background.cornerRadius =
                    libraryCardViewData.cardMeta?.cardBackground?.getCornerRadius(8.dp.toFloat())
                        .orZero()
                binding.cardView.background = background
            }
        }

        //overlay
        libraryCardViewData.cardMeta?.cardBackground?.overlayImage.let {
            Glide.with(binding.root).load(it)
                .into(binding.ivBackground)
        }

        //top label
        libraryCardViewData.cardMeta?.labelTop?.text?.let {
            binding.tvLabel.isVisible = true
            binding.tvLabel.text = it.convertToString(contextRef)
        } ?: kotlin.run {
            binding.tvLabel.isVisible = false
        }

        //infographic
        libraryCardViewData.cardMeta?.infographic?.url?.let {
            Glide.with(binding.root).load(it)
                .into(binding.infographic)
            binding.infographic.apply {
                isVisible = true
                updateLayoutParams { width = if (isSingleCard) 132.dp else 84.dp }
            }
        }

        //title and cta text
        libraryCardViewData.cardMeta?.description?.let {
            binding.tvTitle.isVisible = true
            binding.tvTitle.text = it.convertToString(contextRef)
        }
        libraryCardViewData.cardMeta?.cta?.text?.convertToString(contextRef)?.let {
            binding.btnCta.text = it
        }

        val data = PrimaryActionData(
            type = PrimaryActionType.DEEPLINK,
            value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
            order = libraryCardViewData.getSortKey(),
            cardType = libraryCardViewData.getCardType(),
            featureType = libraryCardViewData.featureType
        )
        binding.btnCta.setDebounceClickListener {
            onActionClick.invoke(data, eventData)
        }
        binding.cardView.setDebounceClickListener {
            onActionClick.invoke(data, eventData)
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            onCardShown.invoke(eventData)
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