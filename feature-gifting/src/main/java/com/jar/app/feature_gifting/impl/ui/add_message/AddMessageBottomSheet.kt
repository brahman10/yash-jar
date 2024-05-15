package com.jar.app.feature_gifting.impl.ui.add_message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingFragmentAddMessageBinding
import com.jar.app.feature_gifting.shared.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class AddMessageBottomSheet :
    BaseBottomSheetDialogFragment<FeatureGiftingFragmentAddMessageBinding>() {

    companion object {
        private const val MAX_MESSAGE_CHAR_LIMIT = 140
    }

    private val args by navArgs<AddMessageBottomSheetArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGiftingFragmentAddMessageBinding
        get() = FeatureGiftingFragmentAddMessageBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            skipCollapsed = true,
            shouldShowFullHeight = false,
            isCancellable = false,
            isDraggable = false
        )

    override fun setup() {
        binding.btnClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }

        binding.btnDone.setDebounceClickListener {
            setData()
            dismissAllowingStateLoss()
        }

        binding.etMessage.doAfterTextChanged {
            binding.tvCharCount.text = "${it?.length.orZero()}/$MAX_MESSAGE_CHAR_LIMIT"
        }

        binding.etMessage.setText(args.message)
    }

    private fun setData() {
        findNavController().getBackStackEntry(R.id.sendGiftFragment).savedStateHandle
            .set(Constants.EXTRA_MESSAGE, binding.etMessage.text?.toString())
    }

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }
}