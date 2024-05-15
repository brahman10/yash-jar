package com.jar.app.feature_gold_lease.impl.ui.jeweller_details

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseV2JewellerDetailsBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2JewellerDetails
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2JewellerDetailsViewModel
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class GoldLeaseV2JewellerDetailsBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentGoldLeaseV2JewellerDetailsBinding>(){

    companion object {
        const val MAX_LINES_COLLAPSE = 6
        const val MAX_LINES_DEFAULT = 100
    }

    private var adapter: GoldLeaseV2TitleValuePairAdapter? = null

    private val spaceItemDecoration: SpaceItemDecoration = SpaceItemDecoration(0.dp, 4.dp)

    private val viewModelProvider by viewModels<GoldLeaseV2JewellerDetailsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<GoldLeaseV2JewellerDetailsBottomSheetFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseV2JewellerDetailsBinding
        get() = FragmentGoldLeaseV2JewellerDetailsBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    private var isExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup() {
        observeLiveData()
        setupUI()
        setupListeners()
    }

    private fun getData() {
        if (args.jewellerId.isEmpty()) {
            dismiss()
        } else {
            viewModel.fetchJewellerDetails(args.jewellerId)
        }
    }

    private fun observeLiveData() {
        val weakRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goldLeaseJewellerDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setupDataInUI(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakRef.get()!!)
                    }
                )
            }
        }
    }

    private fun setupDataInUI(data: GoldLeaseV2JewellerDetails) {
        adapter?.submitList(data.jewellerSummary.orEmpty())
        Glide.with(requireContext()).load(data.jewellerIcon.orEmpty()).into(binding.ivJewellerIcon)
        binding.tvJewellerName.setHtmlText(data.jewellerName.orEmpty())
        binding.tvJewellerEst.setHtmlText(data.establishedText.orEmpty())
        binding.tvJewellerTitle.setHtmlText(data.jewellerTitle.orEmpty())
        binding.tvMessage.setHtmlText(data.jewellerDescription.orEmpty())
        binding.tvExpand.isVisible = data.jewellerDescription.isNullOrEmpty().not()
        binding.expandableLayout.isVisible = data.jewellerDescription.isNullOrEmpty().not()
        toggleDescriptionText()
    }

    private fun setupListeners() {
        binding.btnOkay.setDebounceClickListener {
            dismiss()
        }

        binding.ivClose.setDebounceClickListener {
            dismiss()
        }

        binding.expandableLayout.setDebounceClickListener {
            toggleDescriptionText()
        }

        binding.tvExpand.setDebounceClickListener {
            toggleDescriptionText()
        }
    }

    private fun toggleDescriptionText() {
        if (isExpanded) {
            collapseText()
        } else {
            expandText()
        }
        isExpanded = !isExpanded
    }

    private fun expandText() {
        binding.tvMessage.maxLines = MAX_LINES_DEFAULT
        binding.tvExpand.text = getString(R.string.feature_gold_lease_read_less)
    }

    private fun collapseText() {
        binding.tvMessage.maxLines = MAX_LINES_COLLAPSE
        binding.tvExpand.text = getString(R.string.feature_gold_lease_read_more)
    }

    private fun setupUI() {
        adapter = GoldLeaseV2TitleValuePairAdapter(
            onClickedCopyTransactionId = {},
            onWebsiteClicked = {
                openUrlInChromeTabOrExternalBrowser(requireContext(), it)
            }
        )
        binding.rvTitleValuePairs.adapter = adapter
        binding.rvTitleValuePairs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTitleValuePairs.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.tvExpand.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
}