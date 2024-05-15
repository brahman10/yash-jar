package com.jar.app.core_base.domain.model

import com.jar.app.core_base.util.roundUp
import com.jar.app.core_base.util.roundUpToPlainString

enum class GoldBalanceViewType {
    ONLY_RS,
    ONLY_GM,
    GM_ND_RS,
    RS_ND_GM
}

data class GoldBalance(

    private val balanceView: String? = null,

    val unit: String,

    val volume: Float,

    val currentValue: Float? = null,

    val investedValue: Float? = null,

    val unitPreference: String? = null,

    val volumeInMg: Float? = null,

    val showLeaseBanner: Boolean? = null,

    val goldLeaseBreakupObject: GoldLeaseBreakupObject? = null,

    val firstTransactionLockerDataObject: FirstTransactionLockerDataObject? = null,

    val jarWinningsFooter: JarWinningsFooterObject? = null
) {

    fun getBalanceViewData(): GoldBalanceViewType {
        return GoldBalanceViewType.values().find { it.name == balanceView }
            ?: GoldBalanceViewType.ONLY_GM
    }

    fun getGoldVolumeWithUnit(
        volumeInMg: Float? = this.volumeInMg,
        volume: Float = this.volume
    ): String {
        return if (unitPreference == "mg")
            "${volumeInMg?.roundUp(1)}${unitPreference}"
        else "${volume.roundUpToPlainString(4)}${unit}"
    }

    fun getGoldVolumeWithUnitInFloatWithUnit(
        volumeInMg: Float? = this.volumeInMg,
        volume: Float = this.volume
    ): Pair<Float?, String> {
        return if (unitPreference == "mg")
            Pair(volumeInMg?.roundUp(1), unitPreference)
        else Pair(volume.roundUp(4), unit)
    }

    fun hasGold(): Boolean {
        if (volume == 0.0f)
            return false
        return true
    }

    fun isGoldLeased(): Boolean {
        if (goldLeaseBreakupObject == null)
            return false
        return true
    }
}

data class JarWinningsFooterObject(
    val text: String? = null,

    val iconUrl: String? = null,

    val bgColor: String? = null
)

data class GoldLeaseBreakupObject(
    val title: String? = null,

    val volumeLeased: Float? = null,

    val amountLeased: Float? = null
)

data class FirstTransactionLockerDataObject(
    val backgroundImage: String? = null,

    val header: String? = null,

    val title: String? = null,

    val subTitle: String? = null,

    val showGoldBalanceAnimation: Boolean? = null,

    val firstTransactionLockerCtaObject: FirstTransactionLockerCtaObject? = null,

    val txnCount: Int? = null,

    val variant: String? = null,

    val primaryTextColor: String? = null,

    val secondaryTextColor: String? = null,
)


data class FirstTransactionLockerCtaObject(

    val ctaDeeplink: String? = null,

    val ctaText: String? = null,

    val title: String? = null,

    val ctaColor: String? = null,
)