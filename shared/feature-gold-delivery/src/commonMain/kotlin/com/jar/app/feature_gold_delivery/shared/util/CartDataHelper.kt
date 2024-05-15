package com.jar.app.feature_gold_delivery.shared.util

import com.jar.app.core_base.util.orZero
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData

object CartDataHelper {
    fun calculateTotalAmountFromCart(cartItemData: List<CartItemData?>?): Double {
        return cartItemData?.sumOf { (it?.totalAmount.orZero()) } ?: 0.0
    }

    fun createMapForAnalytics(
        eventName: String,
        cartAPIData: CartAPIData?
    ): MutableMap<String, String> {
        return mutableMapOf<String, String>(
            eventName to cartAPIData?.cartItemData?.size.orZero().toString() + " Items"
        ).apply {
            cartAPIData?.cartItemData?.takeIf { it.isNotEmpty() }
                ?.joinToString { it?.label ?: it?.productId ?: "0" }?.let {
                this["items"] = it
            }
        }
    }
}