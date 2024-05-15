package com.jar.app.feature_settings.domain.model

import com.jar.app.feature_user_api.domain.model.SavedVPA

data class SavedUpiIdsPaymentMethod(
    val savedUpiIds: List<SavedVPA>,
    override val position: Int
) : PaymentMethod()