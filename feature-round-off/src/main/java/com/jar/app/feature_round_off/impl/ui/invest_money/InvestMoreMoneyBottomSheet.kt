package com.jar.app.feature_round_off.impl.ui.invest_money

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_round_off.databinding.FeatureRoundOffInvestMoreMoneyBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class InvestMoreMoneyBottomSheet :
    BaseBottomSheetDialogFragment<FeatureRoundOffInvestMoreMoneyBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffInvestMoreMoneyBottomSheetBinding
        get() = FeatureRoundOffInvestMoreMoneyBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel: InvestMoreMoneyViewModel by viewModels()

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + com.jar.app.feature_round_off.shared.util.RoundOffConstants.Illustration.INVEST_MORE_MONEY)
            .into(binding.ivHeaderIllustration)
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_Invest10,
            mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
        )
    }

    private fun setupListener() {
        binding.btnClose.setDebounceClickListener {
            dismiss()
        }
        binding.btnNoDontAllow.setDebounceClickListener {
            dismiss()
        }
        binding.btnYesAllow.setDebounceClickListener {
            viewModel.updateUserInvestNoSpends()
        }
    }

    private fun observeLiveData() {
        viewModel.updateUserSettingsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.AutomaticRoundoff_Invest10,
                    mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.YesAllowClicked)
                )
                dismissProgressBar()
                dismiss()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }
}