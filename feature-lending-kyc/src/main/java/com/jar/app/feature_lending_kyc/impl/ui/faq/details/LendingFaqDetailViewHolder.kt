package com.jar.app.feature_lending_kyc.impl.ui.faq.details

import androidx.core.view.isVisible
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending_kyc.shared.domain.model.Faq
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycCellFaqDetailBinding

internal class LendingFaqDetailViewHolder(private val binding: FeatureLendingKycCellFaqDetailBinding) :
    BaseViewHolder(binding.root) {

    fun setLendingFaqDetail(faq: Faq) {
        binding.tvQuestion.text = faq.question
        binding.tvAnswer.text = faq.answer
        faq.disclaimer?.let {
            binding.tvDisclaimer.isVisible = true
            binding.tvDisclaimer.text = it
        } ?: run {
            binding.tvDisclaimer.isVisible = false
        }
    }
}