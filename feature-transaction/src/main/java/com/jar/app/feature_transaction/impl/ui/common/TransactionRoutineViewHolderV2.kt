package com.jar.app.feature_transaction.impl.ui.common

import androidx.core.view.isVisible
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellTransactionRoutineBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnRoutine

class TransactionRoutineViewHolderV2(private val binding: FeatureTransactionCellTransactionRoutineBinding) :
    BaseViewHolder(binding.root) {

    fun setTransactionRoutine(txnRoutine: com.jar.app.feature_transaction.shared.domain.model.TxnRoutine) {
        binding.clPlaceholder.isVisible = false
        binding.clContent.isVisible = true
        binding.tvKey.text = txnRoutine.key
        binding.tvValue.text = txnRoutine.value
    }
}