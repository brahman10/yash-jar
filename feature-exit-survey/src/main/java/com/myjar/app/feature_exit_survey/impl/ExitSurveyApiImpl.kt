package com.myjar.app.feature_exit_survey.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.myjar.app.feature_exit_survey.api.ExitSurveyApi
import dagger.Lazy
import javax.inject.Inject

internal class ExitSurveyApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : ExitSurveyApi, BaseNavigation {
    private val navController by lazy {
        navControllerRef.get()
    }
    override fun openExitSurvey(surveyFor: String) {
        navController.navigate(Uri.parse("android-app://com.jar.app/featureExitSurvey/$surveyFor"), getNavOptions(shouldAnimate = true))
    }
}