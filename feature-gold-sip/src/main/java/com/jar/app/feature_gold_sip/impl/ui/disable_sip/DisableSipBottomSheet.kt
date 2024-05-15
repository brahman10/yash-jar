package com.jar.app.feature_gold_sip.impl.ui.disable_sip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.RefreshGoldSipEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipBottomSheetDisableSipBinding
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class DisableSipBottomSheet :
    BaseBottomSheetDialogFragment<FeatureGoldSipBottomSheetDisableSipBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipBottomSheetDisableSipBinding
        get() = FeatureGoldSipBottomSheetDisableSipBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var coreUiApi: CoreUiApi

    private val args: DisableSipBottomSheetArgs by navArgs()

    private val viewModelProvider by viewModels<DisableSipViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override fun setup() {
        binding.btnWantToPauseInstead.isVisible = args.isAlreadyPaused.not()
        viewModel.fireSipBottomSheetEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_StopSIPBottomSheet,
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown
        )
        setupListener()
        observeLiveData()
    }

    private fun setupListener() {
        binding.btnDisableSip.setDebounceClickListener {
            viewModel.fireSipBottomSheetEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_StopSIPBottomSheet,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Disable
            )
            viewModel.disableSip()
        }

        binding.btnWantToPauseInstead.setDebounceClickListener {
            viewModel.fireSipBottomSheetEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_StopSIPBottomSheet,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Pause
            )
            navigateTo(
                DisableSipBottomSheetDirections.actionDisableSipBottomSheetToPauseSipBottomSheet(
                    args.sipSubscriptionType
                ),
                popUpTo = R.id.disableSipBottomSheet,
                inclusive = true
            )
        }

        binding.ivCross.setDebounceClickListener {
            viewModel.fireSipBottomSheetEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_StopSIPBottomSheet,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Cross
            )
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.disableGoldSipFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        disableSipAndRedirect()
                    },
                    onError = { message, errorCode ->
                        message.snackBar(binding.root)
                        dismissProgressBar()
                        disableSipAndRedirect()
                    }
                )
            }
        }
    }

    private fun disableSipAndRedirect() {
        viewModel.fireSipBottomSheetEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_DisableStatus,
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown
        )
        coreUiApi.openGenericPostActionStatusFragment(
            GenericPostActionStatusData(
                postActionStatus = PostActionStatus.DISABLED.name,
                header = getCustomString(GoldSipMR.strings.feature_gold_sip_disabled),
                title = getCustomString(GoldSipMR.strings.feature_gold_sip_you_will_no_longer_be_saving_via_sip),
                imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_disabled
            )
        ) {
            EventBus.getDefault().post(DisableSipEvent())
            EventBus.getDefault().post(RefreshGoldSipEvent())
            popBackStack(R.id.disableSipBottomSheet, true)
        }
    }
}