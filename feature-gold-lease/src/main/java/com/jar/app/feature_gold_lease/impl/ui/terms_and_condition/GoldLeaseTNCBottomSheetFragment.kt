package com.jar.app.feature_gold_lease.impl.ui.terms_and_condition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseTncBottomSheetBinding
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseTNCViewModel
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class GoldLeaseTNCBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentGoldLeaseTncBottomSheetBinding>() {

    private val viewModelProvider by viewModels<GoldLeaseTNCViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseTncBottomSheetBinding
        get() = FragmentGoldLeaseTncBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isDraggable = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup() {
        observeLiveData()
        setupListeners()
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun getData() {
        viewModel.fetchTermsAndConditions()
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseTNCFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            binding.tvTnC.text = it.termsAndConditions
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                        dismissAllowingStateLoss()
                    }
                )
            }
        }
    }
}