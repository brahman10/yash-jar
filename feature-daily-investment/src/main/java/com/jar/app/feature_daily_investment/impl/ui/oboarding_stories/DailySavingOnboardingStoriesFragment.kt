package com.jar.app.feature_daily_investment.impl.ui.oboarding_stories

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.FeatureFlowData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.attachSnapHelperWithListener
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.listener.OnSnapPositionChangeListener
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentOnboardingStoriesFragmentBinding
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.BACK
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.COUNT
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.iS_VIEWED
import com.jar.app.feature_daily_investment.shared.domain.model.Stories
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingOnboardingStoriesFragment :
    BaseFragment<FeatureDailyInvestmentOnboardingStoriesFragmentBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi


    private val viewModelProvider by viewModels<DailySavingsOnboardingStoriesViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var layoutManager: LinearLayoutManager? = null
    private var onBoardingStories: List<Stories>? = null

    private var autScrollJob: Job? = null

    private var storyNumber: Int? = null

    private var flowType = "autoscroll"

    private var storiesViewed = false

    //To disable auto scroll when user is interacting with viewPager
    private var isAutoScrollEnabled = true

    private val storyAdapter = DailySavingsOnboardingStoriesAdapter()

    private val spaceItemDecoration = SpaceItemDecoration(2.dp, 0.dp)

    private val indicatorAdapter = IndicatorAdapterDS()

    private var indicatorLayoutManager: LinearLayoutManager? = null

    private var storyType: String? = null

    private val smoothScroller by lazy {
        object : LinearSmoothScroller(requireContext()) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
    }

    companion object {
        private const val AUTO_SCROLL_INTERVAL = 3000L
        private const val MILLISECONDS_PER_INCH = 85f
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentOnboardingStoriesFragmentBinding
        get() = FeatureDailyInvestmentOnboardingStoriesFragmentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onResume() {
        activity?.window?.navigationBarColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_141021)
        super.onResume()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        viewModel.fetchOnboardingStoryData()
    }

    private fun setupUI() {
        binding.toolbar.clToolbarContainer.setBackgroundColor(
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_2E2942)
        )
        binding.toolbar.tvTitle.text = getString(R.string.feature_daily_savings)
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_daily_investment_ic_daily_saving_tab)
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.adapter = storyAdapter
        indicatorLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvIndicator.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvIndicator.layoutManager = indicatorLayoutManager
        binding.rvIndicator.adapter = indicatorAdapter

        binding.rvStory.attachSnapHelperWithListener(
            PagerSnapHelper(),
            onSnapPositionChangeListener = object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    toggleUI(position)
                }
            })
        startAutoScrollTimer()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.DSCancellation_NewUserEducationStoriesClicked,
                mapOf(
                    BaseConstants.ButtonType to BACK
                )
            )
            findNavController().popBackStack()
        }
        binding.btnNext.setOnClickListener {
            analyticsHandler.postEvent(
                EventKey.DSCancellation_NewUserEducationStoriesClicked,
                mapOf(
                    BaseConstants.ButtonType to binding.btnNext.getText(),
                    iS_VIEWED to storiesViewed.toString(),
                    COUNT to viewModel.storyDataFlow.value.data?.data?.stories?.size.toString()
                )
            )
            flowType = EventKey.manual
            dailyInvestmentApi.openDailySavingFlow(
                fromSettingsFlow = false,
                featureFlowData = FeatureFlowData(
                    fromScreen = BaseConstants.ScreenFlowType.DAILY_SAVING_ONBOARDING_STORIES_SCREEN.name
                ),
            )
        }


        binding.rvStory.setOnTouchListener { _, event ->
            if (indicatorAdapter.currentList.isNotEmpty()) {
                val oldTouchValue = 0f
                val storyNumber = storyNumber.orZero()
                val childCount = binding.rvIndicator.childCount
                if (storyNumber in 0 until childCount) {
                    val childView = binding.rvIndicator.getChildAt(storyNumber)
                    val holder = binding.rvIndicator.getChildViewHolder(childView)
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            cancelAutoScrollJob()
                            if (holder is IndicatorViewHolderDS) {
                                holder.cellAnimation(BaseConstants.AnimationOperation.PAUSE)
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            if (holder is IndicatorViewHolderDS) {
                                holder.cellAnimation(BaseConstants.AnimationOperation.RESUME)
                            }
                            startAutoScrollTimer()
                        }
                    }
                }
            }
            false
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.storyDataFlow
                    .collect(
                        onLoading = {
                            binding.shimmerPlaceholder.isVisible = true
                            binding.clOnBoardingStories.isVisible = false
                            binding.btnNext.isVisible = false
                            binding.shimmerPlaceholder.startShimmer()
                            binding.btnNext.setDisabled(true)
                        },
                        onSuccess = {
                            analyticsHandler.postEvent(
                                EventKey.DSCancellation_NewUserEducationStoriesShown,
                                mapOf(
                                    BaseConstants.storyType to it?.storyType.toString()
                                )
                            )
                            binding.shimmerPlaceholder.isVisible = false
                            binding.clOnBoardingStories.isVisible = true
                            binding.btnNext.isVisible = true
                            onBoardingStories = it?.stories
                            storyType = it?.storyType
                            storyAdapter.submitList(onBoardingStories)
                            binding.btnNext.setDisabled(false)
                        },
                        onError = { errorMessage, _ ->
                            errorMessage.snackBar(binding.root)
                            binding.shimmerPlaceholder.isVisible = true
                            binding.clOnBoardingStories.isVisible = false
                            binding.btnNext.isVisible = false
                            binding.shimmerPlaceholder.startShimmer()
                            binding.btnNext.setDisabled(true)
                        }
                    )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.indicatorFlowData.collect { indicatorAdapter.submitList(it) }
            }
        }

    }

    private fun startAutoScrollTimer() {
        cancelAutoScrollJob()
        autScrollJob = lifecycleScope.launch {
            doRepeatingTask(repeatInterval = AUTO_SCROLL_INTERVAL) {
                moveToNextSlide()
            }
        }
    }

    private fun cancelAutoScrollJob() {
        autScrollJob?.cancel()
    }

    private fun moveToNextSlide() {
        if (isAutoScrollEnabled) {
            var currentPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
            val finalPosition = if (currentPosition == onBoardingStories?.size.orZero() - 1)
                0 else ++currentPosition
            if (finalPosition == 0) {
                layoutManager?.scrollToPosition(finalPosition)
            } else {
                smoothScroller.targetPosition = finalPosition
                layoutManager?.startSmoothScroll(smoothScroller)
            }
            toggleUI(finalPosition)
        }
    }

    private fun toggleUI(position: Int) {
        storyNumber = position
        if (storyNumber == 2) {
            storiesViewed = true
        }
        if (indicatorAdapter.currentList.isNotEmpty()) {
            viewModel.updateIndicatorData(indicatorAdapter.currentList, position)
        }
    }

    override fun onDestroy() {
        layoutManager = null
        cancelAutoScrollJob()
        super.onDestroy()
    }
}