package com.jar.app.feature_transaction.shared.ui

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_transaction.shared.domain.model.FilterKeyData
import com.jar.app.feature_transaction.shared.domain.model.FilterResponse
import com.jar.app.feature_transaction.shared.domain.model.FilterValueData
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTransactionFilterUseCase
import com.jar.app.feature_user_api.domain.model.UserKycStatus
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus

class TransactionFragmentViewModel constructor(
    private val fetchTransactionFilterUseCase: IFetchTransactionFilterUseCase,
    private val fetchUserKycStatusUseCase: FetchUserKycStatusUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _uiAppBarEventLiveData = MutableSharedFlow<UiEvent?>()
    val uiAppBarEventLiveData: CFlow<UiEvent?>
        get() =
            _uiAppBarEventLiveData.toCommonFlow()

    private val _uiPullRefreshEventLiveData = MutableSharedFlow<UiEvent?>()
    val uiPullRefreshEventLiveData: CFlow<UiEvent?>
        get() = _uiPullRefreshEventLiveData.toCommonFlow()

    private val _uiSetWinningsTabAlert = MutableSharedFlow<UiEvent?>()
    val uiSetWinningsTabAlert: CFlow<UiEvent?>
        get() =
            _uiSetWinningsTabAlert.toCommonFlow()

    private val _filterResponseLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<List<FilterResponse>>>>(
            RestClientResult.none()
        )
    val filterResponseLiveData: CFlow<RestClientResult<ApiResponseWrapper<List<FilterResponse>>>>
        get() = _filterResponseLiveData.toCommonFlow()

    private val _filterKeyLiveData = MutableStateFlow<List<FilterKeyData>?>(null)
    val filterKeyLiveData: CFlow<List<FilterKeyData>?>
        get() = _filterKeyLiveData.toCommonFlow()

    private val _filterValuesLiveData = MutableStateFlow<List<FilterValueData>?>(null)
    val filterValuesLiveData: CFlow<List<FilterValueData>?>
        get() = _filterValuesLiveData.toCommonFlow()

    private val _selectedFiltersLiveData = MutableStateFlow<List<FilterValueData>?>(null)
    val selectedFiltersLiveData: CStateFlow<List<FilterValueData>?>
        get() = _selectedFiltersLiveData.toCommonStateFlow()

    private val _userKycStatusLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>(RestClientResult.none())
    val userKycStatusLiveData: CFlow<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>
        get() = _userKycStatusLiveData.toCommonFlow()

    var selectedDates: Pair<Long, Long>? = null

    private var filtersKeyList = ArrayList<FilterKeyData>()
    private var filterValueList = ArrayList<FilterValueData>()
    private var tempList = ArrayList<FilterValueData>()
    private var selectedFilterKey = ""

    fun fetchUserKycStatus() {
        viewModelScope.launch {
            fetchUserKycStatusUseCase.fetchUserKycStatus().collect {
                _userKycStatusLiveData.emit(it)
            }
        }
    }

    // ################ Start : Methods for Filter ########################
    /**
     * Fetch filters from backend and populate `filtersKeyList` and `filterValueList`
     * These keys will persist until tearDown is called
     * We add `All` option in all the `filterValueList` manually, except for Date
     */
    fun fetchFilters(getAllFilterString: () -> String) {
        if (filtersKeyList.isNotEmpty() || filterValueList.isNotEmpty())
            return
        viewModelScope.launch {
            fetchTransactionFilterUseCase.fetchTransactionFilters().collect { response ->
                _filterResponseLiveData.emit(response)
                if (response.status == RestClientResult.Status.SUCCESS) {
                    withContext(Dispatchers.Default) {
                        response.data?.data?.forEachIndexed { index, result ->
                            val key = result.key
                            filtersKeyList.add(
                                index,
                                FilterKeyData(
                                    key,
                                    result.keyName
                                )
                            )
                            //Manually add 'All' in all filters except Date
                            if (!key.equals(BaseConstants.FilterValues.DATE_FILTER, true))
                                filterValueList.add(
                                    FilterValueData(
                                        "ALL",
                                        key,
                                        getAllFilterString.invoke()
                                    )
                                )
                            repeat(result.values.size) {
                                filterValueList.add(
                                    FilterValueData(
                                        result.values[it],
                                        key,
                                        result.valueNames[it]
                                    )
                                )
                            }
                        }
                        tempList = ArrayList(filterValueList.map { it.copy() })
                        setFilterKeySelection(filtersKeyList.firstOrNull()?.name ?: "")
                    }
                }
            }
        }
    }

    /**
     * Initialise the list with preselected values(if any). This list holds the changes done my user.
     * Those changes can be Cleared, Cancelled, Applied, Selected, Unselected, Removed. Hence this list was
     * needed to accommodate all those temporary changes
     */
    fun initTempList() {
        viewModelScope.launch(Dispatchers.Default) {
            tempList = ArrayList()
            filterValueList.forEach { data ->
                val newData = data.copy()
                (_selectedFiltersLiveData.value?.let { selectedList ->
                    selectedList.find { it.name == data.name }?.let { newData.isSelected = true }
                })
                tempList.add(newData)
            }
        }
    }

    /**
     * Unselect all the values and clear the Filter selections
     */
    fun onClearClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            selectedDates = null
            tempList.forEach {
                it.isSelected = false
            }
            _selectedFiltersLiveData.emit(emptyList())
            _filterValuesLiveData.emit(
                tempList.filter { it.keyName == selectedFilterKey }.map { it.copy() }
            )
        }
    }

    /**
     * Pretty self explanatory.
     * We don't show `All` in selected filters. Hence we filter that out
     */
    fun onApplyClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            _selectedFiltersLiveData.emit(
                tempList.filter {
                    it.isSelected.orFalse() && !it.name.equals(
                        BaseConstants.FilterValues.FILTER_All,
                        true
                    )
                }
            )
            _filterValuesLiveData.emit(
                tempList.filter { it.keyName == selectedFilterKey }
            )
        }
    }

    /**
     * Right now viewmodel is scoped to Activity. And we have single activity architecture
     * Tried making it NavGraph scoped, but there are certain challenges.
     * So we need to reset all the data, so that when user comes back, he doesn't see stale data
     */
    fun tearDownData() {
        viewModelScope.launch(Dispatchers.Default) {
            filtersKeyList.clear()
            filterValueList.clear()
            tempList.clear()
            selectedFilterKey = ""
            selectedDates = null
            _selectedFiltersLiveData.emit(null)
            _filterKeyLiveData.emit(null)
            _filterValuesLiveData.emit(null)
        }
    }

    /**
     * Select the Key from left side pane and updated the Values according to the new Key
     */
    fun setFilterKeySelection(filterKey: String) {
        selectedFilterKey = filterKey
        viewModelScope.launch(Dispatchers.Default) {
            _filterKeyLiveData.emit(
                filtersKeyList.map {
                    it.copy(isSelected = it.name == filterKey)
                }
            )
            _filterValuesLiveData.emit(
                tempList.filter { it.keyName == filterKey }.map {
                    it.copy()
                }
            )
        }
    }

    /**
     * Condition 1 : When user Selects/Unselects on `All`
     * Condition 2 : For `DATE` types filters, extract daysCount from ENUM and calculate start and end date
     * Condition 3 : When users selects/unselects Values from Right Side pane.
     */
    fun setFilterValueSelection(valueName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            when {
                valueName.equals(BaseConstants.FilterValues.FILTER_All, true) -> {
                    val shouldSelect =
                        tempList.find { it.keyName == selectedFilterKey && it.name == valueName }?.isSelected.orFalse()
                            .not()
                    tempList.forEach {
                        if (it.keyName == selectedFilterKey) {
                            it.isSelected = shouldSelect
                        }
                    }
                    refreshLists()
                }

                selectedFilterKey.equals(BaseConstants.FilterValues.DATE_FILTER, true) -> {
                    //`valueName` should always be like LAST_15_DAYS, LAST_30_days, in this case
                    val daysCount = valueName.substring(valueName.indexOfFirst { it == '_' } + 1,
                        valueName.indexOfLast { it == '_' }).toInt()
                    val currentInstant = Clock.System.now()
                    val startDate = currentInstant.minus(
                        daysCount,
                        DateTimeUnit.DAY,
                        TimeZone.currentSystemDefault()
                    ).toEpochMilliseconds()

                    val endDate = currentInstant.toEpochMilliseconds()
                    setFilterValueSelection(valueName, Pair(startDate, endDate))
                }

                else -> {
                    tempList.forEach {
                        if (it.keyName == selectedFilterKey && it.name == valueName)
                            it.isSelected = !it.isSelected.orFalse()
                    }
                    refreshLists()
                }
            }
        }
    }

    /**
     * Special case to handle Date selection, either from picker or from List
     */
    fun setFilterValueSelection(valueName: String, dates: Pair<Long, Long>) {
        viewModelScope.launch(Dispatchers.Default) {
            tempList.forEach {
                if (it.keyName == selectedFilterKey) {
                    if (valueName == it.name) {
                        it.isSelected = true
                        it.startDate = dates.first
                        it.endDate = dates.second
                        selectedDates = dates
                    } else {
                        it.isSelected = false
                        it.endDate = null
                        it.startDate = null
                    }
                }
            }
            refreshLists()
        }
    }

    /**
     * self explanatory
     */
    private fun refreshLists() {
        refreshFilterValuesList()
        refreshSelectedFilterList()
    }

    /**
     * when user click the `X` icon on specific filter
     * special handling for date
     */
    fun removeFilterSelection(valueData: FilterValueData) {
        viewModelScope.launch(Dispatchers.Default) {
            tempList.forEach {
                if (it.name == valueData.name) {
                    if (it.keyName.equals(BaseConstants.FilterValues.DATE_FILTER, true)) {
                        selectedDates = null
                        it.startDate = null
                        it.endDate = null
                    }
                    it.isSelected = false
                }
            }
            refreshLists()
        }
    }

    /**
     * Update the right side pane/ values list. If all the items have been selected, check `All`, else uncheck
     * Also, copy() is called otherwise ListAdapter doesn't calculate the diffs of the items
     */
    private fun refreshFilterValuesList() {
        viewModelScope.launch(Dispatchers.Default) {
            var areAllSelected = true
            tempList.filter {
                it.keyName == selectedFilterKey && !it.name.equals(
                    BaseConstants.FilterValues.FILTER_All,
                    true
                )
            }.forEach {
                areAllSelected = areAllSelected && it.isSelected.orFalse()
            }
            tempList.find {
                it.keyName == selectedFilterKey && it.name.equals(
                    BaseConstants.FilterValues.FILTER_All,
                    true
                )
            }?.let {
                it.isSelected = areAllSelected
            }
            _filterValuesLiveData.emit(
                tempList.filter { it.keyName == selectedFilterKey }.map {
                    it.copy()
                }
            )
        }
    }

    /**
     * copy() is called otherwise ListAdapter doesn't calculate the diffs of the items
     */
    private fun refreshSelectedFilterList() {
        viewModelScope.launch(Dispatchers.Default) {
            _selectedFiltersLiveData.emit(
                tempList.filter {
                    it.isSelected.orFalse() && !it.name.equals(
                        BaseConstants.FilterValues.FILTER_All,
                        true
                    )
                }.map {
                    it.copy()
                }
            )
        }
    }

// ################ End : Methods for Filter ########################


// ################ Methods for UI ########################

    fun sendPullToRefreshEvent() {
        viewModelScope.launch {
            _uiPullRefreshEventLiveData.emit(UiEvent.PulledToRefresh)
        }
    }

    fun sendAppBarOffsetChangeEvent(color: Int) {
        viewModelScope.launch {
            _uiAppBarEventLiveData.emit(UiEvent.AppBarBackgroundColor(color))
        }
    }

    fun setWinningsTabAlert(shouldShow: Boolean) {
        viewModelScope.launch {
            _uiSetWinningsTabAlert.emit(UiEvent.WinningTabAlert(shouldShow))
        }
    }
}


sealed class UiEvent {
    object PulledToRefresh : UiEvent()
    class AppBarBackgroundColor(val color: Int) : UiEvent()
    class WinningTabAlert(val shouldShow: Boolean): UiEvent()
}