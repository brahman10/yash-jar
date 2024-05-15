package com.jar.app.feature_goal_based_saving.impl.ui.manage

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_goal_based_saving.databinding.FragmentGbfBinding
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.setOnClickListener
import com.jar.app.feature_goal_based_saving.shared.data.model.ActiveResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.ProgressStatus
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class ManageGoalFragment: BaseFragment<FragmentGbfBinding>(), GbsProgressBarCallbackListener {
    private val args by navArgs<ManageGoalFragmentArgs>()
    private val viewModel: ManageGoalFragmentViewModel by viewModels { defaultViewModelProviderFactory }
    private val sharedViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }
    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi
    @Inject
    lateinit var coreUiApi: CoreUiApi
    @Inject
    lateinit var prefsApi: PrefsApi
    companion object {
        const val FIRST_MILESTONE = 25f
        const val SECOND_MILESTONE = 50f
        const val THIRD_MILESTONE = 75f
        const val FOURTH_MILESTONE = 100f
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGbfBinding
        get() = FragmentGbfBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.handleActions(ManageGoalFragmentActions.Init(args.activeStateData))
        setupView()
        observeState()
        setupClickListener()
    }

    private fun setupClickListener() {
        listOf<View>(
            binding.appCompatImageView2,
            binding.tvTextSetting,
            binding.iconSettingArrow,
            binding.rlSettings
        ).setOnClickListener {
            viewModel.handleActions(ManageGoalFragmentActions.OnOpenSettings)
        }
    }

    private var isRoundOffsEnabled: Boolean = false
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.loading.collect() {
                        if (it) {
                            showProgressBar()
                        } else {
                            dismissProgressBar()
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.userMandateInfo.collect {
                        it?.let {
                            val newAmount = it.second
                            val mandateInfo = it.first
                            if (mandateInfo.resetRequired == true) {
                                if (isRoundOffsEnabled) {
                                    EventBus.getDefault().post(
                                        HandleDeepLinkEvent(
                                            "dl.myjar.app/preDailySavingAutopay/${BaseConstants.DSPreAutoPayFlowType.SETUP_DS}/${newAmount}"
                                        )
                                    )
                                } else {
                                    dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                                        mandateAmount = mandateInfo.getFinalMandateAmount(),
                                        source = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                                        authWorkflowType = MandateWorkflowType.valueOf(
                                            mandateInfo.authWorkflowType ?: MandateWorkflowType.TRANSACTION.name
                                        ),
                                        newAmount,
                                        com.jar.app.feature_goal_based_saving.R.id.manageGoalFragment,
                                        userLifecycle = prefsApi.getUserLifeCycleForMandate()
                                    )
                                }
                            } else {
                                viewModel.handleActions(
                                    ManageGoalFragmentActions.UpdateOnEnableDailySavings(
                                        newAmount
                                    )
                                )
                                viewModel.handleActions(
                                    ManageGoalFragmentActions.EnableAutomaticDailySavings
                                )
                            }
                        }
                    }
                }
            }
            launch {
                viewModel.dailyInvestmentStatus.collect() {
                    it?.let {
                        EventBus.getDefault().post(RefreshDailySavingEvent(isSetupFlow = true))
                        coreUiApi.openGenericPostActionStatusFragment(
                            GenericPostActionStatusData(
                                postActionStatus = PostActionStatus.ENABLED.name,
                                header = getString(R.string.feature_daily_investment_daily_investment_setup_successfully),
                                headerColorRes = com.jar.app.core_ui.R.color.color_1EA787,
                                title = getString(
                                    R.string.feature_daily_investment_x_will_be_auto_saved_starting_tomorrow,
                                    it.amount.toInt()
                                ),
                                titleColorRes = com.jar.app.core_ui.R.color.white,
                                imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                                headerTextSize = 18f,
                                titleTextSize = 16f
                            )
                        ) {
                            EventBus.getDefault()
                                .post(
                                    GoToHomeEvent(ManageGoalFragment::javaClass.name)
                                )
                        }
                    }
                }

            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.userSavingsDetails.collect() {
                        isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
                        viewModel.handleActions(
                            ManageGoalFragmentActions.OnFetchUserMandateInfo
                        )
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.openSetting.collect {
                        it?.let {
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent("dl.myjar.app/savingsGoalSettings")
                            )
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.openGoalCompletedScreen.collect {
                        it?.let {
                            showCongratulationScreen(it)
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activeScreenResponse.collect {
                    it?.let {
                        dismissProgressBar()
                        setData(it)
                    }
                }
            }
        }
    }

    private suspend fun setData(activeResponse: ActiveResponse) {
        with(binding) {
            gbsProgressBar.apply {
                setPercentage(activeResponse?.prevPercentage?.toFloat() ?: 0f)
                delay(500)
                animateFillToPercentage(
                    activeResponse?.prevPercentage?.toInt() ?: 0,
                    activeResponse?.currPercentage?.toInt() ?: 0,
                )
                textViewTop.text = "${activeResponse?.investedAmount}"
                textViewBottom.text = "${activeResponse?.totalAmountDesc}"

                Glide.with(indicatorIcon).load(activeResponse?.trackMessage?.iconLink).into(indicatorIcon)
                tvGoalStatus.text = "${activeResponse?.trackMessage?.text}"

                //set goal card
                appCompatTextView2.text = "${activeResponse?.goalDetails?.goalName}"
                Glide.with(ivGoalIcon).load(activeResponse?.goalDetails?.goalImage).into(ivGoalIcon)
                if (activeResponse.goalDetails?.details?.size!! >= 1) {
                    binding.appCompatTextView4.text = activeResponse.goalDetails?.details?.get(0)?.key
                    binding.savingAmount.text = activeResponse.goalDetails?.details?.get(0)?.value
                }
                if (activeResponse.goalDetails?.details?.size == 2) {
                    binding.appCompatTextView5.text = activeResponse.goalDetails?.details?.get(1)?.key
                    binding.appCompatTextView3.text = activeResponse.goalDetails?.details?.get(1)?.value
                }
            }
        }
    }

    private fun setupView() {
        binding.gbsProgressBar.setCallbackListener(this)
    }

    override fun onProgressChange(progress: Float) {}

    override fun onAnimationEnded() {
        uiScope.launch {

            val goalStatus = ProgressStatus.fromString(viewModel.state.value.data?.progressStatus)

            if (goalStatus == ProgressStatus.ACTIVE) {
                viewModel.state.value.data?.activeResponse?.let { showTopPopUp(it) }
                val lottieUrl = viewModel.state.value.data?.activeResponse?.celebrationLottie
                lottieUrl?.let {
                    binding.celebrationLottie.playLottieWithUrlAndExceptionHandling(requireContext(), it)
                }
            } else if (goalStatus == ProgressStatus.COMPLETED) {
                viewModel.handleActions(ManageGoalFragmentActions.OnGoalCompleted)
            }
        }
    }

    private fun showCongratulationScreen(data: String) {
        viewModel.state.value.data?.let {
            val direction = ManageGoalFragmentDirections.actionManageGoalFragmentToGoalSuccessFragment(data, it.goalId?:"")
            sharedViewModel.handleActions(GoalBasedSavingActions.NavigateWithDirection(direction))
        }
    }

    private fun showTopPopUp(activeResponse: ActiveResponse) {
        uiScope.launch {
            if (binding!=null) {
                with(binding) {
                    // setup pop up data
                    activeResponse?.popup?.let { popUp ->
                        llNotify.isVisible = true
                        llNotify.setOnTouchListener { view, event ->
                            handleSwipeToDismiss(view, event)
                        }
                        Glide.with(ivNotifyIcon).load(popUp.iconLink).into(ivNotifyIcon)
                        notifyText.text = popUp.text
                        notifyButton.text = popUp.buttonText
                        formatTextView(notifyButton)
                        notifyButton.setOnClickListener {
                            if (activeResponse.isDailySavingsDisabled == true) {
                                viewModel.handleActions(
                                    ManageGoalFragmentActions.OnClickOnDailySavingRestart
                                )
                            } else {
                                viewModel.handleActions(
                                    ManageGoalFragmentActions.SendSaveNowAnalyticEvent
                                )
                                sharedViewModel.handleActions(
                                    GoalBasedSavingActions.NavigatetoDeeplink(popUp.deeplink ?: "")
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    private fun formatTextView(textView: AppCompatTextView) {
        val spannable = SpannableStringBuilder(textView.text)

        // Apply bold style
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannable.setSpan(boldSpan, 0, spannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Apply underline style
        val underlineSpan = UnderlineSpan()
        spannable.setSpan(underlineSpan, 0, spannable.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannable
    }

    private var initialX: Float = 0F
    private var deltaX: Float = 0F
    private fun handleSwipeToDismiss(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                deltaX = 0F
            }
            MotionEvent.ACTION_MOVE -> {
                deltaX = event.x - initialX
                view.translationX = deltaX
                val alpha = 1 - Math.abs(deltaX) / view.width
                view.alpha = alpha
            }
            MotionEvent.ACTION_UP -> {
                if (Math.abs(deltaX) > view.width / 4) {
                    // Swipe distance exceeds half of the view's width, remove the view
                    removeView(view)
                } else {
                    // Reset the view's position and opacity
                    view.translationX = 0F
                    view.alpha = 1F
                }
            }
        }
        return true
    }

    private fun removeView(view: View) {
        view.visibility = View.INVISIBLE
    }
}