package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.enter_amount_duration

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.textChanges
import com.jar.app.base.util.toLongOrZero
import com.jar.app.core_base.util.BaseConstants.GOAL_BASED_SAVING_DEEPLINK
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.UserAmountDurationFragmentBinding
import com.jar.app.feature_goal_based_saving.impl.extensions.INRCurrencyFormatter
import com.jar.app.feature_goal_based_saving.impl.extensions.getCommaFormattedString
import com.jar.app.feature_goal_based_saving.impl.extensions.openKeyboard
import com.jar.app.feature_goal_based_saving.impl.extensions.vibrate
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.UserEntryFragmentDirections
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalAmount.EnterAmountFragmentAction
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalDuration.days.DurationAdapter
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.KeyboardObserver
import com.jar.app.feature_goal_based_saving.impl.utils.convertEpochToCustomFormat
import com.jar.app.feature_goal_based_saving.shared.data.model.CalculateDailyAmountResponse
import com.jar.app.feature_goal_based_saving.shared.data.model.CreateGoalRequest
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedTime
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class UserAmountAndDuration: BaseFragment<UserAmountDurationFragmentBinding>() {

    private val userEntryViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }
    private val gbsViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }
    private val viewModel: UserAmountAndDurationViewModel by viewModels { defaultViewModelProviderFactory  }
    private var durationAdapter: DurationAdapter? = null
    @Inject
    lateinit var appScope: CoroutineScope
    private var mandateJob: Job? = null
    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi
    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
            private fun handleBackPress() {
                userEntryViewModel.shouldNavigateForward = false
                popBackStack()
            }
        }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> UserAmountDurationFragmentBinding
        get() = UserAmountDurationFragmentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        viewModel.handelAction(EnterAmountFragmentAction.Init)
        initViews()
        setupListeners()
        observeState()

    }
    private val list =  mutableListOf<GoalRecommendedTime>()
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

    private fun initViews() {
        binding.llInfoContainer.visibility = View.VISIBLE
        val savingForTitle = if (userEntryViewModel.state.value.onGoalTitleChange?.isEmpty()?.not() == true) {
            userEntryViewModel.state.value.onGoalTitleChange
        } else {
            userEntryViewModel.state.value.onGoalSelectedFromList?.name
        }
        binding.goalName.text = savingForTitle
        userEntryViewModel.state.value.onGoalSelectedFromList?.bottomShadowImage?.let { it1 ->
            if (it1 != "-1") {
                Glide.with(binding.goalImage).load(it1).into(binding.goalImage)
            } else {
                binding.goalImage.setImageResource(
                    R.drawable.goal_default_icon
                )
            }
        }
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
                    binding.btnNext.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                    currentSelectedItem = position
                    userEntryViewModel.handleActions(
                        GoalBasedSavingActions.OnDurationChanged(
                            duration
                        )
                    )
                }
                vibrate(vibrator)
            }
            adapter = durationAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userEntryViewModel.state.collect {
                    it.onAmountChanged?.let {
                        binding.btnNext.apply {
                            isEnabled = false
                            alpha = 0.5f
                        }
                        if (it.isEmpty().not()) {
                            val amount = try {
                                it.toInt()
                            } catch (e: Exception) {
                                0
                            }
                            if (amount > (viewModel.state.value.goalAmountResponse?.maxAmount
                                    ?: 1500000) || amount == 0
                            ) {
                                binding.dailyAmountBreakDown.isVisible = false
                                if (binding.durationGroup.isVisible)
                                    binding.durationGroup.isVisible = false
                                viewModel.handelAction(
                                    EnterAmountFragmentAction.ClearOldDurationData
                                )
                                binding.tvErrorMsg.text =
                                    viewModel.state.value.goalAmountResponse?.higherAmountTip?.message
                                        ?: ""
                                binding.llErrorContainer.visibility = View.VISIBLE
                                binding.ivErrorIcon.isVisible = true
                                binding.llInfoContainer.visibility = View.GONE
                                binding.ivCorrectAmount.visibility = View.GONE
                                viewModel.handelAction(
                                    EnterAmountFragmentAction.SentAmountChangedEvent(
                                        screenType = "Amount screen",
                                        action = it,
                                        errorMessageShown = viewModel.state.value.goalAmountResponse?.higherAmountTip?.message
                                            ?: ""
                                    )
                                )
                            } else if (amount < (viewModel.state.value.goalAmountResponse?.minAmount
                                    ?: 1000)
                            ) {
                                binding.dailyAmountBreakDown.isVisible = false
                                if (binding.durationGroup.isVisible)
                                    binding.durationGroup.isVisible = false
                                viewModel.handelAction(
                                    EnterAmountFragmentAction.ClearOldDurationData
                                )
                                binding.tvErrorMsg.text =
                                    viewModel.state.value.goalAmountResponse?.lowerAmountTip?.message
                                        ?: ""
                                binding.llErrorContainer.visibility = View.VISIBLE
                                binding.ivErrorIcon.isVisible = true
                                binding.llInfoContainer.visibility = View.GONE
                                binding.ivCorrectAmount.visibility = View.GONE
                                viewModel.handelAction(
                                    EnterAmountFragmentAction.SentAmountChangedEvent(
                                        screenType = "Amount screen",
                                        action = it,
                                        errorMessageShown = viewModel.state.value.goalAmountResponse?.lowerAmountTip?.message
                                            ?: ""
                                    )
                                )
                            } else {
                                binding.llInfoContainer.visibility = View.VISIBLE
                                viewModel.handelAction(
                                    EnterAmountFragmentAction.ClearOldDurationData
                                )
                                viewModel.handelAction(
                                    EnterAmountFragmentAction.FetchDuration(
                                        it
                                    )
                                )
                                enableButton()
                            }
                        } else {
                            viewModel.handelAction(
                                EnterAmountFragmentAction.ClearOldDurationData
                            )
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect() {
                    it.goalAmountResponse?.let { goalAmountResponse ->
                        with(binding) {
                            etAmount.hint = (goalAmountResponse.amountInputText)
                            Glide.with(ivInfoIcon).load(goalAmountResponse.generalTip?.icon).into(ivInfoIcon)
                            tvInfoMsg.text = goalAmountResponse.generalTip?.message
                            Glide.with(ivErrorIcon).load(goalAmountResponse.lowerAmountTip?.icon).into(ivErrorIcon)
                            ivErrorIcon.isVisible = false
                            binding.etAmount.INRCurrencyFormatter()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.goalDurationResponse.collect {
                it?.let {
                    (it.goalRecommendedTimes as? List<GoalRecommendedTime>)?.let {
                        list.clear()
                        list.addAll(
                            it
                        )
                        if (currentSelectedItem !=-1) {
                            list.getOrNull(currentSelectedItem)?.isSelected = true
                        }
                    }
                    durationAdapter?.notifyDataSetChanged()
                    binding.durationGroup.isVisible = true
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.calculateDailyAmountResponse.collect {
                    it?.let {
                        binding.btnNext.apply {
                            isEnabled = true
                            alpha = 1f
                        }
                        showBreakDown(it)
                    } ?: kotlin.run {
                        disableButton()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                userEntryViewModel.state.collect {
                    it.onDurationChanged?.let {
                        if (it != -1){
                            val amount = try {
                                userEntryViewModel.state.value.onAmountChanged?.toInt() ?: 0
                            } catch (e: Exception) {
                                0
                            }
                            delay(100)
                            viewModel.handelAction(
                                EnterAmountFragmentAction.FetchBreakDown(
                                    amount,
                                    userEntryViewModel.state.value.onDurationChanged ?: 0
                                )
                            )
                        }
                    }
                }
            }
        }

        val weakReference: WeakReference<View> = WeakReference(binding.root)

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

        viewModel.mandateAndSavingDetailsMediatorLiveData.observe(viewLifecycleOwner) {

            val mandateInfo = it.mandateInfo
            val userSavingDetails = it.savingsType

            val userDailyAmountAfterSelectingDuration = viewModel.dailyAmount!!
            val newMandateAmount = mandateInfo?.newMandateAmount ?: 0f
            val roundOffAmount = userSavingDetails?.subscriptionAmount ?: 0f

            val date = userSavingDetails?.updateDate ?: 0L

            val goalImage = if (userEntryViewModel.state.value.onGoalSelectedFromList?.bottomShadowImage.isNullOrEmpty().not() && userEntryViewModel.state.value.onGoalSelectedFromList?.bottomShadowImage != "-1"){
                userEntryViewModel.state.value.onGoalSelectedFromList?.bottomShadowImage ?: ""
            } else {
                userEntryViewModel.state.value.defaultGoalImage ?: ""
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
                val goalName = if (userEntryViewModel.state.value.onGoalTitleChange.isNullOrEmpty().not()){
                    userEntryViewModel.state.value.onGoalTitleChange!!
                } else {
                    userEntryViewModel.state.value.onGoalSelectedFromList?.name!!
                }
                val goalAmount = userEntryViewModel.state.value.onAmountChanged?.toLongOrZero()
                val goalDuration = userEntryViewModel.state.value.onDurationChanged

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
                gbsViewModel.handleActions(
                    GoalBasedSavingActions.NavigateWithDirection(
                        action
                    )
                )
            } else {
                val goalName = if (userEntryViewModel.state.value.onGoalTitleChange.isNullOrEmpty().not()){
                    userEntryViewModel.state.value.onGoalTitleChange!!
                } else {
                    userEntryViewModel.state.value.onGoalSelectedFromList?.name!!
                }
                val goalAmount = userEntryViewModel.state.value.onAmountChanged?.toLongOrZero()
                val goalDuration = userEntryViewModel.state.value.onDurationChanged
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
                            title = "Let’s automate your savings for ₹${viewModel.dailyAmount!!}",
                            toolbarIcon = 0,
                            featureFlow = MandatePaymentEventKey.FeatureFlows.SavingsGoal,
                            userLifecycle = null,
                            savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                            mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION
                        ),
                        initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                            mandateAmount = viewModel.dailyAmount!!.toFloat(),
                            authWorkflowType = MandateWorkflowType.TRANSACTION,
                            subscriptionType = SavingsType.DAILY_SAVINGS.name,
                            goalId = createGoalResponse.goalId
                        )
                    ).collectUnwrapped(
                        onSuccess = {
                            gbsViewModel.state.value.isCallHomeFeedApi.set(false)
                            if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS) {
                                viewModel.updateDailyGoalRecurringAmount(viewModel.dailyAmount?.toFloat() ?: 0f)
                            }
                            gbsViewModel.handleActions(
                                GoalBasedSavingActions.NavigateToPaymentSuccessScreen(
                                    createGoalResponse.goalId ?: ""
                                )
                            )
                        },
                        onLoading = {},
                        onError = { errorMsg, errorCode ->
                            if (errorMsg == "Transaction cancelled")
                                EventBus.getDefault().post(HandleDeepLinkEvent(GOAL_BASED_SAVING_DEEPLINK))
                        }
                    )
                }
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun disableButton() {
        binding.llErrorContainer.isVisible = false
        binding.ivErrorIcon.isVisible = false
        binding.btnNext.apply {
            isEnabled = false
            alpha = 0.5f
        }
    }

    private fun enableButton() {
        binding.llErrorContainer.isVisible = false
        binding.ivErrorIcon.isVisible = false
    }

    private fun setupListeners() {
        KeyboardObserver(binding.root, findNavController(), onOpen = {
            binding.btnNext.isVisible = false
            binding.dailyAmountBreakDown.isVisible = false
            binding.llEnterAmount.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_active))

        }) {
            binding.btnNext.isVisible = true
            if (viewModel.dailyAmount != null) {
                binding.dailyAmountBreakDown.isVisible = true
            }
            binding.llEnterAmount.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dummy_et_bg))
        }.apply {
            lifecycle.addObserver(
                this
            )
        }
        binding.icClearText.setOnClickListener {
            currentSelectedItem = -1
            binding.etAmount.setText("")
            binding.durationGroup.isVisible = false
            userEntryViewModel.handleActions(
                GoalBasedSavingActions.OnDurationChanged(null)
            )
        }
        binding.etAmount.apply {
            val inputFilter = InputFilter.LengthFilter(9)
            filters = arrayOf(inputFilter)
            textChanges()
                .debounce(500)
                .onEach { text ->
                    currentSelectedItem = -1
                    binding.dailyAmountBreakDown.isVisible = false
                    val amount = text.toString().replace(",", "")
                    withContext(Dispatchers.Main) {
                        if (amount.isNotEmpty()) {
                            binding.icClearText.visibility = View.VISIBLE
                            binding.ivCorrectAmount.visibility = View.GONE
                        } else {
                            binding.icClearText.visibility = View.GONE
                        }
                        userEntryViewModel.handleActions(GoalBasedSavingActions.OnAmountChanged(amount))
                    }
                }
                .launchIn(CoroutineScope(Dispatchers.Main))
            requireActivity().openKeyboard(this)
            val amount = userEntryViewModel.state.value.onAmountChanged
            if(amount.isNullOrEmpty().not()) {
                setText(userEntryViewModel.state.value.onAmountChanged?.getCommaFormattedString())
                userEntryViewModel.handleActions(GoalBasedSavingActions.OnAmountChanged(amount!!))
                binding.icClearText.isVisible = false
                binding.ivCorrectAmount.isVisible = true
            }
        }
        binding.editGaol.setDebounceClickListener {
            userEntryViewModel.shouldNavigateForward = false
            viewModel.handelAction(
                EnterAmountFragmentAction.OnEditGoalIconClicked(
                    binding.etAmount.text.toString(),
                    userEntryViewModel.state.value.onDurationChanged,
                    viewModel.dailyAmount
                )
            )
            popBackStack()
        }

        binding.btnNext.setDebounceClickListener {
            viewModel.fetchMandateInfo()
            viewModel.fetchSavingDetails(SavingsType.ROUND_OFFS)
            userEntryViewModel.shouldNavigateForward = true
            vibrate(vibrator)
            val goalName = if (userEntryViewModel.state.value.onGoalSelectedFromList?.name.isNullOrEmpty().not()) {
                userEntryViewModel.state.value.onGoalSelectedFromList?.name
            } else {
                userEntryViewModel.state.value.onGoalTitleChange
            }
            viewModel.handelAction(
                EnterAmountFragmentAction.OnConfirmButtonClicked(
                    binding.etAmount.text.toString(),
                    userEntryViewModel.state.value.onDurationChanged,
                    viewModel.dailyAmount
                )
            )
        }
    }

    private fun showBreakDown(calculateDailyAmountResponse: CalculateDailyAmountResponse) {
        if (binding.dailyAmountBreakDown.isVisible.not())
            binding.dailyAmountBreakDown.isVisible = true
        val dailyAmount = calculateDailyAmountResponse.amount
        binding.tvBreakdownAmount.text = "\u20B9${dailyAmount.toString().getCommaFormattedString()}"
    }

    override fun onDestroyView() {
        viewModel.handelAction(
            EnterAmountFragmentAction.ClearOldDurationData
        )
        super.onDestroyView()
    }

}