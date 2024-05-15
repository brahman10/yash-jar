package com.jar.app.feature_daily_investment.impl.ui.education

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.core_ui.dynamic_cards.DynamicEpoxyController
import com.jar.app.core_ui.dynamic_cards.base.EpoxyBaseEdgeEffectFactory
import com.jar.app.core_ui.extension.runLayoutAnimation
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentEducationBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingEducationFragment :
    BaseFragment<FeatureDailyInvestmentFragmentEducationBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentEducationBinding
        get() = FeatureDailyInvestmentFragmentEducationBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel: DailySavingEducationViewModel by viewModels()

    private var animation: ObjectAnimator? = null
    private var animateToTopAnimator: Animation? = null

    private var adapter: DailySavingEducationAdapter? = null

    private var controller: DynamicEpoxyController? = null

    private var layoutManager: LinearLayoutManager? = null

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 14.dp, escapeEdges = false)

    private val args: DailySavingEducationFragmentArgs by navArgs()

    private val edgeEffectFactory = EpoxyBaseEdgeEffectFactory()

    private var position = 0
    private var animationSecs = 4L

    companion object {
        const val DailySavingEducationFragment = "DailySavingEducationFragment"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchEducationList()
        setupDynamicCards()
        if (args.isDSSetupFlow)
            viewModel.fetchOrderStatusDynamicCards()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(DailySavingsEventKey.Shown_DailySaving_EducationScreen)
        adapter = DailySavingEducationAdapter { position, _, isExpanded ->
            analyticsHandler.postEvent(
                DailySavingsEventKey.Clicked_DailySaving_EducationScreen,
                mapOf(
                    DailySavingsEventKey.ButtonType to DailySavingsEventKey.Step,
                    DailySavingsEventKey.StepPosition to position,
                    DailySavingsEventKey.StepIsExpanded to isExpanded
                )
            )
        }
        binding.rvDSEducation.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDSEducation.adapter = adapter
        binding.rvDSEducation.runLayoutAnimation(R.anim.feature_daily_investment_layout_animation_bottom_to_top)

        binding.tvSkip.isVisible = args.isDSSetupFlow
        binding.ivCross.isVisible = args.isDSSetupFlow.not()
        binding.tvSkip.paintFlags =
            binding.tvSkip.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.btnGoToHomePage.setText(getString(if (args.isDSSetupFlow) com.jar.app.core_ui.R.string.core_ui_go_to_home else R.string.feature_daily_investment_okay_got_it))

    }

    private fun setupListener() {
        binding.btnGoToHomePage.setDebounceClickListener {
            if (args.isDSSetupFlow)
                EventBus.getDefault().post(
                    GoToHomeEvent(
                        DailySavingEducationFragment, BaseConstants.HomeBottomNavigationScreen.HOME
                    )
                )
            else
                popBackStack()
        }

        binding.tvSkip.setDebounceClickListener {
            animationSecs = 0L
            setLinearProgressWithAnimation()
            analyticsHandler.postEvent(
                DailySavingsEventKey.Clicked_DailySaving_EducationScreen,
                mapOf(DailySavingsEventKey.ButtonType to DailySavingsEventKey.Skip)
            )
            binding.tvSkip.isVisible = false
        }
        binding.ivCross.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewModel.educationSavingEducationLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                binding.tvHeader.text = it.dailySavingEducationData.header
                if (viewModel.dataListSize > 0)
                    viewModel.emitDsEducationData(position)
            },
            onError = { dismissProgressBar() }
        )

        viewModel.educationListLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                adapter?.submitList(it)
                setLinearProgressWithAnimation()
            }
        )

        viewModel.isListExhaustedLiveData.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressHorizontal.isVisible = false
                binding.tvHeader.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_ACA1D3
                    )
                )
                animateToTopAnimator =
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        com.jar.app.base.R.anim.slide_to_top
                    )
                animateToTopAnimator?.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {
                        binding.dsSetupFlowGroup.isVisible = args.isDSSetupFlow
                        binding.clBottomContainer.isVisible = true
                    }

                    override fun onAnimationEnd(p0: Animation?) {}
                    override fun onAnimationRepeat(p0: Animation?) {}
                })
                binding.clBottomContainer.startAnimation(animateToTopAnimator!!)
            }
        }

        viewModel.dynamicCardsLiveData.observe(viewLifecycleOwner) {
            controller?.cards = it
            binding.dynamicRecyclerView.invalidateItemDecorations()
        }
    }

    private fun setLinearProgressWithAnimation() {
        val durationInMillis = Duration.ofSeconds(animationSecs).toMillis()
        animation = ObjectAnimator.ofInt(binding.progressHorizontal, "progress", 0, 100)
        animation?.duration = durationInMillis
        animation?.interpolator = LinearInterpolator()
        animation?.doOnEnd {
            viewModel.emitDsEducationData(++position)
        }
        animation?.start()
    }


    @SuppressLint("Range")
    private fun setupDynamicCards() {
        layoutManager = LinearLayoutManager(context)
        controller = DynamicEpoxyController(
            uiScope = uiScope,
            onPrimaryCtaClick = { primaryActionData, eventData ->
                popBackStack(R.id.dailySavingEducationFragment, inclusive = true)
                EventBus.getDefault().post(HandleDeepLinkEvent(primaryActionData.value))
                analyticsHandler.postEvent(
                    EventKey.Clicked_dynamicCard,
                    eventData.map
                )
            },
            onEndIconClick = { staticInfoData, eventData ->
                popBackStack()
                EventBus.getDefault().post(HandleDeepLinkEvent(staticInfoData.value))
                analyticsHandler.postEvent(
                    EventKey.Clicked_EndIcon_dynamicCard,
                    eventData.map
                )
            }
        )

        binding.dynamicRecyclerView.layoutManager = layoutManager
        binding.dynamicRecyclerView.setItemSpacingPx(0)
        binding.dynamicRecyclerView.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.dynamicRecyclerView.edgeEffectFactory = edgeEffectFactory
        val visibilityTracker = EpoxyVisibilityTracker()
        visibilityTracker.partialImpressionThresholdPercentage = 50
        visibilityTracker.attach(binding.dynamicRecyclerView)
        binding.dynamicRecyclerView.setControllerAndBuildModels(controller!!)

    }

    override fun onDestroyView() {
        controller = null
        binding.dynamicRecyclerView.adapter = null
        layoutManager = null
        animation?.cancel()
        animateToTopAnimator?.cancel()
        binding.dynamicRecyclerView.layoutManager = null
        super.onDestroyView()
    }
}