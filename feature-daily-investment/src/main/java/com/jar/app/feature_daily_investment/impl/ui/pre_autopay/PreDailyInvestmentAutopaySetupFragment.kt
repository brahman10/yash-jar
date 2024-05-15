package com.jar.app.feature_daily_investment.impl.ui.pre_autopay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.getDateMonthNameAndYear
import com.jar.app.base.util.isFragmentInBackStack
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentPreAutopayBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PreDailyInvestmentAutopaySetupFragment :
    BaseFragment<FeatureDailyInvestmentFragmentPreAutopayBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentPreAutopayBinding
        get() = FeatureDailyInvestmentFragmentPreAutopayBinding::inflate

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    private val viewModel: PreDailyInvestmentAutopaySetupViewModel by viewModels()

    private val args: PreDailyInvestmentAutopaySetupFragmentArgs by navArgs()
    private var roundOffAmount = 0f
    private var autoPaySetupAmount = 0f
    private var mandateWorkflowType: com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType? =
        null

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
        viewModel.isAutoPayResetRequired(args.dailySavingAmount)
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getString(R.string.feature_daily_savings)
        binding.toolbar.separator.isVisible = true
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
        binding.btnProceed.setDebounceClickListener {
            mandateWorkflowType?.let {
                analyticsApi.postEvent(
                    DailySavingsEventKey.Clicked_Proceed_AutopayMandateScreen,
                    mapOf(
                        DailySavingsEventKey.SourceFlow to "DailySavings",
                        DailySavingsEventKey.DailySavingsAmount to args.dailySavingAmount,
                        DailySavingsEventKey.MandateAmount to autoPaySetupAmount
                    )
                )
                dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                    mandateAmount = autoPaySetupAmount,
                    source = when (args.flowType) {
                        BaseConstants.DSPreAutoPayFlowType.SETUP_DS -> {
                            MandatePaymentEventKey.FeatureFlows.SetupDailySaving
                        }

                        BaseConstants.DSPreAutoPayFlowType.UPDATE_DS -> {
                            MandatePaymentEventKey.FeatureFlows.UpdateDailySaving
                        }

                        BaseConstants.DSPreAutoPayFlowType.POST_SETUP_DS -> {
                            MandatePaymentEventKey.FeatureFlows.UpdateDailySaving
                        }

                        else -> {
                            MandatePaymentEventKey.FeatureFlows.UpdateDailySaving
                        }
                    },
                    authWorkflowType = it,
                    newDailySavingAmount = args.dailySavingAmount,
                    popUpToId = if (findNavController().isFragmentInBackStack(R.id.dailyInvestmentCustomOnboardingVariantsFragment)) R.id.dailyInvestmentCustomOnboardingVariantsFragment else R.id.preDailyInvestmentAutopaySetupFragment,
                    userLifecycle = prefsApi.getUserLifeCycleForMandate()
                )
            }
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                autoPaySetupAmount = it.getFinalMandateAmount()
                it.authWorkflowType?.let {
                    mandateWorkflowType =
                        com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType.valueOf(
                            it
                        )
                }
                viewModel.fetchUserRoundOffDetails()
            },
            onError = {
                dismissProgressBar()
            }
        )
        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                dismissProgressBar()
                roundOffAmount = it.subscriptionAmount

                binding.tvXTotalAmount.text =
                    getString(
                        R.string.feature_daily_investment_rsx_per_day,
                        autoPaySetupAmount.toInt()
                    )

                binding.tvDSRsXPerDay.text =
                    getString(
                        R.string.feature_daily_investment_rsx_per_day,
                        args.dailySavingAmount.toInt()
                    )

                binding.tvUptoRORsX.text =
                    getString(
                        R.string.feature_daily_investment_upto_x_per_day,
                        roundOffAmount.toInt()
                    )

                binding.tvActivatedOn.isVisible = it.updateDate != null

                binding.tvActivatedOn.text =
                    getString(
                        R.string.feature_daily_investment_activated_on_s_date,
                        it.updateDate?.getDateMonthNameAndYear("d MMM''yy").orEmpty()
                    )

                binding.tvJarWillNeverDebitX.text =
                    getString(
                        R.string.feature_daily_investment_jar_will_never_debit_more_than_xf,
                        autoPaySetupAmount.toInt()
                    )
                analyticsApi.postEvent(
                    DailySavingsEventKey.Shown_DailySavingsScreen,
                    mapOf(
                        DailySavingsEventKey.SourceFlow to "DailySavings",
                        DailySavingsEventKey.DailySavingsAmount to args.dailySavingAmount,
                        DailySavingsEventKey.MandateAmount to autoPaySetupAmount,
                        DailySavingsEventKey.UserLifecycle to prefsApi.getUserLifeCycleForMandate().orEmpty()
                    )
                )
            }
        )
    }
}