package com.jar.app.feature_vasooli.impl.ui.entry

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_vasooli.databinding.FragmentPromptBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class PromptBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentPromptBottomSheetBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPromptBottomSheetBinding
        get() = FragmentPromptBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.btnOkay.setDebounceClickListener {
            dismissAllowingStateLoss()
        }
    }

}