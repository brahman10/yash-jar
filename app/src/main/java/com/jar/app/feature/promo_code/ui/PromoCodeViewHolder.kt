package com.jar.app.feature.promo_code.ui

import androidx.core.view.isVisible
import com.jar.app.R
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellPromoCodeBinding
import com.jar.app.feature.promo_code.domain.data.PromoCode
import com.jar.app.core_ui.extension.setDebounceClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset

class PromoCodeViewHolder(
    private val binding: CellPromoCodeBinding,
    private val uiScope: CoroutineScope,
    private val onApplyClick: (Int, PromoCode) -> Unit
) : BaseViewHolder(binding.root) {

    private var promoCode: PromoCode? = null

    private var job: Job? = null

    init {
        binding.btnApply.setDebounceClickListener {
            promoCode?.let { onApplyClick.invoke(bindingAdapterPosition, it) }
        }
    }

    fun startTimer() {
        if (promoCode != null && promoCode?.expiresOn != null) {
            job?.cancel()
            job = uiScope.countDownTimer(
                diffBetweenEndEpochToCurrentInMillis(promoCode?.expiresOn!!),
                onInterval = {
                    toggleApplyButton(true)
                    binding.tvTimer.text =
                        context.getString(R.string.feature_buy_gold_expires_in_n, it.milliSecondsToCountDown())
                },
                onFinished = {
                    toggleApplyButton(false)
                }
            )
        }
    }

    private fun diffBetweenEndEpochToCurrentInMillis(endTimeEpochInSeconds: Long): Long {
        val endInstant = Instant.ofEpochSecond(endTimeEpochInSeconds).atOffset(ZoneOffset.UTC)
        val currentInstant = Instant.now().atOffset(ZoneOffset.UTC)
        return Duration.between(currentInstant, endInstant).toMillis()
    }

    fun setPromoCode(promoCode: PromoCode) {
        this.promoCode = promoCode
        binding.tvPromoCode.text = promoCode.promoCode
        binding.tvPromoCodeDescription.text = promoCode.description
        binding.tvTimer.isVisible = promoCode.expiresOn != null
    }

    fun stopTimer() {
        job?.cancel()
    }

    private fun toggleApplyButton(enable: Boolean) {
        binding.btnApply.alpha = if (enable) 1f else 0.5f
        binding.btnApply.isEnabled = enable
    }
}