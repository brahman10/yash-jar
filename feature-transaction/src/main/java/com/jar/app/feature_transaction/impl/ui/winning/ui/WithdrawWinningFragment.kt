package com.jar.app.feature_transaction.impl.ui.winning.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.CacheEvictionUtil
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.TransactionNavigationDirections
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentWithdrawWinningsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawWinningFragment : BaseFragment<FeatureTransactionFragmentWithdrawWinningsBinding>() {

    @Inject
    lateinit var cacheEvictionUtil: CacheEvictionUtil

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val viewModel by viewModels<WithdrawWinningFragmentViewModel> { defaultViewModelProviderFactory }

    private var buyPriceTimerJob: Job? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentWithdrawWinningsBinding
        get() = FeatureTransactionFragmentWithdrawWinningsBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(AppBarData(ToolbarDefault(title = getString(R.string.feature_transaction_invest_all_winnings)))))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupListeners() {
        binding.btnInvestInGold.setDebounceClickListener {
            investWinningInGold()
        }

        binding.btnInvestAllWinnings.setOnCheckedChangeListener { _, checked ->
            if (checked)
                binding.etAmount.setText("${viewModel.userWinningsLiveData.value?.data?.data?.winningsAmount.orZero()}")
        }
    }

    private fun investWinningInGold() {
        val amount = binding.etAmount.text
        if (!amount.isNullOrBlank()) {
            viewModel.investWinningInGold(amount.toString().toDouble())
        } else {
            getString(R.string.feature_transaction_enter_upi_id).snackBar(binding.rsSymbol)
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.userWinningsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                binding.tvValue.text = getString(R.string.feature_transaction_rupee_x_in_double, it.winningsAmount)
                binding.tvMinWinningMessage.text = it.winningLimitMessage
                binding.tvMinWinningMessage.isVisible = !it.winningLimitMessage.isNullOrBlank()
            }
        )

        viewModel.currentGoldBuyPriceLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                val prefix = getString(R.string.feature_transaction_current_buy_price)
                val suffix = getString(
                    R.string.feature_transaction_n_double_gm,
                    it.price
                )
                val spannable = SpannableStringBuilder()
                    .append(prefix)
                    .append(" ")
                    .bold { append(suffix) }
                binding.tvCurrentBuyPrice.text = spannable
                startPriceValidityTimer(it.getValidityInMillis())
            }
        )

        viewModel.investInWinningLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                cacheEvictionUtil.evictHomePageCache()
                navigateTo(
                    TransactionNavigationDirections.actionWinningWithdrawalStatusDialog()
                )
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                cacheEvictionUtil.evictHomePageCache()
                navigateTo(
                    TransactionNavigationDirections.actionWinningWithdrawalStatusDialog()
                )
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun getData() {
        viewModel.fetchUserWinnings()
        viewModel.fetchCurrentGoldBuyPrice()
    }

    private fun startPriceValidityTimer(validityInMillis: Long) {
        buyPriceTimerJob?.cancel()
        buyPriceTimerJob = uiScope.countDownTimer(
            validityInMillis,
            onInterval = {
                val spannable = SpannableStringBuilder()
                    .append(getString(R.string.feature_transaction_price_valid_for))
                    .append(" ")
                    .bold { append(it.milliSecondsToCountDown()) }
                binding.tvPriceValidity.text = spannable
            },
            onFinished = {
                viewModel.fetchCurrentGoldBuyPrice()
            }
        )
    }
}