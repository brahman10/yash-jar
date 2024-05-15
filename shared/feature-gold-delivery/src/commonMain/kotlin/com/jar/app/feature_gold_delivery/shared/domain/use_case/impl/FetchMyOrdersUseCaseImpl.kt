package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchMyOrdersUseCase

class FetchMyOrdersUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    FetchMyOrdersUseCase {
    override suspend fun fetchMyOrders(request: TransactionListingRequest) = deliveryRepository.getTransactionListingPaginated(request)
}