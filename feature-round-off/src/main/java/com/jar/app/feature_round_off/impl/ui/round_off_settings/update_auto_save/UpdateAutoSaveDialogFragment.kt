package com.jar.app.feature_round_off.impl.ui.round_off_settings.update_auto_save

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_daily_investment.api.domain.event.SetupAutoPayEvent
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_round_off.databinding.FeatureRoundOffDialogUpdateAutoSaveBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class UpdateAutoSaveDialogFragment :
    BaseDialogFragment<FeatureRoundOffDialogUpdateAutoSaveBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffDialogUpdateAutoSaveBinding
        get() = FeatureRoundOffDialogUpdateAutoSaveBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        binding.btnUpdate.setDebounceClickListener {
            EventBus.getDefault().post(
                SetupAutoPayEvent(
                    null,
                    null,
                    flowName = MandatePaymentEventKey.FeatureFlows.AutomateRoundoff,
                    isRoundOffAutoPayFlow = true
                )
            )
            dismiss()
        }

        binding.btnIllDoItLater.setDebounceClickListener {
            popBackStack()
        }
        binding.btnClose.setDebounceClickListener {
            popBackStack()
        }
    }
}