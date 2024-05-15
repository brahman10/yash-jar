package com.myjar.app.feature_graph_manual_buy.impl.ui

sealed class GraphManualBuyFragmentAction {
    object Init: GraphManualBuyFragmentAction()
    object ClickOnNeedHelp: GraphManualBuyFragmentAction()
    object OnClickOnPreviousOnCalender: GraphManualBuyFragmentAction()
    object OnClickOnNextOnCalender: GraphManualBuyFragmentAction()
    object OnClickOnInfoIcon: GraphManualBuyFragmentAction()
    object OnClickOnCalenderCta: GraphManualBuyFragmentAction()
    object OnClickOnFaqs: GraphManualBuyFragmentAction()
    object OnClickOnBack: GraphManualBuyFragmentAction()
    object OnDayClick: GraphManualBuyFragmentAction()

}