package com.jar.app.feature_gold_lease.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class GoldLeaseV2Filters(
    @SerialName("leasePlanFilterInfoList")
    val leasePlanFilterInfoList: List<LeasePlanFilterInfoList>? = null
)

@kotlinx.serialization.Serializable
data class LeasePlanFilterInfoList(
    @SerialName("leasePlanListingFilterEnum")
    private val leasePlanListingFilterEnum: String? = null,

    @SerialName("leasePlanListingFilterName")
    val leasePlanListingFilterName: String? = null,

    @SerialName("leasePlanCount")
    val leasePlanCount: Int? = null,

    @SerialName("defaultFilter")
    val defaultFilter: Boolean? = null,

    @SerialName("noLeaseTitle")
    val noLeaseTitle: String? = null,

    @SerialName("noLeaseDescription")
    val noLeaseDescription: String? = null,

    //For UI
    var isSelected: Boolean = false
) {
    fun getLeasePlanListingFilter() = leasePlanListingFilterEnum ?: LeasePlanListingFilterEnum.ALL.name
}

enum class LeasePlanListingFilterEnum {
    ALL
}