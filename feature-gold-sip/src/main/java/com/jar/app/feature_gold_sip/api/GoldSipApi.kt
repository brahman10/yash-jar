package com.jar.app.feature_gold_sip.api

import com.jar.app.feature_gold_sip.impl.ui.gold_sip_type_selection.SipTypeSelectionScreenData

//This module has been renamed from Gold SIP to --> "Savings Plan"
interface GoldSipApi {

    fun setupGoldSip()

    fun openGoldSipIntro()

    fun openGoldSipDetails(isUpdateFlow: Boolean = false)

    fun openGoldSipTypeSelectionScreen(sipTypeSelectionScreenData: SipTypeSelectionScreenData?)
}