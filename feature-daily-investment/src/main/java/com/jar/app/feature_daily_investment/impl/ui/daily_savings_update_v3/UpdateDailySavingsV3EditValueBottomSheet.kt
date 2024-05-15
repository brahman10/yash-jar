package com.jar.app.feature_daily_investment.impl.ui.daily_savings_update_v3

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.shakeAnimation
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentUpdateEditAmountBottomsheetBinding
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.ui.SuggestedAmountAdapter
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_daily_investment.shared.ui.UpdateDailySavingsEditValueBottomSheetViewModel
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class UpdateDailySavingsV3EditValueBottomSheet :
    BaseBottomSheetDialogFragment<FeatureDailyInvestmentUpdateEditAmountBottomsheetBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentUpdateEditAmountBottomsheetBinding
        get() = FeatureDailyInvestmentUpdateEditAmountBottomsheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = true,
            isDraggable = true,
        )

    private var adapter: SuggestedAmountAdapter? = null

    private var isAmountValid = true

    private var maxValue = 5000f

    private var minValue = 0f

    private val viewModelProvider by viewModels<UpdateDailySavingsV3EditValueViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args: UpdateDailySavingsV3EditValueBottomSheetArgs by navArgs()

    private var dSAmount: Float = 10f

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    override fun setup() {
        observeLiveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.fetchDSAmountData()
        super.onCreate(savedInstanceState)
    }

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }

    override fun onResume() {
        openBottomSheetWithDelay()
        super.onResume()
    }

    fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            dismissBottomSheetWithoutPassingValues()
        }
        binding.etAmount.doAfterTextChanged {
            var message = ""
            isAmountValid = if (it.isNullOrEmpty()) {
                message = getString(R.string.feature_daily_investment_this_field_cannot_be_left_empty)
                false
            } else if (it.toString().toFloatOrNull().orZero() > maxValue) {
                message = getString(
                    R.string.feature_daily_investment_maximum_value_you_can_save_is_x, maxValue.toInt()
                )
                false
            } else if (it.toString().toFloatOrNull().orZero() < minValue) {
                message = getString(
                    R.string.feature_daily_investment_minimum_value_you_can_save_is_x,
                    minValue.toInt()
                )
                false
            } else {
                message = requireContext().getString(
                    R.string.daily_investment_edit_daily_saving_amount_footer,
                    maxValue.toInt()
                )
                true
            }
            if (isAmountValid.not()) {
                binding.tvFooter.text = message
                binding.tvFooter.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_FF8D91
                    )
                )
                makeErrorInEditAmountBox()
            } else {
                binding.tvFooter.text = message
                binding.tvFooter.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_D5CDF2
                    )
                )
                resetErrorEditAmountBox()
                setDsAmountAndTellParentFragment()
            }
        }
        binding.etAmount.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                if (isAmountValid) {
                    dismissBottomSheetWithDelay()
                }
            }
            false
        }
        binding.amountEditBoxHolder.setDebounceClickListener {
            binding.etAmount.performClick()
        }
        binding.btnProceed.setDebounceClickListener {
            if(isAmountValid){
                analyticsApi.postEvent(
                    DailySavingsEventKey.DailySavingsAmountBSClicked,
                    mapOf(
                        DailySavingsEventKey.AmountRec to dSAmount,
                    )
                )
                setDsAmountAndTellParentFragment()
                dismissBottomSheetWithDelay()
            }else{
             binding.tvFooter.text = requireContext().getString(R.string.feature_daily_investment_update_edit_daily_saving_default_error_text)
            }
        }
    }

    private fun setDsAmountAndTellParentFragment(){
        dSAmount = binding.etAmount.text?.toString()?.toFloatOrNull().orZero()
        findNavController()
            .getBackStackEntry(R.id.updateDailySavingsV3Fragment)
            .savedStateHandle[DailySavingConstants.DAILY_SAVING_AMOUNT_EDIT] = dSAmount
    }
    private fun dismissBottomSheetWithoutPassingValues(){
        findNavController()
            .getBackStackEntry(R.id.updateDailySavingsV3Fragment)
            .savedStateHandle[DailySavingConstants.DAILY_SAVING_AMOUNT_EDIT] = args.dsAmount
        uiScope.launch {
            dismissBottomSheetWithDelay()
        }
    }

    private fun dismissBottomSheetWithDelay(){
        binding.root.hideKeyboard()
        uiScope.launch {
            delay(300)
            dismiss()
        }
    }

    private fun openBottomSheetWithDelay(){
        uiScope.launch {
            delay(300)
            binding.root.showKeyboard()
        }
    }

    private fun makeErrorInEditAmountBox() {
        val errorColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_FF8D91)
        binding.etAmount.setTextColor(errorColor)
        binding.amountEditBoxHolder.setBackgroundResource(R.drawable.feature_daily_investment_edit_daily_saving_error_box)
        binding.etAmount.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.feature_daily_investment_ic_rs_sign,
            0,
            0,
            0
        )
        binding.etAmount.shakeAnimation()
    }

    private fun resetErrorEditAmountBox() {
        binding.etAmount.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                com.jar.app.core_ui.R.color.white
            )
        )
        binding.etAmount.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.feature_daily_investment_ic_rs_sign,
            0,
            0,
            0
        )
        binding.amountEditBoxHolder.setBackgroundResource(R.drawable.feature_daily_investment_edit_daily_saving_value_bottom_sheet)
    }

    private fun setData(data: SavingSetupInfo) {
        binding.etAmount.requestFocus()
        binding.etAmount.setText(args.dsAmount.toInt().toString())
        binding.etAmount.setSelection(
            binding.etAmount.text.toString().trim().length
        )
        binding.tvHeader.text =
            requireContext().getString(R.string.feature_daily_investment_update_edit_daily_saving_amount_header)
        binding.btnProceed.setText(requireContext().getString(R.string.feature_daily_investment_update_edit_daily_saving_update_daily_savings_amount))
        binding.tvFooter.text = requireContext().getString(
            R.string.daily_investment_edit_daily_saving_amount_footer,
            data.sliderMaxValue.toInt()
        )

        maxValue = data.sliderMaxValue
        minValue = data.sliderMinValue
    }

    private fun setRecyclerView(suggestedRecurringAmounts: List<SuggestedRecurringAmount>) {
        binding.rvSuggestedAmount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = SuggestedAmountAdapter {
            binding.etAmount.setText("${it.amount.toInt()}")
            binding.etAmount.setSelection(
                binding.etAmount.text.toString().trim().length
            )
        }
        binding.rvSuggestedAmount.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmount.adapter = adapter
        adapter?.submitList(suggestedRecurringAmounts)
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.dsAmountFlowData.collectUnwrapped(
                    onLoading = { },
                    onSuccess = {
                        analyticsApi.postEvent(
                            DailySavingsEventKey.DSUpdateFlow_EditAmount_ShownBottomSheet,
                            mapOf(
                                DailySavingsEventKey.AmountRec to it.data.recommendedSubscriptionAmount,
                                DailySavingsEventKey.AmountDefault to args.dsAmount.toInt().orZero()
                            )
                        )
                        setData(it.data)
                        viewModel.createRvListData(it.data)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.rVFlowData.collectLatest {
                    setRecyclerView(it)
                    setupListeners()
                }
            }
        }
    }
}