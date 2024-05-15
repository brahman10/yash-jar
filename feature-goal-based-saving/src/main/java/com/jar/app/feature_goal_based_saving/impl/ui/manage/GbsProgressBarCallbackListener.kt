package com.jar.app.feature_goal_based_saving.impl.ui.manage

internal interface GbsProgressBarCallbackListener {
    fun onProgressChange(progress: Float)
    fun onAnimationEnded()
}