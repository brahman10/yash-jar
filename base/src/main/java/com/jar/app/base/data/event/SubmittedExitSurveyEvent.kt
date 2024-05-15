package com.jar.app.base.data.event

data class SubmittedExitSurveyEvent(
    val fromWhichScreen: String? = null,
    val featureFlow: String? = null
)