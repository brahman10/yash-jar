package com.jar.app.feature_lending_kyc.impl.ui.faq

import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycCellFaqBinding
import com.jar.app.feature_lending_kyc.shared.domain.model.FaqTitleAndType

internal class LendingFaqViewHolder(
    private val binding: FeatureLendingKycCellFaqBinding,
    private val onClick: (faqTitleAndType: FaqTitleAndType) -> Unit
) : BaseViewHolder(binding.root) {

    private var faqTitleAndType: FaqTitleAndType? = null

    init {
        binding.root.setDebounceClickListener {
            faqTitleAndType?.let { onClick(it) }
        }
    }

    fun setFaq(faqTitleAndType: FaqTitleAndType) {
        this.faqTitleAndType = faqTitleAndType
        binding.tvTitle.text = faqTitleAndType.description
    }
}