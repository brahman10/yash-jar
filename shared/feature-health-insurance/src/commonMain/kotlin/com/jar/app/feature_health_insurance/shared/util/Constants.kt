package com.jar.app.feature_health_insurance.shared.util

object Constants {

    const val MANUAL_PAYMENT = "manual"
    const val MANDATE_PAYMENT = "mandate"
    const val INSURANCE_FLOWTYPE = "INSURANCE"
    const val INSURANCE_MANDATE_TITLE = "Health Insurance"
    const val INSURANCE_DETAILS_MANDATE = "Let’s automate your monthly premium"
    const val ADD_DETAILS_SCREEN = "ADD DETAIL SCREEN"
    const val PAYMENT_STATUS = "PAYMENT_STATUS"

    internal object Endpoints{
        const val FETCH_PREMIUM_DETAILS = "v1/api/insurance/insuranceConfig"
        const val FETCH_LANDING_DETAILS = "/v1/api/healthInsurance/landing"
        const val FETCH_BENEFITS = "/v1/api/healthInsurance/benefits"
        const val FETCH_INCOMPLETE_PROPOSAL = "v1/api/insurance/getIncompleteProposal"
        const val FETCH_PAYMENT_STATUS = "/v1/api/insurance/status"
        const val FETCH_PLAN_COMPARISONS = "/v1/api/insurance/insurancePlanBenefits"
        const val INITIATE_INSURANCE_PROPOSAL = "v1/api/insurance/initiateProposal"
        const val FETCH_PLAN_CONFIG = "v1/api/insurance/insuranceConfig"
        const val CREATE_PROPOSAL = "v1/api/insurance/createProposal"
        const val FETCH_PAYMENT_CONFIG = "v1/api/insurance/getPaymentConfig"
        const val FETCH_ADD_DETAILS_SCREEN_STATIC_DATA = "v1/api/healthInsurance/addDetails"
        const val FETCH_MANAGE_SCREEN_DATA = "v1/api/insurance/details"
        const val FETCH_INSURANCE_TRANSACTIONS = "v1/api/insurance/transactions"
        const val FETCH_INSURANCE_TRANSACTION_DETAILS = "/v1/api/insurance/transactions/details"
    }
}