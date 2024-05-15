package com.jar.app.feature_gold_price.shared.data.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class FetchCurrentGoldPriceResponse(
    @SerialName("price")
    val price: Float,

    @SerialName("applicableTax")
    val applicableTax: Float? = null,

    @SerialName("rateId")
    val rateId: String,

    @SerialName("rateValidity")
    val rateValidity: String? = null,

    @SerialName("isPriceDrop")
    val isPriceDrop: Boolean? = null,

    @SerialName("validity")
    val validityInSeconds: Long? = null
) : Parcelable {

    fun getValidityInMillis(): Long {
        //Used not-null assertion instead of `?: 0L` for null handling.
        //Because if the validity is 0, API will be called every second
        return validityInSeconds!! * 1000

        /* Keeping the below code for future reference*/

//        val expiryDate = LocalDateTime.parse(rateValidity, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//        val expiryInstant = expiryDate.toInstant(ZoneOffset.UTC)
//        val nowDate = LocalDateTime.now(ZoneId.systemDefault())
//        val nowInstant = nowDate.toInstant(ZoneOffset.UTC)
//        return Duration.between(nowInstant, expiryInstant).toMillis()
    }

}