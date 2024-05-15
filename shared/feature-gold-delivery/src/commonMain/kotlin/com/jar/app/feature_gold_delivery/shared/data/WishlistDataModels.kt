package com.jar.app.feature_gold_delivery.shared.data

import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData
import com.jar.app.feature_gold_delivery.shared.domain.model.WishlistAPIData

sealed class WishlistData {
    data class WishlistHeader(val title: String) : WishlistData()
    data class WishlistBody(
        val body: WishlistAPIData,
        var isChecked: Boolean,
        val cart: CartItemData
    ) : WishlistData()
}