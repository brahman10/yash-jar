package com.jar.app.feature.onboarding.util

import androidx.navigation.NavDirections
import com.jar.app.NewOnboardingNavigationDirections
import com.jar.app.base.data.event.OnboardingCompletedEvent
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import org.greenrobot.eventbus.EventBus

object OnboardingNavigationUtil {

    fun getNavDirectionForOnboardingNavigation(onboardingNavigation: OnboardingStateMachine.OnboardingNavigation): NavDirections? {
        val navigationDirection = when (val sideEffect = onboardingNavigation.sideEffect) {
            OnboardingStateMachine.SideEffect.NavigateToOnBoardingStory -> {
                NewOnboardingNavigationDirections.actionToOnboardingStoryFragment()
            }

            is OnboardingStateMachine.SideEffect.NavigateToLanguageSelection -> {
                NewOnboardingNavigationDirections.actionToAppLanguageFragment(
                    fromScreen = sideEffect.fromScreen
                )
            }

            is OnboardingStateMachine.SideEffect.NavigateToSelectNumber -> {
                NewOnboardingNavigationDirections.actionToSelectNumberFragment(
                    sideEffect.existingPhoneNumber
                )
            }

            is OnboardingStateMachine.SideEffect.NavigateToEnterNumber -> {
                NewOnboardingNavigationDirections.actionToNewEnterNumberFragment(
                    sideEffect.existingPhoneNumber
                )
            }

            is OnboardingStateMachine.SideEffect.NavigateToEnterOtp -> {
                if (sideEffect.phoneNumber.isNullOrBlank() || sideEffect.countryCode.isNullOrBlank()) {
                    NewOnboardingNavigationDirections.actionToNewEnterNumberFragment(null)
                } else {
                    NewOnboardingNavigationDirections.actionToNewEnterOtpFragment(
                        sideEffect.phoneNumber!!,
                        sideEffect.countryCode!!,
                        sideEffect.hasExperianConsent
                    )
                }
            }

            is OnboardingStateMachine.SideEffect.NavigateToOtlLogin -> {
                NewOnboardingNavigationDirections.actionToOtlLoginStatusFragment(
                    sideEffect.phoneNumber!!,
                    sideEffect.countryCode!!,
                    sideEffect.hasExperianConsent,
                    sideEffect.correlationId!!,
                )
            }

            OnboardingStateMachine.SideEffect.NavigateToEnterName -> {
                NewOnboardingNavigationDirections.actionToNewEnterNameFragment()
            }

            OnboardingStateMachine.SideEffect.NavigateToSmsPermission -> {
                NewOnboardingNavigationDirections.actionToNewSmsPermissionFragment()
            }

            OnboardingStateMachine.SideEffect.NavigateToSavingGoal -> {
                NewOnboardingNavigationDirections.actionToSavingGoalSelectionFragment()
            }

            OnboardingStateMachine.SideEffect.NavigateToHome -> {
                EventBus.getDefault().postSticky(OnboardingCompletedEvent())
                NewOnboardingNavigationDirections.actionToHomeFragment()
            }

            else -> {
                null
            }
        }
        return navigationDirection
    }
}