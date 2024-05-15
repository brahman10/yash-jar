package com.jar.app.feature_transaction.impl.ui.details_bottom_sheet

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.feature_transaction.databinding.FeatureTransactionStaticTransactionDetailBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PauseTransactionDetailBottomSheet :
    BaseBottomSheetDialogFragment<FeatureTransactionStaticTransactionDetailBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionStaticTransactionDetailBottomSheetBinding
        get() = FeatureTransactionStaticTransactionDetailBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {

    }

    private fun setupListener() {

    }

    private fun observeLiveData() {

    }
}