package com.jar.app.feature_vasooli.impl.ui.add_repayment

import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.impl.domain.model.PaymentMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PaymentModeListGenerator @Inject constructor(){

    fun getPaymentModeList(): List<PaymentMode> {
        val list = ArrayList<PaymentMode>()

        list.add(
            PaymentMode(id = 1, title = R.string.feature_vasooli_upi)
        )

        list.add(
            PaymentMode(id = 2, title = R.string.feature_vasooli_card)
        )

        list.add(
            PaymentMode(id = 3, title = R.string.feature_vasooli_cash)
        )

        list.add(
            PaymentMode(id = 4, title = R.string.feature_vasooli_other)
        )

        return list
    }

}