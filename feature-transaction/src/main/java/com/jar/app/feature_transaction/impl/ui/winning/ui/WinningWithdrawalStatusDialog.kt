package com.jar.app.feature_transaction.impl.ui.winning.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.RefreshUserGoldBalanceEvent
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.countDownTimer
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentWinningWithdrawalStatusDialogBinding
import org.greenrobot.eventbus.EventBus

class WinningWithdrawalStatusDialog : BaseDialogFragment<FeatureTransactionFragmentWinningWithdrawalStatusDialogBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentWinningWithdrawalStatusDialogBinding
        get() = FeatureTransactionFragmentWinningWithdrawalStatusDialogBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    override fun setup() {
        binding.animTick.playLottieWithUrlAndExceptionHandling(requireContext(), BaseConstants.LottieUrls.NOTE_STACK)
        uiScope.countDownTimer(3000, onFinished = {
            EventBus.getDefault().post(GoToHomeEvent("WinningWithdrawalStatusDialog"))
        })
        EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())
    }
}