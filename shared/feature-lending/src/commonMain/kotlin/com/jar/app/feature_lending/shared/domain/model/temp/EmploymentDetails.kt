package com.jar.app.feature_lending.shared.domain.model.temp

import com.jar.app.core_base.util.orZero
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class EmploymentDetails(
    @SerialName("employmentDetails")
    val employmentDetails: EmploymentDetailsRequest? = null,

    //Only used when we need to update details instead of creating a new application
    @SerialName("applicationId")
    var applicationId: String? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class EmploymentDetailsRequest(
    @SerialName("employmentType")
    val employmentType: String? = null,

    @SerialName("companyName")
    val companyName: String? = null,

    @SerialName("monthlyIncome")
    val monthlyIncome: Float? = null,

    @SerialName("annualIncome")
    val annualIncome: Int? = null,

    @SerialName("status")
    val status: String? = null
) : Parcelable {
    fun getMonthlyIncome(): Int {
        return if (monthlyIncome.orZero() > 0) {
            monthlyIncome.orZero().toInt()
        } else {
            annualIncome.orZero()/12
        }
    }
}

enum class EmploymentType {
    SALARIED,
    SELF_EMPLOYED
}