package com.jar.gold_redemption.api

/**
 * Gold redemption feature API
 * **/
interface GoldRedemptionApi {

    /**
     * Method to start the intro screen of the gold redemption module
     * **/
    fun openIntroScreen(sourceScreen: String?)

    fun openBrandCatalogueScreen()

    fun openMyOrdersScreen(tabType: String?)

    fun openVoucherStatusScreen(voucherId: String?, orderType: String?)

    fun openVoucherPurchaseScreen(voucherId: String)

}