package com.jar.app.feature_round_off.impl.ui.user_validation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffChangeToManualBottomSheetBinding
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RoundOffChangeToManualBottomSheet :
    BaseBottomSheetDialogFragment<FeatureRoundOffChangeToManualBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffChangeToManualBottomSheetBinding
        get() = FeatureRoundOffChangeToManualBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setup() {
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_manualPayemnt_AutomaticRoundoffSettingsScreen,
            mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown)
        )
    }

    private fun setupListener() {
        binding.btnDisableAutoSave.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_manualPayemnt_AutomaticRoundoffSettingsScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.DisableAutoSave)
            )
            navigateTo(
                NavigationRoundOffDirections.actionToRoundOffAutoSaveDisabledFragment(),
                popUpTo = R.id.roundOffChangeToManualBottomSheet,
                inclusive = true
            )
        }

        binding.btnCancel.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_manualPayemnt_AutomaticRoundoffSettingsScreen,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Cancel)
            )
            dismiss()
        }
    }
}