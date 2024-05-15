package com.jar.app.feature.onboarding.ui.experian_bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.databinding.BottomSheetExperianTcBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class ExperianTCBottomSheet : BaseBottomSheetDialogFragment<BottomSheetExperianTcBinding>() {

    private val viewModelProvider: ExperianBottomSheetViewModelAndroid by viewModels()

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetExperianTcBinding
        get() = BottomSheetExperianTcBinding::inflate


    override val bottomSheetConfig = DEFAULT_CONFIG

    override fun setup() {
        viewModel.fetchBottomSheetData()
        observeLiveData()
        setupListeners()
    }

    fun observeLiveData() {
        viewModel.bottomSheetDataFlow.asLiveData().observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                binding.shimmerPlaceholder.isVisible = true
                binding.clContainer.isVisible = false
                binding.shimmerPlaceholder.startShimmer()
            },
            onSuccess = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
                setData(it)
            },
            onError = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
            }
        )
    }

    private fun setData(data: com.jar.app.feature_onboarding.shared.domain.model.ExperianTCResponse) {

        binding.tvHeading.text = data.experianConsent.title

        binding.tvBody.text = data.experianConsent.description

    }

    fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            dismiss()
        }
    }
}