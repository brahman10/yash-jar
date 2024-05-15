package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseFaq(
    @SerialName("leaseFaqSubObjects")
    val leaseFaqSubObjects: List<GoldLeaseFaqObjects>? = null
)

@kotlinx.serialization.Serializable
data class GoldLeaseFaqObjects(
    @SerialName("header")
    val header: String? = null,

    @SerialName("leaseFaqIndividualObjects")
    val leaseFaqIndividualObjects: List<GoldLeaseFaqIndividualObject>? = null,

    //For UI
    var isExpanded: Boolean = false
)

@kotlinx.serialization.Serializable
data class GoldLeaseFaqIndividualObject(
    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null
)