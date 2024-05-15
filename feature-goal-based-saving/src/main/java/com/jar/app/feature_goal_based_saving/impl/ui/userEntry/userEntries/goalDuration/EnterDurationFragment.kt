package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalDuration

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.FragmentGoalDurationBinding
import com.jar.app.feature_goal_based_saving.impl.extensions.getCommaFormattedString
import com.jar.app.feature_goal_based_saving.impl.extensions.vibrate
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.UserEntryFragmentDirections
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalDuration.days.DurationAdapter
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.GOAL_BASED_SAVING_STEPS
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.impl.utils.convertEpochToCustomFormat
import com.jar.app.feature_goal_based_saving.shared.data.model.CalculateDailyAmountResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalRequest
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalDurationResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedTime
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class EnterDurationFragment:  BaseFragment<FragmentGoalDurationBinding>() {

    private val viewModel: GoalDurationViewModel by viewModels<GoalDurationViewModel> {defaultViewModelProviderFactory}
    private val subSharedViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }
    private val sharedViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }
    private var durationAdapter: DurationAdapter? = null
    @Inject
    lateinit var analyticsHandler: AnalyticsApi
    @Inject
    lateinit var serializer: Serializer
    private var currentSelectedItem: Int = -1
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
    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
            private fun handleBackPress() {
                subSharedViewModel.handleActions(
                    GoalBasedSavingActions.NavigateTo(
                        R.id.action_enterAmountFragment_to_abandonFragment
                    )
                )
            }
        }

    private val list =  mutableListOf<GoalRecommendedTime>()
    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi
    @Inject
    lateinit var appScope: CoroutineScope
    private var mandateJob: Job? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoalDurationBinding
        get() = FragmentGoalDurationBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            SavingsGoal_ScreenShown,
            mapOf(
                screen_type to "Goal Duration Screen"
            )
        )
        subSharedViewModel.handleActions(
            actions = GoalBasedSavingActions.OnStepChange(
                GOAL_BASED_SAVING_STEPS.GOAL_DURATION
            )
        )
        subSharedViewModel.handleActions(
            GoalBasedSavingActions.ScrollToEnd
        )
        sharedViewModel.state.value.isCallHomeFeedApi.set(true)
        val amount = try {
            subSharedViewModel.state.value.onAmountChanged?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
        viewModel.fetch(amount)
        initViews()
        registerBackPressDispatcher()
        observeState()
        observeLiveData()
        setListeners()
    }

    private fun initViews() {
        binding.rvDuration.apply {
            durationAdapter = DurationAdapter(
                list
            ) { duration, position ->
                if (currentSelectedItem != position) {
                    list[position].isSelected = true
                    if (currentSelectedItem != -1) {
                        list[currentSelectedItem].isSelected = false
                        durationAdapter?.notifyItemChanged(currentSelectedItem)
                    }
                    durationAdapter?.notifyItemChanged(position)
                    currentSelectedItem = position
                    subSharedViewModel.handleActions(
                        GoalBasedSavingActions.OnDurationChanged(
                            duration
                        )
                    )
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Goal Duration Screen",
                            "action" to "Goal Duration Select",
                            "selected_duration" to "${list[currentSelectedItem].number} ${list[currentSelectedItem].monthText}"
                        )
                    )
                } else {
                    list[position].isSelected = false
                    durationAdapter?.notifyItemChanged(position)
                    currentSelectedItem = -1
                    binding.detailsBottomSheet.visibility = View.GONE
                    subSharedViewModel.handleActions(
                        GoalBasedSavingActions.OnDurationChanged(null)
                    )
                }
                vibrate(vibrator)
            }
            adapter = durationAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)
        viewModel.goalDurationResponse.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onSuccess = {
                setUpView(it)
            }
        )

        viewModel.amountBreakDown.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                viewModel.dailyAmount = it.amount ?: 0
                showBreakDown(it)
                if (sharedViewModel.state.value.isNavigateToMergePlan.get()) {
                    binding.btnConfirmGoal.callOnClick()
                }
                dismissProgressBar()
            }
        )

        viewModel.mandateInfoResponse.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                viewModel.mandateInfo.value = it
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.savingDetails.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                viewModel.userSavingsDetails.value = it
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.goalCreationResponse.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {createGoalResponse ->
                dismissProgressBar()
                mandateJob = appScope.launch {
                    mandatePaymentApi.initiateMandatePayment(
                        paymentPageHeaderDetails = PaymentPageHeaderDetail(
                            toolbarHeader = "Savings Goal",
                            title = "Let’s automate your savings for ₹${viewModel.dailyAmount}",
                            toolbarIcon = 0,
                            featureFlow = MandatePaymentEventKey.FeatureFlows.SavingsGoal,
                            userLifecycle = null,
                            savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                            mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION
                        ),
                        initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                            mandateAmount = viewModel.dailyAmount.toFloat(),
                            authWorkflowType = MandateWorkflowType.TRANSACTION,
                            subscriptionType = SavingsType.DAILY_SAVINGS.name,
                            goalId = createGoalResponse.goalId
                        )
                    ).collectUnwrapped(
                        onSuccess = {
                            sharedViewModel.state.value.isCallHomeFeedApi.set(false)
                            if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS) {
                                viewModel.updateDailyGoalRecurringAmount(viewModel.dailyAmount.toFloat())
                            }
                            sharedViewModel.handleActions(
                                GoalBasedSavingActions.NavigateToPaymentSuccessScreen(
                                    createGoalResponse.goalId ?: ""
                                )
                            )
                        },
                        onLoading = {},
                        onError = { errorMsg, errorCode ->
                            if (errorMsg == "Transaction cancelled")
                                EventBus.getDefault().post(HandleDeepLinkEvent("dl.myjar.app/savingsGoal"))
                        }
                    )
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.mandateAndSavingDetailsMediatorLiveData.observe(viewLifecycleOwner) {

            val mandateInfo = it.mandateInfo
            val userSavingDetails = it.savingsType

            val userDailyAmountAfterSelectingDuration = viewModel.dailyAmount
            val newMandateAmount = mandateInfo?.newMandateAmount ?: 0f
            val roundOffAmount = userSavingDetails?.subscriptionAmount ?: 0f

            val date = userSavingDetails?.updateDate ?: 0L

            val goalImage = if (subSharedViewModel.state.value.onGoalSelectedFromList?.bottomShadowImage.isNullOrEmpty().not() && subSharedViewModel.state.value.onGoalSelectedFromList?.bottomShadowImage != "-1"){
                subSharedViewModel.state.value.onGoalSelectedFromList?.bottomShadowImage ?: ""
            } else {
                subSharedViewModel.state.value.defaultGoalImage ?: ""
            }

            if (userSavingDetails?.enabled == true && userSavingDetails.autoSaveEnabled==true && roundOffAmount != 0f ) {
                // show merge plan screen
                val roundOffAmount = roundOffAmount
                val totalAmount = if (mandateInfo?.resetRequired == true) {
                    newMandateAmount
                } else {
                    userDailyAmountAfterSelectingDuration + roundOffAmount
                }
                val stringFormatOfDate = convertEpochToCustomFormat(date)
                val goalName = if (subSharedViewModel.state.value.onGoalTitleChange.isNullOrEmpty().not()){
                    subSharedViewModel.state.value.onGoalTitleChange!!
                } else {
                    subSharedViewModel.state.value.onGoalSelectedFromList?.name!!
                }
                val goalAmount = try {
                    subSharedViewModel.state.value.onAmountChanged?.toLong()
                } catch (e: Exception) {
                    0L
                }
                val goalDuration = subSharedViewModel.state.value.onDurationChanged

                val action = UserEntryFragmentDirections.actionFirstFragmentToConfirmGoalBasedSavingFragment(
                    goalName,
                    goalAmount ?: 0L,
                    goalDuration ?: 0,
                    goalImage,
                    userDailyAmountAfterSelectingDuration,
                    roundOffAmount.toInt(),
                    totalAmount.toInt(),
                    stringFormatOfDate
                )
                sharedViewModel.handleActions(
                    GoalBasedSavingActions.NavigateWithDirection(
                        action
                    )
                )
            } else {
                val goalName = if (subSharedViewModel.state.value.onGoalTitleChange.isNullOrEmpty().not()){
                    subSharedViewModel.state.value.onGoalTitleChange!!
                } else {
                    subSharedViewModel.state.value.onGoalSelectedFromList?.name!!
                }
                val goalAmount = try {
                    subSharedViewModel.state.value.onAmountChanged?.toLong()
                } catch (e: Exception) {
                    0L
                }
                val goalDuration = subSharedViewModel.state.value.onDurationChanged
                viewModel.createGoal(
                    CreateGoalRequest(
                        name = goalName,
                        amount = goalAmount,
                        duration = goalDuration,
                        image = goalImage
                    )
                )
            }
        }
    }

    private fun showBreakDown(calculateDailyAmountResponse: CalculateDailyAmountResponse) {
        val dailyAmount = calculateDailyAmountResponse.amount
        binding.text1.text = calculateDailyAmountResponse.header
        binding.text2.text = "\u20B9${dailyAmount.toString().getCommaFormattedString()}"
        binding.text3.text = calculateDailyAmountResponse.subText
        calculateDailyAmountResponse.footerButtonText?.let { binding.btnConfirmGoal.setText(it) }

        subSharedViewModel.state.value.onGoalSelectedFromList?.bottomShadowImage?.let { it1 ->
            if (it1 != "-1") {
                Glide.with(binding.image1).load(it1).into(binding.image1)
            } else {
                binding.image1.setImageResource(
                    R.drawable.default_goal_gbs
                )
            }
        }
        if (currentSelectedItem == -1) {
            binding.detailsBottomSheet.visibility = View.GONE
        } else {
            binding.detailsBottomSheet.visibility = View.VISIBLE
        }
        subSharedViewModel.handleActions(
            GoalBasedSavingActions.ScrollToEnd
        )
    }

    private fun setUpView(goalDurationResponse: GoalDurationResponse) {
        // Get the starting and ending indices of the portion you want to replace
        val startIndex = goalDurationResponse.timeQuestion?.indexOfFirst {
            it == '{'
        } ?: 0
        val endOfThePlaceholder = goalDurationResponse.timeQuestion?.indexOfFirst {
            it == '}'
        } ?: 0

        val originalQuestion = goalDurationResponse.timeQuestion
        val commaSepratedString = subSharedViewModel.state.value.onAmountChanged.toString().getCommaFormattedString()
        val replaceString = originalQuestion?.replace("{0}", commaSepratedString)
        val originalQuestionSpan = SpannableString(replaceString)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#C5B0FF"))
        val questionColor = ForegroundColorSpan(Color.parseColor("#EEEAFF"))

        originalQuestionSpan.setSpan(
            questionColor,
            0,
            startIndex,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )

        originalQuestionSpan.setSpan(
            questionColor,
            endOfThePlaceholder,
            originalQuestion?.length ?: endOfThePlaceholder,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )

        originalQuestionSpan.setSpan(
            colorSpan,
            startIndex-1,
            startIndex + (commaSepratedString?.length ?: 0),
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )

        val boldSpan = StyleSpan(Typeface.BOLD)
        originalQuestionSpan.setSpan(boldSpan,
            startIndex-1,
            startIndex + (commaSepratedString?.length ?: 0),
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        binding.tv.text = originalQuestionSpan
        val previousSelectedItem = subSharedViewModel.state.value.onDurationChanged
        if (previousSelectedItem != null && previousSelectedItem != -1){
            goalDurationResponse.goalRecommendedTimes?.forEachIndexed { index, goalRecommendedTime ->
                if (goalRecommendedTime?.number == previousSelectedItem) {
                    currentSelectedItem = index
                    goalRecommendedTime.isSelected = true
                }
            }
            subSharedViewModel.handleActions(
                GoalBasedSavingActions.OnDurationChanged(
                    previousSelectedItem
                )
            )
        }
        (goalDurationResponse.goalRecommendedTimes as? List<GoalRecommendedTime>)?.let {
            list.addAll(
                it
            )
        }
        durationAdapter?.notifyDataSetChanged()
        if (currentSelectedItem != -1) {
            binding.rvDuration.smoothScrollToPosition(currentSelectedItem)
        }
    }

    private fun setListeners() {
        binding.btnConfirmGoal.setDebounceClickListener {
            viewModel.fetchMandateInfo()
            viewModel.fetchSavingDetails(SavingsType.ROUND_OFFS)
            vibrate(vibrator)
            val goalName = if (subSharedViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty().not()) {
                subSharedViewModel.state.value.onGoalSelectedFromList?.name
            } else {
                subSharedViewModel.state.value.onGoalTitleChange
            }
            analyticsHandler.postEvent(
                SavingsGoal_ScreenClicked,
                mapOf(
                    "screen_type" to "Goal Duration Screen",
                    "clickaction" to "confirm goal",
                    "goal" to (goalName ?: ""),
                    "amount" to subSharedViewModel.state.value.onAmountChanged!!,
                    "goalduration" to "${subSharedViewModel.state.value.onDurationChanged} months",
                )
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                subSharedViewModel.state.collect {
                    it.onDurationChanged?.let {
                        if (it != -1){
                            val amount = try {
                                subSharedViewModel.state.value.onAmountChanged?.toInt() ?: 0
                            } catch (e: Exception) {
                                0
                            }
                            delay(100)
                            viewModel.getDailyAmount(
                                amount,
                                subSharedViewModel.state.value.onDurationChanged ?: 0
                            )
                        }
                    }
                }
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }
}