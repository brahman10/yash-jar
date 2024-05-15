package com.jar.gold_price_alerts.impl.ui.gold_price_detail

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointD
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.event.UpdateBuyGoldPriceInToolbarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.countDownTimer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.getHtmlTextValue
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.setTextAnimation
import com.jar.app.core_ui.extension.slideRightToReveal
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_utils.data.convertDateFormat
import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2Api
import com.jar.app.feature_gold_price_alerts.R
import com.jar.app.feature_gold_price_alerts.databinding.FragmentGoldPriceDetailBinding
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrend
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendAlertStatus
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendScreenStaticData
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendTopRibbon
import com.jar.feature_gold_price_alerts.shared.domain.model.SaveGoldCtaAnimType
import com.jar.feature_gold_price_alerts.shared.domain.model.SavingsCard
import com.jar.feature_gold_price_alerts.shared.domain.model.SavingsCardFooterTable
import com.jar.feature_gold_price_alerts.shared.domain.model.TableData
import com.jar.feature_gold_price_alerts.shared.domain.model.Timeframe
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.ActiveAlertsExists
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.Lowest_Price_Nudge
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.cardFlowType
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.flowContext
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.Constants.UPDATE_STATE_FROM_BOTTOMSHEET
import com.jar.gold_price_alerts.impl.util.animateToZero
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.cachapa.expandablelayout.ExpandableLayout
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@AndroidEntryPoint
internal class GoldPriceDetailFragment : BaseFragment<FragmentGoldPriceDetailBinding>() {

    @Inject
    lateinit var buyGoldApi: BuyGoldV2Api

    @Inject
    lateinit var analyticsApi: AnalyticsApi
    var showRippleImageView: Job? = null
    var animator: Animator? = null

    private var alertCtaIconAnimation: ObjectAnimator? = null

    private var alertIconRepeatAnimationJob: Job? = null
    private var alertIconDelayJob: Job? = null
    private var animationJob: Job? = null

