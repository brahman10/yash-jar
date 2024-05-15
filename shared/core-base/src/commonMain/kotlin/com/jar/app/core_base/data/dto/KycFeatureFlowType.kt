package com.jar.app.core_base.data.dto

enum class KycFeatureFlowType {
    LENDING,
    P2P_INVESTMENT,
    UNKNOWN
}

fun getKycFeatureFlowType(type: String): KycFeatureFlowType {
    return when (type) {
        KycFeatureFlowType.LENDING.name -> KycFeatureFlowType.LENDING
        KycFeatureFlowType.P2P_INVESTMENT.name -> KycFeatureFlowType.P2P_INVESTMENT
        else -> KycFeatureFlowType.UNKNOWN
    }
}

fun KycFeatureFlowType.isFromLending() = (this == KycFeatureFlowType.LENDING)
fun KycFeatureFlowType.isFromP2PInvestment() = (this == KycFeatureFlowType.P2P_INVESTMENT)
fun KycFeatureFlowType.isFromP2POrLending() = this.isFromP2PInvestment() || this.isFromLending()