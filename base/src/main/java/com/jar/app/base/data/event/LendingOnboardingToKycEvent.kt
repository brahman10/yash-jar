package com.jar.app.base.data.event

import com.jar.app.core_base.domain.model.KycProgressResponse

data class LendingOnboardingToKycEvent(
    val flowType: String,
    val progressResponse: KycProgressResponse?
)