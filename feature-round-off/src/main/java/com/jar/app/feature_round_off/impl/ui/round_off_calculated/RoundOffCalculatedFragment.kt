package com.jar.app.feature_round_off.impl.ui.round_off_calculated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentRoundOffCalculatedBinding
import com.jar.app.feature_round_off.impl.ui.initial_round_off.InitialRoundOffAdapter
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffCalculatedFragment :
    BaseFragment<FeatureRoundOffFragmentRoundOffCalculatedBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentRoundOffCalculatedBinding
        get() = FeatureRoundOffFragmentRoundOffCalculatedBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel: RoundOffCalculatedViewModel by viewModels()
    private var adapter: InitialRoundOffAdapter? = null
    private var dividerItemDecorator: DividerItemDecoration? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private val args by navArgs<RoundOffCalculatedFragmentArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    private var orderId: String? = null
    private var roundOffAmount = 0f
    private var isExpanded = false
    private var roundOffsListSize = 0

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        viewModel.fetchInitialRoundOffsData()
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_round_off_round_off_label)
        binding.toolbar.ivTitleImage.isVisible = true
        binding.toolbar.separator.isVisible = true
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_round_off_ic_round_off)
        binding.toolbar.ivEndImage.isVisible = true
        binding.toolbar.ivEndImage.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_custom_question_mark)
        binding.toolbar.ivEndImage.backgroundTintList = ContextCompat.getColorStateList(requireContext(), com.jar.app.core_ui.R.color.white)
        setupAdapter()
    }

    private fun setupAdapter() {
        dividerItemDecorator = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rvRoundOffsDetected.addItemDecorationIfNoneAdded(dividerItemDecorator!!)
        adapter = InitialRoundOffAdapter()
        binding.rvRoundOffsDetected.adapter = adapter
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_Detection_Screen,
            mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Hide_Transaction,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.RoundoffAmount to roundOffAmount,
            )
        )
    }

    private fun setupListener() {
        binding.btnProceed.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_Detection_Screen,
                mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.SaveNowClicked,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.RoundoffAmount to roundOffAmount
                )
            )
            navigateTo(
                RoundOffCalculatedFragmentDirections.actionRoundOffCalculatedFragmentToSelectRoundOffSaveMethodFragment()
            )
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.toolbar.ivEndImage.setDebounceClickListener {
            navigateTo(
                "android-app://com.jar.app/roundOffExplanation/${true}/${System.currentTimeMillis()}/${args.screenFlow}",
                popUpTo = R.id.roundOffCalculatedFragment,
                inclusive = true
            )
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.initialRoundOffLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                orderId = it?.orderId
                roundOffAmount = it?.transactionAmount.orZero()
                if (orderId.isNullOrEmpty().not())
                    viewModel.fetchPaymentTransactionBreakup(orderId, null)
                analyticsHandler.postEvent(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_Detection_Screen,
                    mapOf(
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown,
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.RoundoffAmount to roundOffAmount
                    )
                )
                val currentTime = System.currentTimeMillis()
                if (args.screenFlow == EventKey.HOME_SCREEN) {
                    analyticsHandler.postEvent(
                        RoundOffEventKey.Roundoff_Detection_Screen_Ts,
                        mapOf(
                            EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(endTimeTime = currentTime, startTime = args.clickTime.toLong())
                        )
                    )
                }
                if (it?.transactionAmount?.toInt().orZero() == 0)
                    showNoSpendsDetectedUI()
                else {
                    showSpendsDetectedUI()
                    binding.tvRoundOffValue.text =
                        getCustomStringFormatted(
                            MR.strings.feature_round_off_currency_int_x,
                            roundOffAmount.toInt()
                        )
                }
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                showNoSpendsDetectedUI()
            },
            onError = {
                dismissProgressBar()
            }
        )
        viewModel.paymentTransactionBreakupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                binding.rvRoundOffsDetected.isVisible = true
                roundOffsListSize = it.transactions.size
                binding.clPaymentNumberContainer.isVisible = roundOffsListSize > 0
                if (roundOffsListSize > 0)
                    binding.tvNumberOfPayment.text =
                        getCustomStringFormatted(MR.strings.feature_round_off_x_payments, roundOffsListSize)

                binding.tvSeeMoreOrLess.isVisible =
                    (roundOffsListSize > 2) && (roundOffsListSize != 3)
                if (roundOffsListSize > 2) {
                    showMoreUI()
                    setupSeeMoreOrLessListener()
                } else if (roundOffsListSize in 1..2) {
                    showLessUI(shouldShowSeeMoreOrLess = false)
                }
                adapter?.submitList(it.transactions)
            },
            onError = { dismissProgressBar() }
        )
    }

    private fun showNoSpendsDetectedUI() {
        binding.clNoRoundOffDetected.isVisible = true
        binding.clRoundOffContainer.isVisible = false
    }

    private fun showSpendsDetectedUI() {
        binding.clRoundOffContainer.isVisible = true
        binding.clNoRoundOffDetected.isVisible = false
    }

    private fun setupSeeMoreOrLessListener() {
        binding.tvSeeMoreOrLess.setDebounceClickListener {
            if (isExpanded) {
                isExpanded = false
                showMoreUI()
            } else {
                isExpanded = true
                showLessUI(true)
            }
        }
    }

    private fun showLessUI(shouldShowSeeMoreOrLess: Boolean = false) {
        binding.tvSeeMoreOrLess.isVisible = shouldShowSeeMoreOrLess
        binding.tvSeeMoreOrLess.text = getCustomString(MR.strings.feature_round_off_see_less)
        binding.tvSeeMoreOrLess.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_up,
            0
        )
        gridLayoutManager =
            GridLayoutManager(
                requireContext(),
                roundOffsListSize,
                RecyclerView.HORIZONTAL,
                false
            )
        binding.rvRoundOffsDetected.layoutManager = gridLayoutManager
    }

    private fun showMoreUI() {
        binding.tvSeeMoreOrLess.text = getCustomString(MR.strings.feature_round_off_see_more)
        binding.tvSeeMoreOrLess.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_down,
            0
        )
        gridLayoutManager =
            object : GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        gridLayoutManager?.canScrollHorizontally()
        binding.rvRoundOffsDetected.layoutManager = gridLayoutManager
    }

    private fun openUserTransactionsBottomSheet() {
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Roundoff_Detection_Screen,
            mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.View_Transaction)
        )
        navigateTo(
            RoundOffCalculatedFragmentDirections.actionRoundOffCalculatedFragmentToInitialRoundOffBottomSheet(
                orderId,
                com.jar.app.feature_round_off.shared.domain.model.RoundOffType.SMS.name,
                roundOffAmount
            )
        )
    }
}