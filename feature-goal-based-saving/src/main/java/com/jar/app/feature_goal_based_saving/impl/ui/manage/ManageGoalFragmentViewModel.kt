package com.jar.app.feature_goal_based_saving.impl.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.data.model.*
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.FetchMandateInfoUseCase
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.UpdateGoalDailyRecurringAmountUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ManageGoalFragmentViewModel @Inject constructor(
    private val serializer: Serializer,
    private val fetchMandateInfoUseCase: FetchMandateInfoUseCase,
    private val fetchUserSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase,
    private val updateGoalDailyInvestmentStatusUseCase: UpdateGoalDailyRecurringAmountUseCase,
    private val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state = MutableStateFlow(ManageGoalFragmentState())
    val state: StateFlow<ManageGoalFragmentState> = _state

    private val _loading = MutableSharedFlow<Boolean>()
    val loading:SharedFlow<Boolean> = _loading


    private val _activeScreenResponse = MutableSharedFlow<ActiveResponse?>()
    val activeScreenResponse: SharedFlow<ActiveResponse?> = _activeScreenResponse

    private val _openSetting = MutableSharedFlow<String?>()
    val openSetting: SharedFlow<String?> = _openSetting

    private val _openGoalCompletedScreen = MutableSharedFlow<String?>()
    val openGoalCompletedScreen: SharedFlow<String?> = _openGoalCompletedScreen

    private val _userMandateInfo = MutableSharedFlow<Pair<MandateInfo, Float>?>()
    val userMandateInfo: SharedFlow<Pair<MandateInfo, Float>?> = _userMandateInfo

    private val _userSavingsDetails = MutableSharedFlow<UserSavingsDetails>()
    val userSavingsDetails: SharedFlow<UserSavingsDetails> = _userSavingsDetails

    private val _dailyInvestmentStatus = MutableSharedFlow<DailyInvestmentStatus?>()
    val dailyInvestmentStatus: SharedFlow<DailyInvestmentStatus?> = _dailyInvestmentStatus


    fun handleActions(manageGoalFragmentActions: ManageGoalFragmentActions) {
        viewModelScope.launch(Dispatchers.IO) {
            when(manageGoalFragmentActions) {
                is ManageGoalFragmentActions.Init -> {
                    val homescreenfeedresponse = serializer.decodeFromString<HomefeedGoalProgressReponse>(manageGoalFragmentActions.data)
                    val goalStatus = ProgressStatus.fromString(homescreenfeedresponse.progressStatus)
                    val activeResponse = if (goalStatus ==  ProgressStatus.ACTIVE) {
                        homescreenfeedresponse.activeResponse
                    } else {
                        homescreenfeedresponse.goalCompletedResponse?.activeResponse
                    }
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenShown,
                        mapOf(
                            screen_type to "Progress Screen",
                            "Daily_savings_amount" to (activeResponse?.investedAmount ?: ""),
                            "Duration_Left" to (activeResponse?.goalDetails?.details?.getOrNull(1)?.value  ?: ""),
                            "progressstatus" to (activeResponse?.currPercentage ?: ""),
                            "milestonemessage" to (activeResponse?.trackMessage ?: ""),
                            "goalstatus" to (activeResponse?.popup?.text ?: "")
                        )
                    )
                    val endScreenResponse = homescreenfeedresponse.goalCompletedResponse?.endStateResponse
                    val showEndState = homescreenfeedresponse.goalCompletedResponse?.showEndState

                    launch(Dispatchers.Main) {
                        _activeScreenResponse.emit(
                            activeResponse
                        )
                    }
                    launch(Dispatchers.Main) {
                        _loading.emit(false)
                    }

                    _state.value = _state.value.copy(
                        data = homescreenfeedresponse,
                        endScreenResponse = endScreenResponse,
                        showEndState = showEndState
                    )
                }
                ManageGoalFragmentActions.OnOpenSettings -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Progress Screen",
                            "buttonclicked" to "settings"
                        )
                    )
                    launch(Dispatchers.Main) {
                        _openSetting.emit(_state.value.data?.activeResponse?.settings?.deeplink)
                    }
                }
                ManageGoalFragmentActions.OnGoalCompleted -> {
                    val data = serializer.encodeToString(_state.value.data?.goalCompletedResponse?.endStateResponse)
                    _openGoalCompletedScreen.emit(
                        data
                    )
                }
                ManageGoalFragmentActions.OnClickOnDailySavingRestart -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Progress Screen",
                            "buttonclicked" to "restart savings clicked"
                        )
                    )
                    fetchUserSavingsDetailsUseCase.fetchSavingsDetails(SavingsType.ROUND_OFFS).collect(
                        onLoading = {
                            launch(Dispatchers.Main) {
                                _loading.emit(true)
                            }
                        },
                        onSuccess = {
                            launch(Dispatchers.Main) {
                                _loading.emit(false)
                            }
                            launch(Dispatchers.Main) {
                                _userSavingsDetails.emit(
                                    it
                                )
                            }
                        },
                        onError = {_, _ ->
                            launch(Dispatchers.Main) {
                                _loading.emit(false)
                            }
                        }
                    )
                }

                ManageGoalFragmentActions.OnFetchUserMandateInfo -> {
                    val amount = _state.value.data?.activeResponse?.dailyAmount!!
                    fetchMandateInfoUseCase.execute(
                        amount.toInt(),
                        SavingsType.DAILY_SAVINGS.name
                    ).collect(
                        onLoading = {
                            launch(Dispatchers.Main) {
                                _loading.emit(true)
                            }
                        },
                        onSuccess = {
                            launch(Dispatchers.Main) {
                                _loading.emit(false)
                            }
                            launch(Dispatchers.Main) {
                                _userMandateInfo.emit(
                                    Pair(it, amount.toFloat())
                                )
                            }
                        },
                        onError = {_,_ ->
                            launch(Dispatchers.Main) {
                                _loading.emit(false)
                            }
                        }

                    )
                }

                is ManageGoalFragmentActions.UpdateOnEnableDailySavings -> {
                    updateGoalDailyInvestmentStatusUseCase.execute(manageGoalFragmentActions.amount).collect(
                        onLoading = {
                            launch(Dispatchers.Main) {
                                _loading.emit(true)
                            }
                        },
                        onSuccess = {
                            launch(Dispatchers.Main) {
                                _loading.emit(false)
                            }
                            launch(Dispatchers.Main) {
                                _dailyInvestmentStatus.emit(
                                    it
                                )
                            }
                        },
                        onError = {_,_ ->
                            launch(Dispatchers.Main) {
                                _loading.emit(false)
                            }
                        }
                    )
                }
                ManageGoalFragmentActions.EnableAutomaticDailySavings -> {
                    manageSavingPreferenceUseCase.manageSavingsPreference(
                        savingsType = SavingsType.DAILY_SAVINGS,
                        enableAutoSave = true
                    )
                }
                ManageGoalFragmentActions.SendSaveNowAnalyticEvent -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(


                            screen_type to "Progress Screen",
                            "buttonclicked" to "Save now"
                        )
                    )
                }
                else -> Unit
            }

        }
    }
}