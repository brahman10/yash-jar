package com.jar.app.feature_goal_based_saving.impl.ui.qna

sealed class QnAFragmentActions {
    data class Init(val goalId: String, val goalEndResponse: String): QnAFragmentActions()
    data class OnOptionSelected(val message: String, val index: Int = -1): QnAFragmentActions()
    object OnClickOnSubmit: QnAFragmentActions()
    object OnClickOnClose: QnAFragmentActions()
}