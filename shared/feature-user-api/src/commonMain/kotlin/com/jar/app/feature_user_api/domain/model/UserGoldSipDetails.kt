package com.jar.app.feature_user_api.domain.model

@kotlinx.serialization.Serializable
data class UserGoldSipDetails(
    val bankLogo: String?,
    
    val bankName: String?,
    
    val enabled: Boolean,
    
    val nextDeductionDate: Long?,

    var pauseStatus: PauseStatusData?,

    val subsState: String?,
    
    val provider: String?,
    
    val subscriptionStatus: String?,
    
    val subscriptionAmount: Float,
    
    val subscriptionDay: Int,
    
    val subscriptionId: String?,
    
    val subscriptionType: String?,
    
    val updateDate: Long?,
    
    val upiId: String?,
    
    val manualPaymentDetails: FullPaymentInfo?,
    
    val mandateAmount: Float?,
    
    val order: Int?
)
