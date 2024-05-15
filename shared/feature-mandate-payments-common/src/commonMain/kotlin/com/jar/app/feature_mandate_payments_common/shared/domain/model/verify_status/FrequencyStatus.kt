package com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status

import com.jar.app.feature_mandate_payments_common.shared.MR
import dev.icerock.moko.resources.StringResource

enum class FrequencyStatus(val frequencyRes: StringResource) {
    daily(MR.strings.feature_mandate_payment_per_day_limit),
    once(MR.strings.feature_mandate_payment_day),
    weekly(MR.strings.feature_mandate_payment_per_week_limit),
    monthly(MR.strings.feature_mandate_payment_per_month_limit)
}