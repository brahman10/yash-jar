package com.jar.app.feature_user_api.domain.mappers

import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.toBoolean
import com.jar.app.feature_user_api.data.dto.UserMetaDTO
import com.jar.app.feature_user_api.domain.model.UserMetaData
import com.jar.app.featureuserapi.shared.UserMetaDataEntity

fun UserMetaDTO.toUserMetaData(): UserMetaData {
    return UserMetaData(
        id = id?.toLong().orZero(),
        referAndEarnDescription = referAndEarnDescription,
        referralEarnings = referralEarnings,
        notificationCount = notificationCount?.toLong(),
        popupType = popupType,
        pendingGoldGift = pendingGoldGift,
        creditCardShow = creditCardShow,
        shouldShowLoanCard = shouldShowLoanCard,
        showVasooliCard = showVasooliCard,
        showDuoCard = showDuoCard
    )
}

fun UserMetaDataEntity.toUserMetaData(): UserMetaData {
    return UserMetaData(
        id = id,
        referAndEarnDescription = referAndEarnDescription,
        referralEarnings = referralEarnings,
        notificationCount = notificationCount,
        popupType = popupType,
        pendingGoldGift = pendingGoldGift?.toBoolean(),
        creditCardShow = creditCardShow?.toBoolean(),
        shouldShowLoanCard = shouldShowLoanCard?.toBoolean(),
        showVasooliCard = showVasooliCard?.toBoolean(),
        showDuoCard = showDuoCard?.toBoolean()
    )
}