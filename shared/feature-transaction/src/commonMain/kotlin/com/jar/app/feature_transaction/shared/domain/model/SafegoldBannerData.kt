package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants

class SafegoldBannerData : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override val uniqueKey: String
        get() = "SAFE_GOLD_BANNER".plus(BaseConstants.TxnDetailsPosition.SAFE_GOLD_BANNER)

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.SAFE_GOLD_BANNER
    }

    override fun equals(other: Any?): Boolean {
        return other is SafegoldBannerData
    }
}