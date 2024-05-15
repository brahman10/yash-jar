package com.jar.app.feature_daily_investment.impl.ui.disable_savings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.feature_daily_investment.NavigationDailyInvestmentDirections
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailySavingsDisableDailySavingsConfirmationDialogNewBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.Clicked_Disable_DisableDailySavingsPopUp
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.Shown_Success_DisableDailySavingsPopUp
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PauseOrDisableDailySavingsConfirmationBottomSheetNew :
    BaseBottomSheetDialogFragment<FeatureDailySavingsDisableDailySavingsConfirmationDialogNewBinding>() {

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false)

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var coreUiApi: CoreUiApi

    private val args by navArgs<PauseOrDisableDailySavingsConfirmationBottomSheetNewArgs>()


    private val viewModelProvider by viewModels<DisableDailySavingViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailySavingsDisableDailySavingsConfirmationDialogNewBinding
        get() = FeatureDailySavingsDisableDailySavingsConfirmationDialogNewBinding::inflate

    private var isAutoInvestDisabled = false

    override fun setup() {
        analyticsHandler.postEvent(DailySavingsEventKey.Shown_DisableDailySavingsPopUp_new)
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/sad-emoji.json"
        )
        binding.btnPause.isVisible = args.isPaused.not()
        binding.tvHeader.text = if (args.isPaused)
            getString(R.string.feature_daily_investment_sure_you_want_to_disable_daily_saving)
        else getString(R.string.feature_daily_investment_sure_you_want_to_cancel_daily_saving)
    }

    private fun setupListeners() {
        binding.btnChangeAmount.setDebounceClickListener {
            analyticsHandler.postEvent(DailySavingsEventKey.Clicked_Change_Amount_DisableDailySavingsPopUp)
            val defaultAmount = args.defaultAmount
            navigateTo(
                "android-app://com.jar.app/updateDailySavingBottomSheet/${defaultAmount}/false",
                true
            )
        }


        binding.btnDisable.setDebounceClickListener {
            analyticsHandler.postEvent(Clicked_Disable_DisableDailySavingsPopUp)
            viewModel.disableDailySavings()
        }

        binding.btnPause.setDebounceClickListener {
            analyticsHandler.postEvent(DailySavingsEventKey.Clicked_Pause_DisableDailySavingsPopUp)
            navigateTo(
                NavigationDailyInvestmentDirections.actionToPauseDailySavings(),
                popUpTo = R.id.pauseOrDisableDailySavingsConfirmationBottomSheet,
                inclusive = true
            )
        }

        binding.btnClose.setDebounceClickListener {
            dismiss()
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.disableDailySavingFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        coreUiApi.openGenericPostActionStatusFragment(
                            GenericPostActionStatusData(
                                postActionStatus = PostActionStatus.DISABLED.name,
                                header = getString(R.string.feature_daily_investment_daily_saving_disabled),
                                title = getString(R.string.feature_daily_investment_daily_saving_we_re_sorry_to_disable_daily_saving),
                                imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_disabled,
                                headerTextSize = 20f,
                                titleTextSize = 12f,
                                titleColorRes = com.jar.app.core_ui.R.color.color_ACA1D3
                            )
                        ) {
                            analyticsHandler.postEvent(
                                DailySavingsEventKey.Shown_StopConfirmation_DSSettings, mapOf(
                                    DailySavingsEventKey.Status to "Disabled"
                                )
                            )
                            analyticsHandler.postEvent(Shown_Success_DisableDailySavingsPopUp)
                            EventBus.getDefault().post(DisableDailySavingEvent())
                            EventBus.getDefault().post(RefreshDailySavingEvent())
                            popBackStack(
                                R.id.pauseOrDisableDailySavingsConfirmationBottomSheetNew,
                                true
                            )
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
}