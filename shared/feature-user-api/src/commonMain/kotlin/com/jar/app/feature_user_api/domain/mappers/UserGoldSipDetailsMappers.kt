package com.jar.app.feature_user_api.domain.mappers

import com.jar.app.feature_user_api.data.dto.PauseStatusDTO
import com.jar.app.feature_user_api.data.dto.UserGoldSipDetailsDTO
import com.jar.app.feature_user_api.domain.model.PauseStatusData
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails

fun UserGoldSipDetailsDTO.toUserGoldSipDetails(): UserGoldSipDetails {
    return UserGoldSipDetails(
        bankLogo = bankLogo,
        bankName = bankName,
        enabled = enabled,
        nextDeductionDate = nextDeductionDate,
        pauseStatus = pauseStatus?.toPauseStatusData(),
        provider = provider,
        subsState = subsState,
        subscriptionStatus = subscriptionStatus,
        subscriptionAmount = subscriptionAmount,
        subscriptionDay = subscriptionDay,
        subscriptionId = subscriptionId,
        subscriptionType = subscriptionType,
        mandateAmount = mandateAmount,
        updateDate = updateDate,
        upiId = upiId,
        manualPaymentDetails = manualPaymentDetails?.toFullPaymentInfo(),
        order = order
    )
}

fun PauseStatusDTO.toPauseStatusData(): PauseStatusData {
    return PauseStatusData(
        pausedFor = pausedFor,
        pausedOn = pausedOn,
        savingsPaused = savingsPaused,
        showAlertToUser = showAlertToUser,
        unPausedOn = unPausedOn,
        willResumeOn = willResumeOn
    )
}