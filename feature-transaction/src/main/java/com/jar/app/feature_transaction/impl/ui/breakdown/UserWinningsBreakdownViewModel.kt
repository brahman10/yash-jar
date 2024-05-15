package com.jar.app.feature_transaction.impl.ui.breakdown

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserWinningBreakdownUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class UserWinningsBreakdownViewModel @Inject constructor(
    private val fetchUserWinningBreakdown: IFetchUserWinningBreakdownUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _userWinningsBreakdown =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserWinningBreakdownModel>>>()
    val userWinningsBreakdown: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserWinningBreakdownModel>>>
        get() = _userWinningsBreakdown

    fun fetchUserWinningsBreakdown() {
        viewModelScope.launch {
            fetchUserWinningBreakdown.fetchUserWinningBreakdown().collect {
                _userWinningsBreakdown.postValue(it)
            }
        }
    }

    suspend fun getWinningsBreakdownAmountData(
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