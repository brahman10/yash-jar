package com.jar.app.feature_gifting.shared.domain.model

sealed interface GiftView {
    fun getOrder(): Int
}