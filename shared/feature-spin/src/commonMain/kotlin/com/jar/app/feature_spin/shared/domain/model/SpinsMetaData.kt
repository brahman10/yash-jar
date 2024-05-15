package com.jar.app.feature_spin.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class SpinsMetaData(
    @SerialName("spinsRemaining")
    val spinsRemaining: Int,

    @SerialName("spinsRemainingToday")
    val spinsRemainingToday: Int?,

    @SerialName("areSpinsExhausted")
    val areSpinsExhausted: Boolean,

    @SerialName("spinsPerDayLimit")
    val spinsPerDayLimit: Int,

    var shouldShowDialog: Boolean = false,

    @SerialName("winnersScroll")
    val winnersScroll: List<String>,

    var cardTitle: String?,

    var cardDescription: String?

) : Parcelable {

    fun canSpin() = spinsRemaining > 0 && !areSpinsExhausted

    // TODO: FIX THIS
//    fun getLockedReason(context: Context): String? {
//        return when {
//            spinsRemaining == 0 -> context.getString(R.string.feature_homepage_the_game_will_be_unlocked_on_your_next_transaction)
//            areSpinsExhausted -> context.getString(
//                R.string.feature_homepage_you_ve_used_spins_x_daily_spins,
//                spinsPerDayLimit
//            )
//            else -> null
//        }
//    }
}