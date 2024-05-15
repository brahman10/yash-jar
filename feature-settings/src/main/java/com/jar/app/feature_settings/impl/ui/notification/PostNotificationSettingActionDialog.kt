package com.jar.app.feature_settings.impl.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_settings.databinding.FeatureSettingsDialogPostNotificationSettingActionBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class PostNotificationSettingActionDialog :
    BaseDialogFragment<FeatureSettingsDialogPostNotificationSettingActionBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureSettingsDialogPostNotificationSettingActionBinding
        get() = FeatureSettingsDialogPostNotificationSettingActionBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = true)

    private val args: PostNotificationSettingActionDialogArgs by navArgs()

    override fun setup() {
        uiScope.launch {
            binding.actionLottie.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                if (args.isSuccessAction) BaseConstants.LottieUrls.TICK else BaseConstants.LottieUrls.SAD_EMOJI
            )
            binding.tvActionDescription.text = args.description
            delay(3000)
            dismiss()
        }
    }
}