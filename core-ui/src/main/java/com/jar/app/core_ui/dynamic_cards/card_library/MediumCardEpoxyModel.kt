package com.jar.app.core_ui.dynamic_cards.card_library

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.DynamicCardsCellMediumCardBinding
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.*
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class MediumCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val libraryCardViewData: LibraryCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    private val onEndIconClick: (staticInfoData: StaticInfoData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null,
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<DynamicCardsCellMediumCardBinding>(R.layout.dynamic_cards_cell_medium_card) {

    private var job: Job? = null
    private var visibilityState: Int? = null

    private var shimmerLayout: ShimmerFrameLayout? = null

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

    @SuppressLint("ClickableViewAccessibility")
    override fun bindItem(binding: DynamicCardsCellMediumCardBinding) {
        this@MediumCardEpoxyModel.shimmerLayout = binding.shimmerLayout
        val endIconCta = libraryCardViewData.staticInfoData
        binding.root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
        endIconCta?.deeplink = libraryCardViewData.cardMeta?.cta?.deepLink
        if (endIconCta?.value != null) {
            binding.ivEndIcon.setDebounceClickListener {
                onEndIconClick.invoke(endIconCta, eventData)
            }
        }

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

        val background = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor(libraryCardViewData.cardMeta?.cardBackground?.startColor),
                Color.parseColor(libraryCardViewData.cardMeta?.cardBackground?.endColor),
            )
        )
        background.cornerRadius = libraryCardViewData.cardMeta?.cardBackground?.getCornerRadius()?.dp.orZero()

        binding.root.background = background

        Glide.with(binding.root).load(libraryCardViewData.cardMeta?.startIcon)
            .into(binding.ivStartIcon)
        Glide.with(binding.root).load(libraryCardViewData.cardMeta?.endIcon).into(binding.ivEndIcon)
        when (libraryCardViewData.cardMeta?.infographic?.getInfographicType()) {
            InfographicType.GIF,
            InfographicType.IMAGE -> {
                binding.ivInfoGraphicLottie.isVisible = false
                binding.ivInfoGraphicImage.isVisible = true
                Glide.with(binding.root).load(libraryCardViewData.cardMeta?.infographic?.url)
                    .into(binding.ivInfoGraphicImage)
            }
            InfographicType.LOTTIE -> {
                binding.ivInfoGraphicImage.isVisible = false
                binding.ivInfoGraphicLottie.isVisible = true
                binding.ivInfoGraphicLottie.playLottieWithUrlAndExceptionHandling(
                    binding.root.context,
                    libraryCardViewData.cardMeta?.infographic?.url!!
                )
                binding.ivInfoGraphicLottie.playAnimation()
            }
            else -> {
                //Do Nothing
            }
        }
        Glide.with(binding.root).load(libraryCardViewData.cardMeta?.cardBackground?.overlayImage)
            .into(binding.ivOverlayImage)
        val contextRef = WeakReference(binding.root.context)
        binding.tvTitle.text = libraryCardViewData.cardMeta?.title?.convertToString(contextRef)
        binding.tvDescription.text =
            libraryCardViewData.cardMeta?.description?.convertToString(contextRef)
        libraryCardViewData.cardMeta?.cta?.text?.convertToString(contextRef)
            ?.let { binding.btnAction.setText(it) }

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

        if (libraryCardViewData.cardMeta?.startIcon.isNullOrEmpty()) {
            binding.tvTitle.setPadding(24)
            binding.ivEndIcon.setPadding(20)
        }

        if (libraryCardViewData.cardMeta?.footer.isNullOrEmpty().not()) {
            binding.footerImages.isVisible = true
            if (binding.footerImages.childCount == 0) {
                libraryCardViewData.cardMeta?.footer?.forEach {
                    Glide.with(binding.root)
                        .asBitmap()
                        .load(it).into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val imageView = AppCompatImageView(binding.footerImages.context)
                                imageView.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                )
                                binding.footerImages.addView(imageView)
                                imageView.setImageBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                }
            }
        } else {
            binding.footerImages.isVisible = false
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

    override fun getBinding(view: View): DynamicCardsCellMediumCardBinding {
        return DynamicCardsCellMediumCardBinding.bind(view)
    }
}