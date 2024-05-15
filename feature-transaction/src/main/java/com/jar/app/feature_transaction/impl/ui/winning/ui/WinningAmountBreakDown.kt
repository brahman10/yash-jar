package com.jar.app.feature_transaction.impl.ui.winning.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey.TransactionsV2.Clicked_Close_Winningsbreakdown
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureWinningAmountBreakdownBinding
import com.jar.app.feature_transaction.impl.ui.breakdown.UserGoldBreakdownAdapter
import com.jar.app.feature_transaction.impl.ui.breakdown.UserWinningsBreakdownViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
class WinningAmountBreakDown :
    BaseBottomSheetDialogFragment<FeatureWinningAmountBreakdownBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel by viewModels<UserWinningsBreakdownViewModel> { defaultViewModelProviderFactory }

    private val adapter by lazy {
        UserGoldBreakdownAdapter()
    }
    private val spaceItemDecoration by lazy {
        SpaceItemDecoration(0.dp, 8.dp)
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureWinningAmountBreakdownBinding
        get() = FeatureWinningAmountBreakdownBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                it.background = context?.let { it1 -> ContextCompat.getDrawable(it1, com.jar.app.core_ui.R.drawable.bottom_sheet_background_rounded) }
            }
        }
        return dialog
    }

    override fun setup() {
        setUpUI()
        observeData()
        viewModel.fetchUserWinningsBreakdown()
    }

    private fun observeData() {
        val weakReferenceContext = WeakReference(context)
        viewModel.userWinningsBreakdown.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.tvTotalWinningsValue.text =
                    getString(R.string.feature_transaction_rupee_x_in_double, it.totalWinningsReceived)
                uiScope.launch {
                    it.keys?.let { keys ->
                        it.values?.let { values ->
                            viewModel.getWinningsBreakdownAmountData(
                                keys,
                                values,
                                weakReferenceContext
                            ).let {
                                adapter.submitList(it)
                            }
                        }
                    }
                    binding.rvData.visibility = View.VISIBLE
                    binding.shimmerPlaceholder.stopShimmer()
                }
            })
    }

    private fun setUpUI() {
        binding.rvData.layoutManager = LinearLayoutManager(context)
        binding.rvData.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvData.adapter = adapter
        binding.ivClose.setOnClickListener {
            analyticsHandler
                .postEvent(
                    Clicked_Close_Winningsbreakdown
                )
            dismiss()
        }
    }
}
