package com.jar.app.feature_gold_delivery.shared.domain.use_case.impl

import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchTransactionListingUseCase

class FetchTransactionListingUseCaseImpl constructor(private val deliveryRepository: DeliveryRepository) :
    FetchTransactionListingUseCase {
    override suspend fun fetchTransactionListing(request: TransactionListingRequest) = deliveryRepository.getTransactionListingPaginated(request)
}