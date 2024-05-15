package com.jar.app.feature_goal_based_saving.impl.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_analytics.EventKey.TransactionsV2.paramters.status
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenClicked
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.IntroductionScreen.SavingsGoal_ScreenShown
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.clickaction
import com.jar.app.feature_goal_based_saving.impl.utils.GBSAnalyticsConstants.screen_type
import com.jar.app.feature_goal_based_saving.shared.domain.use_cases.GetGoalTransactionScreenResponseUseCase
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
internal class TransactionFragmentViewModel @Inject constructor(
    private val getGoalTransactionScreenResponseUseCase: GetGoalTransactionScreenResponseUseCase,
    private val remoteConfigManager: RemoteConfigApi,
    private val analyticsHandler: AnalyticsApi
): ViewModel() {
    private val _state = MutableStateFlow(TransactionFragmentState())
    val state: StateFlow<TransactionFragmentState> = _state

    private val _loading = MutableSharedFlow<Boolean>()
    val loading: SharedFlow<Boolean> = _loading

    private val _onGoToHome = MutableSharedFlow<String?>()
    val onGoToHome: SharedFlow<String?> = _onGoToHome

    private val _onTrackMyGoal = MutableSharedFlow<String?>()
    val onTrackMyGoal: SharedFlow<String?> = _onTrackMyGoal

    private val _onContactUs = MutableSharedFlow<Pair<String, Pair<Boolean, String>>?>()
    val onContactUs: SharedFlow<Pair<String, Pair<Boolean, String>>?> = _onContactUs

    private val _downloadInvoice = MutableSharedFlow<String?>()
    val downloadInvoice: SharedFlow<String?> = _downloadInvoice

    fun handleActions(action: TransactionFragmentAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when(action) {
                is TransactionFragmentAction.Init -> {
                    val goalId = action.goalId
                    viewModelScope.launch {
                        getGoalTransactionScreenResponseUseCase.execute(
                            goalId
                        ).collect(
                            onLoading = {
                                _loading.emit(
                                    true
                                )
                            },
                            onSuccess = {
                                analyticsHandler.postEvent(
                                    SavingsGoal_ScreenShown,
                                    mapOf(
                                        screen_type to "Post Order Screen",
                                        "status" to (it.status ?: "")
                                    )
                                )
                                viewModelScope.launch {
                                    _loading.emit(
                                        false
                                    )
                                }
                                _state.value = _state.value.copy(
                                    OnData = it
                                )
                            },
                            onError = {_, _ ->
                                viewModelScope.launch {
                                    _loading.emit(
                                        false
                                    )
                                }
                            }
                        )
                    }
                }
                TransactionFragmentAction.OnClickOnBackButton -> {}
                TransactionFragmentAction.OnClickOnContactUs -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Post Order Screen",
                            status to (_state.value.OnData?.status ?: ""),
                            clickaction to "Contact Support"
                        )
                    )
                    val number = remoteConfigManager.getWhatsappNumber()
                    val goalId = _state.value.OnData?.goalId

                    val id = _state.value.OnData?.orderId ?: goalId
                    viewModelScope.launch {
                        _onContactUs.emit(
                            Pair(number, Pair(
                                _state.value.OnData?.orderId != null,
                                id ?: ""
                            ))
                        )
                    }
                }
                TransactionFragmentAction.OnClickOnDownloadInvoice -> {
                    viewModelScope.launch {
                        _downloadInvoice.emit(
                            _state.value.OnData?.statusDetails?.invoiceLink
                        )
                    }
                }
                TransactionFragmentAction.OnClickOnGoToHome -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Post Order Screen",
                            status to (_state.value.OnData?.status ?: ""),
                            clickaction to "Go to Homepage"
                        )
                    )
                    viewModelScope.launch {
                        _onGoToHome.emit(
                            _state.value.OnData?.buttonCta?.deeplink ?: ""
                        )
                    }
                }
                TransactionFragmentAction.OnClickOnRetryPayment -> {}

                TransactionFragmentAction.OnTrackMyGoal -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Post Order Screen",
                            status to (_state.value.OnData?.status ?: ""),
                            clickaction to "Track my goal"
                        )
                    )
                    viewModelScope.launch {
                        _onTrackMyGoal.emit(
                            _state.value.OnData?.buttonCta?.deeplink ?: ""
                        )
                    }
                }
                TransactionFragmentAction.OnOrderSectionChevronClicked -> {
                    analyticsHandler.postEvent(
                        SavingsGoal_ScreenClicked,
                        mapOf(
                            screen_type to "Post Order Screen",
                            status to (_state.value.OnData?.status ?: ""),
                            clickaction to "Order Details Clicked"
                        )
                    )
                }
            }
        }
    }
}
