package com.jar.app.feature_spends_tracker.impl.ui.main_page

import com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData.SpendsData


interface RvSummaryClickListener {
    fun balanceViewClickListener()
    fun spendsViewClickListener()
    fun promptViewClickListener()
    fun btnSaveGoldClickListener(spendsData: SpendsData)
    fun graphViewClickListener()
}