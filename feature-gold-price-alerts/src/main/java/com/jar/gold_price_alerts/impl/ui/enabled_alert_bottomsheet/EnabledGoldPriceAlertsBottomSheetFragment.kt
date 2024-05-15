package com.jar.gold_price_alerts.impl.ui.enabled_alert_bottomsheet

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.R
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.core_ui.util.applyRoundedRectBackground
import com.jar.app.feature_gold_price_alerts.databinding.FragmentEnabledGoldPriceAlertsBottomSheetBinding
import com.jar.feature_gold_price_alerts.shared.domain.model.LatestGoldPriceAlertResponse
import com.jar.feature_gold_price_alerts.shared.domain.model.TableData
import com.jar.feature_gold_price_alerts.shared.ui.EnabledGoldAlertsBottomSheetViewModel
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.Cross_Clicked
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.Remove_Alert
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.Constants.UPDATE_STATE_FROM_BOTTOMSHEET
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class EnabledGoldPriceAlertsBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentEnabledGoldPriceAlertsBottomSheetBinding>() {

    private val viewModelProvider by viewModels<EnabledGoldAlertsBottomSheetViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val labelAndValueAdapter = LabelAndValueAdapter()

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 4.dp, escapeEdges = false)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEnabledGoldPriceAlertsBottomSheetBinding
        get() = FragmentEnabledGoldPriceAlertsBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup() {
        setupUI()
        setupListeners()
        setupObservers()
    }
    private fun setupUI() {
        applyRoundedRectBackground(
            targetView = binding.clRoot,
            bgColor = ContextCompat.getColor(requireContext(), R.color.color_2e2942),
            topLeftRadius = 16f.dp,
            topRightRadius = 16f.dp
        )
        binding.labelValueRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.labelValueRv.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.labelValueRv.adapter = labelAndValueAdapter
    }
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.bottomSheetStaticDataFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        setData(it.data)
                    })
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.disableGoldAlertFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            UPDATE_STATE_FROM_BOTTOMSHEET,
                            true
                        )
                        dismissProgressBar()
                        dismissAllowingStateLoss()
                    })
            }
        }
    }

    private fun setData(data: LatestGoldPriceAlertResponse?) {
        data?.let {
            binding.label.text = it.title
            binding.label3.setHtmlText(it.description.orEmpty())
            Glide.with(requireContext())
                .load(it.imageUrl)
                .into(binding.label2)
            binding.removeAlertTv.text = it.removeAlertCta?.title.orEmpty()
            binding.removeAlertTv.movementMethod = LinkMovementMethod()
            binding.removeAlertTv.paint.isUnderlineText = true
            setupLabelAdapter(data.tableData)
            viewModel.postShownAnalyticEvent()
        }
    }

    private fun setupLabelAdapter(tableData: List<TableData>?) {
        val labelValueList =
            tableData?.map {
                LabelAndValue(
                    label = it.key.orEmpty(),
                    value = it.value.orEmpty(),
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    valueColorRes = com.jar.app.core_ui.R.color.color_EEEAFF,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                    valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle,
                )
            }
        labelAndValueAdapter.submitList(labelValueList)
    }

    private fun getData() {
        viewModel.fetchBottomSheetStaticDataUseCase()
    }

    private fun setupListeners() {
        binding.icClose.setDebounceClickListener {
            viewModel.postAnalyticEventForClickAction(Cross_Clicked)
            dismissAllowingStateLoss()
        }
        binding.removeAlertTv.setDebounceClickListener {
            viewModel.postAnalyticEventForClickAction(Remove_Alert)
            viewModel.removeAlert()
        }
    }
}