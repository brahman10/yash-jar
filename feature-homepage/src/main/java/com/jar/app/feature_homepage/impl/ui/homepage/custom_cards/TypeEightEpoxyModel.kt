package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeEightCardBinding
import com.robinhood.ticker.TickerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

private var isAnimated = false


internal class TypeEightEpoxyModel(
    internal val libraryCardViewData: LibraryCardData,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellTypeEightCardBinding>(R.layout.feature_homepage_cell_type_eight_card) {

    private val eventData by lazy {
        CardEventData(
            map = mutableMapOf(
                DynamicCardEventKey.CardType to libraryCardViewData.cardType,
                DynamicCardEventKey.FeatureType to libraryCardViewData.featureType,
                DynamicCardEventKey.Data to libraryCardViewData.state.toString(),
                DynamicCardEventKey.CardVerticalPosition to libraryCardViewData.verticalPosition.toString(),
                DynamicCardEventKey.CardHorizontalPosition to libraryCardViewData.horizontalPosition.toString(),
                DynamicCardEventKey.CardTitle to libraryCardViewData.cardMeta?.title?.textList?.getOrNull(
                    0
                )?.toString().orEmpty(),
                DynamicCardEventKey.CardDescription to libraryCardViewData.cardMeta?.description?.textList?.getOrNull(
                    0
                )?.toString().orEmpty(),
                DynamicCardEventKey.TopLabelTitle to libraryCardViewData.cardMeta?.labelTop?.text?.convertToRawString()
                    .toString(),
                DynamicCardEventKey.BottomLabelTitle to libraryCardViewData.cardMeta?.labelBottom?.text?.convertToRawString()
                    .toString(),
            ),
            fromSection = libraryCardViewData.header?.convertToRawString(),
            fromCard = libraryCardViewData.featureType
        )
    }

    private var job: Job? = null

    private var shimmerLayout: ShimmerFrameLayout? = null

    override fun getBinding(view: View): FeatureHomepageCellTypeEightCardBinding {
        return FeatureHomepageCellTypeEightCardBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellTypeEightCardBinding) {
        val contextRef = WeakReference(binding.root.context)
        shimmerLayout = binding.shimmerPlaceholder

        val background = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor(libraryCardViewData.cardMeta?.cardBackground?.startColor),
                Color.parseColor(libraryCardViewData.cardMeta?.cardBackground?.endColor),
            )
        )
        background.cornerRadius = libraryCardViewData.cardMeta?.cardBackground?.getCornerRadius(8.dp.toFloat()).orZero()

        binding.clCard.background = background

        libraryCardViewData.cardMeta?.cardBackground?.overlayImage.let {
            Glide.with(binding.root).load(it)
                .into(binding.ivBackground)
        }

        //top label
        libraryCardViewData.cardMeta?.labelTop?.text?.let {
            binding.tvTopLabel.isVisible = true
            binding.tvTopLabel.text = it.convertToString(contextRef)
        } ?: kotlin.run {
            binding.tvTopLabel.isVisible = false
        }
        //infographic
        libraryCardViewData.cardMeta?.infographic?.url?.let {
            when (libraryCardViewData.cardMeta?.infographic?.getInfographicType()) {
                InfographicType.GIF,
                InfographicType.IMAGE -> {
                    Glide.with(binding.root).load(it)
                        .into(binding.ivCardImage)
                    binding.ivCardImage.isVisible = true
                    binding.lottieView.isVisible = false
                }
                InfographicType.LOTTIE -> {
                    isAnimated = true
                    binding.lottieView.playLottieWithUrlAndExceptionHandling(
                        binding.root.context,
                        it
                    )
                    binding.lottieView.playAnimation()
                    binding.ivCardImage.isVisible = false
                    binding.lottieView.isVisible = true
                }
                else -> {
                    //Do Nothing
                }
            }
        }
        val data = PrimaryActionData(
            type = PrimaryActionType.DEEPLINK,
            value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
            order = libraryCardViewData.getSortKey(),
            cardType = libraryCardViewData.getCardType(),
            featureType = libraryCardViewData.featureType
        )



        //title and cta text
        libraryCardViewData.cardMeta?.title?.let {
            binding.tvHeader.isVisible = true
            binding.tvHeader.text = it.convertToString(contextRef)
        }
        binding.tvDescription.text = libraryCardViewData.cardMeta?.description?.convertToString(contextRef)
        libraryCardViewData.cardMeta?.cta?.text?.convertToString(contextRef)?.let {
            binding.btnGetCash.setText(it)
        }
        binding.btnGetCash.setDebounceClickListener {
            onActionClick.invoke(data, eventData)
        }
        binding.clCard.setDebounceClickListener {
            onActionClick.invoke(data, eventData)
        }
        //Footer labels
        binding.llFooterHolder.removeAllViews()
        libraryCardViewData.cardMeta?.textListFooter?.textList?.let { textList->
            binding.llFooterHolder.visibility = View.VISIBLE
            addViewsToLinearLayout(textList = textList,context=binding.root.context,footerLayout=binding.llFooterHolder)
        }?:run{
            binding.llFooterHolder.visibility = View.GONE
        }}

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

    private fun addViewsToLinearLayout(textList: List<TextData>, context: Context, footerLayout: LinearLayoutCompat) {
        footerLayout.removeAllViews()
        val requestOptions = RequestOptions.centerInsideTransform()
        val horizontalLayout = LinearLayout(context)
        footerLayout.weightSum = textList.size.toFloat()
        textList.forEachIndexed { index, textData ->
            horizontalLayout.orientation = LinearLayout.HORIZONTAL
            horizontalLayout.gravity = Gravity.CENTER
            horizontalLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            val imageView = ImageView(context)
            Glide.with(context)
                .load(textData.textIcon)
                .apply(requestOptions)
                .into(imageView)

            val imageLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            imageLayoutParams.marginEnd = 6.dp

            val textView = TextView(context)
            textView.text = textData.text
            textView.textSize = 10f
            val textParams =  LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)
            horizontalLayout.addView(imageView, imageLayoutParams)
            horizontalLayout.addView(textView,textParams)
        }
        footerLayout.addView(horizontalLayout)
    }

}