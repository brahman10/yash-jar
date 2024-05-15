package com.jar.app.feature_lending.shared.domain.model.v2


import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class ForeCloseTxnData(
    val txnId: String,
    val txnDate: String,
    val paidUsing: String,
    val paidUsingDetail: String,
) : Parcelable