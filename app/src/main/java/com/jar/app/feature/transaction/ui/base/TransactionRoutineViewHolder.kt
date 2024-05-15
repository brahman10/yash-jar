package com.jar.app.feature.transaction.ui.base

import androidx.core.view.isVisible
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellTransactionRoutineBinding
import com.jar.app.feature_transaction.shared.domain.model.TxnRoutine

class TransactionRoutineViewHolder(private val binding: CellTransactionRoutineBinding) :
    BaseViewHolder(binding.root) {

    fun setTransactionRoutine(txnRoutine: com.jar.app.feature_transaction.shared.domain.model.TxnRoutine) {
        binding.clPlaceholder.isVisible = false
        binding.clContent.isVisible = true
        binding.tvKey.text = txnRoutine.key
        binding.tvValue.text = txnRoutine.value
    }
}