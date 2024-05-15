package com.jar.app.feature_goal_based_saving.impl.ui.confirmScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_goal_based_saving.api.GoalBasedSavingApi
import com.jar.app.feature_goal_based_saving.databinding.FragmentConfirmGoalBinding
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.TransactionFlow
import com.jar.app.feature_goal_based_saving.shared.data.model.MergeGoalResponse
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class ConfirmGoalBasedSavingFragment: BaseFragment<FragmentConfirmGoalBinding>() {

    private val args by navArgs<ConfirmGoalBasedSavingFragmentArgs>()
    private val viewModel: ConfirmGoalBasedSavingFragmentViewModel by viewModels { defaultViewModelProviderFactory }
    private val sharedViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }
    @Inject
    lateinit var goalBasedSavingApi: GoalBasedSavingApi
    @Inject
    lateinit var serializer: Serializer
    @Inject
    lateinit var appScope: CoroutineScope
    private var mandateJob: Job? = null

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    init {
        retainInstance = true
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentConfirmGoalBinding
        get() = FragmentConfirmGoalBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.handleAction(
            ConfirmGoalBasedSavingFragmentAction.Init(args)
        )
        sharedViewModel.state.value.isCallHomeFeedApi.set(true)
        sharedViewModel.state.value.isNavigateToMergePlan.set(false)
        observeState()
        setupClickListeners()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect() {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    it.mergeGoalResponse?.let {
                        dismissProgressBar()
                        setUpView(it)
                    }
                }
            }
        }
        mandateJob = appScope.launch(Dispatchers.Main) {
            viewModel.goalCreated.collect() { createGoalResponse ->
                mandatePaymentApi.initiateMandatePayment(
                    paymentPageHeaderDetails = PaymentPageHeaderDetail(
                        toolbarHeader = "Savings Goal",
                        title = "Let’s automate your savings for ₹${
                            args.savingGoalAmount.toFloat()?.toInt()}",
                        toolbarIcon = 0,
                        featureFlow = MandatePaymentEventKey.FeatureFlows.SavingsGoal,
                        userLifecycle = null,
                        savingFrequency = MandatePaymentEventKey.SavingFrequencies.Daily,
                        mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.DAILY_SAVINGS_MANDATE_EDUCATION
                    ),
                    initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                        mandateAmount = args.totalAmount.toFloat(),
                        authWorkflowType = MandateWorkflowType.TRANSACTION,
                        subscriptionType = SavingsType.DAILY_SAVINGS.name,
                        goalId = createGoalResponse.goalId
                    )
                ).collectUnwrapped(
                    onSuccess = {
                        var recurringAmount = -1f
                        if(it.second.recurringAmount != null && it.second.recurringAmount != 0f ){
                            recurringAmount = args.savingGoalAmount.toFloat()
                        }
                        TransactionFlow.updateFlow(createGoalResponse.goalId!!, recurringAmount, it.second.getAutoInvestStatus())
                    },
                    onLoading = {},
                    onError = { errorMsg, errorCode ->
                        if (errorMsg == "Transaction cancelled") {
                            sharedViewModel.state.value.isNavigateToMergePlan.set(true)
                            EventBus.getDefault().post(HandleDeepLinkEvent("dl.myjar.app/savingsGoal"))
                        }
                    }
                )
            }
        }
    }

    private fun setUpView(mergeGoalResponse: MergeGoalResponse) {
        with(binding) {
            tvHeading.text = mergeGoalResponse.header1
            tvSubHeading.text = mergeGoalResponse.header2

            if ((mergeGoalResponse.subscriptionSetupDetails?.size ?: 0) >= 1) {
                tvSettingUpNow.text = mergeGoalResponse.subscriptionSetupDetails?.get(0)?.setupStateInfo
                tvSavingGoal.text = mergeGoalResponse.subscriptionSetupDetails?.get(0)?.subscriptionName
                tvAmountEveryDay.text = mergeGoalResponse.subscriptionSetupDetails?.get(0)?.amountInfo?.replace("{0}", args.savingGoalAmount.toString())
            }
            if ((mergeGoalResponse.subscriptionSetupDetails?.size ?: 0) >= 2) {
                tvSetupDate.text = "Setup on ${args.date}"
                tvRoundOff.text = mergeGoalResponse.subscriptionSetupDetails?.get(1)?.subscriptionName
                tvAmountUpto.text = mergeGoalResponse.subscriptionSetupDetails?.get(1)?.amountInfo?.replace("{0}",
                    "${args.roundOffAmount}")
            }

            tvTotalAmount.text = mergeGoalResponse.totalAmountInfo?.title
            tvTotalPayPerDay.text = mergeGoalResponse.totalAmountInfo?.amountText?.replace("{0}","${ args.totalAmount }")

            Glide.with(ivShowInfo).load(mergeGoalResponse.totalAmountInfo?.icon).into(ivShowInfo)

            appCompatTextView.text = mergeGoalResponse.privacyText?.replace("{0}", "${ args.totalAmount }")
            Glide.with(ivShield).load(mergeGoalResponse.privacyIcon).into(ivShield)

            tvTotalAmountCalculation.text = mergeGoalResponse.totalAmountInfo?.amountCalculationHeader
            tvInfoDescription.text = mergeGoalResponse.totalAmountInfo?.amountCalculationAnswer

            tvTrustedBy.text = mergeGoalResponse?.socialProofingText
            buttonNext.setText(mergeGoalResponse.footerButtonText ?: "Proceed")
            Glide.with(ivNpci).load(mergeGoalResponse.npciIcon).into(ivNpci)
            Glide.with(ivSecureShield).load(mergeGoalResponse.secureIcon).into(ivSecureShield)

        }
    }

    private fun setupClickListeners() {
        with(binding) {
            ivShowInfo.setOnClickListener {
                viewModel.handleAction(
                    ConfirmGoalBasedSavingFragmentAction.OnClickOnInfo(
                        args
                    )
                )
                if (infoGroup.isVisible.not()) {
                    infoGroup.isVisible = true
                    bottomGroup.isVisible = false
                }
            }
            ivHideInfo.setOnClickListener {
                infoGroup.isVisible = false
                bottomGroup.isVisible = true
            }
            buttonNext.setDebounceClickListener {
                viewModel.handleAction(
                    ConfirmGoalBasedSavingFragmentAction.OnClickOnContinue(args)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mandateJob?.cancel()
    }
}