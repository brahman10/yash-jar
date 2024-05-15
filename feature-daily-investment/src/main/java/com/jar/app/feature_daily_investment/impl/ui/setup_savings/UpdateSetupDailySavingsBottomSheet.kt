package com.jar.app.feature_daily_investment.impl.ui.setup_savings

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.*
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_daily_investment.NavigationDailyInvestmentDirections
import com.jar.app.feature_daily_investment.R
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.AmountBar
import com.jar.app.core_base.util.BaseConstants.CancellationPage
import com.jar.app.core_base.util.BaseConstants.VALUE_FALSE
import com.jar.app.core_base.util.BaseConstants.Old_daily_Savings_amount
import com.jar.app.core_base.util.BaseConstants.VALUE_TRUE
import com.jar.app.core_base.util.BaseConstants.UpdateAmount
import com.jar.app.core_base.util.BaseConstants.Updated_daily_savings_amount
import com.jar.app.core_base.util.BaseConstants.is_recommended_amount
import com.jar.app.core_base.util.BaseConstants.is_suggested_amount
import com.jar.app.core_base.util.BaseConstants.popularAmount
import com.jar.app.core_base.util.BaseConstants.recommendedValues
import com.jar.app.core_base.util.BaseConstants.setup_flow_type
import com.jar.app.core_base.util.BaseConstants.suggestedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_daily_investment.api.domain.event.SetupMandateEvent
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_daily_investment.api.util.EventKey.amount
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentSetupDailyInvestmentDialogBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.ButtonType
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.FromScreen
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.Recommended_values
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.button_type
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.close
import com.jar.app.feature_daily_investment.impl.domain.data.IntermediateTransitionScreenArgs
import com.jar.app.feature_daily_investment.impl.ui.SuggestedAmountAdapter
import com.jar.app.feature_savings_common.shared.domain.model.SavingSuggestedAmount
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class UpdateSetupDailySavingsBottomSheet :
    BaseBottomSheetDialogFragment<FeatureDailyInvestmentFragmentSetupDailyInvestmentDialogBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel by viewModels<UpdateDailySavingsViewModel> { defaultViewModelProviderFactory }

    private var adapter: SuggestedAmountAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    private val args by navArgs<UpdateSetupDailySavingsBottomSheetArgs>()

    private var newDailySavingAmount = 0f
    private var oldDailySavingAmount = 0f
    private var isRoundOffsEnabled = false
    private var maxDSAmount = 0f
    private var minDSAmount = 0f
    private var recommendedAmount = 0f

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentSetupDailyInvestmentDialogBinding
        get() = FeatureDailyInvestmentFragmentSetupDailyInvestmentDialogBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.fetchSeekBarData()
        super.onCreate(savedInstanceState)
    }

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }

    private fun setupUI() {
        dialog?.setCancelable(false)
        binding.tvDesc.text =
            if (args.isFromCancellationFlow) getString(R.string.feature_daily_investment_description_for_cancellation_flow) else getString(
                R.string.feature_daily_investment_description
            )
        binding.tvHeader.text =
            if (args.isFromCancellationFlow) getString(R.string.update_daily_saving_amount) else getString(
                R.string.feature_daily_investment_change_amount
            )
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
        binding.rvSuggestedAmounts.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = SuggestedAmountAdapter {
            analyticsHandler.postEvent(
                DailySavingsEventKey.Clicked_Update_EditDailySavingsPopUp,
                mapOf(
                    ButtonType to Recommended_values,
                    amount to it.amount.toString(),
                    FromScreen to if (args.isFromCancellationFlow) CancellationPage else BaseConstants.ScreenFlowType.SETTINGS_SCREEN
                )
            )

            binding.etBuyAmount.setText("${it.amount.toInt()}")
            binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
            if (args.defaultAmount != it.amount) {
                binding.tvDesc.visibility = View.VISIBLE
            }
        }
        binding.rvSuggestedAmounts.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmounts.adapter = adapter

        binding.etBuyAmount.setText(args.defaultAmount.toInt().toString())
        if (args.defaultAmount != 0f) {
            binding.btnGoAhead.setText(
                if (args.isFromCancellationFlow) getString(R.string.feature_daily_investment_update_amount) else getString(
                    R.string.feature_daily_investment_go_ahead
                )
            )
            binding.tvSuccessDes.text =
                getString(R.string.feature_daily_investment_updated_successfully)
        } else {
            analyticsHandler.postEvent(
                DailySavingsEventKey.Shown_DailySavingsPopUp,
            )
        }
        binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
        binding.etBuyAmount.showKeyboard()
    }

    private fun setupListeners() {
        binding.icCross.setDebounceClickListener {
            binding.etBuyAmount.setText("")
        }
        binding.btnGoAhead.setDebounceClickListener {
            val amount = binding.etBuyAmount.text
            if (!amount.isNullOrBlank()) {
                if (amount.toString().toFloatOrNull().orZero() <= maxDSAmount) {
                    viewModel.isAutoPayResetRequired(amount.toString().toFloatOrNull().orZero())
                } else {
                    getString(R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_2000)
                        .snackBar(binding.root)
                }
            } else {
                getString(R.string.feature_daily_investment_please_enter_the_valid_amount)
                    .snackBar(binding.root)
            }

        }

        binding.ivCross.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.Clicked_Update_EditDailySavingsPopUp,
                mapOf(
                    ButtonType to close,
                    FromScreen to if (args.isFromCancellationFlow) CancellationPage else BaseConstants.ScreenFlowType.SETTINGS_SCREEN
                )
            )
            dismissAllowingStateLoss()
        }

        binding.etBuyAmount.doAfterTextChanged {
            handleEditTextCases(it)
        }

        binding.etBuyAmount.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.Clicked_Update_EditDailySavingsPopUp,
                mapOf(
                    ButtonType to AmountBar,
                    FromScreen to if (args.isFromCancellationFlow) CancellationPage else BaseConstants.ScreenFlowType.SETTINGS_SCREEN
                )
            )
        }
    }

    private fun handleEditTextCases(editable: Editable?) {
        val isAmountValid: Boolean
        var message = ""
        if (editable.isNullOrEmpty()) {
            isAmountValid = false
            message = getString(R.string.feature_daily_investment_this_field_cannot_be_left_empty)
        } else if (editable.toString().toIntOrNull().orZero() > maxDSAmount) {
            isAmountValid = false
            message = getString(
                R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_x,
                maxDSAmount.toInt()
            )
        } else if (editable.toString().toIntOrNull().orZero() < minDSAmount) {
            isAmountValid = false
            message = getString(
                R.string.feature_daily_investment_min_amount_cannot_be_less_than_rs_x,
                minDSAmount.toInt()
            )
        } else {
            isAmountValid = true
            viewModel.setDailyAmount(editable.toString().toFloatOrNull().orZero())
        }
        binding.btnGoAhead.setDisabled(isAmountValid.not())
        binding.tvErrorMessage.isVisible = isAmountValid.not()
        binding.tvErrorMessage.text = message
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.dsSeekBarLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                analyticsHandler.postEvent(
                    DailySavingsEventKey.Shown_EditDailySavingsPopUp,
                    mapOf(
                        recommendedValues to getRecommendedAmountList(it.options),
                        popularAmount to it.recommendedSubscriptionAmount,
                        suggestedAmount to args.defaultAmount,
                        FromScreen to if (args.isFromCancellationFlow) CancellationPage else BaseConstants.ScreenFlowType.SETTINGS_SCREEN
                    )
                )
                maxDSAmount = it.sliderMaxValue
                minDSAmount = it.sliderMinValue
                recommendedAmount = it.recommendedSubscriptionAmount
                binding.tvMaxInvestLimit.text =
                    getString(
                        R.string.feature_daily_investment_you_can_invest_up_to_n_per_day,
                        it.sliderMaxValue.getFormattedAmount()
                    )
                binding.etBuyAmount.setText("${it.recommendedSubscriptionAmount.orZero().toInt()}")
                binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
                viewModel.createRvListData(it)
            }
        )

        viewModel.rVLiveData.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }

        viewModel.dailyInvestmentStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                oldDailySavingAmount = it.amount
                if (binding.etBuyAmount.text.isNullOrBlank()) {
                    binding.etBuyAmount.setText(if (it.enabled) "${it.amount.toInt()}" else "${recommendedAmount.toInt()}")
                    binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
                }
            }
        )

        viewModel.updateDailyInvestmentStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                binding.etBuyAmount.hideKeyboard()
                newDailySavingAmount = it?.amount.orZero()
                if (args.defaultAmount == 0f) {
                    EventBus.getDefault().post(RefreshDailySavingEvent())
                    analyticsHandler.postEvent(DailySavingsEventKey.Shown_Success_DailySavingsPopUp)
                } else {
                    EventBus.getDefault().post(RefreshDailySavingEvent())
                    analyticsHandler.postEvent(DailySavingsEventKey.Shown_Success_EditDailySavingsPopUp)
                }
                dismiss()
                navigateTo(
                    UpdateSetupDailySavingsBottomSheetDirections.toIntermediateTransitionFragment(
                        IntermediateTransitionScreenArgs(
                            com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick,
                            getString(R.string.feature_daily_investment_amount_updated_successfully),
                            getString(
                                R.string.feature_daily_investment_you_will_be_saving_x,
                                it?.amount.orZero().toInt()
                            ),
                            shouldShowProgress = true,
                            shouldShowConfettiAnimation = true
                        )
                    )
                )
            },
            onError = { dismissProgressBar() }
        )

        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
            }
        )

        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                analyticsHandler.postEvent(
                    DailySavingsEventKey.Clicked_Update_EditDailySavingsPopUp,
                    mapOf(
                        button_type to UpdateAmount,
                        Old_daily_Savings_amount to it.mandateAmount.toString(),
                        Updated_daily_savings_amount to it.newMandateAmount.toString(),
                        is_recommended_amount to if (it.newMandateAmount == viewModel.rVLiveData.value?.let { it1 ->
                                getRecommendedAmount(it1)
                            }) VALUE_TRUE else VALUE_FALSE,
                        is_suggested_amount to if (it.newMandateAmount == args.defaultAmount) VALUE_TRUE else VALUE_FALSE,
                        setup_flow_type to it.authWorkflowType.toString(),
                        FromScreen to if (args.isFromCancellationFlow) CancellationPage else BaseConstants.ScreenFlowType.SETTINGS_SCREEN
                    )
                )

                newDailySavingAmount =
                    binding.etBuyAmount.text?.toString()?.toFloatOrNull().orZero()
                if (it.isResetRequired) {
                    if (isRoundOffsEnabled) {
                        navigateTo(
                            NavigationDailyInvestmentDirections.actionToPreDailyInvestmentAutopaySetupFragment(
                                BaseConstants.DSPreAutoPayFlowType.SETUP_DS,
                                newDailySavingAmount
                            )
                        )
                    } else if (newDailySavingAmount > oldDailySavingAmount)
                        EventBus.getDefault()
                            .post(
                                SetupMandateEvent(
                                    newMandateAmount = it.getFinalMandateAmount(),
                                    newDailySavingAmount = newDailySavingAmount
                                )
                            )
                } else {
                    viewModel.enableOrUpdateDailySaving(amount = newDailySavingAmount)
                    viewModel.enableAutomaticDailySavings()
                }
            }
        )
    }

    private fun getRecommendedAmount(suggestedAmountList: List<SuggestedRecurringAmount>): Float? {
        return suggestedAmountList.find { it.recommended }?.amount
    }

    private fun getRecommendedAmountList(suggestedAmountList: List<SavingSuggestedAmount>): String {
        return suggestedAmountList.joinToString(",") { it.amount.toString() }
    }

    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG
}