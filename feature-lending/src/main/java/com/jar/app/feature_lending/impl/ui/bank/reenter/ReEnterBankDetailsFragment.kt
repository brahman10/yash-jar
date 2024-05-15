package com.jar.app.feature_lending.impl.ui.bank.reenter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.feature_lending.databinding.FragmentReEnterBankDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ReEnterBankDetailsFragment : BaseBottomSheetDialogFragment<FragmentReEnterBankDetailsBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReEnterBankDetailsBinding
        get() = FragmentReEnterBankDetailsBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(shouldShowFullHeight = true)

    override fun setup() {

    }

    private fun setupToolbar() {
        binding.lendingToolbar.btnBack.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_close)
        binding.lendingToolbar.btnBack.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.white
            )
        )

        binding.lendingToolbar.separator.isVisible = false

        binding.lendingToolbar.tvTitle.text = getString(com.jar.app.feature_lending.shared.R.string.feature_lending_enter_bank_details)
    }
}