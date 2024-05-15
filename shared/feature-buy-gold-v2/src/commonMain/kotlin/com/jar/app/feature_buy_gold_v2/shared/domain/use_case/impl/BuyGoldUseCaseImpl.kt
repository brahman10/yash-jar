package com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl

import com.jar.app.core_base.util.addPercentage
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByVolumeRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.InitiateBuyGoldRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt

internal class BuyGoldUseCaseImpl constructor(
    private val fetchCurrentGoldBuyUseCase: FetchCurrentGoldPriceUseCase,
    private val buyGoldV2Repository: BuyGoldV2Repository,
    private val remoteConfigApi: RemoteConfigApi
) : BuyGoldUseCase {

    override suspend fun calculateVolumeFromAmount(
        amount: Float,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse?
    ): Flow<RestClientResult<Float>> = flow {
        emit(RestClientResult.loading())
        if (fetchCurrentGoldPriceResponse != null) {
            val currentPriceWithTax =
                fetchCurrentGoldPriceResponse.price.addPercentage(fetchCurrentGoldPriceResponse.applicableTax.orZero())
                    .roundUp(2)

            val finalVolume = (amount / currentPriceWithTax).roundDown(4)
            emit(RestClientResult.success(finalVolume))
        } else {
            fetchCurrentGoldBuyUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collect(
                onLoading = {
                    emit(RestClientResult.loading())
                },
                onSuccess = {
                    val response = it
                    val currentPriceWithTax =
                        response.price.addPercentage(response.applicableTax!!).roundUp(2)
                    val finalVolume = (amount / currentPriceWithTax).roundDown(4)
                    emit(RestClientResult.success(finalVolume))
                },
                onError = { errorMessage, errorCode ->
                    emit(RestClientResult.error(errorMessage))
                }
            )
        }
    }

    override suspend fun calculateVolumeFromAmountSync(
        amount: Float,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse?
    ): RestClientResult<Float> {
        return if (fetchCurrentGoldPriceResponse != null) {
            val currentPriceWithTax =
                fetchCurrentGoldPriceResponse.price.addPercentage(fetchCurrentGoldPriceResponse.applicableTax!!)
                    .roundUp(2)

            val finalVolume = (amount / currentPriceWithTax).roundDown(4)
            RestClientResult.success(finalVolume)
        } else {
            val response = fetchCurrentGoldBuyUseCase.fetchCurrentGoldPriceSync(GoldPriceType.BUY)
            return when (response.status) {
                RestClientResult.Status.SUCCESS -> {
                    val data = response.data?.data
                    if (data != null) {
                        val currentPriceWithTax =
                            data.price.addPercentage(data.applicableTax!!).roundUp(2)
                        val finalVolume = (amount / currentPriceWithTax).roundDown(4)
                        RestClientResult.success(finalVolume)
                    } else {
                        RestClientResult.error(response.message.orEmpty())
                    }
                }
                RestClientResult.Status.ERROR -> {
                    RestClientResult.error(response.message.orEmpty())
                }
                RestClientResult.Status.LOADING -> {
                    // This case will not happen since function is synchronous
                    RestClientResult.loading()
                }
                RestClientResult.Status.NONE -> {
                    // This case will not happen
                    RestClientResult.none()
                }
            }
        }
    }

    override suspend fun calculateAmountFromVolume(
        volume: Float,
        fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse?
    ): Flow<RestClientResult<Float>> = flow {
        if (fetchCurrentGoldPriceResponse != null) {
            val finalVolume = volume.roundDown(4)

            val currentPriceWithTax =
                fetchCurrentGoldPriceResponse.price.addPercentage(fetchCurrentGoldPriceResponse.applicableTax!!)
                    .roundUp(2)

            val minimumVolume =  (remoteConfigApi.getMinimumGoldBuyAmount() / currentPriceWithTax).roundDown(4)

            var finalAmount = (currentPriceWithTax * finalVolume).roundUp(2)

            if (finalAmount.roundToInt() < remoteConfigApi.getMinimumGoldBuyAmount() && volume < minimumVolume) {
                finalAmount = 1.0f
            }
            emit(RestClientResult.success(finalAmount))
        } else {
            fetchCurrentGoldBuyUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collect(
                onLoading = {
                    emit(RestClientResult.loading())
                },
                onSuccess = {
                    val response = it
                    val finalVolume = volume.roundDown(4)

                    val currentPriceWithTax =
                        response.price.addPercentage(response.applicableTax!!).roundUp(2)

                    var finalAmount = (currentPriceWithTax * finalVolume).roundUp(2)

                    if (finalAmount < 1) {
                        finalAmount = 1.0f
                    }

                    emit(RestClientResult.success(finalAmount))
                },
                onError = { errorMessage, errorCode ->
                    emit(RestClientResult.error(errorMessage))
                }
            )
        }
    }

    override suspend fun buyGoldByAmount(buyGoldByAmountRequest: BuyGoldByAmountRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>> =
        flow {
            emit(RestClientResult.loading())
            val amount = buyGoldByAmountRequest.amount.roundUp(2)
            if (amount < 1) {
                emit(RestClientResult.error("Minimum amount should be ₹10"))
            }
            calculateVolumeFromAmount(
                amount,
                buyGoldByAmountRequest.fetchCurrentGoldPriceResponse
            ).collectUnwrapped(
                onLoading = {
                    emit(RestClientResult.loading())
                },
                onSuccess = {
                    val initiateBuyGoldRequest = InitiateBuyGoldRequest(
                        amount = amount,
                        volume = it,
                        fetchCurrentGoldPriceResponse = buyGoldByAmountRequest.fetchCurrentGoldPriceResponse,
                        requestType = BuyGoldRequestType.AMOUNT.name,
                        paymentProvider = buyGoldByAmountRequest.paymentGateway.name,
                        auspiciousTimeId = buyGoldByAmountRequest.auspiciousTimeId,
                        couponCodeId = buyGoldByAmountRequest.couponCodeId,
                        couponCode = buyGoldByAmountRequest.couponCode,
                        offerAmount = buyGoldByAmountRequest.offerAmount,
                        giftingId = buyGoldByAmountRequest.giftingId,
                        deliveryOrderId = buyGoldByAmountRequest.deliveryOrderId,
                        jarWinningsUsedAmount = buyGoldByAmountRequest.jarWinningsUsedAmount,
                        deliveryMakingCharge = buyGoldByAmountRequest.deliveryMakingCharge,
                        userProductId = buyGoldByAmountRequest.userProductId,
                        flowContext = buyGoldByAmountRequest.flowContext
                    )

                    buyGoldV2Repository.buyGoldManual(initiateBuyGoldRequest).collect {
                        emit(it)
                    }
                },
                onError = { it, _ ->
                    emit(RestClientResult.error(it))
                })
        }

    override suspend fun buyGoldByVolume(buyGoldByVolumeRequest: BuyGoldByVolumeRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>> =
        flow {
            emit(RestClientResult.loading())
            val volume = buyGoldByVolumeRequest.volume.roundDown(4)
            calculateAmountFromVolume(
                volume,
                buyGoldByVolumeRequest.fetchCurrentGoldPriceResponse
            ).collectUnwrapped(
                onLoading = {
                    emit(RestClientResult.loading())
                },
                onSuccess = {
                    val initiateBuyGoldRequest = InitiateBuyGoldRequest(
                        amount = it,
                        volume = volume,
                        fetchCurrentGoldPriceResponse = buyGoldByVolumeRequest.fetchCurrentGoldPriceResponse,
                        requestType = BuyGoldRequestType.VOLUME.name,
                        paymentProvider = buyGoldByVolumeRequest.paymentGateway.name,
                        auspiciousTimeId = buyGoldByVolumeRequest.auspiciousTimeId,
                        couponCodeId = buyGoldByVolumeRequest.couponCodeId,
                        couponCode = buyGoldByVolumeRequest.couponCode,
                        offerAmount = buyGoldByVolumeRequest.offerAmount,
                        giftingId = buyGoldByVolumeRequest.giftingId,
                        deliveryOrderId = buyGoldByVolumeRequest.deliveryOrderId,
                        jarWinningsUsedAmount = buyGoldByVolumeRequest.jarWinningsUsedAmount,
                        deliveryMakingCharge = buyGoldByVolumeRequest.deliveryMakingCharge,
                        leaseId = buyGoldByVolumeRequest.leaseId
                    )
                    buyGoldV2Repository.buyGoldManual(initiateBuyGoldRequest).collect {
                        emit(it)
                    }
                },
                onError = { it, _ ->
                    emit(RestClientResult.error(it))
                }
            )
        }

}

enum class BuyGoldRequestType {
    AMOUNT, VOLUME
}
