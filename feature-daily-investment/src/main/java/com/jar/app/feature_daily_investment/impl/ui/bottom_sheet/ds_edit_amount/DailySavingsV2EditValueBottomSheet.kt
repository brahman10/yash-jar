package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_edit_amount

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.shakeAnimation
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.databinding.FeatureDailySavingEditAmountBottomsheetBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.ui.SuggestedAmountAdapter
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class DailySavingsV2EditValueBottomSheet :
    BaseBottomSheetDialogFragment<FeatureDailySavingEditAmountBottomsheetBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailySavingEditAmountBottomsheetBinding
        get() = FeatureDailySavingEditAmountBottomsheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = false,
            isDraggable = false,
        )

    private var adapter: SuggestedAmountAdapter? = null

    private var isAmountValid = true

    private var maxValue = 5000f

    private var minValue = 0f

    private val viewModel by viewModels<DailySavingsV2EditValueViewModel> { defaultViewModelProviderFactory }

    private val args: DailySavingsV2EditValueBottomSheetArgs by navArgs()

    private var dSAmount: Float = 10f

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    override fun setup() {
        observeLiveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.fetchSeekBarData()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setStyle(DialogFragment.STYLE_NORMAL, com.jar.app.core_ui.R.style.BottomSheetDialogInput)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        binding.etAmount.showKeyboard()
        binding.root.postDelayed(
            {
                binding.root.showKeyboard()
            },
            300
        )
        super.onResume()
    }

    fun setupListeners() {

        binding.btnClose.setDebounceClickListener {
            binding.root.hideKeyboard()

            if (!isAmountValid) {
                findNavController()
                    .getBackStackEntry(R.id.dailySavingsV2Fragment)
                    .savedStateHandle[DailySavingConstants.DAILY_SAVING_AMOUNT_EDIT] = args.dsAmount
            }
            uiScope.launch {
                delay(300)
                dismiss()
            }
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
        binding.etAmount.setOnEditorActionListener { v, actionId, event ->
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
            setDsAmountAndTellParentFragment()
            dismissBottomSheetWithDelay()
        }
    }

    private fun setDsAmountAndTellParentFragment(){
        dSAmount = binding.etAmount.text?.toString()?.toFloatOrNull().orZero()
        findNavController()
            .getBackStackEntry(R.id.dailySavingsV2Fragment)
            .savedStateHandle[DailySavingConstants.DAILY_SAVING_AMOUNT_EDIT] = dSAmount
    }

    private fun dismissBottomSheetWithDelay(){
        binding.root.hideKeyboard()
        uiScope.launch {
            delay(300)
            dismiss()
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

    private fun setErrorText(message: String) {

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
        val position: Int = binding.etAmount.length()
        val etAmount: Editable? = binding.etAmount.text
        Selection.setSelection(etAmount, position)
        binding.tvHeader.text =
            requireContext().getString(R.string.daily_investment_edit_daily_saving_amount_header)
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
            binding.etAmount.setSelection(binding.etAmount.text?.length.orZero())
            val position: Int = binding.etAmount.length()
            val etAmount: Editable? = binding.etAmount.text
            Selection.setSelection(etAmount, position)
        }
        binding.rvSuggestedAmount.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmount.adapter = adapter
        adapter!!.submitList(suggestedRecurringAmounts)
    }

    private fun observeLiveData() {

        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewModel.dsSeekBarLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { },
            onSuccess = {
                setData(it)
                viewModel.createRvListData(it)
            },
            onError = { }
        )

        viewModel.rVLiveData.observe(viewLifecycleOwner) {
            setRecyclerView(it)
            setupListeners()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        analyticsApi.postEvent(
            DailySavingsEventKey.Clicked_DailySavings_Card,
            mapOf(
                DailySavingsEventKey.PageName to "Setup Screen V2",
                DailySavingsEventKey.ButtonType to "Edit Amount",
                DailySavingsEventKey.EntryType to "Typed",
                DailySavingsEventKey.AmountSelected to dSAmount
            )
        )
    }
}