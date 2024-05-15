package com.jar.app.base.data.event

data class OnboardingStoryImageResourceReadyEvent(val timeTaken: Long, val isFromCache: Boolean? = null)