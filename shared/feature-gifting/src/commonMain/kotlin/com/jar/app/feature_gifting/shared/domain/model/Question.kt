package com.jar.app.feature_gifting.shared.domain.model

data class Question(
    val text: String,
    val questionOrder: Int
) : GiftView {

    override fun getOrder(): Int {
        return questionOrder
    }

}