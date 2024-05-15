package com.jar.health_insurance.impl.ui.landing_page

import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingPageResponse1
import com.jar.health_insurance.impl.ui.add_details.AddDetailsFragmentEvents

data class LandingPageState(
    val landingPageData: LandingPageResponse1? = null,
    val errorMessage: String? = null,
    val isLoading:Boolean = false
)

sealed class LandingPageEvent {
    object LoadLandingPageData : LandingPageEvent()
    object ErrorMessageDisplayed : LandingPageEvent()
}