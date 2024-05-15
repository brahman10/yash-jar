package com.jar.app.feature_transaction.impl.ui.retry

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellVpaChipBinding

class VpaChipViewHolder(
    private val binding: FeatureTransactionCellVpaChipBinding,
    private val onClick: (string: String) -> Unit
) : BaseViewHolder(binding.root) {

    private lateinit var vpaName: String

    init {
        binding.tvVpaName.setDebounceClickListener {
            if (::vpaName.isInitialized)
                onClick.invoke(vpaName)
        }
    }

    fun setVpaChip(vpa: String) {
        vpaName = vpa
        binding.tvVpaName.text = vpa
    }
}