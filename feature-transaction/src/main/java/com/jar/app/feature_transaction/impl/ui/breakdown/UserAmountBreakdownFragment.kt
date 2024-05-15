package com.jar.app.feature_transaction.impl.ui.breakdown

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FragmentUserAmountBreakdownBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class UserAmountBreakdownFragment :
    BaseBottomSheetDialogFragment<FragmentUserAmountBreakdownBinding>() {

    private val viewModel by viewModels<UserAmountBreakdownViewModel> { defaultViewModelProviderFactory }

    private val adapter = UserGoldBreakdownAdapter()
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUserAmountBreakdownBinding
        get() = FragmentUserAmountBreakdownBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                it.setBackgroundResource(com.jar.app.core_ui.R.drawable.bottom_sheet_background_rounded)
            }
        }
        return dialog
    }

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        binding.rvData.layoutManager = LinearLayoutManager(context)
        binding.rvData.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvData.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.investedAmountBreakdownLivedata.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.shimmerPlaceholder.stopShimmer()
                binding.shimmerPlaceholder.isVisible = false
                binding.rvData.isVisible = true
                binding.tvAmount.text = getString(R.string.feature_transaction_rs_value, it?.investedValue.orZero())
                binding.tvYouInvestedValue.text = getString(R.string.feature_transaction_rs_value, it?.values?.get(0))
                binding.tvExtraGold.text = it?.extraGoldBreakupObject?.extraGoldKey
                if (it?.values?.size == 2) {
                    if (it.values[1].isNumberNegative()) {
                        binding.tvTotalAmount.text = getString(R.string.feature_transaction_rs_negative_value,
                            it.values[1].toPositive()
                        )
                    } else {
                        binding.tvTotalAmount.text = getString(R.string.feature_transaction_rs_value,
                            it.values[1]
                        )
                    }
                }
                uiScope.launch {
                    if (it?.extraGoldBreakupObject != null) {
                        val weakContext = WeakReference(context)
                        viewModel.getInvestedAmountData(it.extraGoldBreakupObject.keys, it.extraGoldBreakupObject.values, weakContext).let { list ->
                            adapter.submitList(list)
                        }
                    }
                }
            }
        )
    }

    private fun getData() {
        viewModel.fetchUserAmountBreakDown()
    }
}