package com.jar.app.feature_lending.shared.ui.realtime_flow_with_camps.bank_selection

import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_lending.shared.domain.model.camps_flow.CamsSdkRedirectionData
import com.jar.app.feature_lending.shared.domain.model.camps_flow.RealtimeBankData
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsBanksUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsSdkRedirectDataUseCase
import com.jar.app.feature_lending.shared.domain.use_case.ScheduleBankUptimeNotificationUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SelectBankUiState(
    val isLoading: Boolean = true,
    val topBanks: List<RealtimeBankData>? = null,
    val secondaryBanks: List<RealtimeBankData>? = null,
    val selectedBank: RealtimeBankData? = null,
    val scheduledBankUpTimeNotification: RestClientResult<ApiResponseWrapper<Unit?>>? = null,
)

class RealtimeBankSelectionViewModel constructor(
    private val fetchCamsBanksUseCase: FetchCamsBanksUseCase,
    private val scheduleBankUptimeNotificationUseCase: ScheduleBankUptimeNotificationUseCase,
    private val fetchCamsSdkRedirectDataUseCase: FetchCamsSdkRedirectDataUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _uiStateFlow = MutableStateFlow(SelectBankUiState())
    val uiStateFlow: StateFlow<SelectBankUiState>
        get() = _uiStateFlow.asStateFlow()


    private val _sdkDataSharedFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<CamsSdkRedirectionData?>>>()
    val sdkDataSharedFlow: SharedFlow<RestClientResult<ApiResponseWrapper<CamsSdkRedirectionData?>>>
        get() = _sdkDataSharedFlow.asSharedFlow()

    private var masterBankList: List<RealtimeBankData>? = null
    private var isSecondaryBankSelectedFirstTime = true

    fun fetchBankDataForRealtime() {
        viewModelScope.launch {
            fetchCamsBanksUseCase.fetchCamsBanks().collectLatest { response ->
                withContext(Dispatchers.Default) {
                    masterBankList = response.data?.data
                    _uiStateFlow.update {
                        it.copy(
                            isLoading = response.status == RestClientResult.Status.LOADING,
                            topBanks = response.data?.data?.filter { it.order != SECONDARY_BANKS_ORDER }
                                ?.sortedBy { it.order },       //Top 6 banks will have non zero order
                            secondaryBanks = response.data?.data?.filter { it.order == SECONDARY_BANKS_ORDER }
                                ?.sortedBy { it.bankName },   //Other banks will have zero order
                        )
                    }
                }
            }
        }
    }

    fun onBankSelected(fipId: String, isPrimary: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            masterBankList?.findLast { it.fipId == fipId }?.let { selectedBank ->
                _uiStateFlow.update {
                    it.copy(selectedBank = selectedBank)
                }
                /**
                 * If it is Top 6 bank, we don't have to do anything.
                 * In case of Secondary Banks, for the First time selection, we have to move item to Top list 1st position and remove last item from Top to secondary
                 * In case of subsequent selections, items would be swapped from primary to secondary list
                 * Secondary banks appear in the bottomsheet while primary are on the Main screen
                 */
                if (isPrimary.not()) {
                    val topBanks = uiStateFlow.value.topBanks?.toMutableList()
                    val secondaryBanks = uiStateFlow.value.secondaryBanks?.toMutableList()

                    //Only if Item not already present in the list
                    if (topBanks?.firstOrNull { it.fipId == fipId } == null) {
                        /**
                         * In case of Primary bank as first position, always append
                         * In case of Secondary bank, always swap (else part), except for the first time
                         */
                        if (topBanks?.firstOrNull()?.order.orZero() != SECONDARY_BANKS_ORDER || isSecondaryBankSelectedFirstTime) {
                            isSecondaryBankSelectedFirstTime = false
                            topBanks?.add(0, selectedBank)
                            topBanks?.last()?.let { last ->
                                //Only if Item not already present in the list
                                if (secondaryBanks?.firstOrNull { it.fipId == last.fipId } == null)
                                    secondaryBanks?.add(last)
                            }
                            topBanks?.removeLast()
                        } else {
                            topBanks?.firstOrNull()?.let {
                                secondaryBanks?.add(it)
                            }
                            topBanks?.set(0, selectedBank)
                        }
                        secondaryBanks?.remove(selectedBank)

                        _uiStateFlow.update {
                            it.copy(
                                topBanks = topBanks?.sortedBy { it.order },
                                secondaryBanks = secondaryBanks?.sortedBy { it.bankName },
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSearchQuery(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiStateFlow.update {
                var finalList: List<RealtimeBankData>?
                /**
                 *  assign the default value i.e all Secondary banks, used when [query] is Blank
                 */
                finalList = masterBankList?.filter { it.order == SECONDARY_BANKS_ORDER }
                /**
                 *  If query is not blank, further filter out values
                 */
                if (query.isNotBlank())
                    finalList =
                        finalList?.filter { it.bankName?.contains(query.trim(), true).orFalse() }
                it.copy(
                    secondaryBanks = finalList,
                )
            }
        }
    }

    fun scheduleBankUptimeNotification(fipId: String) {
        viewModelScope.launch {
            scheduleBankUptimeNotificationUseCase.scheduleBankUptimeNotification(fipId)
                .collectLatest { data ->
                    _uiStateFlow.update {
                        it.copy(scheduledBankUpTimeNotification = data)
                    }
                }
        }
    }

    fun fetchSdkRedirectionData(fipId: String) {
        viewModelScope.launch {
            fetchCamsSdkRedirectDataUseCase.fetchCamsSdkRedirectData(fipId).collectLatest {
                _sdkDataSharedFlow.emit(it)
            }
        }
    }

    companion object {
        private const val SECONDARY_BANKS_ORDER = 0
    }
}