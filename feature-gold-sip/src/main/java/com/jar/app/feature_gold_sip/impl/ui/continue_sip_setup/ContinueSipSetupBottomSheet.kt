package com.jar.app.feature_gold_sip.impl.ui.continue_sip_setup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipBottomSheetContinueSipSetupBinding
import com.jar.app.feature_gold_sip.shared.util.GoldSipConstants
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class ContinueSipSetupBottomSheet :
    BaseBottomSheetDialogFragment<FeatureGoldSipBottomSheetContinueSipSetupBinding>() {
    private val args by navArgs<ContinueSipSetupBottomSheetArgs>()
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipBottomSheetContinueSipSetupBinding
        get() = FeatureGoldSipBottomSheetContinueSipSetupBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {

        Glide.with(requireContext()).
        load(com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.IllustrationUrl.CONTINUE_SAVINGS_PLAN)
            .into(binding.ivContinueSavingsPlan)

        binding.btnContinueSetup.setDebounceClickListener {
            dismiss()
        }

        binding.btnCancelSetup.setDebounceClickListener {
            EventBus.getDefault().post(
                GoToHomeEvent(
                    "SetupGoldSip",
                    if (findNavController().isPresentInBackStack(R.id.setupGoldSipFragment))
                        BaseConstants.HomeBottomNavigationScreen.PROFILE
                    else BaseConstants.HomeBottomNavigationScreen.HOME
                )
            )
        }

        binding.ivCross.setDebounceClickListener {
            dismiss()
        }
    }
}