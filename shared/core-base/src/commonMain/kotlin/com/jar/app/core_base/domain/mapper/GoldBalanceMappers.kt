package com.jar.app.core_base.domain.mapper

import com.jar.app.core_base.data.dto.*
import com.jar.app.core_base.domain.model.*

fun GoldBalanceDTO.toGoldBalance(): GoldBalance {
    return GoldBalance(
        balanceView = balanceView,
        unit = unit,
        volume = volume,
        currentValue = currentValue,
        investedValue = investedValue,
        unitPreference = unitPreference,
        volumeInMg = volumeInMg,
        showLeaseBanner = showLeaseBanner,
        goldLeaseBreakupObject = goldLeaseBreakupObject?.toGoldLeaseBreakupObject(),
        firstTransactionLockerDataObject = firstTransactionLockerDataObject?.toFirstTransactionLockerDataObject(),
        jarWinningsFooter = jarWinningsFooter?.toJarWinningsFooterObject()
    )
}

fun JarWinningsFooterDTO.toJarWinningsFooterObject(): JarWinningsFooterObject {
    return JarWinningsFooterObject(
        text = text,
        iconUrl = iconUrl,
        bgColor = bgColor
    )
}

fun GoldLeaseBreakupObjectDTO.toGoldLeaseBreakupObject(): GoldLeaseBreakupObject {
    return GoldLeaseBreakupObject(
        title = title,
        volumeLeased = volumeLeased,
        amountLeased = amountLeased
    )
}

fun FirstTransactionLockerDataDTO.toFirstTransactionLockerDataObject(): FirstTransactionLockerDataObject {
    return FirstTransactionLockerDataObject(
        backgroundImage = backgroundImage,
        header = header,
        title = title,
        subTitle = subTitle,
        showGoldBalanceAnimation = showGoldBalanceAnimation,
        firstTransactionLockerCtaObject = firstTransactionLockerCtaObject?.toFirstTransactionLockerCtaObject(),
        txnCount = txnCount,
        variant = variant,
        primaryTextColor = primaryTextColor,
        secondaryTextColor = secondaryTextColor
    )
}

fun FirstTransactionLockerCtaDTO.toFirstTransactionLockerCtaObject(): FirstTransactionLockerCtaObject {
    return FirstTransactionLockerCtaObject(
        ctaDeeplink = ctaDeeplink,
        title = title,
        ctaText = ctaText,
        ctaColor = ctaColor,
    )
}