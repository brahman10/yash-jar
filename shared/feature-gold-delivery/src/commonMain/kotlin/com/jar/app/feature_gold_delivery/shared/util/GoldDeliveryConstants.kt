package com.jar.app.feature_gold_delivery.impl.util

object GoldDeliveryConstants {

    const val REGION_CODE = "IN"
    const val DEFAULT_COUNTRY_CODE_WITH_PLUS_SIGN = "+91"
    const val GOLD_DELIVERY_FAQ = "GOLD_DELIVERY_FAQ"
    const val DELETE_ALL_ITEMS_FROM_BOTTOMSHEET = "DELETE_ALL_ITEMS_FROM_BOTTOMSHEET"
    const val BACK_FACTOR_SCALE = 1.4f

    internal object Endpoints {
        const val FETCH_ALL_STORE_ITEMS = "v2/api/delivery/products"
        const val FETCH_ORDER_STATE = "v2/api/payments/order/state "
        const val FETCH_STORE_ITEM_FAQ = "v2/api/dashboard/static"
        const val SUBMIT_DELIVERY_FEEDBACK = "v2/api/delivery/feedback"
        const val FETCH_GOLD_DELIVERY_ORDER_TRANSACTION_LIST =
            "v4/api/transactions/listTransactions"
        const val FETCH_GOLD_DELIVERY_LANDING_SCREEN_DETAILS = "v2/api/delivery/landing"
        const val DELIVER_PRODUCT = "v1/api/delivery"
        const val VALIDATE_PIN_CODE = "v1/api/delivery/validatePin"
        const val FETCH_ADDRESS_BY_ID = "v1/api/user/settings/address/id"
        const val ADD_ADDRESS = "v1/api/user/settings/address"
        const val FETCH_ALL_ADDRESS = "v1/api/user/settings/address"
        const val DELETE_ADDRESS = "v1/api/user/settings/address"
        const val EDIT_ADDRESS = "v1/api/user/settings/address"
        const val ADD_ITEM_TO_CART = "v2/api/delivery/cart"
        const val UPDATE_ITEM_QUANTITY_IN_CART = "v2/api/delivery/cart"
        const val PLACE_GOLD_DELIVERY_ORDER = "v2/api/delivery/placeOrder"
        const val FETCH_PRODUCT_FROM_WISHLIST = "v2/api/delivery/wishlist"
        const val REMOVE_PRODUCT_FROM_WISHLIST = "v2/api/delivery/wishlist"
        const val ADD_PRODUCT_TO_WISHLIST = "v2/api/delivery/wishlist"
        const val NOTIFY_USER = "v2/api/delivery/notify"
        const val DELETE_ITEM_OF_CART = "v2/api/delivery/cart"
        const val FETCH_FULL_CART = "v2/api/delivery/cart"
        const val FETCH_CART_BREAKDOWN = "v2/api/delivery/cart/priceBreakDown"
        const val FETCH_DELIVERY_TRANSACTION_ORDER_DETAIL = "v2/api/delivery/orderDetails"
    }

    object AnalyticsKeys {
        const val ShownPaymentGoldDelivery =
            "ShownPaymentGoldDelivery"
        const val GoldInAccount = "GoldInAccount"
        const val PinCodeEntered = "PinCodeEntered"
        const val pincode = "pincode"
        const val pincode_typed = "pincode_typed"
        const val pincode_entered = "pincode_entered"
        const val pincode_clear = "pincode_clear"
        const val pincode_cleared = "pincode_cleared"
        const val pincode_not_serviceable = "pincode_not_serviceable"
        const val pincode_check = "pincode_check"
        const val pincode_serviceable = "pincode_serviceable"
        const val pincode_check_clicked = "pincode_check_clicked"

        const val ShownAddressGoldDelivery = "ShownAddressGoldDelivery"
        const val ShownHomeScreenGoldDelivery = "Shown_HomeScreen_GoldDelivery"
        const val ClickButtonHomeScreenGoldDelivery = "ClickButton_HomeScreen_GoldDelivery"

        const val ShownPDPGoldDelivery = "Shown_PDP_GoldDelivery"
        const val ClickPDPGoldDelivery = "ClickButton_PDP_GoldDelivery"

        const val ShownCheckoutGoldDelivery = "Shown_Checkout_GoldDelivery"
        const val ClickButtonCheckoutGoldDelivery = "ClickButton_Checkout_GoldDelivery"

        const val ShownStatusScreenGoldDelivery = "Shown_OrderDetailsScreen_GoldDelivery"
        const val ClickButtonStatusScreenGoldDelivery =
            "ClickButton_OrderDetailsScreen_GoldDelivery"

        const val ShownOrderSuccessGoldDelivery = "Shown_OrderSuccess_GoldDelivery"
        const val ClickButtonOrderSuccessGoldDelivery = "ClickButton_OrderSuccess_Gold_Delivery"

        const val ShownWishlistScreenGoldDelivery = "Shown_WishlistScreen_GoldDelivery"
        const val ClickButtonWishlistScreenGoldDelivery = "ClickButton_WishlistScreen_GoldDelivery"

        const val BackClick_GoldDeliveryScreen = "BackClick_GoldDeliveryScreen"
        const val PinCodeCheckClick_GoldDeliveryScreen = "PinCodeCheckClick_GoldDeliveryScreen"
        const val Click_ShownSaveAddressPopUp_GoldDelivery =
            "Click_ShownSaveAddressPopUp_GoldDelivery"
        const val ShowNewAddressScreen_GoldDelivery = "ShowNewAddressScreen_GoldDelivery"
        const val ClickButtonAddressGoldDelivery = "ClickButtonAddressGoldDelivery"
        const val Click_type = "Click_type"
        const val save_new_address_clicked = "save_new_address_clicked"
        const val cart_bottomsheet_weight_added = "cart_bottomsheet_weight_added"
        const val cart_bottomsheet_weight_edited = "cart_bottomsheet_weight_edited"
        const val cart_bottomsheet_weight_selected = "cart_bottomsheet_weight_selected"
        const val cart_bottomsheet_wishlist_added = "cart_bottomsheet_wishlist_added"
        const val cart_bottomsheet_wishlist_removed = "cart_bottomsheet_wishlist_removed"
        const val label = "label"
        const val volume = "volume"
        const val title = "title"

        const val saved_address_selected = "saved_address_selected"
        const val add_new_address_clicked = "add_new_address_clicked"
        const val selected_address_continue_clicked = "selected_address_continue_clicked"

        const val gold_item_deleted = "gold_item_deleted"
        const val Label = "Label"

        const val gold_item_quantity_decreased = "gold_item_quantity_decreased"
        const val gold_item_quantity_increased = "gold_item_quantity_increased"
        const val place_order_clicked = "place_order_clicked"
        const val view_breakdown_clicked = "view_breakdown_clicked"
        const val jar_savings_enabled = "jar_savings_enabled"
        const val jar_savings_disabled = "jar_savings_disabled"
        const val gold_item_edit_clicked = "gold_item_edit_clicked"
        const val cart_proceed = "cart_proceed"
        const val cart_tab = "cart_tab"
        const val cart_container = "cart_container"
        const val remove_from_wishlist = "remove_from_wishlist"
        const val similar_item = "similar_item"
        const val cart_bottomsheet_open = "cart_bottomsheet_open"
        const val cart_tab_click = "cart_tab_click"
        const val cart_bottomsheet_proceed_click = "cart_bottomsheet_proceed_click"
    }
}