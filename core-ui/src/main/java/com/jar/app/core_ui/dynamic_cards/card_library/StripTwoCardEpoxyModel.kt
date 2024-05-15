package com.jar.app.core_ui.dynamic_cards.card_library

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.DynamicCardsCellStripTwoBinding
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.*
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class StripTwoCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val libraryCardViewData: LibraryCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null,
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<DynamicCardsCellStripTwoBinding>(R.layout.dynamic_cards_cell_strip_two) {

    private var job: Job? = null

    private var shimmerLayout: ShimmerFrameLayout? = null
    private var visibilityState: Int? = null

    private val eventData by lazy {
        CardEventData(
            map = mutableMapOf(
                DynamicCardEventKey.CardType to libraryCardViewData.cardType,
                DynamicCardEventKey.FeatureType to libraryCardViewData.featureType,
                DynamicCardEventKey.Data to libraryCardViewData.state.toString(),
                DynamicCardEventKey.CardVerticalPosition to libraryCardViewData.verticalPosition.toString(),
                DynamicCardEventKey.CardHorizontalPosition to libraryCardViewData.horizontalPosition.toString(),
                DynamicCardEventKey.CardTitle to libraryCardViewData.cardMeta?.title?.textList?.getOrNull(0)?.toString().orEmpty(),
                DynamicCardEventKey.CardDescription to libraryCardViewData.cardMeta?.description?.textList?.getOrNull(0)?.toString().orEmpty(),
                DynamicCardEventKey.TopLabelTitle to libraryCardViewData.cardMeta?.labelTop?.text?.convertToRawString()
                    .toString(),
                DynamicCardEventKey.BottomLabelTitle to libraryCardViewData.cardMeta?.labelBottom?.text?.convertToRawString()
                    .toString(),
            ),
            fromSection = libraryCardViewData.header?.convertToRawString(),
            fromCard = libraryCardViewData.featureType
        )
    }

    override fun bindItem(binding: DynamicCardsCellStripTwoBinding) {
        this@StripTwoCardEpoxyModel.shimmerLayout = binding.shimmerLayout
        binding.root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
        val background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor(libraryCardViewData.cardMeta?.cardBackground?.startColor),
                Color.parseColor(libraryCardViewData.cardMeta?.cardBackground?.endColor),
            )
        )
        background.cornerRadius = libraryCardViewData.cardMeta?.cardBackground?.getCornerRadius()?.dp.orZero()

        binding.root.background = background

        val contextRef = WeakReference(binding.root.context)
        binding.tvTitle.text = libraryCardViewData.cardMeta?.title?.convertToString(contextRef)
        binding.tvDescription.text =
            libraryCardViewData.cardMeta?.description?.convertToString(contextRef)

        Glide.with(binding.root)
            .asDrawable()
            .load(libraryCardViewData.cardMeta?.cta?.icon)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    binding.btnAction.setCompoundDrawablesRelativeWithIntrinsicBounds(icon = resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
        libraryCardViewData.cardMeta?.cardBackground?.overlayImage?.let {
            Glide.with(binding.root).load(it)
                .into(binding.ivOverlayImage)
        }
        libraryCardViewData.cardMeta?.cta?.text?.convertToString(contextRef)
            ?.let { binding.btnAction.setText(it) }

        binding.btnAction.setDebounceClickListener {
            val data = PrimaryActionData(
                type = PrimaryActionType.DEEPLINK,
                value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
                order = libraryCardViewData.getSortKey(),
                cardType = libraryCardViewData.getCardType(),
                featureType = libraryCardViewData.featureType
            )
            onActionClick.invoke(data, eventData)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startShimmerAndShake() {
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Main) {
            delay(1000)
            if (isActive) {
                shimmerLayout?.startShimmer()
                delay(2000)
                shimmerLayout?.stopShimmer()
            }
        }
    }

    private fun stopShimmerAnimation() {
        shimmerLayout?.stopShimmer()
        shimmerLayout?.hideShimmer()
        shimmerLayout?.clearAnimation()
        job?.cancel()
        shimmerLayout = null
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

            if (libraryCardViewData.cardMeta?.shouldRunShimmer.orFalse())
                startShimmerAndShake()
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            stopShimmerAnimation()
        } else if (visibilityState == VisibilityState.PARTIAL_IMPRESSION_INVISIBLE) {
            stopShimmerAnimation()
        }
    }

    override fun getBinding(view: View): DynamicCardsCellStripTwoBinding {
        return DynamicCardsCellStripTwoBinding.bind(view)
    }
}