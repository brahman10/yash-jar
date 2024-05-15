package com.jar.app.feature_daily_investment.impl.ui.update_ds_v2

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.strikeThrough
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.shakeAnimation
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_daily_investment.NavigationDailyInvestmentDirections
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentUpdateSavingV2Binding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.ui.daily_saving_setup_v2.DailySavingsV2Fragment
import com.jar.app.feature_daily_investment.impl.ui.suggestion.AmountSuggestionAdapter
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyRequestEnum
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class UpdateDailySavingV2Fragment :
    BaseFragment<FeatureDailyInvestmentFragmentUpdateSavingV2Binding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentUpdateSavingV2Binding
        get() = FeatureDailyInvestmentFragmentUpdateSavingV2Binding::inflate

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    private val args: UpdateDailySavingV2FragmentArgs by navArgs()
    private var isRoundOffsEnabled = false
    private val viewModel: UpdateDailySavingV2ViewModel by viewModels()

    private var isAmountValid = true
    private var maxValue = 0
    private var minValue = 0
    private var currentDSAmount = 0f
    private var newDSAmount = 0f
    private var amountSuggestionAdapter: AmountSuggestionAdapter? = null

    private val suggestedAmountRvSpaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.getExitSurveyData()
            }
        }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        getData()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchUserDSDetails()
        viewModel.fetchUserRoundOffDetails()
        viewModel.fetchDSSetupInfo()
    }

    private fun setupUI() {
        setupToolbar()
        binding.rvSuggestedAmount.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        amountSuggestionAdapter = AmountSuggestionAdapter {
            binding.etAmount.setText(it.amount.toInt().toString())
        }
        binding.rvSuggestedAmount.addItemDecorationIfNoneAdded(suggestedAmountRvSpaceItemDecoration)
        binding.rvSuggestedAmount.adapter = amountSuggestionAdapter

        binding.graphLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(), DailySavingConstants.LottieUrl.DS_UPDATE_SCREEN_LOTTIE
        )
    }

    private fun setupToolbar() {
        binding.toolBar.tvTitle.text = getString(R.string.feature_daily_savings)
        binding.toolBar.ivEndImage.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_custom_question_mark)
        binding.toolBar.ivEndImage.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), com.jar.app.core_ui.R.color.white)
        binding.toolBar.ivEndImage.imageTintList =
            ContextCompat.getColorStateList(binding.root.context, com.jar.app.core_ui.R.color.white)
        binding.toolBar.ivEndImage.isVisible = true
        binding.toolBar.separator.isVisible = true
    }

    private fun setupListeners() {

        binding.etAmount.doAfterTextChanged {
            val message: String
            isAmountValid = if (it.isNullOrEmpty()) {
                message = getString(R.string.feature_daily_investment_this_field_cannot_be_left_empty)
                false
            } else if (it.toString().toIntOrNull().orZero() > maxValue) {
                message = getString(
                    R.string.feature_daily_investment_maximum_value_you_can_save_is_x, maxValue
                )
                false
            } else if (it.toString().toIntOrNull().orZero() < minValue) {
                message = getString(
                    R.string.feature_daily_investment_minimum_value_should_be_x, minValue
                )
                false
            } else {
                message = requireContext().getString(
                    R.string.daily_investment_edit_daily_saving_amount_footer, maxValue
                )
                true
            }
            binding.etAmount.setSelection(binding.etAmount.text?.length.orZero())
            binding.btnProceed.setDisabled(isAmountValid.not())
            binding.tvErrorMessage.isVisible = isAmountValid.not()
            binding.tvErrorMessage.text = message
            if (isAmountValid) {
                resetErrorEditAmountBox()
                newDSAmount = binding.etAmount.text.toString().toFloat()
            } else {
                makeErrorInEditAmountBox()
            }
            viewModel.updateDsInfoChanges(
                currentDSAmount,
                if (binding.etAmount.text.isNullOrEmpty()) 0f else binding.etAmount.text.toString()
                    .toFloat().orZero()
            )
        }

        binding.toolBar.ivEndImage.setDebounceClickListener {
            EventBus.getDefault()
                .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.DAILY_SAVING_EDUCATION + "/${false}"))
        }

        binding.ivEditIcon.setDebounceClickListener {
            binding.etAmount.showKeyboard()
        }

        binding.toolBar.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.btnProceed.setDebounceClickListener {
            analyticsApi.postEvent(
                when (args.flow) {
                    BaseConstants.DSPreAutoPayFlowType.POST_SETUP_DS -> {
                        DailySavingsEventKey.PostSetupDS_UpdateScreenProceedClicked
                    }

                    else -> {
                        DailySavingsEventKey.PostSetupDS_UpdateScreenProceedClicked
                    }
                }, mapOf(
                    DailySavingsEventKey.DailySavingsAmount to currentDSAmount,
                    DailySavingsEventKey.UpdatedDailySavingsAmount to newDSAmount
                )
            )
            viewModel.isAutoPayResetRequired(newDSAmount)
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewModel.dsSetupInfoLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                amountSuggestionAdapter?.submitList(it.options)
                minValue = it.sliderMinValue.toInt()
                maxValue = it.sliderMaxValue.toInt()
                binding.etAmount.setText(it.recommendedSubscriptionAmount.toInt().toString())
            }
        )

        viewModel.dsDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                currentDSAmount = it.subscriptionAmount

                analyticsApi.postEvent(
                    when (args.flow) {
                        BaseConstants.DSPreAutoPayFlowType.POST_SETUP_DS -> {
                            DailySavingsEventKey.PostSetupDS_UpdateScreenShown
                        }

                        else -> {
                            DailySavingsEventKey.PostSetupDS_UpdateScreenShown
                        }
                    }, mapOf(DailySavingsEventKey.DailySavingsAmount to currentDSAmount)
                )

                viewModel.updateDsInfoChanges(currentDSAmount, newDSAmount)
                val activeDSAmountText =
                    SpannableStringBuilder().append(getString(R.string.feature_daily_investment_current_daily_saving_amount))
                        .append(" ")
                        .color(
                            ContextCompat.getColor(
                                requireContext(), com.jar.app.core_ui.R.color.white
                            )
                        ) {
                            bold {
                                append(
                                    getString(
                                        com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                                        currentDSAmount.toInt()
                                    )
                                )
                            }
                        }
                binding.tvActiveDSAmount.text = activeDSAmountText
            }
        )

        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
            }
        )

        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                if (it.isResetRequired) {
                    if (isRoundOffsEnabled) navigateTo(
                        NavigationDailyInvestmentDirections.actionToPreDailyInvestmentAutopaySetupFragment(
                            flowType = BaseConstants.DSPreAutoPayFlowType.SETUP_DS,
                            dailySavingAmount = newDSAmount
                        )
                    )
                    else
                        dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                            mandateAmount = it.getFinalMandateAmount(),
                            source = MandatePaymentEventKey.FeatureFlows.UpdateDailySaving,
                            authWorkflowType = MandateWorkflowType.valueOf(
                                it.authWorkflowType ?: MandateWorkflowType.PENNY_DROP.name
                            ),
                            newDSAmount,
                            R.id.updateDailySavingV2Fragment,
                            userLifecycle = prefsApi.getUserLifeCycleForMandate()
                        )
                } else {
                    viewModel.enableOrUpdateDailySaving(newDSAmount)
                    viewModel.enableAutomaticDailySavings()
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.updateDailyInvestmentStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                navigateTo(
                    "android-app://com.jar.app/dsSuccessState/${args.flow}",
                    popUpTo = R.id.updateDailySavingV2Fragment,
                    inclusive = true
                )
            }
        )
        viewModel.dsInfoChangesLiveData.observe(viewLifecycleOwner) {
            val saveUptoText =
                SpannableStringBuilder().append(
                    getString(R.string.feature_daily_investment_you_will_save)
                ).append(" ").color(
                    ContextCompat.getColor(
                        requireContext(),
                        if (it.first < it.second) com.jar.app.core_ui.R.color.color_58DDC8 else com.jar.app.core_ui.R.color.color_EB6A6E
                    )
                ) {
                    bold {
                        append(
                            getString(
                                com.jar.app.core_ui.R.string.core_ui_rs_x_int, it.third.toInt()
                            )
                        ).append(if (it.first < it.second) "↑️" else "↓️")
                    }
                }
                    .append(getString(if (it.first < it.second) R.string.feature_daily_investment_more else R.string.feature_daily_investment_less))
            binding.tvYouCanSave.text = saveUptoText
            binding.tvInXMonthsBy.text = getString(
                R.string.feature_daily_investment_in_x_months,
                remoteConfigManager.updateDsV2MonthCount()
            )
            val oldDsNewDsDailyText =
                SpannableStringBuilder()
                    .color(
                        ContextCompat.getColor(
                            requireContext(), com.jar.app.core_ui.R.color.color_EBB46A
                        )
                    ) {
                        strikeThrough {
                            append(
                                getString(
                                    com.jar.app.core_ui.R.string.core_ui_rs_x_int, it.first.toInt()
                                )
                            )
                        }.append(" ")
                        bold {
                            append(
                                getString(
                                    com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                                    it.second.toInt()
                                )
                            )
                        }
                    }.append(" ").bold {
                        append(getString(R.string.feature_daily_investment_everyday))
                    }
            binding.tvOldDsNewDsDaily.isVisible = currentDSAmount != 0f
            binding.tvOldDsNewDsDaily.text = oldDsNewDsDailyText
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.exitSurveyResponse.collectLatest {
                    it?.let {
                        if (it) {
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent("${BaseConstants.EXIT_SURVEY_DEEPLINK}/${ExitSurveyRequestEnum.DAILY_SAVINGS.name}")
                            )
                        } else {
                            popBackStack()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loading.collect {
                    if (it) {
                        showProgressBar()
                    } else {
                        dismissProgressBar()
                    }
                }
            }
        }

    }

    private fun makeErrorInEditAmountBox() {
        val errorColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_FF8D91)
        binding.etAmount.setTextColor(errorColor)
        binding.clAmountEditBoxHolder.setBackgroundResource(R.drawable.feature_daily_investment_edit_daily_saving_error_box)
        binding.etAmount.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.feature_daily_investment_ic_rs_sign, 0, 0, 0
        )
        binding.etAmount.shakeAnimation()
    }

    private fun resetErrorEditAmountBox() {
        binding.etAmount.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.white
            )
        )
        binding.etAmount.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.feature_daily_investment_ic_rs_sign, 0, 0, 0
        )
        binding.clAmountEditBoxHolder.setBackgroundResource(R.drawable.feature_daily_investment_edit_daily_saving_value_bottom_sheet)
    }
}