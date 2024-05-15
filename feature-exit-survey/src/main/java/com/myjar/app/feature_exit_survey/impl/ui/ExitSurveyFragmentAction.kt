package com.myjar.app.feature_exit_survey.impl.ui

import com.jar.app.feature_exit_survey.shared.domain.model.ExitSurveyQuestions

sealed class ExitSurveyFragmentAction {
    data class Init(val screen: String): ExitSurveyFragmentAction()
    data class SubmitResponse(val reason: String): ExitSurveyFragmentAction()
    object OnClickOnHelpCta: ExitSurveyFragmentAction()
    object OnClickOnClose: ExitSurveyFragmentAction()
}