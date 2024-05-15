package com.jar.app.feature.force_update

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.openAppInPlayStore
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.DialogForceUpdateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForceUpdateDialog : BaseBottomSheetDialogFragment<DialogForceUpdateBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogForceUpdateBinding
        get() = DialogForceUpdateBinding::inflate
    override val bottomSheetConfig = BottomSheetConfig(isHideable = false, isCancellable = false)

    override fun setup() {
        binding.btnYes.setDebounceClickListener {
            context?.openAppInPlayStore(BaseConstants.PLAY_STORE_URL)
        }
    }
}