package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellDetectedSpendBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.shared.domain.model.detected_spends.DetectedSpendData
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

internal class DetectedSpendCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val detectedSpendData: DetectedSpendData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onInvestClick: (cardEventData: CardEventData) -> Unit,
    private val onAmountClick: () -> Unit,
    private val onPartPaymentClick: () -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellDetectedSpendBinding>(R.layout.feature_homepage_cell_detected_spend) {

    private var job: Job? = null
    private var visibilityState: Int? = null

    private var shimmerLayout: ShimmerFrameLayout? = null

    private val eventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to detectedSpendData.cardType,
                DynamicCardEventKey.FeatureType to detectedSpendData.featureType,
                DynamicCardEventKey.Data to detectedSpendData.detectedSpendsData.fullPaymentInfo?.txnAmt.toString()
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageCellDetectedSpendBinding) {
        this@DetectedSpendCardEpoxyModel.shimmerLayout = binding.shimmerLayout
        binding.root.setPlotlineViewTag(tag = detectedSpendData.featureType)
        binding.btnAction.setPlotlineViewTag(tag = "${detectedSpendData.featureType}Cta")

        binding.btnAction.setDebounceClickListener {
            onInvestClick.invoke(eventData)
        }

        binding.amount.setDebounceClickListener {
            onAmountClick.invoke()
        }

        binding.partPaymentTapHere.setDebounceClickListener {
            onPartPaymentClick.invoke()
        }

        binding.btnAction.setText(detectedSpendData.cta?.text?.convertToString(WeakReference(binding.root.context))!!)

        Glide.with(binding.root)
            .asDrawable()
            .load(detectedSpendData.cta?.icon)
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

        binding.amount.text =
            binding.root.context.getString(
                R.string.feature_homepage_rupee_x_in_double,
                detectedSpendData.detectedSpendsData.fullPaymentInfo?.txnAmt
            )

        binding.heading.text = detectedSpendData.detectedSpendsData.fullPaymentInfo?.title

        binding.heading.isVisible =
            !detectedSpendData.detectedSpendsData.fullPaymentInfo?.title.isNullOrBlank()

        binding.line.isVisible = detectedSpendData.detectedSpendsData.partPaymentInfo != null

        binding.textDontWantToInvestFullAmount.isVisible =
            detectedSpendData.detectedSpendsData.partPaymentInfo != null

        binding.partPaymentTapHere.isVisible =
            detectedSpendData.detectedSpendsData.partPaymentInfo != null

        binding.ivHeaderFlowerDecoration.isVisible = false

        binding.btnAction.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginEnd = 20.dp
        }
        binding.amount.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginStart = 22.dp
            topMargin = 16.dp
        }
    }

    private fun startShimmerAndShake() {
        job?.cancel()
        job = uiScope.launch(Dispatchers.Main) {
            shimmerLayout?.startShimmer()
            if (isActive) {
                delay(1000)
                if (isActive) {
                    shimmerLayout?.stopShimmer()
                    shimmerLayout?.startAnimation(
                        AnimationUtils.loadAnimation(
                            shimmerLayout?.context,
                            R.anim.feature_homepage_shake
                        )
                    )
                }
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
            if (detectedSpendData.shouldRunShimmer)
                startShimmerAndShake()
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            stopShimmerAnimation()
        } else if (visibilityState == VisibilityState.PARTIAL_IMPRESSION_INVISIBLE) {
            stopShimmerAnimation()
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellDetectedSpendBinding {
        return FeatureHomepageCellDetectedSpendBinding.bind(view)
    }
}