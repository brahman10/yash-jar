package com.jar.app.feature_user_api.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class PauseSavingResponse(
    @SerialName("isSavingsPaused")
    val isSavingPaused: Boolean? = null,
    @SerialName("pausedFor")
    val pausedFor: String? = null,
    @SerialName("willResumeIn")
    val willResumeIn: Int? = null,
    @SerialName("customPausedFor")
    val customPausedFor: Long? = null
) {
    fun getPauseSavingNumericValue(pausedFor: String?): String {
        return pausedFor?.let {
            when (it) {
                PauseSavingOption.ONE.name -> "1 Day"
                PauseSavingOption.TWO.name -> "2 Days"
                PauseSavingOption.FIVE.name -> "5 Days"
                PauseSavingOption.EIGHT.name -> "8 Days"
                PauseSavingOption.TEN.name -> "10 Days"
                PauseSavingOption.TWELVE.name -> "12 Days"
                PauseSavingOption.FIFTEEN.name -> "15 Days"
                PauseSavingOption.TWENTY.name -> "20 Days"
                PauseSavingOption.MONTH.name -> "1 Month"
                else -> "0 Month"
            }
        } ?: run {
            ""
        }
    }

}
