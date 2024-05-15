package com.jar.app.feature_gold_price.shared.data

import com.jar.app.core_base.util.countDownTimer
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class GoldPriceFlow constructor(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    scope: CoroutineScope?
) {

    private var fetchPriceJob: Job? = null
    private var timerJob: Job? = null

    private val appScope = (scope ?: CoroutineScope(Dispatchers.IO + SupervisorJob()))

    private var _sellPriceStatus: RestClientResult.Status = RestClientResult.Status.NONE

    private val _sellPrice = MutableSharedFlow<RestClientResult<FetchCurrentGoldPriceResponse>>()
    val sellPrice: SharedFlow<RestClientResult<FetchCurrentGoldPriceResponse>> = _sellPrice.shareIn(
        scope = appScope,
        started = SharingStarted.Lazily,
        replay = 1
    )

    private val _priceTimer = MutableSharedFlow<Long>()
    val priceTimer: SharedFlow<Long> = _priceTimer.shareIn(
        scope = appScope,
        started = SharingStarted.Lazily,
        replay = 1
    )

    init {
        fetchGoldSellPrice()
    }

    private fun fetchGoldSellPrice() {
        fetchPriceJob?.cancel()
        fetchPriceJob = appScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(
                goldPriceType = GoldPriceType.SELL
            ).collect(
                onLoading = {
                    _sellPrice.emit(RestClientResult.loading())
                    _sellPriceStatus = RestClientResult.Status.LOADING
                },
                onSuccess = {
                    _sellPrice.emit(RestClientResult.success(it))
                    startExpiryTimer(it)
                    _sellPriceStatus = RestClientResult.Status.SUCCESS
                },
                onError = { errorMessage, errorCode ->
                    _sellPriceStatus = RestClientResult.Status.ERROR
                    _sellPrice.emit(
                        RestClientResult.error(
                            message = errorMessage,
                            errorCode = errorCode
                        )
                    )
                    retryIOs(
                        times = 10,
                        initialDelay = 100,
                        maxDelay = 2000,
                        block = {
                            fetchGoldSellPrice()
                        },
                        shouldRetry = {
                            _sellPriceStatus == RestClientResult.Status.ERROR
                        }
                    )
                }
            )
        }
    }

    private suspend fun <T> retryIOs(
        times: Int = Int.MAX_VALUE,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T,
        shouldRetry: (result: T) -> Boolean
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            val result = block()
            if (shouldRetry(result).not()) {
                return result
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return block()
    }


    private fun startExpiryTimer(fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse) {
        timerJob?.cancel()
        timerJob = appScope.countDownTimer(
            totalMillis = fetchCurrentGoldPriceResponse.getValidityInMillis(),
            onInterval = { millisLeft ->
                _priceTimer.emit(millisLeft)
            },
            onFinished = {
                _priceTimer.emit(0)
                fetchGoldSellPrice()
            }
        )
    }
}