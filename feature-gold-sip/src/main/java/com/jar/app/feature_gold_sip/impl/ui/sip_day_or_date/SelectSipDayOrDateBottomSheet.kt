package com.jar.app.feature_gold_sip.impl.ui.sip_day_or_date

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipBottomSheetSelectSipDayOrDateBinding
import com.jar.app.feature_gold_sip.impl.ui.sip_day_or_date.adapter.WeekOrMonthAdapter
import com.jar.app.feature_gold_sip.shared.util.WeekGenerator
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SelectSipDayOrDateBottomSheet :
    BaseBottomSheetDialogFragment<FeatureGoldSipBottomSheetSelectSipDayOrDateBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipBottomSheetSelectSipDayOrDateBinding
        get() = FeatureGoldSipBottomSheetSelectSipDayOrDateBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isDraggable = false)

    @Inject
    lateinit var weekGenerator:WeekGenerator

    @Inject
    lateinit var analyticsHandler: AnalyticsApi


    private val viewModelProvider by viewModels<SelectSipDayOrDateViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private var activityRef: WeakReference<FragmentActivity>? = null

    private val spaceItemDecoration by lazy {
        SpaceItemDecoration(
            0.dp, when (args.sipSubscriptionType) {
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> 8.dp
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> 6.dp
            }
        )
    }
    private val args: SelectSipDayOrDateBottomSheetArgs by navArgs()
    private var adapter: WeekOrMonthAdapter? = null

    override fun setup() {
        getData()
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun getData() {
        activityRef = WeakReference(requireActivity())
        viewModel.fetchWeekOrMonth(args.sipSubscriptionType, args.recommendedDay)
    }

    private fun setupUI() {
        val spannableString = SpannableString(
            getCustomStringFormatted(
                GoldSipMR.strings.feature_gold_sip_your_s_saving_of_x,
                getCustomString(args.sipSubscriptionType.textRes),
                args.amount.toInt()
            )
        )
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(), com.jar.app.core_ui.R.color.color_EBB46A
                )
            ),
            spannableString.length - args.amount.toString().length - 1,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvSelectDayForPayment.text = spannableString
        binding.rvDaySelector.layoutManager = GridLayoutManager(
            requireContext(), when (args.sipSubscriptionType) {
                SipSubscriptionType.WEEKLY_SIP -> 1
                SipSubscriptionType.MONTHLY_SIP -> 6
            }
        )
        binding.rvDaySelector.addItemDecorationIfNoneAdded(spaceItemDecoration)
        adapter = WeekOrMonthAdapter(args.sipSubscriptionType) { weekOrMonthData, pos ->
            updateDayOrDateList(pos)
        }
        binding.rvDaySelector.adapter = adapter
        analyticsHandler.postEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_PaymentDayBottomSheet,
            mapOf(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                    args.sipSubscriptionType.textRes
                ),
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to args.amount,
            )
        )
    }

    private fun setupListener() {
        binding.btnConfirm.setDebounceClickListener {
            viewModel.weekOrMonthLocalObjectFlow.value?.let {
                viewModel.updateGoldSip(
                    com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails(
                        args.amount,
                        it.value,
                        args.sipSubscriptionType.name
                    )
                )
            } ?: kotlin.run {
                getCustomString(GoldSipMR.strings.feature_gold_sip_please_select_a_day_to_proceed).snackBar(binding.root)
            }
        }
        binding.ivCross.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_PaymentDayBottomSheet,
                mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Cross,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                        args.sipSubscriptionType.textRes
                    ),
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to args.amount,
                )
            )
            findNavController().getBackStackEntry(R.id.goldSipTypeSelectionFragment).savedStateHandle[com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.SELECT_DAY_OR_DATE_BOTTOM_SHEET_CLOSED] =
                true
            popBackStack()
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.weekOrMonthFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        adapter?.submitList(it)
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.weekOrMonthLocalObjectFlow.collect {
                    binding.btnConfirm.setDisabled(it == null)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateGoldSipDetailsFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = { sipData ->
                        dismissProgressBar()
                        viewModel.weekOrMonthLocalObjectFlow.value?.let {
                            val data = it.text?.stringRes?.let { getCustomString(it) }
                                ?: kotlin.run { it.value.toString() }
                            analyticsHandler.postEvent(
                                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_PaymentDayBottomSheet,
                                mapOf(
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Confirm,
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(
                                        args.sipSubscriptionType.textRes
                                    ),
                                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to args.amount,
                                    when (args.sipSubscriptionType) {
                                        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.DateSelected
                                        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.WeekDaySelected
                                    } to data
                                )
                            )
                            if (args.isSetupFlow) {
                                binding.tvSSavingWillBeDebitedOnS.text =
                                    when (args.sipSubscriptionType) {
                                        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> it.text?.stringRes?.let { it1 ->
                                            getCustomStringFormatted(
                                                GoldSipMR.strings.feature_gold_sip_weekly_savings_will_be_debited_on_every_s,
                                                it1
                                            )
                                        } ?: kotlin.run { "" }

                                        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> getCustomStringFormatted(
                                            GoldSipMR.strings.feature_gold_sip_monthly_savings_will_be_debited_on_every_s,
                                            it.value.getDayOfMonthAndItsSuffix()
                                        )
                                    }
                                binding.tvDaySuccessfullyUpdated.text =
                                    when (args.sipSubscriptionType) {
                                        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> getCustomStringFormatted(
                                            GoldSipMR.strings.feature_gold_sip_day_successfully_updated,
                                        )

                                        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> getCustomString(
                                            GoldSipMR.strings.feature_gold_sip_date_successfully_updated
                                        )

                                        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> getCustomString(
                                            GoldSipMR.strings.feature_gold_sip_date_successfully_updated
                                        )
                                    }
                                binding.tvSipAmount.text = getString(
                                    com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                                    args.amount.toInt()
                                )
                                binding.tvNextPaymentDate.text =
                                    sipData.nextDeductionDate?.epochToDate()
                                        ?.getFormattedDate("d MMM''yy")
                                        .orEmpty()
                                binding.clDaySelectionContainer.slideToRevealNew(binding.clSuccessContainer) {
                                    binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
                                        requireContext(),
                                        BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                                    )
                                    binding.successLottie.playLottieWithUrlAndExceptionHandling(
                                        requireContext(),
                                        BaseConstants.LottieUrls.SMALL_CHECK
                                    )
                                    uiScope.launch {
                                        delay(3000)
                                        findNavController().getBackStackEntry(R.id.goldSipSetupAutopaySuccessFragment).savedStateHandle[com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.DAY_OR_DATE_UPDATED] =
                                            it.text?.stringRes?.getString(requireContext()).orEmpty()
                                        popBackStack()
                                    }
                                }
                            } else {
                                EventBus.getDefault().post(
                                    com.jar.app.feature_gold_sip.shared.domain.event.GoldSipUpdateEvent(
                                        args.amount,
                                        when (args.sipSubscriptionType) {
                                            com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> getCustomString(
                                                weekGenerator.getWeekFromDay(it.value).stringRes
                                            )

                                            com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> it.value.toString()
                                        },
                                        it.value,
                                        args.sipSubscriptionType.name
                                    )
                                )
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun updateDayOrDateList(position: Int) {
        adapter?.currentList?.let {
            viewModel.updateListOnItemClick(it, position)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        activityRef = null
    }
}