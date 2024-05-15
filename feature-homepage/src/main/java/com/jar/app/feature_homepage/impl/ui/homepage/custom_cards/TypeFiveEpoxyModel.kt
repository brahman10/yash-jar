package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.*
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_daily_investment.shared.domain.model.Steps
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeFiveBinding
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.lang.ref.WeakReference

internal class TypeFiveEpoxyModel(
    private val libraryCardViewData: LibraryCardData,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : CustomViewBindingEpoxyModel<FeatureHomepageCellTypeFiveBinding>(R.layout.feature_homepage_cell_type_five),
    HomeFeedCard {

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

    override fun getBinding(view: View): FeatureHomepageCellTypeFiveBinding {
        return FeatureHomepageCellTypeFiveBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellTypeFiveBinding) {
        binding.clBottomView.removeAllViews()
        val contextRef = WeakReference(binding.root.context)
        binding.root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
        binding.tvHeader.text = libraryCardViewData.cardMeta?.title?.convertToString(contextRef)
        binding.tvDescription.text = libraryCardViewData.cardMeta?.description?.convertToString(contextRef)
        Glide.with(binding.root).load(libraryCardViewData.cardMeta?.endIcon)
            .into(binding.ivEndIcon)
        Glide.with(binding.root).load(libraryCardViewData.cardMeta?.infographic?.url)
            .into(binding.ivCardImage)

        libraryCardViewData.cardMeta?.cardBackground?.let {
            val background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor(it.startColor),
                    Color.parseColor(it.endColor),
                )
            )
            background.cornerRadius = it.getCornerRadius(8f).dp
            binding.root.background = background
        }

        libraryCardViewData.cardMeta?.textListFooter?.textList?.forEach { it ->
            val footerView = TypeFiveEpoxyModelFooterView(binding.root.context)
            if(libraryCardViewData.cardMeta?.textListFooter!!.textList.size == 1){
                binding.clBottomView.weightSum = 1f
                footerView.changeLayoutParamsFooterImage()
            }else{
                binding.clBottomView.weightSum = 3f
            }
            footerView.setData(Steps(it.text,it.textIcon))
            val layoutParams = LinearLayoutCompat.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            binding.clBottomView.addView(footerView, layoutParams)
        }

        val data = PrimaryActionData(
            type = PrimaryActionType.DEEPLINK,
            value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
            order = libraryCardViewData.getSortKey(),
            cardType = libraryCardViewData.getCardType(),
            featureType = libraryCardViewData.featureType,
            data = JsonPrimitive(libraryCardViewData.data?.jsonPrimitive?.floatOrNull.orZero())
        )

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