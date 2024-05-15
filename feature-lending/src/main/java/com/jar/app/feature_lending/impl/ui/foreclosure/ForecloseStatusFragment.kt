package com.jar.app.feature_lending.impl.ui.foreclosure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_lending.LendingNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentForcloseStatusBinding
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.experiment.ScreenData
import com.jar.app.feature_lending.shared.domain.model.v2.ForeCloseStatus
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class ForecloseStatusFragment : BaseFragment<FragmentForcloseStatusBinding>() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<ForecloseStatusFragmentArgs>()
    private val viewModelProvider by viewModels<ForeclosureStatusViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentForcloseStatusBinding
        get() = FragmentForcloseStatusBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }


    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
    }

    private fun setupUI() {
        when (args.statusType) {
            ForeCloseStatus.SUCCESS -> {
                //Do Nothing
            }
            ForeCloseStatus.FAILURE -> {
                analyticsApi.postEvent(LendingEventKeyV2.Lending_ForeclosurePaymentFailedScreenShown)
                binding.btnRetry.setText(getString(com.jar.app.core_ui.R.string.retry))
                binding.ivStatus.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_red_cross)
                binding.tvTitle.text = getCustomString(MR.strings.feature_lending_foreclose_failed_title)
                binding.tvSubTitle.text = getCustomString(MR.strings.feature_lending_foreclose_failed_sub_title)
            }
            ForeCloseStatus.PENDING -> {
                analyticsApi.postEvent(LendingEventKeyV2.Lending_ForeclosurePaymentPendingScreenShown)
                binding.btnRetry.setText(getString(com.jar.app.core_ui.R.string.refresh))
                binding.ivStatus.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_hour_glass)
                binding.tvTitle.text = getCustomStringFormatted(MR.strings.feature_lending_foreclose_pending_title)
                binding.tvSubTitle.text = getCustomStringFormatted(MR.strings.feature_lending_foreclose_pending_sub_title)
            }
        }

        if (args.isFromRepayment)
            binding.btnGoToHome.setText(getCustomString(MR.strings.feature_lending_see_loan_overview))
    }

    private fun setupListeners() {
        binding.btnGoToHome.setDebounceClickListener {
            analyticsApi.postEvent(
                event = if (args.statusType == ForeCloseStatus.FAILURE)
                    LendingEventKeyV2.Lending_ForeclosurePaymentFailedScreenClicked
                else
                    LendingEventKeyV2.Lending_ForeclosurePaymentPendingScreenClicked,
                values = mapOf(
                    LendingEventKeyV2.action to (if (args.isFromRepayment) "See Jar Loan Overview" else "Go to Home")
                )
            )
            if (args.isFromRepayment) {
                popBackStack(
                    R.id.repaymentOverviewFragment,
                    inclusive = false
                )
            } else
                EventBus.getDefault().post(GoToHomeEvent(ForecloseStatusFragment::class.java.name))
        }

        binding.btnRetry.setDebounceClickListener {
            if (args.statusType == ForeCloseStatus.FAILURE) {
                //pop to go back Foreclosure Summary screen
                gotoForeclosureSummaryScreen()
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_ForeclosurePaymentFailedScreenClicked,
                    values = mapOf(
                        LendingEventKeyV2.action to "Retry"
                    )
                )
            } else {
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_ForeclosurePaymentFailedScreenClicked,
                    values = mapOf(
                        LendingEventKeyV2.action to "Refresh"
                    )
                )
                //Refresh status
                    viewModel.fetchManualPaymentStatus(args.orderId, OneTimePaymentGateway.JUSPAY.name)
            }
        }

        binding.tvContactSupport.setDebounceClickListener {
            analyticsApi.postEvent(
                event = if (args.statusType == ForeCloseStatus.FAILURE)
                    LendingEventKeyV2.Lending_ForeclosurePaymentFailedScreenClicked
                else
                    LendingEventKeyV2.Lending_ForeclosurePaymentPendingScreenClicked,
                values = mapOf(
                    LendingEventKeyV2.action to "Contact Support"
                )
            )
            val number = remoteConfigManager.getWhatsappNumber()
            val message = getCustomStringFormatted(MR.strings.feature_lending_foreclose_support_messsage,
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(number, message)
        }
    }

    private fun gotoForeclosureSummaryScreen() {
        navigateTo(
            LendingNavigationDirections.actionToForecloseSummaryFragment(args.loanId, false),
            popUpTo = R.id.forecloseStatusFragment,
            inclusive = true
        )
    }

    private fun gotFinalDetailsScreen() {
        val argsData = encodeUrl(
            serializer.encodeToString(
                ReadyCashScreenArgs(
                    loanId = args.loanId,
                    source = ForecloseStatusFragment::class.java.name,
                    type = "",
                    screenName = ReadyCashScreen.DISBURSAL,
                    screenData = ScreenData(
                        shouldShowProgress = false,
                        nextScreen = "HOME_PAGE", //hardcoded data not required
                        backScreen = "HOME_PAGE",
                        status = "PENDING"
                    ),
                    isRepeatWithdrawal = false,
                    isRepayment = true
                )
            )
        )
        navigateTo(
            "android-app://com.jar.app/loanFinalDetails/$argsData",
            popUpTo = R.id.forecloseStatusFragment,
            inclusive = true
        )
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchManualPaymentResponseFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        when (it.getManualPaymentStatus()) {
                            ManualPaymentStatus.SUCCESS -> {
                                if (args.isFromRepayment) {
                                    popBackStack(
                                        R.id.repaymentOverviewFragment,
                                        inclusive = false
                                    )
                                } else
                                    gotFinalDetailsScreen()
                            }

                            ManualPaymentStatus.PENDING -> {
                                binding.tvTitle.setText(getCustomStringFormatted(MR.strings.feature_lending_foreclose_pending_title_still))
                            }

                            else -> {
                                //Do nothing
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }
}