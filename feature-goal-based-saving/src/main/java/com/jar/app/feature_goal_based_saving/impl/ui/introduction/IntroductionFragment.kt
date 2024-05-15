package com.jar.app.feature_goal_based_saving.impl.ui.introduction

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.toast
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.FragmentIntroductionBinding
import com.jar.app.feature_goal_based_saving.impl.GoalBasedSavingViewModel
import com.jar.app.feature_goal_based_saving.impl.extensions.vibrate
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.IntroScreen
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedItem
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalSavingsIntoPage
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class IntroductionFragment : BaseFragment<FragmentIntroductionBinding>() {

    private val viewModel by viewModels<GoalBasedSavingViewModel>() { defaultViewModelProviderFactory }
    private val sharedViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }
    private val subSharedViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntroductionBinding
        get() = FragmentIntroductionBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        checkAndNavigateForward()
    }

    private fun checkAndNavigateForward() {
        lifecycleScope.launch {
            if ((subSharedViewModel.state.value.onGoalTitleChange.isNullOrEmpty().not()
                        || subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty()
                    .not())
                && subSharedViewModel.state.value.onAmountChanged.isNullOrEmpty().not()
                && subSharedViewModel.shouldNavigateForward
            ) {
                delay(90)
                val action = R.id.action_firstFragment_to_userEntryFragment
                sharedViewModel.handleActions(
                    GoalBasedSavingActions.NavigateTo(action)
                )
            } else {
                sharedViewModel.handleActions(
                    GoalBasedSavingActions.HideAppBar(true)
                )
                subSharedViewModel.handleActions(GoalBasedSavingActions.OnGoalTitleChange(""))
                subSharedViewModel.handleActions(
                    GoalBasedSavingActions.OnGoalSelectedFromList(
                        GoalRecommendedItem("", "", "")
                    )
                )
                subSharedViewModel.handleActions(GoalBasedSavingActions.OnDurationChanged(-1))
                subSharedViewModel.handleActions(GoalBasedSavingActions.OnAmountChanged(""))

                analyticsHandler.postEvent(
                    SavingsGoal_ScreenShown,
                    mapOf(
                        screen_type to IntroScreen
                    )
                )
                setupListeners()
                viewModel.fetchIntroductionScreenStaticData()
                viewModel.introductionScreenStaticData.observeNetworkResponse(
                    viewLifecycleOwner,
                    WeakReference(binding.root),
                    onSuccess = {
                        setupView(it)
                    },
                    onError = {
                        it.toast(binding.root)
                    }
                )
            }
        }
    }

    private fun setupView(it: GoalSavingsIntoPage) {
        with(binding) {
            tvHeadingFirst.text = it.goalSavingsIntroPageResponse.header1
            tvHeadingSecond.text = it.goalSavingsIntroPageResponse.header2
            lottie.playLottieWithUrlAndExceptionHandling(requireContext(), it.goalSavingsIntroPageResponse.displayUrl)
            buttonNext.setText(it.goalSavingsIntroPageResponse.footerButtonText)
            buttonNext.isVisible = true
        }
    }

    private fun setupListeners() {
        binding.buttonNext.setOnClickListener {
            analyticsHandler.postEvent(
                SavingsGoal_ScreenClicked,
                mapOf(
                    screen_type to "IntroScreen",
                    GBSAnalyticsConstants.clickaction to GBSAnalyticsConstants.CreateGoal
                )
            )
            vibrate(vibrator)
            val action = R.id.action_firstFragment_to_userEntryFragment
            sharedViewModel.handleActions(
                GoalBasedSavingActions.NavigateTo(action)
            )
        }
        binding.closeButton.setOnClickListener {
            viewModel.sendBackButton()
            sharedViewModel.handleActions(
                GoalBasedSavingActions.PopBackStack
            )
        }
    }
}