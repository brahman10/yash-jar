package com.jar.app.feature_goal_based_saving.impl.ui.abandonSheet

import com.jar.app.feature_goal_based_saving.shared.data.model.AbandonedScreenResponse

internal sealed class AbandonScreenFragmentState {
    object onLoading: AbandonScreenFragmentState()
    object OnContinue: AbandonScreenFragmentState()
    object OnClose: AbandonScreenFragmentState()
    data class OnIWllDoItLater(val deepLink: String): AbandonScreenFragmentState()
    data class OnData(
        val abandonedScreenResponse: AbandonedScreenResponse
    ): AbandonScreenFragmentState()
}