package com.jar.app.feature_daily_investment.impl.ui.daily_savings_update_v3

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.NoUpiAppShown
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.Update_daily_savings
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class UpdateDailySavingsV3Fragment : BaseComposeFragment() {

    private val viewModelProvider by viewModels<UpdateDailySavingsV3ViemodelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var prefsApi: PrefsApi

    private var dSAmount: Float? = null

    private var mandateUpiApp: UpiApp? = null

    private var initiateMandatePaymentJob: Job? = null

    private var proceedClicked: Boolean = true

    private var recommendedAmount: Float? = null
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    @Preview
    override fun RenderScreen() {
        val staticUpdateFlowData = viewModel.staticUpdateFlowData.collectAsState()
        val dailySavingsValuesFlowData = viewModel.dailySavingsValuesFlow.collectAsState()

        Scaffold(
            topBar = {
                RenderToolbar {
                    viewModel.postClickEvent(
                        buttonType = DailySavingsEventKey.Back,
                        defaultAmount = recommendedAmount
                    )
                    findNavController().popBackStack()
                }
            },
            content = {
                UpdateDailySavingsScreen(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    staticData = staticUpdateFlowData.value.data?.data,
                    dailySavingsUpdateFlowValues = dailySavingsValuesFlowData.value,
                ) {
                    viewModel.postClickEvent(
                        buttonType = DailySavingsEventKey.EditAmount,
                        defaultAmount = recommendedAmount
                    )
                    openEditValueBottomSheet()
                }
            },
            bottomBar = {
                if (mandateUpiApp != null) {
                    OneTapPayment(
                        payNowCtaText = staticUpdateFlowData.value.data?.data?.paymentButtonText.orEmpty(),
                        appChooserText = stringResource(id = R.string.feature_daily_investment_update_daily_saving_pay_using),
                        mandateUpiApp = mandateUpiApp,
                        isEnabled = dailySavingsValuesFlowData.value?.currentDailySavingsAmount.orZero() != dailySavingsValuesFlowData.value?.recommendedDailySavingsAmount.orZero(),
                        onAppChooserClicked = {
                            proceedClicked = false
                            viewModel.postClickEvent(
                                buttonType = DailySavingsEventKey.PayUsing,
                                defaultAmount = recommendedAmount
                            )
                            viewModel.isAutoPayResetRequired(dSAmount.orZero())
                        },
                        onPayNowClicked = {
                            proceedClicked = true
                            viewModel.postClickEvent(
                                buttonType = DailySavingsEventKey.Proceed,
                                defaultAmount = recommendedAmount
                            )
                            oneTapPayment()
                        },
                    )
                } else {
                    DefaultPayment(
                        payNowCtaText = staticUpdateFlowData.value.data?.data?.paymentButtonText.orEmpty(),
                        onPayNowClicked = {
                            oneTapPayment()
                        },
                    )
                }
            }
        )
    }


    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        getData()
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mandatePaymentApi.fetchLastUsedUpiApp(null)
                    .collectUnwrapped(
                        onSuccess = { upiApp ->
                            mandateUpiApp = upiApp
                            viewModel.postShownEvent(
                                dsCurrent = viewModel.dailySavingsValuesFlow.value?.currentDailySavingsAmount?.toInt()
                                    .orZero(),
                                dsRecommended = viewModel.dailySavingsValuesFlow.value?.recommendedDailySavingsAmount?.toInt()
                                    .orZero(),
                                suggestedUpiApp = mandateUpiApp?.appName ?: NoUpiAppShown,
                            )
                        }
                    )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isAutoPayResetRequiredFlowData.collectUnwrapped(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        initiatePayment(it.data)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.staticUpdateFlowData.collectUnwrapped(
                    onSuccess = {
                        recommendedAmount = it.data?.dsRecommendedAmount.orZero()
                        viewModel.updateDailySavingsFlowValues(data = it.data, null)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.dailySavingsValuesFlow.collectLatest {
                    dSAmount = it?.recommendedDailySavingsAmount.orZero()
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Float>(
            DailySavingConstants.DAILY_SAVING_AMOUNT_EDIT
        )?.observe(viewLifecycleOwner) {
            viewModel.updateDailySavingsFlowValues(data = null, updatedRecommendedValue = it)
            this.view?.hideKeyboard()
        }
    }

    private fun getData() {
        viewModel.fetchUpdateDailyInvestmentStaticData()
    }

    private fun initiateMandateFlowForCustomUI(
        mandateWorkflowType: MandateWorkflowType
    ) {
        dailyInvestmentApi.initiateDailySavingCustomUIMandateBottomSheet(
            customMandateUiFragmentId = R.id.updateDailySavingsV3Fragment,
            newDailySavingAmount = dSAmount.orZero(),
            mandateWorkflowType = mandateWorkflowType,
            flowSource = MandatePaymentEventKey.FeatureFlows.UpdateDailySaving,
            customBottomSheetDeeplink = "android-app://com.jar.app/dailyInvestMandateBottomSheet/$dSAmount/${R.id.updateDailySavingsV3Fragment}",
            popUpToId = R.id.updateDailySavingsV3Fragment,
            userLifecycle = prefsApi.getUserLifeCycleForMandate()
        )
    }

    private fun openEditValueBottomSheet() {
        val uri =
            "android-app://com.jar.app/updateDailySavingsFragmentEditValueBottomSheet/${dSAmount.orZero()}"
        navigateTo(uri)
    }

    private fun oneTapPayment() {
        if (viewModel.dailySavingsValuesFlow.value?.currentDailySavingsAmount.orZero() < viewModel.dailySavingsValuesFlow.value?.recommendedDailySavingsAmount.orZero()) {
            viewModel.isAutoPayResetRequired(dSAmount.orZero())
        } else if (viewModel.dailySavingsValuesFlow.value?.currentDailySavingsAmount.orZero() > viewModel.dailySavingsValuesFlow.value?.recommendedDailySavingsAmount.orZero()) {
            viewModel.enableOrUpdateDailySaving(dSAmount!!)
            coreUiApi.openGenericPostActionStatusFragment(
                GenericPostActionStatusData(
                    postActionStatus = PostActionStatus.ENABLED.name,
                    header = getString(R.string.feature_daily_investment_daily_investment_setup_successfully),
                    headerColorRes = com.jar.app.core_ui.R.color.color_1EA787,
                    title = getString(
                        R.string.feature_daily_investment_x_will_be_auto_saved_starting_tomorrow,
                        dSAmount.orZero().toInt()
                    ),
                    titleColorRes = com.jar.app.core_ui.R.color.white,
                    imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                    headerTextSize = 18f,
                    titleTextSize = 16f,
                )
            ) {
                EventBus.getDefault().post(RefreshDailySavingEvent())
                navigateToHome()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.feature_daily_investment_update_daily_saving_error_state),
                Toast.LENGTH_LONG
            )
        }
    }

    private fun initiateMandateFlow(
        mandateAmount: Float,
        mandateWorkflowType: MandateWorkflowType
    ) {
        dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
            mandateAmount = mandateAmount.orZero(),
            source = MandatePaymentEventKey.FeatureFlows.UpdateDailySaving,
            authWorkflowType = mandateWorkflowType,
            newDailySavingAmount = dSAmount.orZero(),
            popUpToId = R.id.dailySavingsV2Fragment,
            userLifecycle = prefsApi.getUserLifeCycleForMandate()
        )
    }

    private fun initiatePayment(it: AutopayResetRequiredResponse) {
        dismissProgressBar()
        val mandateWorkflowType =
            it.authWorkflowType?.let { MandateWorkflowType.valueOf(it) }
                ?: run { MandateWorkflowType.PENNY_DROP }
        if (!proceedClicked) {
            initiateMandateFlowForCustomUI(mandateWorkflowType)
        } else {
            initiateMandatePaymentJob?.cancel()
            initiateMandatePaymentJob = appScope.launch(Dispatchers.Main) {
                if (mandateUpiApp == null) {
                    initiateMandateFlow(it.getFinalMandateAmount(), mandateWorkflowType)
                } else {
                    mandatePaymentApi.initiateMandatePaymentWithUpiApp(
                        paymentPageHeaderDetails = PaymentPageHeaderDetail(
                            toolbarHeader = getString(R.string.feature_daily_investment_daily_investment),
                            title = "Let’s automate your Daily Savings of $dSAmount",
                            featureFlow = MandatePaymentEventKey.FeatureFlows.UpdateDailySaving,
                            userLifecycle = Update_daily_savings,
                            savingFrequency = MandatePaymentCommonConstants.MandateStaticContentType.INSURANCE_AUTOPAY_SETUP.name,
                            mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.INSURANCE_AUTOPAY_SETUP,
                            toolbarIcon = R.drawable.feature_daily_investment_ic_daily_saving
                        ),

                        initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
                            mandateAmount = it.newMandateAmount.orZero(),
                            authWorkflowType = if (it.authWorkflowType != null) MandateWorkflowType.valueOf(
                                it.authWorkflowType.orEmpty()
                            ) else MandateWorkflowType.TRANSACTION,
                            subscriptionType = SavingsType.DAILY_SAVINGS.name,
                        ),

                        upiApp = mandateUpiApp!!,
                        initiateMandateFragmentId = R.id.updateDailySavingsV3Fragment
                    ).collectUnwrapped(onSuccess = {
                        if (it.second.getAutoInvestStatus() == MandatePaymentProgressStatus.SUCCESS)
                            viewModel.enableOrUpdateDailySaving(dSAmount!!)
                        dailyInvestmentApi.openDailySavingSetupStatusFragment(
                            dailySavingAmount = dSAmount!!,
                            fetchAutoInvestStatusResponse = it.second,
                            mandatePaymentResultFromSDK = it.first,
                            isFromOnboarding = false,
                            flowName = Update_daily_savings,
                            popUpToId = R.id.updateDailySavingsV3Fragment,
                            userLifecycle = prefsApi.getUserLifeCycleForMandate()
                        )
                    }, onError = { errorMessage, errorCode ->
                        if (errorCode == com.jar.app.feature_mandate_payment.impl.util.MandateErrorCodes.BACK_PRESSES_FROM_PAYMENT_SCREEN) {
                        } else if (errorMessage.isNotEmpty())
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show()
                    })
                }
            }
        }
    }

    fun navigateToHome() {
        navigateTo(
            uri = BaseConstants.InternalDeepLinks.HOME,
            popUpTo = R.id.updateDailySavingsV3Fragment,
            inclusive = true
        )
    }

}