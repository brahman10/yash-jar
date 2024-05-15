package com.jar.app.feature_spin.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
@Parcelize
data class SpinsData(
    @SerialName("id")
    val id: String,

    @SerialName("outcome")
    val outcome: Int,

    @SerialName("outcomeType")
    val outcomeType: String,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("options")
    val options: List<Int>,

    @SerialName("investedAmount")
    val investedAmount: Float = 0.0f,

    @SerialName("shareMsg")
    val shareMsg: String? = null
): Parcelable {

    companion object {
        private const val OUTCOME_DOUBLE = "DOUBLE"
        private const val OUTCOME_JACKPOT = "JACKPOT"
        private const val OUTCOME_DOUBLE_AMOUNT = 10000
        private const val OUTCOME_JACK_POT = 10001
    }

    fun getSpinOutcome(): SpinOutcome = when (outcomeType) {
        SpinsData.Companion.OUTCOME_JACKPOT -> SpinOutcome.JACKPOT
        SpinsData.Companion.OUTCOME_DOUBLE -> SpinOutcome.DOUBLE
        else -> SpinOutcome.REAL
    }

    //TODO: FIX THIS
//    fun getOptionText(context: Context, option: Int): String = when (option) {
//        OUTCOME_JACK_POT -> context.getString(com.jar.app.core_ui.R.string.jackpot)
//        OUTCOME_DOUBLE_AMOUNT -> context.getString(R.string.savings_double)
//        else -> context.getString(R.string.win_x, option)
//    }

    fun isJackpot() = outcome == OUTCOME_JACK_POT
}

enum class SpinOutcome {
    DOUBLE, JACKPOT, REAL
}

enum class CouponType{
    BRAND_COUPON,
    JAR_COUPON
}