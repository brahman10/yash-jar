package com.jar.app.core_ui.generic_post_action.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_ui.databinding.CoreUiDialogGenericPostActionStatusBinding
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionDialogData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GenericPostActionStatusDialog :
    BaseDialogFragment<CoreUiDialogGenericPostActionStatusBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreUiDialogGenericPostActionStatusBinding
        get() = CoreUiDialogGenericPostActionStatusBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    @Inject
    lateinit var serializer: Serializer

    private val args: GenericPostActionStatusDialogArgs by navArgs()

    private val genericPostActionDialogData by lazy {
        serializer.decodeFromString<GenericPostActionDialogData>(
            decodeUrl(args.genericPostActionDialogData)
        )
    }

    private val postActionStatus by lazy {
        PostActionStatus.valueOf(genericPostActionDialogData.postActionStatus)
    }

    override fun setup() {
        uiScope.launch {
            binding.actionLottie.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                when (postActionStatus) {
                    PostActionStatus.ENABLED -> BaseConstants.LottieUrls.TICK
                    PostActionStatus.DISABLED -> BaseConstants.LottieUrls.SAD_EMOJI
                    PostActionStatus.RESUMED -> BaseConstants.LottieUrls.TICK
                }
            )
            binding.tvActionDescription.text = genericPostActionDialogData.title
            delay(genericPostActionDialogData.dialogVisibilityDuration)
            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                BaseConstants.ON_GENERIC_DIALOG_DISMISSED,
                true
            )
            dismiss()
        }
    }
}