package com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder

import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPreferredBankBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PreferredBankPageItem

internal class PreferredBankViewHolder(
    private val binding: FeatureMandatePaymentCellPreferredBankBinding,
    private val onPreferredBankClicked: (preferredBankPageItem: PreferredBankPageItem) -> Unit
): BaseViewHolder(binding.root) {
    private var preferredBankPageItem: PreferredBankPageItem? = null

    init {
        binding.root.setDebounceClickListener {
            preferredBankPageItem?.let {
                onPreferredBankClicked.invoke(it)
            }
        }
    }

    fun bind(data: PreferredBankPageItem) {
        preferredBankPageItem = data
        binding.tvPreferredBankTitle.text = data.title.orEmpty()
        binding.tvPreferredBankName.text = data.bankName.orEmpty()
        data.bankIconLink.takeIf { it.isNullOrEmpty().not() }?.let {
            Glide.with(context).load(it).into(binding.ivPreferredBankLogo)
        }
    }
}