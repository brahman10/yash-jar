package com.jar.gold_redemption.impl.ui.search_store


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllCityListUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllStatesListUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllStoreFromCityUseCase
import com.jar.app.feature_gold_redemption.shared.data.network.model.AllCitiesResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.StateData
import com.jar.gold_redemption.impl.ui.search_store.model.StateListData
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SearchStoreViewModel @Inject constructor(
    private val voucherAllStatesListUse: VoucherAllStatesListUseCase,
    private val voucherAllCityListUse: VoucherAllCityListUseCase,
    private val voucherAllStoreFromCityUse: VoucherAllStoreFromCityUseCase,
) : ViewModel() {

    val statesList: SnapshotStateList<StateListData> = mutableStateListOf<StateListData>()
    private var apiStatesList: List<StateData?>? = null
    private val _currentStateName = MutableLiveData<String?>(null)
    val currentStateName: LiveData<String?> = _currentStateName

    private val _brandName = MutableLiveData<String>("")
    val brandName: LiveData<String> = _brandName

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private fun showLoading(b: Boolean) {
//        _showLoading.value = b
    }

    fun setStates() {
        statesList.apply {
            val list = parseStatesList(apiStatesList)
            val states = getStatesList(list)
            clear()
            addAll(states)
        }
    }

    fun fetchStates() {
        viewModelScope.launch {
            voucherAllStatesListUse.fetchAllStatesList(_brandName.value.orEmpty()).collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    apiStatesList = it
                    setStates()
                },
                onError = { _, _ ->
                    showLoading(false)
                }
            )
        }
    }

    fun fetchStoreFromCity(cityName: String) {
        viewModelScope.launch {
            voucherAllStoreFromCityUse.fetchAllStoreFromCity(
                cityName,
                brandName = _brandName.value.orEmpty()
            ).collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    it?.let {
                        addStoreInCitiesList(it, cityName, statesList)
                    }
                }
            )
        }
    }

    fun fetchCities(title: String) {
        viewModelScope.launch {
            voucherAllCityListUse.fetchAllCityList(title, _brandName.value.orEmpty()).collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    statesList.apply {
                        clear()
                        it?.let {
                            addAll(getCityList(it) ?: listOf())
                        }
                    }
                    _currentStateName.value = "${brandName.value} in ${title}"
                }
            )
        }
    }

    private fun getCityList(data: AllCitiesResponse): List<StateListData>? {
        return data.states?.map {
            StateListData.StateCityNonExpanded(
                it?.cityName.orEmpty(),
                it?.storesCount.orZero()
            )
        }
    }

    fun clearStateName() {
        setStates()
        _currentStateName.value = null
    }

    fun setBrandName(it: String?) {
        this._brandName.value = it.orEmpty()
        if (!it.isNullOrEmpty())
            fetchStates()
    }
}
