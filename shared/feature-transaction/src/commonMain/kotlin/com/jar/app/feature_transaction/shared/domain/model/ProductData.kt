package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.util.BaseConstants
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ProductData(
    @SerialName("productName")
    val productName: String? = null,
    @SerialName("productLink")
    val productLink: String? = null,
    @SerialName("images")
    val images: List<String>? = null,

    override val uniqueKey: String = productName?.plus(productLink)?.plus(images?.size).orEmpty()
) : TxnDetailsCardView {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getSortKey(): Int {
        return BaseConstants.TxnDetailsPosition.PRODUCT_DETAILS
    }

    override fun equals(other: Any?): Boolean {
        return other is ProductData
    }
}