package com.jar.app.feature_user_api.domain.mappers

import com.jar.app.feature_user_api.data.dto.*
import com.jar.app.feature_user_api.domain.model.*

fun DetectedSpendsDTO.toDetectedSpends(): DetectedSpendsData {
    return DetectedSpendsData(
        isPGEnabled = isPGEnabled,
        initialInvestment = initialInvestment,
        fullPaymentInfo = fullPaymentInfoDTO?.toFullPaymentInfo(),
        partPaymentInfo = partPaymentInfoDTO?.toPartPaymentInfo(),
        promptEnabled = promptEnabled,
        investPromptTitle = investPromptTitle,
        investPromptSubTitle = investPromptSubTitle,
        investPromptAmt = investPromptAmt,
        investPromptSuggestions = investPromptSuggestions?.toSuggestedAmount()
    )
}

fun FullPaymentInfoDTO.toFullPaymentInfo(): FullPaymentInfo {
    return FullPaymentInfo(
        txnAmt = txnAmt,
        title = title,
        orderId = orderId,
        description = description,
        nudgeText = nudgeText
    )
}

fun PartPaymentInfoDTO.toPartPaymentInfo(): PartPaymentInfo {
    return PartPaymentInfo(
        title = title,
        description = description,
        skipAvailable = skipAvailable,
        skipInfo = skipInfo,
        paymentOptions = paymentOptions.toPartPaymentOption()
    )
}

fun List<PartPaymentOptionDTO>.toPartPaymentOption(): List<PartPaymentOption> {
    return map {
        PartPaymentOption(
            percentage = it.percentage,
            amount = it.amount
        )
    }
}

fun List<SuggestedAmountDTO>.toSuggestedAmount(): List<SuggestedAmount> {
    return map {
        SuggestedAmount(
            amount = it.amount,
            recommended = it.recommended,
            unit = it.unit
        )
    }
}