package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageReadyCashCardBinding
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.ready_cash.ReadyCashViewPagerAdapter
import com.jar.app.feature_homepage.shared.domain.model.RecommendedHomeCardData
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.random.Random

private var isAnimated = false

internal class LendingReadyCashCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val lendingCardData: RecommendedHomeCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageReadyCashCardBinding>(
        R.layout.feature_homepage_ready_cash_card
    ) {
    private var viewBinding: FeatureHomepageReadyCashCardBinding? = null
    private val autoScrollListener = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            autoScrollJob = uiScope.launch(Dispatchers.Main.immediate) {
                delay(5000)
                viewBinding?.footerCarousel?.currentItem = position + 1
            }
        }
    }

    private var visibilityState: Int? = null

    private var job: Job? = null
    private var animationJob: Job? = null
    private var tickerView: TickerView? = null
    private var offerAmount = ""
    private var randomAmount = ""
    private var autoScrollJob: Job? = null

    private var shimmerLayout: ShimmerFrameLayout? = null
    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to lendingCardData.cardType,
                DynamicCardEventKey.FeatureType to lendingCardData.featureType,
            )
        )
    }
    private val actionData by lazy {
        PrimaryActionData(
            type = PrimaryActionType.DEEPLINK,
            value = lendingCardData.cardData?.cta?.deepLink.orEmpty(),
            order = lendingCardData.getSortKey(),
            cardType = lendingCardData.getCardType(),
            featureType = lendingCardData.featureType
        )
    }

    /**
     * this returns ViewBinding
     */
    override fun getBinding(view: View): FeatureHomepageReadyCashCardBinding {
        return FeatureHomepageReadyCashCardBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageReadyCashCardBinding) {
        viewBinding = binding
        val contextRef = WeakReference(binding.root.context)
        binding.root.setPlotlineViewTag(lendingCardData.featureType)
        shimmerLayout = binding.shimmerPlaceholder
        tickerView = binding.tvOfferAmount
        binding.tvOfferAmount.setCharacterLists(TickerUtils.provideNumberList())
        lendingCardData.offerAmount?.let { amount ->
            binding.tvOfferAmount.isVisible = true
            offerAmount = binding.root.context.getString(
                R.string.rupee_x_in_string,
                amount.getFormattedAmount()
            )
            randomAmount = offerAmount
                .toCharArray()
                .map { if (it.isDigit()) "${Random.nextInt(2, 9)}" else it }
                .joinToString("")

            binding.tvOfferAmount.setPreferredScrollingDirection(TickerView.ScrollingDirection.UP)
            binding.tvOfferAmount.animationDuration = 1000L
            binding.tvOfferAmount.animationDelay = 0L
            binding.tvOfferAmount.animationInterpolator = LinearInterpolator()
            binding.tvOfferAmount.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    binding.buttonShimmer.startShimmer()
                }
            })
        } ?: kotlin.run {
            isAnimated = true
            binding.tvOfferAmount.isVisible = false
        }

        //background
        val background = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor(lendingCardData.cardData?.cardBackground?.startColor),
                Color.parseColor(lendingCardData.cardData?.cardBackground?.endColor),
            )
        )
        background.cornerRadius =
            lendingCardData.cardData?.cardBackground?.getCornerRadius(default = 8f)?.dp.orZero()
        binding.clCard.background = background
        //top label
        lendingCardData.cardData?.labelTop?.text?.let {
            binding.tvTopLabel.isVisible = true
            binding.tvTopLabel.text = it.convertToString(contextRef)
        } ?: kotlin.run {
            binding.tvTopLabel.isVisible = false
        }
        //infographic
        lendingCardData.cardData?.infographic?.url?.let {
            when (lendingCardData.cardData?.infographic?.getInfographicType()) {
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

        //title and cta text
        binding.tvYouAreEligible.text = lendingCardData.cardData?.title?.convertToString(contextRef)
        lendingCardData.cardData?.cta?.text?.convertToString(contextRef)?.let {
            binding.btnGetCash.setText(it)
        }
        binding.btnGetCash.setDebounceClickListener {
            onCtaClick.invoke(actionData, cardEventData)
        }
        binding.clCard.setDebounceClickListener {
            onCtaClick.invoke(actionData, cardEventData)
        }
        //Footer labels
        binding.llFooterHolder.removeAllViews()
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        lendingCardData.cardData?.textListFooter?.textList?.let { textList ->
            binding.llFooterHolder.visibility = View.VISIBLE
            addViewsToLinearLayout(
                textList = textList,
                context = binding.root.context,
                footerLayout = binding.llFooterHolder
            )
        } ?: run {
            binding.llFooterHolder.visibility = View.GONE
        }

        lendingCardData.cardData?.footerCarousel?.let { cardData ->
            binding.footerCarousel.apply{
                visibility = View.VISIBLE
                isUserInputEnabled = false
                val mAdapter = cardData.slides?.let { ReadyCashViewPagerAdapter(it) }
                adapter =  mAdapter
                autoScrollJob?.cancel()
                if(cardData.slides?.size != 1){
                    registerOnPageChangeCallback(autoScrollListener)
                }
            }
        } ?: run {
            binding.footerCarousel.visibility = View.GONE
        }

    }

    override fun onViewDetachedFromWindow(view: View) {
        super.onViewDetachedFromWindow(view)
        viewBinding?.footerCarousel?.unregisterOnPageChangeCallback(autoScrollListener)
        autoScrollJob?.cancel()
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            this.visibilityState = visibilityState
            if (!isAnimated) {
                animationJob?.cancel()
                animationJob = uiScope.launch(Dispatchers.Main.immediate) {
                    tickerView?.text =
                        offerAmount.toCharArray().map { if (it.isDigit()) "0" else it }
                            .joinToString("")
                    delay(700)
                    tickerView?.text = randomAmount
                    tickerView?.text = offerAmount
                    isAnimated = true
                }
            } else {
                if (offerAmount.isNotEmpty())
                    tickerView?.text = offerAmount
            }
            startShowEventJob(
                uiScope,
                isCardFullyVisible = {
                    this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                },
                onCardShownEvent = {
                    onCardShown.invoke(cardEventData)
                }
            )

        }
    }

    private fun addViewsToLinearLayout(
        textList: List<TextData>,
        context: Context,
        footerLayout: LinearLayoutCompat
    ) {
        footerLayout.removeAllViews()
        val requestOptions = RequestOptions.centerInsideTransform()
        textList.forEachIndexed { index, textData ->
            val horizontalLayout = LinearLayout(context)
            horizontalLayout.orientation = LinearLayout.HORIZONTAL
            horizontalLayout.gravity = Gravity.CENTER_VERTICAL

            val imageView = ImageView(context).apply {
                minimumWidth = 12.dp
                minimumHeight = 12.dp
            }
            Glide.with(context)
                .load(textData.textIcon)
                .apply(requestOptions)
                .into(imageView)


            val imageLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            imageLayoutParams.marginEnd = 6.dp
            if (index != 0) {
                imageLayoutParams.marginStart = 16.dp
            }


            val textView = AppCompatTextView(context)
            textView.textSize = 10f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(com.jar.app.core_ui.R.style.USPTextViewStyle)
            } else {
                textView.setTextAppearance(
                    context,
                    com.jar.app.core_ui.R.style.USPTextViewStyle
                )
            }
            textView.text = textData.text

            horizontalLayout.addView(imageView, imageLayoutParams)
            horizontalLayout.addView(textView)
            footerLayout.addView(horizontalLayout)
        }
    }
}