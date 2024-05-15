package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchOrderDetailUseCase

class FetchOrderDetailUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    FetchOrderDetailUseCase {
    override suspend fun fetchOrderDetail(orderId: String, assetSourceType: String, assetTxnId: String) = deliveryRepository.fetchTxnDetails(orderId, assetSourceType, assetTxnId)
}