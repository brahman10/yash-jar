package com.jar.app.feature_onboarding.shared.data.state_machine

import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.StateMachine
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_onboarding.shared.domain.model.CustomOnboardingData
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.util.Serializer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OnboardingStateMachine constructor(
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    private val remoteConfigManager: RemoteConfigApi
) {

    private var stateMachine: StateMachine<State, Event, SideEffect>? = null

    var phoneNumber: String? = null

    var countryCode: String? = null

    var hasExperianConsent: Boolean? = null

    var mUser: User? = null

    var existingPhoneFromDevice: String? = null

    var isRegisteredUser = false

    var usingNewNumber = true

    var isOtlFlow = false

    var correlationId: String? = null

    var shouldAskForConsent: Boolean? = null

    var customOnboardingData: CustomOnboardingData? = null

    private var currentNavigation: OnboardingNavigation? = null

    private var isBackPressRedirection: Boolean = false

    fun initStateMachine(
        hasSmsPermission: () -> Boolean
    ): CFlow<OnboardingNavigation> {
        val flow: Flow<OnboardingNavigation> = callbackFlow {
            val initialStateString = prefs.getNewOnboardingState()
            stateMachine = StateMachine.create {
                val initialState = getInitialStateFromString(initialStateString)
                initialState(initialState)

                state<State.LanguageSelection> {
                    val currentLanguageCode = prefs.getCurrentLanguageCode()
                    on<Event.Pending> {
                        transitionTo(
                            State.LanguageSelection,
                            SideEffect.NavigateToLanguageSelection(
                                currentLanguageCode = prefs.getCurrentLanguageCode(),
                                fromScreen = "NewOnboarding"
                            )
                        )
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        prefs.setNewOnboardingState(toString())
                        val transition = if(prefs.isOnBoardingStoryShown()){
                            if (existingPhoneFromDevice == null) {
                                transitionTo(
                                    State.EnterNumber,
                                    SideEffect.NavigateToEnterNumber(existingPhoneFromDevice)
                                )
                            }else{
                                transitionTo(
                                    State.SelectNumber,
                                    SideEffect.NavigateToSelectNumber(existingPhoneFromDevice!!)
                                )
                            }
                        }else{
                                transitionTo(
                                    State.OnBoardingStory,
                                    SideEffect.NavigateToOnBoardingStory,
                                )
                        }

                        if (prefs.getCurrentLanguageCode() != currentLanguageCode) {
                            prefs.setNewOnboardingState(transition.toState.toString())
                            trySend(OnboardingNavigation(SideEffect.RecreateApp, false))
                        }
                        transition
                    }
                }

                state<State.OnBoardingStory> {
                    on<Event.Pending> {
                        val transition = if(prefs.isOnBoardingStoryShown()){
                            if (existingPhoneFromDevice == null) {
                                transitionTo(
                                    State.EnterNumber,
                                    SideEffect.NavigateToEnterNumber(existingPhoneFromDevice)
                                )
                            }else{
                                transitionTo(
                                    State.SelectNumber,
                                    SideEffect.NavigateToSelectNumber(existingPhoneFromDevice!!)
                                )
                            }
                        }else{
                            transitionTo(State.OnBoardingStory, SideEffect.NavigateToOnBoardingStory)
                        }
                        transition
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                            if (existingPhoneFromDevice == null) {
                                prefs.setNewOnboardingState(toString())
                                transitionTo(
                                    State.EnterNumber,
                                    SideEffect.NavigateToEnterNumber(existingPhoneFromDevice)
                                )
                            } else {
                                prefs.setNewOnboardingState(toString())
                                transitionTo(
                                    State.SelectNumber,
                                    SideEffect.NavigateToSelectNumber(existingPhoneFromDevice!!)
                                )
                            }
                    }
                }

                state<State.SelectNumber> {
                    on<Event.Pending> {
                        if (existingPhoneFromDevice == null) {
                            prefs.setNewOnboardingState(toString())
                            transitionTo(
                                State.EnterNumber,
                                SideEffect.NavigateToEnterNumber(existingPhoneFromDevice),
                            )
                        } else {
                            transitionTo(
                                State.SelectNumber,
                                SideEffect.NavigateToSelectNumber(existingPhoneFromDevice!!)
                            )
                        }
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        prefs.setNewOnboardingState(toString())
                        if (usingNewNumber) {
                            transitionTo(
                                State.EnterNumber,
                                SideEffect.NavigateToEnterNumber(existingPhoneFromDevice)
                            )
                        } else {
                            val transition = navigateAfterEnteringNumber()
                            transitionTo(transition.first, transition.second)
                        }

                    }
                }
                state<State.EnterNumber> {
                    on<Event.Pending> {
                        prefs.setNewOnboardingState(toString())
                        if (existingPhoneFromDevice == null) {
                            transitionTo(
                                State.EnterNumber,
                                SideEffect.NavigateToEnterNumber(existingPhoneFromDevice)
                            )
                        } else {
                            transitionTo(
                                State.SelectNumber,
                                SideEffect.NavigateToSelectNumber(existingPhoneFromDevice!!)
                            )
                        }
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        val transition = navigateAfterEnteringNumber()
                        prefs.setNewOnboardingState(transition.first.toString())
                        transitionTo(transition.first, transition.second)
                    }
                    on<Event.OnTruecallerCompleted> {
                        val transition = getLoginTransition(
                            isTruecallerLogin = true,
                            hasSmsPermission = hasSmsPermission
                        )
                        prefs.setNewOnboardingState(transition.first.toString())
                        transitionTo(transition.first, transition.second)
                    }
                }
                state<State.EnterOtp> {
                    on<Event.Pending> {
                        prefs.setNewOnboardingState(toString())
                        transitionTo(
                            State.EnterNumber,
                            SideEffect.NavigateToEnterNumber(existingPhoneFromDevice)
                        )
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        val transition = getLoginTransition(
                            isTruecallerLogin = false,
                            hasSmsPermission = hasSmsPermission
                        )
                        prefs.setNewOnboardingState(transition.first.toString())
                        transitionTo(transition.first, transition.second)
                    }
                    on<Event.BackPress> {
                        isBackPressRedirection = true
                        prefs.setNewOnboardingState(toString())
                        if (usingNewNumber) {
                            transitionTo(
                                State.EnterNumber,
                                SideEffect.NavigateToEnterNumber(existingPhoneFromDevice)
                            )
                        } else {
                            transitionTo(
                                State.SelectNumber,
                                SideEffect.NavigateToSelectNumber(existingPhoneFromDevice!!)
                            )
                        }
                    }
                    on<Event.OnTruecallerCompleted> {
                        val transition = getLoginTransition(
                            isTruecallerLogin = true,
                            hasSmsPermission = hasSmsPermission
                        )
                        prefs.setNewOnboardingState(transition.first.toString())
                        transitionTo(transition.first, transition.second)
                    }
                }
                state<State.OtlLogin> {
                    on<Event.Pending> {
                        isBackPressRedirection = false
                        val transition = getLoginTransition(
                            isTruecallerLogin = false,
                            hasSmsPermission = hasSmsPermission
                        )
                        prefs.setNewOnboardingState(transition.first.toString())
                        transitionTo(transition.first, transition.second)
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        val transition = getLoginTransition(
                            isTruecallerLogin = false,
                            hasSmsPermission = hasSmsPermission
                        )
                        prefs.setNewOnboardingState(transition.first.toString())
                        transitionTo(transition.first, transition.second)
                    }
                    on<Event.OnTruecallerCompleted> {
                        val transition = getLoginTransition(
                            isTruecallerLogin = true,
                            hasSmsPermission = hasSmsPermission
                        )
                        prefs.setNewOnboardingState(transition.first.toString())
                        transitionTo(transition.first, transition.second)
                    }
                }
                state<State.SmsPermission> {
                    on<Event.Pending> {
                        prefs.setNewOnboardingState(toString())
                        transitionTo(State.SmsPermission, SideEffect.NavigateToSmsPermission)
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        prefs.setNewOnboardingState(toString())
                        val transition = if (getUser()?.onboarded.orFalse()) {
                            val loginTransition = getLoginTransition(
                                isTruecallerLogin = false,
                                hasSmsPermission = hasSmsPermission
                            )
                            prefs.setNewOnboardingState(loginTransition.first.toString())
                            transitionTo(loginTransition.first, loginTransition.second)
                        } else if (doesUserNameExistInBE()) {
                            prefs.setNewOnboardingState(toString())
                            transitionTo(State.SavingGoal, SideEffect.NavigateToSavingGoal)
                        } else {
                            prefs.setNewOnboardingState(toString())
                            transitionTo(State.EnterName, SideEffect.NavigateToEnterName)
                        }
                        transition
                    }
                }
                state<State.EnterName> {
                    on<Event.Pending> {
                        prefs.setNewOnboardingState(toString())
                        transitionTo(State.EnterName, SideEffect.NavigateToEnterName)
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        prefs.setNewOnboardingState(toString())
                        transitionTo(State.SavingGoal, SideEffect.NavigateToSavingGoal)
                    }
                }
                state<State.SavingGoal> {
                    on<Event.Pending> {
                        prefs.setNewOnboardingState(toString())
                        transitionTo(State.SavingGoal, SideEffect.NavigateToSavingGoal)
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        prefs.setNewOnboardingState(toString())
                        if (customOnboardingData?.customOnboardingLink.isNullOrEmpty().not()) {
                            transitionTo(
                                State.CustomOnboarding,
                                SideEffect.NavigateToCustomOnboarding(customOnboardingData?.customOnboardingLink)
                            )
                        } else {
                            transitionTo(State.Home, SideEffect.NavigateToHome)
                        }
                    }
                    on<Event.BackPress> {
                        isBackPressRedirection = true
                        prefs.setNewOnboardingState(toString())
                        transitionTo(State.EnterName, SideEffect.NavigateToEnterName)
                    }
                }
                state<State.CustomOnboarding> {
                    on<Event.Pending> {
                        prefs.setNewOnboardingState(toString())
                        transitionTo(
                            State.CustomOnboarding,
                            SideEffect.NavigateToCustomOnboarding(customOnboardingData?.customOnboardingLink)
                        )
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        prefs.setNewOnboardingState(toString())
                        transitionTo(State.Home, SideEffect.NavigateToHome)
                    }
                }
                state<State.Home> {
                    on<Event.Pending> {
                        prefs.setNewOnboardingState(toString())
                        transitionTo(State.Home, SideEffect.NavigateToHome)
                    }
                    on<Event.Completed> {
                        isBackPressRedirection = false
                        prefs.setNewOnboardingState(toString())
                        transitionTo(State.Home, SideEffect.NavigateToHome)
                    }
                }
                onTransition {
                    val validTransition =
                        it as? StateMachine.Transition.Valid ?: return@onTransition
                    currentNavigation = OnboardingNavigation(
                        validTransition.sideEffect!!,
                        isBackPressRedirection
                    )
                    trySend(currentNavigation!!)
                }
            }

            awaitClose {

            }
        }
        return flow.toCommonFlow()
    }

    private fun navigateAfterEnteringNumber(): Pair<State, SideEffect> {
        return if (isOtlFlow)
            Pair(
                State.OtlLogin, SideEffect.NavigateToOtlLogin(
                    phoneNumber,
                    countryCode,
                    hasExperianConsent.orFalse(),
                    correlationId
                )
            )
        else
            Pair(
                State.EnterOtp, SideEffect.NavigateToEnterOtp(
                    phoneNumber,
                    countryCode,
                    hasExperianConsent.orFalse()
                )
            )
    }

    fun getCurrentSideEffect(): OnboardingNavigation? {
        return currentNavigation
    }

    private fun getLoginTransition(
        isTruecallerLogin: Boolean,
        hasSmsPermission: () -> Boolean
    ): Pair<State, SideEffect> {
        val user = getUser()
        return if (user != null) {
            if (hasSmsPermission()) {
                getAfterPermissionTransition(user, isTruecallerLogin)
            } else
                Pair(State.SmsPermission, SideEffect.NavigateToSmsPermission)
        } else
            if (remoteConfigManager.isLanguageExperimentRunning()) {
                Pair(
                    State.LanguageSelection,
                    SideEffect.NavigateToLanguageSelection(
                        currentLanguageCode = prefs.getCurrentLanguageCode(),
                        fromScreen = "NewOnboarding"
                    )
                )
            } else {
                if (isOtlFlow)
                    Pair(
                        State.EnterOtp,
                        SideEffect.NavigateToEnterOtp(
                            phoneNumber,
                            countryCode,
                            hasExperianConsent.orFalse()
                        )
                    )
                else
                    Pair(
                        State.EnterNumber,
                        SideEffect.NavigateToEnterNumber(existingPhoneFromDevice)
                    )
            }
    }

    private fun getAfterPermissionTransition(
        user: User,
        isTruecallerLogin: Boolean
    ): Pair<State, SideEffect> {
        val isUserNameEntered = !user.firstName.isNullOrBlank()
        return if (isUserNameEntered) {
            if (isTruecallerLogin) {
                Pair(State.EnterName, SideEffect.NavigateToEnterName)
            } else {
                if (user.userGoalSetup == false) {
                    Pair(State.SavingGoal, SideEffect.NavigateToSavingGoal)
                } else if (customOnboardingData?.customOnboardingLink.isNullOrEmpty().not()) {
                    Pair(
                        State.CustomOnboarding,
                        SideEffect.NavigateToCustomOnboarding(customOnboardingData?.customOnboardingLink)
                    )
                } else {
                    Pair(State.Home, SideEffect.NavigateToHome)
                }
            }
        } else {
            Pair(State.EnterName, SideEffect.NavigateToEnterName)
        }
    }

    fun getTotalOnboardingSteps(): Int {
        return BaseConstants.NO_OF_ONBOARDING_SCREENS
    }

    fun getUser(): User? {
        return if (mUser != null) {
            mUser
        } else {
            val userString = prefs.getUserStringSync()
            if (userString.isNullOrBlank().not()) {
                serializer.decodeFromString<User?>(userString!!)
            } else {
                null
            }
        }
    }

    private fun doesUserNameExistInBE() = mUser != null && mUser!!.firstName.isNullOrEmpty().not()

    private fun getInitialStateFromString(state: String?): State {
        return when (state) {
            null, "" -> if(remoteConfigManager.isLanguageExperimentRunning()) State.LanguageSelection else State.OnBoardingStory
            State.LanguageSelection.toString() -> State.LanguageSelection
            State.OnBoardingStory.toString() -> State.OnBoardingStory
            State.SelectNumber.toString() -> State.SelectNumber
            State.EnterNumber.toString() -> State.EnterNumber
            State.OtlLogin.toString() -> State.OtlLogin
            State.EnterOtp.toString() -> State.EnterOtp
            State.SmsPermission.toString() -> State.SmsPermission
            State.EnterName.toString() -> State.EnterName
            State.SavingGoal.toString() -> State.SavingGoal
            State.Home.toString() -> State.Home
            else -> State.OnBoardingStory
        }
    }

    fun navigateAfterTruecallerCompletion() {
        stateMachine?.transition(Event.OnTruecallerCompleted)
    }

    fun navigateToPending() {
        stateMachine?.transition(Event.Pending)
    }

    fun navigateAhead() {
        stateMachine?.transition(Event.Completed)
    }

    fun navigateBack() {
        stateMachine?.transition(Event.BackPress)
    }

    fun isGoingBackAllowed(): Boolean {
        return when (stateMachine?.state) {
            State.EnterOtp -> true
            State.SavingGoal -> true
            else -> false
        }
    }

    sealed class State {
        object OnBoardingStory : State()
        object LanguageSelection : State()
        object SelectNumber : State()
        object EnterNumber : State()
        object EnterOtp : State()
        object OtlLogin : State()
        object SmsPermission : State()
        object EnterName : State()
        object SavingGoal : State()
        object CustomOnboarding : State()
        object Home : State()

        override fun toString(): String {
            return this::class.simpleName.orEmpty()
        }
    }

    sealed class Event {
        object Pending : Event()
        object Completed : Event()
        object OnTruecallerCompleted : Event()
        object BackPress : Event()

        override fun toString(): String {
            return this::class.simpleName.orEmpty()
        }
    }

    sealed class SideEffect {
        object NavigateToOnBoardingStory : SideEffect()
        data class NavigateToLanguageSelection(
            val currentLanguageCode: String,
            val fromScreen: String
        ) : SideEffect()

        data class NavigateToSelectNumber(
            val existingPhoneNumber: String
        ) : SideEffect()

        data class NavigateToEnterNumber(
            val existingPhoneNumber: String?
        ) : SideEffect()

        data class NavigateToOtlLogin(
            val phoneNumber: String?,
            val countryCode: String?,
            val hasExperianConsent: Boolean,
            val correlationId: String?
        ) : SideEffect()

        data class NavigateToEnterOtp(
            val phoneNumber: String?,
            val countryCode: String?,
            val hasExperianConsent: Boolean
        ) : SideEffect()

        data class NavigateToCustomOnboarding(
            val customOnboarding: String?
        ) : SideEffect()

        object NavigateToSmsPermission : SideEffect()
        object NavigateToEnterName : SideEffect()
        object NavigateToSavingGoal : SideEffect()
        object NavigateToHome : SideEffect()
        object RecreateApp : SideEffect()
    }

    data class OnboardingNavigation(
        val sideEffect: SideEffect,
        val isBackPressRedirection: Boolean
    )

    private fun String.onlyLetters(): Boolean = (firstOrNull { !it.isLetter() } == null)

}