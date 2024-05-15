package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants

class ContactUsData : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override val uniqueKey: String
        get() = "CONTACT_US_SECTION".plus(BaseConstants.TxnDetailsPosition.CONTACT_US)

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.CONTACT_US
    }

    override fun equals(other: Any?): Boolean {
        return other is ContactUsData
    }
}