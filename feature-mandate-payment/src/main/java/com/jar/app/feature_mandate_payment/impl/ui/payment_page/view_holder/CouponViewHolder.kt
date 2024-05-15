package com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder

import android.view.View
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePageCouponLayoutBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.CouponCodeResponseForMandateScreenItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class CouponViewHolder(
    private val binding: FeatureMandatePageCouponLayoutBinding,
    private val uiScope: CoroutineScope,
) : BaseViewHolder(binding.root) {

    private var timerJob: Job? = null

    fun bind(data: CouponCodeResponseForMandateScreenItem) {
        binding.tvCouponHeader.text = data.couponCode.title.orEmpty()
        binding.tvCouponSubHeading.setHtmlText(data.couponCode.description.orEmpty())
        checkAndCancelTimerJob()
        timerJob = uiScope.countDownTimer(
            totalMillis = data.couponCode.validityInMillis.orZero() - System.currentTimeMillis(),
            onInterval = {
                binding.tvTimer.text = context.getString(
                    com.jar.app.core_ui.R.string.expires_in,
                    it.milliSecondsToCountDown()
                )
            },
            onFinished = {
                binding.clCouponHolder.visibility = View.GONE
            }
        )
        timerJob?.start()
        binding.tvTimer.text = data.couponCode.validityInMillis.orZero().toString()
    }


    fun checkAndCancelTimerJob() {
        if (timerJob?.isActive == true) {
            timerJob?.cancel()
            timerJob = null
        }
    }

}
