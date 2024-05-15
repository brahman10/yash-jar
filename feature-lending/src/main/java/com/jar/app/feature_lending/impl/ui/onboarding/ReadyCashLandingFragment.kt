package com.jar.app.feature_lending.impl.ui.onboarding

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.widget.zoom_layout_manager.CenterZoomLinearLayoutManager
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentReadyCashLandingBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.ui.eligibility.rejected.LendingEligibilityFaqAdapter
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.LandingScreenContent
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class ReadyCashLandingFragment :
    BaseFragment<FeatureLendingFragmentReadyCashLandingBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val parentViewModelProvider: LendingHostViewModelAndroid by activityViewModels { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }


    private val viewModelProvider: ReadyCashLandingViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private var faqAdapter: LendingEligibilityFaqAdapter? = null
    private val sliderAdapter = IntroSliderAdapter()
    private val arguments by navArgs<ReadyCashLandingFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }

    private var job: Job? = null
    private var checkpointAnimationJob: Job? = null
    private var currentPosition = 0
    private var sliderLength = 2
    private var isScrolling = false
    private var isUserTouched = false
    private var isStepsAnimated = false
    private var scrollType = "Autoscrolled"

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentReadyCashLandingBinding
        get() = FeatureLendingFragmentReadyCashLandingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchScreenContentContent()
        parentViewModel.fetchPreApprovedData()
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_PDetailsFirstScreenLaunched,
            values = mapOf(
                LendingEventKeyV2.entry_type to args.source,
                LendingEventKeyV2.screen_type to "New",
                LendingEventKeyV2.lender to args.lender.orEmpty()
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUi()
        initClickListeners()
        observeFlow()
        registerBackPressDispatcher()
    }

    private fun isFromSellGoldFlow() = args.source == BaseConstants.LendingFlowType.SELL_GOLD

    private fun setupUi() {
        faqAdapter = LendingEligibilityFaqAdapter {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_PDetailsFAQClicked,
                values = mapOf(
                    LendingEventKeyV2.faq_title to it,
                )
            )
        }
        binding.rvFaq.layoutManager = LinearLayoutManager(requireContext())
        val decorator = DividerItemDecoration(
            requireContext(), LinearLayoutManager.VERTICAL
        )
        ContextCompat.getDrawable(
            requireContext(),
            com.jar.app.core_ui.R.drawable.core_ui_line_separator
        )?.let {
            decorator.setDrawable(it)
        }
        binding.rvFaq.addItemDecorationIfNoneAdded(decorator)
        binding.rvFaq.adapter = faqAdapter


        binding.rvIntroSlides.layoutManager = CenterZoomLinearLayoutManager(
            context = requireContext(),
            mShrinkDistance = 0.4f,
            mShrinkAmount = 0.1f,
            rvOrientation = RecyclerView.HORIZONTAL
        )
        binding.rvIntroSlides.adapter = sliderAdapter
        PagerSnapHelper().attachToRecyclerView(binding.rvIntroSlides)
    }

    private fun initClickListeners() {
        binding.btnBack.setDebounceClickListener {
            EventBus.getDefault()
                .post(LendingBackPressEvent(LendingEventKeyV2.PDETAILS_FIRST_SCREEN))

            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_BackButtonClicked,
                values = mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.INTRO_SCREEN,
                    LendingEventKeyV2.screen_type to "New",
                    LendingEventKeyV2.lender to args.lender.orEmpty()
                )
            )
            handleBackNavigation()
        }

        binding.btnGetReadyCash.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_PDetailsGetReadyCashClicked,
                values = mapOf(
                    LendingEventKeyV2.screen_type to "New",
                    LendingEventKeyV2.user_action_on_previous_card to scrollType
                )
            )
            args.screenData?.let {
                EventBus.getDefault().postSticky(
                    ReadyCashNavigationEvent(
                        whichScreen = it.nextScreen,
                        source = args.screenName,
                        popupToId = R.id.readyCashLandingFragment
                    )
                )
            }
        }

        binding.rvIntroSlides.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isUserTouched = true
                    }
                    MotionEvent.ACTION_UP -> {
                        isUserTouched = false
                        scrollType = "Manualscrolled"
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })
    }

    private fun handleBackNavigation() {
        EventBus.getDefault().post(LendingBackPressEvent(LendingEventKeyV2.PDETAILS_FIRST_SCREEN))

        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.readyCashLandingFragment,
                    isBackFlow = true
                )
            )
        }
    }

    private fun autoAnimateIntroCards() {
        job?.cancel()
        job = uiScope.doRepeatingTask(repeatInterval = 4000L) {
            if (isScrolling.not() && isUserTouched.not()) {
                if (currentPosition >= sliderAdapter.itemCount - 1)
                    currentPosition = -1
                binding.rvIntroSlides.smoothScrollToPosition(++currentPosition)
                updateTabLayoutIndicator(currentPosition)
            }
        }
        var selectedPosition = 0
        binding.rvIntroSlides.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isScrolling = newState == RecyclerView.SCROLL_STATE_DRAGGING
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position =
                    (recyclerView.layoutManager as CenterZoomLinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (position != selectedPosition) {
                    updateTabLayoutIndicator(position)
                    selectedPosition = position
                }
            }
        })
    }

    private fun updateTabLayoutIndicator(position: Int) {
        if (position < 0 || position > sliderLength - 1) return
        binding.indicatorView.selectIndicator(position)
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.screenContentFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setupUIWithData(it.landingScreenContent)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun setupUIWithData(landingScreenContent: LandingScreenContent) {
        parentViewModel.readyCashJourney?.progressBar?.let { binding.animatedSteps.changeSteps(it) }
        binding.tvTitleIntroducingReadyCash.text = landingScreenContent.title
        binding.tvSubtitleYouHaveOffer.text = landingScreenContent.subTitle
        binding.tvTrustedBy.text = getCustomStringFormatted(
            MR.strings.feature_lending_trusted_by_s_plus_users,
            landingScreenContent.trustCount.orEmpty()
        )
        faqAdapter?.submitList(landingScreenContent.newLandingScreenFAQs)
        sliderAdapter.submitList(landingScreenContent.slider)
        sliderLength = landingScreenContent.slider.size
        binding.indicatorView.setIndicatorCount(sliderLength)
        autoAnimateIntroCards()
        val scrollBounds = Rect()
        binding.animatedSteps.isVisible = true
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            checkpointAnimationJob?.cancel()
            checkpointAnimationJob = uiScope.launch {
                binding.scrollView.getHitRect(scrollBounds)
                if (binding.animatedSteps.getLocalVisibleRect(scrollBounds) && isStepsAnimated.not()) {
                    binding.animatedSteps.startAnimation()
                    isStepsAnimated = true
                }
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }

    override fun onDestroyView() {
        job?.cancel()
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}