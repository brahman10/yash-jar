package com.jar.app.feature_kyc.impl.ui.kyc_faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.BaseItemDecoration
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentKycFaqV2Binding
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class KycFaqFragmentV2 : BaseBottomSheetDialogFragment<FragmentKycFaqV2Binding>(),
    BaseItemDecoration.SectionCallback {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentKycFaqV2Binding
        get() = FragmentKycFaqV2Binding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(shouldShowFullHeight = true, isDraggable = false)

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(0.dp, 8.dp)

    private val headerItemDecoration =
        com.jar.app.core_ui.item_decoration.HeaderItemDecoration(this)

    private val adapter = KycFaqAdapter()

    private val baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()

    private val viewModelProvider by viewModels<KycFaqViewModelAndroid>()
    private val viewModel by lazy { viewModelProvider.getInstance() }


    override fun setup() {
        getData()
        setupUI()
        observeFlow()
    }

    private fun getData() {
        viewModel.fetchFaq()
    }

    private fun setupUI() {
        binding.rvFaq.adapter = adapter
        binding.rvFaq.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvFaq.addItemDecorationIfNoneAdded(spaceItemDecoration, headerItemDecoration)
        binding.ivCross.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.faqFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewLifecycleOwner.lifecycleScope.launch {
                            val faqList = viewModel.getFlattenedFaqData()
                            adapter.submitList(faqList)
                        }
                    }, onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    override fun isItemDecorationSection(position: Int): Boolean {
        return when {
            adapter.currentList.isNullOrEmpty() -> false
            position == 0 -> true
            else -> {
                val prev = adapter.currentList.getOrNull(position)?.type
                val next = adapter.currentList.getOrNull(position - 1)?.type
                prev != next
            }
        }
    }

    override fun getItemDecorationLayoutRes(position: Int): Int {
        return R.layout.cell_kyc_faq_header
    }

    override fun bindItemDecorationData(view: View, position: Int) {
        val header = view.findViewById<AppCompatTextView>(R.id.tvHeader)
        val title = adapter.currentList.getOrNull(position)?.type
        header.isVisible = title != null
        if (title != null)
            header.text = title
    }
}