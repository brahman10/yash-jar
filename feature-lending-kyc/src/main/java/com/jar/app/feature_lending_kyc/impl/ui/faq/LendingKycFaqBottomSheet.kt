package com.jar.app.feature_lending_kyc.impl.ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycBottomSheetFaqBinding
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class LendingKycFaqBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingKycBottomSheetFaqBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycBottomSheetFaqBinding
        get() = FeatureLendingKycBottomSheetFaqBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    private var adapter: LendingFaqAdapter? = null
    private val viewModelProvider: LendingKycFaqViewModelAndroid by viewModels()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private val spaceItemDecoration =
        SpaceItemDecoration(0.dp, 12.dp)

    override fun setup() {
        setupUI()
        setupListener()
        observeFlow()
    }

    private fun setupUI() {
        viewModel.getLendingKycFaqList()
        adapter = LendingFaqAdapter {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_KYCFAQSection,
                mapOf(LendingKycEventKey.optionChosen to it.title)
            )
            navigateTo(
                LendingKycFaqBottomSheetDirections.actionLendingKycFaqBottomSheetToLendingFaqDetailsBottomSheet(
                    it.title
                )
            )
        }
        binding.rvFaqs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFaqs.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvFaqs.adapter = adapter
        analyticsHandler.postEvent(LendingKycEventKey.Shown_KYCFAQSection)
    }

    private fun setupListener() {
        binding.ivCross.setDebounceClickListener {
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
                            adapter?.submitList(it.faqType)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

}