    private val alertCtaIconAnimationListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {}
        override fun onAnimationCancel(p0: Animator) {
            binding.cardGraph.setAlertBtnIv.rotation = 0f
            binding.cardGraph.setAlertBtnIv.alpha = 1f
        }
        override fun onAnimationRepeat(p0: Animator) {}
        override fun onAnimationEnd(p0: Animator) {
            binding.cardGraph.setAlertBtnIv.rotation = 0f
            binding.cardGraph.setAlertBtnIv.alpha = 1f
        }
    }

    companion object {
        const val DEFAULT_TOP_RIBBON_START_COLOR = "#2C915B"
        const val DEFAULT_TOP_RIBBON_END_COLOR = "#5FAB82"
        const val ALERT_ICON_ANIMATION_INTERVAL = 15000L
        const val ALERT_ANIMATION_START_DELAY = 1000L
        const val ALERT_EXPANDED_TEXT_ANIMATION = 300L
        const val ALERT_SLIDE_ANIMATION = 500L
    }

    private val args by navArgs<GoldPriceDetailFragmentArgs>()
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldPriceDetailBinding
        get() = FragmentGoldPriceDetailBinding::inflate

    private val viewModelProvider by viewModels<GoldPriceDetailFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override fun setupAppBar() {
        EventBus.getDefault()
            .post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    fun getData() {
        viewModel.fetchStaticScreenData()
    }

    private fun setupUI() {
        setupGraph()
    }

    private fun setupGraph() {
        // force pinch zoom along both axis
        binding.cardGraph.lineChart.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                binding.lockableScrollView.setScrollingEnabled(false)
            }

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                binding.lockableScrollView.setScrollingEnabled(true)
                showRippleAfter5(true)
            }

            override fun onChartLongPressed(me: MotionEvent?) {
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
                viewModel.postGraphClicked()
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {

            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
            }

        }
        binding.cardGraph.lineChart.xAxis.apply {
            setDrawLimitLinesBehindData(false)
            setDrawAxisLine(false)
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawGridLinesBehindData(false)
        }
        binding.cardGraph.lineChart.axisLeft.apply {
            setDrawTopYLabelEntry(false)
            setDrawLimitLinesBehindData(true)
            setDrawZeroLine(false)
            setDrawAxisLine(false)
            setDrawLabels(false)

            setDrawGridLines(false)
            setDrawGridLinesBehindData(true)
            granularity = 1f
        }
        binding.cardGraph.lineChart.axisRight.apply {
            setDrawTopYLabelEntry(false)
            setDrawLimitLinesBehindData(false)
            setDrawZeroLine(false)
            setDrawAxisLine(false)
            setDrawLabels(false)

            setDrawGridLines(false)
            setDrawGridLinesBehindData(false)
            granularity = 0f
        }
        binding.cardGraph.lineChart.setScaleEnabled(false)
        binding.cardGraph.lineChart.isDragEnabled = false
        binding.cardGraph.lineChart.setTouchEnabled(false)
        binding.cardGraph.lineChart.isDoubleTapToZoomEnabled = false
        binding.cardGraph.lineChart.description.isEnabled = false
        binding.cardGraph.lineChart.legend.isEnabled = false
        binding.cardGraph.lineChart.setDrawBorders(false)
        binding.cardGraph.lineChart.setDrawGridBackground(false)
        binding.cardGraph.lineChart.setTouchEnabled(true)
        binding.cardGraph.lineChart.isHighlightPerTapEnabled = true
        binding.cardGraph.lineChart.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                // Calculate position
                onGraphDataSelected(e)
            }

            override fun onNothingSelected() {
                // Hide the view when nothing is selected
//                binding.cardGraph.imageView.visibility = View.INVISIBLE
            }
        })

        binding.cardGraph.lineChart.getDescription().setEnabled(false)

        binding.cardGraph.lineChart.setTouchEnabled(true)

        binding.cardGraph.lineChart.setDragEnabled(true)

        binding.cardGraph.lineChart.setScaleEnabled(true)

        binding.cardGraph.lineChart.setPinchZoom(false)
    }

    private fun setAverageBuyPriceLineToGraph(amount: Float) {
        binding.cardGraph.tvPriceToday.isInvisible = amount == 0f
        binding.cardGraph.tvCurrentByPrice.isInvisible = amount == 0f
        binding.cardGraph.tvPriceToday.text = getString(
            R.string.feature_gold_price_alerts_rupee_prefix_string,
            amount.getFormattedAmount()
        )
    }

    private fun showRipple(pos: MPPointD, useDelay: Boolean = true) {
        // subtracting the half size of ripple background to make sure it aligns to the center of cords
        binding.cardGraph.rippleBackgroundContainer.x = pos.x.toFloat() - Utils.convertDpToPixel(25f)
        // Adding the offset of lineChart from the top of view so that it aligns perfectly
        binding.cardGraph.rippleBackgroundContainer.y = pos.y.toFloat() + binding.cardGraph.lineChart.y - Utils.convertDpToPixel(25f)

        binding.cardGraph.rippleBackgroundContainer.pivotX = Utils.convertDpToPixel(50f)
        binding.cardGraph.rippleBackgroundContainer.pivotY = Utils.convertDpToPixel(50f)

        binding.cardGraph.imageView.isVisible = true
        binding.cardGraph.storyRippleLayout.stopRippleAnimation()
        binding.cardGraph.storyRippleLayout.alpha = 0f
        binding.cardGraph.blurredMoon.scaleY = 1f
        binding.cardGraph.blurredMoon.scaleX = 1f

        if (binding.lockableScrollView.isScrollable()) {
            showRippleAfter5(useDelay)
        }
    }

    private fun  showRippleAfter5(useDelay: Boolean) {
        uiScope.launch {
            if (useDelay)
                delay(5000L)

            binding.cardGraph.storyRippleLayout.startRippleAnimation()
            binding.cardGraph.storyRippleLayout.animate()
                .alpha(1f)
                .setDuration(1000)

            binding.cardGraph.blurredMoon.animate()
                .scaleY(0f)
                .scaleX(0f)
                .setDuration(1000)
//            binding.cardGraph.imageView.setImageResource(R.drawable.feature_gold_price_alert_graph_moon_indicator)
        }
    }
    private fun onGraphDataSelected(e: Entry) {
        binding.cardGraph.storyRippleLayout.isClickable = false
        binding.cardGraph.dashedLine.isVisible = true
        binding.cardGraph.goldTvPrice.isVisible = true
        val pos = binding.cardGraph.lineChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(e.x, e.y)
        showRipple(pos)
        showDashedLineWithText(pos, e)
    }

    private fun showDashedLineWithText(pos: MPPointD, e: Entry) {
        binding.cardGraph.dashedLine.x = pos.x.toFloat() - 9f

        binding.cardGraph.goldTvPrice.setText(
            genrateTitleForGoldPrice(e.y, (e.data as? String?)),
            TextView.BufferType.SPANNABLE
        )
        var textWidth = binding.cardGraph.goldTvPrice.measuredWidth
        val chartWidth = binding.cardGraph.lineChart.measuredWidth
        if (textWidth < 1f)
            textWidth = 400
        binding.cardGraph.goldTvPrice.x = (pos.x.toFloat() - textWidth/2.0f).coerceAtLeast(20f).coerceAtMost(chartWidth.toFloat()*55/100)
    }

    private fun generateSpanned(text: String, highlightedText: String): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(text + highlightedText)

        // Apply color to the first part
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.white
                )
            ),
            0,
            text.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        // Apply color to the highlighted part
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.color_D5CDF2
                )
            ),
            text.length,
            text.length + highlightedText.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

    private fun genrateTitleForGoldPrice(y: Float, s: String?): SpannableStringBuilder {
        val string = getString(
            R.string.feature_gold_price_alerts_rupee_prefix_string,
            y.getFormattedAmount()
        )
        return generateSpanned("${string}", " on " + convertDateFormat(s.orEmpty()))
    }

    private fun setGraphData(xAxis: List<String>, yAxis: List<String>) {
        binding.cardGraph.lineChart.invalidate()
        val entries = ArrayList<Entry>(xAxis.size + 1)

        yAxis.forEachIndexed { index, s ->
            entries.add(Entry(index.toFloat(), s.toFloat(), xAxis[index]))
        }

        val dataSet = LineDataSet(entries, "GOLD_DATA_SET")
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawCircles(false)
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawIcons(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.isHighlightEnabled = true

        dataSet.lineWidth = 2.5f
        dataSet.setDrawFilled(true)

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.feature_gold_price_alert_yellow_dark_yellow_gradient)
        dataSet.fillDrawable = drawable
        dataSet.color = ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_F2B62A)

        dataSet.isHighlightEnabled = true
        val lineData = LineData(dataSet)
        binding.cardGraph.lineChart.invalidate()
        binding.cardGraph.lineChart.data = lineData
        binding.cardGraph.lineChart.animateXY(300, 300, Easing.EaseOutExpo)
    }

    private fun loadData(timeframePosition: Int) {
        val timeFrame =
            viewModel.staticScreenResponse.value.data?.data?.timeframes?.getOrNull(
                timeframePosition
            )
        if (timeFrame?.period == null || timeFrame.unit == null) {
            viewModel.fetchGoldPriceTrend()
        } else {
            viewModel.fetchGoldPriceTrend(
                timeFrame.unit.orEmpty(),
                timeFrame.period.orZero()
            )
        }
        binding.cardGraph.lineChart.scaleY = 1f
    }
    private fun setupListeners() {
        binding.cardGraph.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { index ->
                    binding.cardGraph.dashedLine.isVisible = false
                    binding.cardGraph.storyRippleLayout.isVisible = false
                    binding.cardGraph.rippleBackgroundContainer.isVisible = false
                    binding.cardGraph.goldTvPrice.isVisible = false
                    if (binding.cardGraph.lineChart.data == null)
                        loadData(index)
                    else {
                        animator?.cancel() // cancel the existing animation
                        animator = animateToZero(
                            binding.cardGraph.lineChart,
                            binding.cardGraph.lineChart.lineData.getDataSetByIndex(0) as LineDataSet,
                            400
                        ) {
                            loadData(index)
                        }
                        animator?.start()
                        showRippleImageView?.cancel()
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.cardContainer.btnAction.setDebounceClickListener {
            viewModel.staticScreenResponse.value.data?.data?.saveGoldCta?.deepLink?.let {
                EventBus.getDefault().post(HandleDeepLinkEvent(it))
            } ?: run {
                buyGoldApi.openBuyGoldFlowWithWeeklyChallengeAmount(0f, BaseConstants.BuyGoldFlowContext.GOLD_PRICE_GRAPH)
            }
            viewModel.postSaveNowButtonClickedAnalytics(GoldPriceAlertsConstants.AnalyticKeys.Save_now, cardFlow = getCardFlowType())
        }

        binding.clTopRibbon.setDebounceClickListener {
            viewModel.postSaveNowButtonClickedAnalytics(GoldPriceAlertsConstants.AnalyticKeys.Nudge, cardFlow = getCardFlowType())
        }
    }


    private fun observeLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            UPDATE_STATE_FROM_BOTTOMSHEET
        )?.observe(viewLifecycleOwner) {
            viewModel.fetchStaticScreenData()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.goldTrendLiveData.collectUnwrapped(
                    onSuccess = { it ->
                        setGoldPriceDetails(it.data)
                        setAverageBuyPriceLineToGraph(it.data.yaxis.firstOrNull()?.toFloatOrNull().orZero())
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticScreenResponse.collect(
                    onLoading = {
                        showProgressBar()
                        binding.cardGraph.tabLayoutContainer.isVisible = false
                    },
                    onSuccess = {
                        dismissProgressBar()
                        //To update the buy price tab in Home Screen
                        EventBus.getDefault().post(UpdateBuyGoldPriceInToolbarEvent())
                        postLoadedAnalytics(it)
                        setupScreen(it)
                    },
                    onError = { errorMessage, _ ->
                        (if (errorMessage.isNotEmpty()) errorMessage else getString(com.jar.app.core_ui.R.string.something_went_wrong)).snackBar(
                            binding.root
                        )
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun postLoadedAnalytics(data: GoldTrendScreenStaticData?) {
        val mutableMap = mutableMapOf<String, String>()

        data?.activeAlertExists?.let {
            mutableMap[ActiveAlertsExists] = it.toString()
        }

        args.context?.let {
            mutableMap[flowContext] = it
        }
        mutableMap[Lowest_Price_Nudge] = data?.topRibbon?.title.orEmpty().getHtmlTextValue().toString()
        mutableMap[cardFlowType] = getCardFlowType()
        analyticsApi.postEvent(GoldPriceAlertsConstants.AnalyticKeys.GoldPrice_HomeScreenShown, mutableMap)
    }

    private fun getCardFlowType() = viewModel.staticScreenResponse.value.data?.data?.savingsCardV3?.cardType
        ?: viewModel.staticScreenResponse.value.data?.data?.savingsCardV3?.title.orEmpty().getHtmlTextValue().toString()

    private fun setupScreen(it: GoldTrendScreenStaticData?) {
        binding.cardGraph.tabLayoutContainer.isVisible = true
        binding.cardGraph.tvCurrentByPrice.setHtmlText(it?.liveBuyPriceTitle ?: getString(R.string.feature_gold_buy_price_live_gold_price))
        binding.cardGraph.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        binding.cardGraph.tabLayout.tabMode = TabLayout.MODE_FIXED
        setToolbar(it?.toolbarTitle)
        setupCta(it)
        setupCard(it?.savingsCardV3)
        setupTabs(it?.timeframes)
        setupTopRibbon(it?.topRibbon)
        /**
         * Animation Order :
         * 1. Set Ribbon Animation
         * 2. Set Alert CTA Animation
         * 3. Set Savings Card CTA Animation
         * This order should be followed while playing animation
         * **/
        setupAnimations(it)
    }

    private fun setupAnimations(data: GoldTrendScreenStaticData?) {
        alertIconRepeatAnimationJob?.cancel()
        alertCtaIconAnimation?.cancel()
        animationJob?.cancel()
        animationJob = uiScope.launch {
            data?.topRibbon?.let {
                if (binding.elRibbonText.isExpanded.not()) {
                    setTopRibbonAnimation(data)
                } else {
                    setAlertCtaAnimation(data)
                }
            } ?: kotlin.run {
                setAlertCtaAnimation(data)
            }
        }
    }

    private fun setTopRibbonAnimation(data: GoldTrendScreenStaticData?) {
        //We will play ribbon animation if ribbon data is available else move to alert cta animation
        binding.elRibbonText.setOnExpansionUpdateListener { _, state ->
            if (state == ExpandableLayout.State.EXPANDED) {
                setAlertCtaAnimation(data)
            }
        }
        binding.elRibbonText.expand()
    }

    private fun setAlertCtaAnimation(data: GoldTrendScreenStaticData?) {
        alertIconDelayJob?.cancel()
        alertIconDelayJob = uiScope.launch {
            delay(ALERT_ANIMATION_START_DELAY)
            //We will play alert cta animation if data is available else move to savings card cta animation
            data?.saveGoldCta?.let {
                it.expandedTitle?.let { _ ->
                    binding.cardGraph.alertBtnLabelCollapsed.setTextAnimation(it.title.orEmpty(), duration = ALERT_EXPANDED_TEXT_ANIMATION)
                    binding.cardGraph.alertBtnLabelExpanded.slideRightToReveal(
                        binding.cardGraph.alertBtnLabelCollapsed,
                        duration = ALERT_SLIDE_ANIMATION,
                        onAnimationEnd = {
                            binding.cardGraph.alertBtnLabelExpanded.isVisible = false
                            setAlertCtaIconAnimation(it.getIconAnimationType())
                            setCardCtaShimmerAnimation(data)
                        }
                    )
                } ?: kotlin.run {
                    setAlertCtaIconAnimation(it.getIconAnimationType())
                    setCardCtaShimmerAnimation(data)
                }
            } ?: kotlin.run {
                setCardCtaShimmerAnimation(data)
            }
        }
    }

    private fun setAlertCtaIconAnimation(animationType: SaveGoldCtaAnimType) {
        when (animationType) {
            SaveGoldCtaAnimType.SWING -> {
                startIconAnimation(
                    view = binding.cardGraph.setAlertBtnIv,
                    propertyName = "rotation",
                    startValue = -30f,
                    endValue = 30f,
                    repeatCount = 10,
                    repeatMode = ValueAnimator.REVERSE,
                    duration = 400L,
                    shouldPlayAgainAfterInterval = true
                )
            }
            SaveGoldCtaAnimType.BLINK -> {
                startIconAnimation(
                    view = binding.cardGraph.setAlertBtnIv,
                    propertyName = "alpha",
                    startValue = 0f,
                    endValue = 1f,
                    repeatCount = ValueAnimator.INFINITE,
                    repeatMode = ValueAnimator.REVERSE,
                    duration = 500L,
                    shouldPlayAgainAfterInterval = false
                )
            }
            SaveGoldCtaAnimType.BLINK_ONCE -> {
                startIconAnimation(
                    view = binding.cardGraph.setAlertBtnIv,
                    propertyName = "alpha",
                    startValue = 0f,
                    endValue = 1f,
                    repeatCount = 4,
                    repeatMode = ValueAnimator.REVERSE,
                    duration = 500L,
                    shouldPlayAgainAfterInterval = true
                )
            }
            SaveGoldCtaAnimType.NONE -> {
                //do nothing
                alertCtaIconAnimation?.cancel()
                alertIconRepeatAnimationJob?.cancel()
            }
        }
    }

    private fun startIconAnimation(
        view: View,
        propertyName: String,
        startValue: Float,
        endValue: Float,
        repeatCount: Int,
        repeatMode: Int,
        duration: Long,
        shouldPlayAgainAfterInterval: Boolean
    ) {
        alertCtaIconAnimation?.cancel()
        alertCtaIconAnimation?.cancel()

        alertCtaIconAnimation = ObjectAnimator.ofFloat(view, propertyName, startValue, endValue)
        alertCtaIconAnimation?.duration = duration
        alertCtaIconAnimation?.repeatCount = repeatCount
        alertCtaIconAnimation?.repeatMode = repeatMode
        alertCtaIconAnimation?.interpolator = LinearInterpolator()
        alertCtaIconAnimation?.addListener(alertCtaIconAnimationListener)
        alertCtaIconAnimation?.start()

        if(shouldPlayAgainAfterInterval) {
            alertIconRepeatAnimationJob = uiScope.countDownTimer(
                totalMillis = ALERT_ICON_ANIMATION_INTERVAL,
                onFinished = {
                    startIconAnimation(
                        view = view,
                        propertyName = propertyName,
                        startValue = startValue,
                        endValue = endValue,
                        repeatCount = repeatCount,
                        repeatMode = repeatMode,
                        duration = duration,
                        shouldPlayAgainAfterInterval = false
                    )
                }
            )
        }
    }

    private fun setCardCtaShimmerAnimation(data: GoldTrendScreenStaticData?) {
        if (data?.savingsCardV3?.saveGoldCta?.showShimmer.orFalse()) {
            binding.cardContainer.shimmerCardCta.startShimmer()
        } else {
            binding.cardContainer.shimmerCardCta.stopShimmer()
        }
    }

    private fun setupTopRibbon(data: GoldTrendTopRibbon?) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(Color.parseColor(data?.startColor ?: DEFAULT_TOP_RIBBON_START_COLOR), Color.parseColor(data?.endColor ?: DEFAULT_TOP_RIBBON_END_COLOR))
        ).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(0f,0f,4f.dp,4f.dp,4f.dp,4f.dp,0f,0f)
        }
        binding.clTopRibbon.background = gradientDrawable
        binding.tvTopRibbonText.setHtmlText(data?.title.orEmpty())
        data?.iconUrl?.takeIf { it.isNotEmpty() }?.let {
            Glide.with(requireContext()).load(it).into(binding.ivTopRibbonIcon)
        }
        binding.elRibbonText.collapse()
        binding.clTopRibbon.isVisible = data != null
    }

    private fun setupCta(data: GoldTrendScreenStaticData?) {
        data?.saveGoldCta?.iconUrl?.takeIf { it.isNotEmpty() }?.let {
            Glide.with(requireContext()).load(it).into(binding.cardGraph.setAlertBtnIv)
        }
        binding.cardGraph.alertBtnLabelExpanded.setHtmlText(data?.saveGoldCta?.expandedTitle ?: data?.saveGoldCta?.title.orEmpty())
        binding.cardGraph.alertBtn.setDebounceClickListener {
            if (data?.getAlertStatus() == GoldTrendAlertStatus.NOT_SET) {
                navigateToSetAlertBottomsheet()
            } else {
                navigateToDisableAlertBottomsheet()
            }
            viewModel.postSaveNowButtonClickedAnalytics(GoldPriceAlertsConstants.AnalyticKeys.Set_Price_Alert, cardFlow = getCardFlowType())
        }
        binding.cardGraph.alertBtn.isVisible = true
    }

    private fun navigateToSetAlertBottomsheet() {
        navigateTo(GoldPriceDetailFragmentDirections.actionGoldPriceDetailFragmentToGoldPriceAlertsBottomSheet())
    }

    private fun navigateToDisableAlertBottomsheet() {
        navigateTo(GoldPriceDetailFragmentDirections.actionGoldPriceDetailFragmentToEnabledGoldPriceAlertsBottomSheet())
    }

    private fun setupTabs(timeframes: List<Timeframe>?) {
        if (binding.cardGraph.tabLayout.tabCount == 0) {
            binding.cardGraph.tabLayout.width
            timeframes?.forEach {
                val newTab = binding.cardGraph.tabLayout.newTab().apply {
                    text = it.getTitle()
                }
                newTab.view.setDebounceClickListener { view ->
                    viewModel.postTabSelectedAnalyticEvent(it.unit.orEmpty(), it.period.orZero())
                }
                binding.cardGraph.tabLayout.addTab(newTab)
            }
        }
        timeframes?.indexOfFirst { it.isSelected.orFalse() }?.takeIf { it >= 0 }?.let { selectedIndex ->
            binding.cardGraph.tabLayout.getTabAt(selectedIndex)?.let {
                binding.cardGraph.tabLayout.selectTab(it)
            }
        } ?: kotlin.run {
            binding.cardGraph.tabLayout.getTabAt(binding.cardGraph.tabLayout.tabCount - 1)?.let {
                binding.cardGraph.tabLayout.selectTab(it)
            }
        }
    }

    private fun setupCard(savingsCard: SavingsCard?) {
        savingsCard?.let {
            binding.cardContainer.title.setHtmlText(it.title.orEmpty())
            binding.cardContainer.btnAction.setText(it.saveGoldCta?.title.orEmpty())
            binding.cardContainer.footerText.setHtmlText(it.footerText.orEmpty())
            it.footerIconUrl?.let { iconUrl ->
                Glide.with(requireContext()).load(iconUrl)
                    .into(binding.cardContainer.footerIcon)
            }
            it.imageUrl?.let { iconUrl ->
                Glide.with(requireContext()).load(iconUrl)
                    .into(binding.cardContainer.rightImage)
            }

            it.footerTable?.takeIf { footerTable -> footerTable.isNotEmpty() }?.let { footerTable ->
                addViewsToLinearLayout(footerTable, binding.cardContainer.llFooterListHolder)
            }

            binding.cardAlertContainer.isVisible = true
            binding.cardContainer.groupFooterTextAndIcon.isVisible = it.footerText.isNullOrEmpty().not() && it.footerIconUrl.isNullOrEmpty().not()
            binding.cardContainer.llFooterListHolder.isVisible = it.footerTable.isNullOrEmpty().not()
        } ?: run {
            binding.cardAlertContainer.isVisible = false
        }
    }

    private fun addViewsToLinearLayout(
        textList: List<SavingsCardFooterTable>,
        footerLayout: LinearLayoutCompat
    ) {
        footerLayout.removeAllViews()
        val requestOptions = RequestOptions.centerInsideTransform()
        textList.forEach { textData ->
            val widthPerView = footerLayout.width/textList.size
            val horizontalLayoutParams = LinearLayout.LayoutParams(widthPerView, LayoutParams.WRAP_CONTENT)

            val horizontalLayout = LinearLayout(context)
            horizontalLayout.orientation = LinearLayout.HORIZONTAL
            horizontalLayout.gravity = Gravity.CENTER
            horizontalLayout.layoutParams = horizontalLayoutParams

            val imageView = ImageView(context)
            Glide.with(requireContext())
                .load(textData.iconUrl)
                .apply(requestOptions)
                .into(imageView)

            val imageLayoutParams = LinearLayout.LayoutParams(
                12.dp,
                12.dp
            )
            imageLayoutParams.marginEnd = 6.dp


            val textView = AppCompatTextView(requireContext())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(com.jar.app.core_ui.R.style.USPTextViewStyle)
            } else {
                textView.setTextAppearance(
                    context,
                    com.jar.app.core_ui.R.style.USPTextViewStyle
                )
            }
            textView.setTextColor(
                ContextCompat.getColor(
                    requireContext(), com.jar.app.core_ui.R.color.color_D5CDF2
                )
            )
            textView.textSize = 10f
            textView.gravity = Gravity.CENTER
            textView.text = textData.value

            horizontalLayout.addView(imageView, imageLayoutParams)
            horizontalLayout.addView(textView)
            footerLayout.addView(horizontalLayout)
        }
    }

    fun setGoldPriceDetails(goldTrend: GoldTrend) {
        if (goldTrend.xaxis.isEmpty() || goldTrend.yaxis.isEmpty()) return
        setGraphData(goldTrend.xaxis.reversed(), goldTrend.yaxis.reversed())
        val dataSet = binding.cardGraph.lineChart.lineData.getDataSetByIndex(binding.cardGraph.lineChart.lineData.dataSetCount - 1) as LineDataSet
        val e = dataSet.getEntryForIndex(dataSet.entryCount - 1)
        val pos = binding.cardGraph.lineChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(e.x, e.y)
        showRipple(pos, false)
        binding.cardGraph.goldTvPrice.setText(
            genrateTitleForGoldPrice(e.y, (e.data as? String?)),
            TextView.BufferType.SPANNABLE
        )
        setupTable(goldTrend.tableData)
        showRippleImageView = viewLifecycleOwner.lifecycleScope.launch {
            delay(400) // delay because graph is being animated 1sec to open
            binding.cardGraph.rippleBackgroundContainer.isVisible = true
            binding.cardGraph.storyRippleLayout.isVisible = true
        }
        binding.cardGraph.xEndLabelTv.text = goldTrend.trendsEndText
        binding.cardGraph.xStartLabelTv.text = goldTrend.trendsStartText
    }

    private fun setupTable(tableData: List<TableData?>?) {
        tableData?.getOrNull(2)?.let {
            setTableData(
                it,
                binding.cardGraph.fiveYChange,
                binding.cardGraph.fiveYLabelIv,
                binding.cardGraph.fiveYValueTv
            )
        }
    }

    private fun setTableData(
        data: TableData,
        title: TextView,
        image: ImageView,
        valueTextView: TextView
    ) {
        title.text = data.key
        title.isVisible = !data.key.isNullOrBlank()

        valueTextView.text = data.value
        valueTextView.isVisible = !data.value.isNullOrBlank()

        Glide.with(this)
            .load(data.iconUrl)
            .into(image)
    }

    private fun setToolbar(toolbarTitle: String?) {
        binding.toolbar.tvTitle.isVisible = true
        binding.toolbar.separator.isVisible = true
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.tvEnd.isVisible = false
        binding.toolbar.ivTitleImage.isVisible = false

        binding.toolbar.tvTitle.text = toolbarTitle ?: getString(R.string.gold_buy_price)

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    override fun onDestroyView() {
        alertIconDelayJob?.cancel()
        animationJob?.cancel()
        alertIconRepeatAnimationJob?.cancel()
        alertCtaIconAnimation?.removeListener(alertCtaIconAnimationListener)
        alertCtaIconAnimation?.cancel()
        alertCtaIconAnimation = null
        showRippleImageView?.cancel()
        animator?.cancel()
        animator = null
        super.onDestroyView()
    }
}