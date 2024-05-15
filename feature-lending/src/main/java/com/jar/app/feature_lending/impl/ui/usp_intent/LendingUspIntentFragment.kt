package com.jar.app.feature_lending.impl.ui.usp_intent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingUspIntentBinding
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class LendingUspIntentFragment : BaseFragment<FragmentLendingUspIntentBinding>(){

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<LendingUspIntentFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingUspIntentBinding
        get() = FragmentLendingUspIntentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        initClickListeners()
        observeLiveData()
        if (args.isFromSellGoldCta) {
            analyticsHandler.postEvent(
                LendingEventKey.Shown_SellGold_GetReadyCash,
                mapOf(
                    LendingEventKey.goldAmount to lendingViewModel.userGoldBalance.toString()
                )
            )
        }
    }

    private fun setupUI() {
        setupToolbar()

//        binding.btnWithdraw.isVisible = args.isFromSellGoldCta
        binding.btnGetInstaCash.setText(
            if (args.isFromSellGoldCta)
                getCustomString(MR.strings.feature_lending_apply_now).toSpannable()
            else
                getCustomString(MR.strings.feature_lending_next).toSpannable()
        )
    }

    private fun initClickListeners() {
        binding.btnGetInstaCash.setDebounceClickListener {
            if (args.isFromSellGoldCta) {
                analyticsHandler.postEvent(
                    LendingEventKey.SellGold_JarReadyCash_Apply,
                    mapOf(
                        LendingEventKey.goldAmount to lendingViewModel.userGoldBalance.toString()
                    )
                )
            } else {
                analyticsHandler.postEvent(
                    LendingEventKey.OnClick_ReadyCash_Overview_Next,
                    mapOf(
                        LendingEventKey.entryPoint to args.flowType
                    )
                )
            }
            navigateTo(
                "android-app://com.jar.app/lendingOnboardingFragment/SELL_GOLD",
                shouldAnimate = true,
                popUpTo = R.id.lendingUspIntentFragment,
                inclusive = true
            )
        }

        binding.btnWithdraw.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingEventKey.SellGold_WithdrawCash,
                mapOf(
                    LendingEventKey.goldAmount to lendingViewModel.userGoldBalance.toString()
                )
            )
            navigateTo(
                BaseConstants.InternalDeepLinks.SELL_GOLD_REVAMP,
                popUpTo = R.id.lendingUspIntentFragment,
                inclusive = true
            )
        }
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text =
            if (args.isFromSellGoldCta)
                getCustomString(MR.strings.feature_lending_sell_gold)
            else
                getCustomString(MR.strings.feature_lending_jar_ready_cash)

        binding.toolbar.separator.isVisible = true

        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            if (args.isFromSellGoldCta.not()) {
                analyticsHandler.postEvent(
                    LendingEventKey.OnClick_ReadyCash_Overview_Back,
                    mapOf(
                        LendingEventKey.entryPoint to args.flowType
                    )
                )
            }
            popBackStack()
        }
    }

    private fun observeLiveData() {
        lendingViewModel.networkStateLiveData.observe(viewLifecycleOwner) {
            binding.toolbar.clNetworkContainer.isSelected = it
            binding.toolbar.tvInternetConnectionText.text =
                if (it) getString(com.jar.app.core_ui.R.string.core_ui_we_are_back_online) else getString(
                    com.jar.app.core_ui.R.string.core_ui_no_internet_available_please_try_again)
            binding.toolbar.tvInternetConnectionText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (it) com.jar.app.core_ui.R.drawable.ic_wifi_on else com.jar.app.core_ui.R.drawable.ic_wifi_off, 0, 0, 0
            )
            if (it) {
                if (binding.toolbar.networkExpandableLayout.isExpanded) {
                    uiScope.launch {
                        delay(500)
                        binding.toolbar.networkExpandableLayout.collapse(true)
                    }
                }
            } else {
                binding.toolbar.networkExpandableLayout.expand(true)
            }
        }
    }
}