package com.jar.app.feature_gifting.shared.domain.model

enum class GiftingState(val order: Int) {
    SHOW_CONTACT_SELECTION(0),
    SHOW_ENTER_NUMBER(1),
    SHOW_ENTER_AMOUNT(2),
    ALL_DETAILS_ENTERED(3)
}