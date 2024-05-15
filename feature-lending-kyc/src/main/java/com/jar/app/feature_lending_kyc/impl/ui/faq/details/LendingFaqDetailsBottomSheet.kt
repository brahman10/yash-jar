package com.jar.app.feature_lending_kyc.impl.ui.faq.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycBottomSheetFaqDetailsBinding
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class LendingFaqDetailsBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingKycBottomSheetFaqDetailsBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycBottomSheetFaqDetailsBinding
        get() = FeatureLendingKycBottomSheetFaqDetailsBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    private val args: LendingFaqDetailsBottomSheetArgs by navArgs()
    private var adapter: LendingFaqDetailAdapter? = null
    private val spaceItemDecoration =
        SpaceItemDecoration(0.dp, 12.dp)

    private val viewModelProvider: LendingKycFaqDetailsViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    override fun setup() {
        setupUI()
        setupListener()
        observeFlow()
    }

    private fun setupUI() {
        adapter = LendingFaqDetailAdapter()
        binding.rvFaqDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFaqDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvFaqDetails.adapter = adapter
        viewModel.getFaqTypeDetails(args.faqType)
    }

    private fun setupListener() {
        binding.ivBack.setDebounceClickListener {
            dismiss()
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.faqDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            adapter?.submitList(it.kYCFaqs.faqDataList[0].faqs)
                        }
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }
}