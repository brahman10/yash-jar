package com.jar.app.feature_transaction.impl.ui.breakdown

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchInvestedAmntBreakupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class UserAmountBreakdownViewModel @Inject constructor(
    private val fetchAmountBreakdownUseCase: IFetchInvestedAmntBreakupUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _investedAmountBreakdownLivedata =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.InvestmentBreakDown?>>>()
    val investedAmountBreakdownLivedata: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.InvestmentBreakDown?>>>
        get() = _investedAmountBreakdownLivedata

    fun fetchUserAmountBreakDown() {
        viewModelScope.launch(dispatcherProvider.io) {
            fetchAmountBreakdownUseCase.fetchInvestedAmountBreakDown().collect {
                _investedAmountBreakdownLivedata.postValue(it)
            }
        }
    }

    suspend fun getInvestedAmountData(
        keys: List<String>,
        values: List<Float>,
        context: WeakReference<Context?>
    ): List<com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown> {
        return withContext(Dispatchers.Default) {
            val list = ArrayList<com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown>()
            repeat(keys.size) {
                val amount =  context.get()?.getString(R.string.feature_transaction_rs_value, values[it].orZero())
                list.add(
                    com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown(
                        keys[it],
                        amount ?: "0"
                    )
                )
            }
            list
        }
    }
}