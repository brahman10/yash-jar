package com.jar.app.feature_gold_lease.api

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse

interface GoldLeaseApi {

    fun openGoldLeaseV2Flow(flowType: String, tabPosition: Int = BaseConstants.GoldLeaseTabPosition.TAB_NEW_LEASE , isNewLeaseUser: Boolean = false)

    fun openGoldLeasePlans(flowType: String, isNewLeaseUser: Boolean)

    fun openGoldLeaseUserLeaseDetails(flowType: String, leaseId: String)

    fun openGoldLeaseSummaryRetryFlow(flowType: String, leaseId: String, isNewLeaseUser: Boolean)
}