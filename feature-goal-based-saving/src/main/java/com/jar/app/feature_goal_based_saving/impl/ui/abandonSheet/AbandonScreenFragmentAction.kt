package com.jar.app.feature_goal_based_saving.impl.ui.abandonSheet

internal sealed class AbandonScreenFragmentAction {
    object Init: AbandonScreenFragmentAction()
    object OnClickOnClose: AbandonScreenFragmentAction()
    data class OnClickOnExit(val deepLink: String): AbandonScreenFragmentAction()
    object OnClickOnContinue: AbandonScreenFragmentAction()
}