package com.jar.app.base.data.event

import com.jar.app.core_base.util.BaseConstants

data class OpenBuyGoldEvent(
    val buyGoldFlowContext: String = BaseConstants.BuyGoldFlowContext.BUY_GOLD
)