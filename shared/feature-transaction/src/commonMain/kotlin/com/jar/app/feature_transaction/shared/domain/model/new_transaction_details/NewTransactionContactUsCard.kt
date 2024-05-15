package com.jar.app.feature_transaction.shared.domain.model.new_transaction_details

import com.jar.app.core_base.util.BaseConstants

class NewTransactionContactUsCard : NewTransactionDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.NewTransactionDetailsPosition.TRANSACTION_CONTACT_US
    }

    override fun equals(other: Any?): Boolean {
        return other is NewTransactionContactUsCard
    }
}