package com.jar.app.feature_gold_lease.shared.util

import com.jar.app.core_base.util.BaseConstants

object GoldLeaseConstants {

    const val VALUE_PLACEHOLDER = "valuePlaceholder"

    const val UNIT_GM = "gm"
    object LottieUrls {
        const val GENERIC_ERROR =
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Lending_Kyc/generic-error.json"
        const val TICK_WITH_CELEBRATION =
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Lending_Kyc/tick_with _celebration.json"
        const val VERIFYING = "${BaseConstants.CDN_BASE_URL}/LottieFiles/KYC/verifying.json"
        const val NEW_LEASE_ARROW = "${BaseConstants.CDN_BASE_URL}/LottieFiles/GoldLease/arrow_main.lottie"
        const val MY_ORDERS_COIN = "${BaseConstants.CDN_BASE_URL}/LottieFiles/GoldLease/coin_main_v2.lottie"
    }

    object LeaseAssetProvider {
        const val SAFEGOLD = "SAFEGOLD"
    }

    internal object Endpoints {
        const val FETCH_GOLD_LEASE_FAQS = "v2/api/leasing/faqs"
        const val FETCH_GOLD_LEASE_ORDER_SUMMARY = "v2/api/leasing/orderSummary"
        const val FETCH_GOLD_LEASE_OPTIONS = "v2/api/leasing/goldOptions"
        const val FETCH_GOLD_LEASE_JEWELLER_LISTING = "v2/api/leasing/jewellerListing"
        const val FETCH_GOLD_LEASE_PLAN_FILTERS = "v2/api/leasing/listingFilters"
        const val FETCH_GOLD_LEASE_PLANS = "v2/api/leasing/listPlans"
        const val FETCH_GOLD_LEASE_JEWELLER_DETAILS = "v2/api/leasing/jewellerDetails"
        const val FETCH_GOLD_LEASE_LANDING_DETAILS = "v2/api/leasing/landing/newLease"
        const val INITIATE_GOLD_LEASE_V2 = "v2/api/leasing/initiateLeaseOrder"
        const val FETCH_GOLD_LEASE_MY_ORDERS = "v2/api/leasing/landing/orders"
        const val FETCH_USER_LEASE = "v2/api/leasing/userLeases"
        const val FETCH_USER_LEASE_DETAILS = "v2/api/leasing/orderDetails"
        const val FETCH_GOLD_LEASE_V2_TRANSACTIONS = "v2/api/leasing/transactionsList"
        const val FETCH_GOLD_LEASE_STATUS = "v2/api/leasing/statusCheck"
        const val FETCH_GOLD_LEASE_RETRY = "v2/api/leasing/retry"
        const val FETCH_LEASE_TERMS_AND_CONDITIONS = "v2/api/leasing/termsAndConditions"
        const val FETCH_LEASE_RISK_FACTORS = "v2/api/leasing/riskFactors"
    }
}