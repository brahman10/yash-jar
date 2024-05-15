package com.jar.app.feature.onboarding.ui.onboarding_story

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import com.jar.app.JarApp
import com.jar.app.R
import com.jar.app.base.data.event.OnboardingStoryImageResourceReadyEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.dp
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.attachSnapHelperWithListener
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.listener.OnSnapPositionChangeListener
import com.jar.app.core_ui.onboarding_stories.IndicatorViewAdapter
import com.jar.app.core_ui.onboarding_stories.IndicatorViewHolder
import com.jar.app.databinding.FragmentOnboardingStoryVariant2Binding
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_onboarding.shared.domain.model.OnboardingStoryData
import com.jar.app.feature_onboarding.shared.domain.model.OnboardingStoryIndicatorData
import com.jar.app.feature_onboarding.shared.domain.model.Stories
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
internal class OnBoardingStoryFragmentVariant2 :
    BaseFragment<FragmentOnboardingStoryVariant2Binding>() {

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val timeInit = System.currentTimeMillis()

    private val viewModelProviderNewOnboarding by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy {
        viewModelProviderNewOnboarding.getInstance()
    }

    private val viewModelProvider by viewModels<OnboardingStoryFragmentViewModelAndroid>()

    private val onboardingStoryViewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var layoutManager: LinearLayoutManager? = null
    private var onBoardingStories: List<Stories>? = null

    private var autScrollJob: Job? = null

    private var tooltipJob: Job? = null

    private var storyNumber: Int? = null

    private var flowType = "autoscroll"

    //To disable auto scroll when user is interacting with viewPager
    private var isAutoScrollEnabled = true

    private var storyAdapter: OnboardingStoriesV2Adpater? = null

    private val spaceItemDecoration = SpaceItemDecoration(2.dp, 0.dp)

    private val indicatorAdapter = IndicatorViewAdapter()

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
        private const val AUTO_SCROLL_INTERVAL = 6000L
        private const val MILLISECONDS_PER_INCH = 85f
        private const val ONBOARDING_DATA = "ONBOARDING_DATA"

        fun getInstance(onboardingStoryData: OnboardingStoryData): Fragment {
            val fragment = OnBoardingStoryFragmentVariant2()
            val bundle = bundleOf()
            bundle.putParcelable(ONBOARDING_DATA, onboardingStoryData)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val onboardingStoryData by lazy {
        requireArguments().getParcelable<OnboardingStoryData>(ONBOARDING_DATA)
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOnboardingStoryVariant2Binding
        get() = FragmentOnboardingStoryVariant2Binding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        newOnboardingViewModel.getPhoneNumberByDeviceId()
        onboardingStoryViewModel.fetchOnboardingStoryData()
    }

    private fun setupUI() {
        storyAdapter = OnboardingStoriesV2Adpater(
            onResourceReady = { time, position ->
                analyticsHandler.postEvent(
                    EventKey.ONBOARDING_RESOURCE_READY,
                    mapOf(
                        EventKey.TIME_SPENT to time.orZero().toString(),
                        BaseConstants.storyNum to position.toString(),
                        BaseConstants.storyType to storyType.toString()
                    )
                )
            }
        )
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
        uiScope.launch {
            kotlinx.coroutines.delay(1000)
            binding.shimmerPlaceholder.isVisible = false
            binding.clTopView.isVisible = true
        }
        onboardingStoryData?.stories.let { stories ->
            onBoardingStories = stories
            storyAdapter?.let { adapter ->
                adapter.submitList(stories)
            }
        }
        onboardingStoryData?.storyType.let { Type ->
            storyType = Type
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.btnStart.setDebounceClickListener {
            val currentTime = System.currentTimeMillis()
            EventBus.getDefault()
                .post(OnboardingStoryImageResourceReadyEvent(timeTaken = currentTime))
            analyticsHandler.postEvent(
                EventKey.CLICKED_START_NOW_ONBOARDING,
                mapOf(
                    BaseConstants.ButtonType to binding.btnStart.getText(),
                    BaseConstants.storyNum to storyNumber.toString(),
                    BaseConstants.storyType to storyType.toString()
                )
            )
            flowType = "manual"
            prefs.setOnBoardingStoryShown()
            onboardingStateMachine.navigateAhead()
        }

        binding.btnNext.setDebounceClickListener {
            val currentTime = System.currentTimeMillis()
            EventBus.getDefault()
                .post(OnboardingStoryImageResourceReadyEvent(timeTaken = currentTime))
            analyticsHandler.postEvent(
                EventKey.CLICKED_START_NOW_ONBOARDING,
                mapOf(
                    BaseConstants.ButtonType to EventKey.Next_Btn,
                    BaseConstants.storyNum to storyNumber.toString(),
                    BaseConstants.storyType to storyType.toString()
                )
            )
            moveToNextSlide()
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
                            if (holder is IndicatorViewHolder) {
                                holder.cellAnimation(BaseConstants.AnimationOperation.PAUSE)
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            val diffX = event.x - oldTouchValue
                            if (diffX > 0) {
                                val currentTime = System.currentTimeMillis()
                                EventBus.getDefault()
                                    .post(OnboardingStoryImageResourceReadyEvent(timeTaken = currentTime))
                                flowType = "manual"
                                analyticsHandler.postEvent(
                                    EventKey.CLICKED_START_NOW_ONBOARDING,
                                    mapOf(
                                        BaseConstants.ButtonType to "gesture - right",
                                        BaseConstants.storyNum to storyNumber.toString(),
                                        BaseConstants.storyType to storyType.toString()
                                    )
                                )
                            } else {
                                val currentTime = System.currentTimeMillis()
                                EventBus.getDefault()
                                    .post(OnboardingStoryImageResourceReadyEvent(timeTaken = currentTime))
                                analyticsHandler.postEvent(
                                    EventKey.CLICKED_START_NOW_ONBOARDING,
                                    mapOf(
                                        BaseConstants.ButtonType to "gesture - left",
                                        BaseConstants.storyNum to storyNumber.toString(),
                                        BaseConstants.storyType to storyType.toString()
                                    )
                                )
                            }
                            if (holder is IndicatorViewHolder) {
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
                newOnboardingViewModel.storyTouchEventFlow.collectLatest {
                    when (it) {
                        MotionEvent.ACTION_DOWN -> {
                            cancelAutoScrollJob()
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            startAutoScrollTimer()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                onboardingStoryViewModel.indicatorFlow.collectLatest {
                    val indicatorData =
                        mutableListOf<com.jar.app.core_ui.onboarding_stories.data.OnboardingStoryIndicatorData>()
                    it.forEach { indicator ->
                        val data =
                            com.jar.app.core_ui.onboarding_stories.data.OnboardingStoryIndicatorData(
                                id = indicator.id,
                                isSelected = indicator.isSelected
                            )
                        indicatorData.add(data)
                    }
                    indicatorAdapter.submitList(indicatorData)
                }
            }
        }
    }

    private fun startAutoScrollTimer() {
        cancelAutoScrollJob()
        autScrollJob = uiScope.launch {
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
        if (position == onBoardingStories?.size.orZero() - 1) {
            binding.btnStart.isInvisible = false
            binding.btnNext.isInvisible = true
            binding.btnStart.setText(getString(R.string.start_now))
            analyticsHandler.postEvent(
                EventKey.Shown_startnowcta_Onboardingstories,
                mapOf(
                    EventKey.variants to storyType.toString()
                ),
                shouldPushOncePerSession = true
            )
            cancelAutoScrollJob()
        } else {
            binding.btnNext.isInvisible = false
            binding.btnStart.isInvisible = true
        }
        if (indicatorAdapter.currentList.isNotEmpty()) {
            val indicatorData = mutableListOf<OnboardingStoryIndicatorData>()

            indicatorAdapter.currentList.forEach {
                val data = OnboardingStoryIndicatorData(
                    id = it.id,
                    isSelected = it.isSelected
                )
                indicatorData.add(data)
            }
            if(position < indicatorAdapter.currentList.size){
                onboardingStoryViewModel.updateIndicatorData(indicatorData, position)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOnboardingStoryImageResourceReadyEvent(onboardingStoryImageResourceReadyEvent: OnboardingStoryImageResourceReadyEvent) {
        val app = (requireContext().applicationContext as JarApp)
        newOnboardingViewModel.responseCount += 1
        if (newOnboardingViewModel.responseCount == 1) {
            if (onboardingStoryImageResourceReadyEvent.isFromCache == null) {
                analyticsHandler.postEvent(
                    EventKey.Shown_StartNowScreen_Onboarding_Ts, mapOf(
                        EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(endTimeTime = onboardingStoryImageResourceReadyEvent.timeTaken, startTime = app.appStartTime)
                    )
                )
            } else {
                analyticsHandler.postEvent(
                    EventKey.Shown_StartNowScreen_Onboarding_Ts, mapOf(
                        EventKey.IS_FROM_CACHE to onboardingStoryImageResourceReadyEvent.isFromCache.orFalse(),
                        EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(endTimeTime = onboardingStoryImageResourceReadyEvent.timeTaken, startTime = app.appStartTime)
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        tooltipJob?.cancel()
        EventBus.getDefault().unregister(this)
        layoutManager = null
        cancelAutoScrollJob()
        analyticsHandler.postEvent(
            EventKey.Exit_StartNow_Onboarding,
            mapOf(
                EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit,
                BaseConstants.FlowType to flowType
            )
        )
        activity?.window?.navigationBarColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.bgColor)
        super.onDestroy()
    }
}