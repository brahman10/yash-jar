package com.jar.app.feature_goal_based_saving.impl.ui.transaction

internal sealed class TransactionFragmentAction {
    data class Init(val goalId: String): TransactionFragmentAction()
    object OnClickOnContactUs: TransactionFragmentAction()
    object OnClickOnGoToHome: TransactionFragmentAction()
    object OnTrackMyGoal: TransactionFragmentAction()
    object OnClickOnDownloadInvoice: TransactionFragmentAction()
    object OnClickOnRetryPayment: TransactionFragmentAction()
    object OnClickOnBackButton: TransactionFragmentAction()
    object OnOrderSectionChevronClicked: TransactionFragmentAction()
}