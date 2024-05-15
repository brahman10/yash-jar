package com.jar.app.feature_homepage.shared.ui.user_gold_breakdown

import com.jar.app.core_base.domain.mapper.toGoldBalance
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.domain.model.GoldBalanceViewType
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.BreakdownDataUnit
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.UserGoldBreakdown
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.UserGoldBreakdownResponse
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUserGoldBreakdownUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchInvestedAmntBreakupUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserGoldBreakdownFragmentViewModel constructor(
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    private val fetchUserGoldBreakdownUseCase: FetchUserGoldBreakdownUseCase,
    private val fetchAmountBreakdownUseCase: IFetchInvestedAmntBreakupUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private var valueInGramList: ArrayList<UserGoldBreakdown>? = null
    private var valueInAmountList: ArrayList<UserGoldBreakdown>? = null

    private val _goldBalanceLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>(
            RestClientResult.none()
        )
    val goldBalanceLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>
        get() = _goldBalanceLiveData.toCommonStateFlow()

    private val _userGoldBreakdownLiveData =
        MutableStateFlow<RestClientResult<UserGoldBreakdownResponse?>>(RestClientResult.none())
    val userGoldBreakdownLiveData: CStateFlow<RestClientResult<UserGoldBreakdownResponse?>>
        get() = _userGoldBreakdownLiveData.toCommonStateFlow()

    fun fetchUserGoldBalance() {
        viewModelScope.launch {
            fetchUserGoldBalanceUseCase.fetchUserGoldBalance()
                .mapToDTO {
                    it?.toGoldBalance()
                }
                .collect {
                    _goldBalanceLiveData.emit(it)
                }
        }
    }

    fun fetchUserGoldBreakDown(
        whichBalanceView: GoldBalanceViewType,
        convertToString: (strRes: StringResource, data: String) -> String
    ) {
        if (whichBalanceView == GoldBalanceViewType.ONLY_GM || whichBalanceView == GoldBalanceViewType.GM_ND_RS) {
            fetchForGms()
        } else {
            fetchForAmount(convertToString)
        }
    }

    private fun fetchForAmount(
        convertToString: (strRes: StringResource, data: String) -> String
    ) {
        viewModelScope.launch {
            fetchAmountBreakdownUseCase.fetchInvestedAmountBreakDown().collect(
                onLoading = {
                    _userGoldBreakdownLiveData.emit(RestClientResult.loading())
                },
                onSuccess = { investmentBreakDown ->
                    investmentBreakDown?.let {
                        val data = it
                        valueInAmountList = ArrayList(data.keys.size.orZero())
                        data.keys.forEachIndexed { index, s ->
                            data.values.getOrNull(index)?.let {
                                valueInAmountList?.add(
                                    UserGoldBreakdown(
                                        s,
                                        convertToString(
                                            MR.strings.feature_buy_gold_v2_currency_sign_x_string,
                                            it.toString()
                                        )
                                    )
                                )
                            }
                        }

                        val finalData = UserGoldBreakdownResponse(
                            userGoldBreakdownList = valueInAmountList.orEmpty(),
                            totalAmount = data.investedValue,
                            keys = data.keys,
                            values = data.values.map { it },
                            goldValues = arrayListOf(),
                            aggrBuyPrice = null,
                            unitPreference = null
                        )
                        _userGoldBreakdownLiveData.emit(RestClientResult.success(finalData))
                    }
                }
            )
        }
    }

    fun fetchForGms() {
        viewModelScope.launch {
            fetchUserGoldBreakdownUseCase.fetchUserGoldBreakdown().collect(
                onLoading = {
                    _userGoldBreakdownLiveData.emit(RestClientResult.loading())
                },
                onSuccess = { userGoldBreakDown ->
                    userGoldBreakDown?.let {
                        val data = it
                        valueInGramList = ArrayList(data.keys?.size.orZero())
                        valueInAmountList = ArrayList(data.keys?.size.orZero())
                        data.keys?.forEachIndexed { index, s ->
                            data.goldValues.getOrNull(index)?.let {
                                valueInGramList?.add(
                                    UserGoldBreakdown(
                                        s,
                                        it.toString() + (if (data.unitPreference == null) "" else " ${data.unitPreference}")
                                    )
                                )
                            }
                            data.values.getOrNull(index)?.let {
                                valueInAmountList?.add(
                                    UserGoldBreakdown(
                                        s,
                                        it.toString()
                                    )
                                )
                            }
                        }
                        val finalData = data.copy(userGoldBreakdownList = valueInGramList.orEmpty())
                        _userGoldBreakdownLiveData.emit(RestClientResult.success(finalData))
                    }
                },
                onError = { errorMessage, _ ->
                    _userGoldBreakdownLiveData.emit(RestClientResult.error(errorMessage))
                }
            )
        }

    }

    fun setDataUnit(breakdownDataUnit: BreakdownDataUnit) {
        viewModelScope.launch {
            when (breakdownDataUnit) {
                BreakdownDataUnit.GRAM -> {
                    val data =
                        userGoldBreakdownLiveData.value.data?.copy(userGoldBreakdownList = valueInGramList.orEmpty())
                    _userGoldBreakdownLiveData.emit(RestClientResult.success(data))
                }

                BreakdownDataUnit.AMOUNT -> {
                    val data =
                        userGoldBreakdownLiveData.value.data?.copy(userGoldBreakdownList = valueInAmountList.orEmpty())
                    _userGoldBreakdownLiveData.emit(RestClientResult.success(data))
                }
            }
        }
    }
}