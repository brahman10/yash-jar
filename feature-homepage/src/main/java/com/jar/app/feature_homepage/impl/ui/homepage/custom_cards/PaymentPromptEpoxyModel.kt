package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.VisibilityState
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.extension.attachSnapHelperWithListener
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.vibrate
import com.jar.app.core_ui.listener.OnSnapPositionChangeListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.widget.carousal_layout_manager.CarouselLayoutManager
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellManualPaymentPromptBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.impl.ui.payment_prompt.ManualPaymentAmountSuggestionAdapter
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.shared.domain.model.payment_prompt.PaymentPromptData
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

internal class PaymentPromptEpoxyModel(
    private val uiScope: CoroutineScope,
    private val paymentPromptData: PaymentPromptData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onInvestClick: (amount: Float, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellManualPaymentPromptBinding>(R.layout.feature_homepage_cell_manual_payment_prompt) {

    companion object {
        private const val DEFAULT_AMOUNT = 51f
    }
    private var visibilityState: Int? = null


    private var job: Job? = null

    private var shimmerLayout: ShimmerFrameLayout? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to paymentPromptData.cardType,
                DynamicCardEventKey.FeatureType to paymentPromptData.featureType
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageCellManualPaymentPromptBinding) {
        shimmerLayout = binding.shimmerLayout
        binding.root.setPlotlineViewTag(tag = paymentPromptData.featureType)
        val layoutManager =
            CarouselLayoutManager(
                binding.root.context,
                0.dp,
                RecyclerView.HORIZONTAL,
                false
            )

        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                binding.root.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else
            binding.root.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        binding.tvTitle.text = paymentPromptData.investPromptTitle
            ?: binding.root.context.getString(R.string.feature_homepage_turn_your_saving_into_gold)

        val adapter = ManualPaymentAmountSuggestionAdapter { _, pos ->
            val currentCenter = layoutManager.currentPosition.orZero()
            val centerOfScreen = when {
                pos < currentCenter -> currentCenter - 1
                pos > currentCenter -> currentCenter + 1
                else -> currentCenter
            }
            binding.rvSuggestedAmount.vibrate(vibrator)
            binding.rvSuggestedAmount.smoothScrollToPosition(centerOfScreen)
        }

        binding.rvSuggestedAmount.layoutManager = layoutManager
        binding.rvSuggestedAmount.adapter = adapter

        adapter.submitList(paymentPromptData.investPromptSuggestions)

        adapter.currentList.indexOfFirst { it.recommended.orFalse() }.let {
            binding.rvSuggestedAmount.post {
                binding.rvSuggestedAmount.smoothScrollToPosition(it)
            }
        }

        if (binding.rvSuggestedAmount.onFlingListener == null) {
            binding.rvSuggestedAmount.post {
                binding.rvSuggestedAmount.attachSnapHelperWithListener(
                    LinearSnapHelper(),
                    onSnapPositionChangeListener = object :
                        OnSnapPositionChangeListener {
                        override fun onSnapPositionChange(position: Int) {
                            binding.root.vibrate(vibrator)
                        }
                    })
            }
        }

        binding.btnManualPayPrompt.setDebounceClickListener {
            layoutManager.currentPosition.let {
                val amount = adapter.currentList.get(it)?.amount ?: DEFAULT_AMOUNT
                val eventData = CardEventData(
                    mutableMapOf(
                        DynamicCardEventKey.CardType to paymentPromptData.cardType,
                        DynamicCardEventKey.FeatureType to paymentPromptData.featureType,
                        DynamicCardEventKey.Data to amount.toString()
                    )
                )
                onInvestClick.invoke(amount, eventData)
            }
        }

        binding.btnManualPayPrompt.setText(
            paymentPromptData.cta?.text?.convertToString(
                WeakReference(binding.root.context)
            )!!
        )

        binding.ivFlowerDecoration.isVisible = false
        binding.ivBgRangoli.isVisible = false
        binding.tvTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = 24.dp
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
            if (paymentPromptData.shouldRunShimmer)
                startShimmer()
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            stopShimmer()
        } else if (visibilityState == VisibilityState.PARTIAL_IMPRESSION_INVISIBLE) {
            stopShimmer()
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellManualPaymentPromptBinding {
        return FeatureHomepageCellManualPaymentPromptBinding.bind(view)
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