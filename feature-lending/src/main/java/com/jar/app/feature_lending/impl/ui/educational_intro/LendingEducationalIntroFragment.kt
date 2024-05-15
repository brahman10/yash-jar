package com.jar.app.feature_lending.impl.ui.educational_intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentEducationalIntroBinding
import com.jar.app.feature_lending.impl.domain.event.LendingNavigateToSellGoldEvent
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
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
internal class LendingEducationalIntroFragment :
    BaseFragment<FeatureLendingFragmentEducationalIntroBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider: LendingHostViewModelAndroid by activityViewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val arguments by navArgs<LendingEducationalIntroFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentEducationalIntroBinding
        get() = FeatureLendingFragmentEducationalIntroBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_PDetailsFirstScreenLaunched,
            values = mapOf(
                LendingEventKeyV2.entry_type to args.source
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupToolbar()
        setupUI()
        initClickListeners()
        observeLiveData()
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_lending_loan_offer)
        binding.toolbar.separator.isVisible = true
    }

    private fun setupUI() {
        viewModel.preApprovedData?.let {
            setUIData(it)
        } ?: run {
            viewModel.fetchPreApprovedData()
        }
        binding.btnSellGold.isVisible = isFromSellGoldFlow()
        if (isFromSellGoldFlow()) {
            binding.btnCheckCreditLimit.setText(getCustomString(MR.strings.feature_lending_apply_for_ready_cash))
        }
    }

    private fun isFromSellGoldFlow() = args.source == BaseConstants.LendingFlowType.SELL_GOLD

    private fun initClickListeners() {
        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_BackButtonClicked,
                values = mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.INTRO_SCREEN
                )
            )
            args.screenData?.let {
                EventBus.getDefault().postSticky(
                    ReadyCashNavigationEvent(
                        whichScreen = it.backScreen,
                        source = args.screenName,
                        popupToId = R.id.lendingEducationalIntroFragment,
                        isBackFlow = true
                    )
                )
            }
        }

        binding.btnCheckCreditLimit.setDebounceClickListener {
            analyticsApi.postEvent(
                if (isFromSellGoldFlow())
                    LendingEventKeyV2.Lending_WithdrawalApplyRCashButtonClicked
                else
                    LendingEventKeyV2.Lending_PDetailsCheckCreditLimitClicked
            )
            args.screenData?.let {
                EventBus.getDefault().postSticky(
                    ReadyCashNavigationEvent(
                        whichScreen = it.nextScreen,
                        source = args.screenName,
                        popupToId = R.id.lendingEducationalIntroFragment
                    )
                )
            }
        }

        binding.btnSellGold.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKey.SellGold_WithdrawCash,
            )
            EventBus.getDefault().post(LendingNavigateToSellGoldEvent())
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.preApprovedDataFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setUIData(it)
                        }

                    },
                    onError = {errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setUIData(offerAmount: PreApprovedData) {
        binding.tvTrustedBy.text = getCustomStringFormatted(MR.strings.feature_lending_trusted_by_s_plus_users,
            offerAmount.trustCount.orEmpty()
        )
        binding.tvGetApprovedLoanUpto.text = getCustomStringFormatted(MR.strings.feature_lending_get_pre_approved_loan_upto_5l,
                offerAmount.offerAmount.orZero().toInt().getFormattedAmount()
            )
    }
}