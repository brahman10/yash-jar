package com.jar.app.feature_lending_kyc.impl.ui.pan.manual.loading

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycDialogEnterPanManuallyLoadingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class EnterPanManuallyLoadingDialog :
    BaseDialogFragment<FeatureLendingKycDialogEnterPanManuallyLoadingBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycDialogEnterPanManuallyLoadingBinding
        get() = FeatureLendingKycDialogEnterPanManuallyLoadingBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI(){

    }

    private fun setupListener(){

    }

    private fun observeLiveData(){

    }
}