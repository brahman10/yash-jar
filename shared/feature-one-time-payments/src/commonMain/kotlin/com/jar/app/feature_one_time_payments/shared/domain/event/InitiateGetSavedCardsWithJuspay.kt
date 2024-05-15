package com.jar.app.feature_one_time_payments.shared.domain.event

import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.GetSavedCardsPayload

data class InitiateGetSavedCardsWithJuspay(val getSavedCardsPayload: GetSavedCardsPayload)