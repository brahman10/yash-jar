package com.jar.app.feature_gold_sip.shared.domain.model

import com.jar.app.feature_gold_sip.shared.GoldSipMR
import dev.icerock.moko.resources.StringResource

enum class SipSubscriptionType(val textRes: StringResource) {
    WEEKLY_SIP(GoldSipMR.strings.feature_gold_sip_weekly),
    MONTHLY_SIP(GoldSipMR.strings.feature_gold_sip_monthly);

    companion object {
        fun getTypeForValue(value: String): SipSubscriptionType {
            return SipSubscriptionType.valueOf(value)
        }
    }
}