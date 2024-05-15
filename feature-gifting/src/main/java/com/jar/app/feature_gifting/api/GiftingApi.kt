package com.jar.app.feature_gifting.api

import android.content.Context
import com.jar.app.feature_gifting.shared.domain.model.GoldGiftReceivedResponse
import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import kotlinx.coroutines.CoroutineScope
import java.lang.ref.WeakReference

/**
 * Api to be used by other modules
 **/
interface GiftingApi {

    /**
     *Function to open send gift screen
     * If you pass [SendGiftGoldRequest] then all the details will be prefilled..
     * Only Receiver name & number will get prefilled as volume & amount can change based on gold price..
     **/
    fun openSendGiftScreen(fromScreen: String, sendGiftGoldRequest: SendGiftGoldRequest? = null)

    /**
     * Function to open gift pending screen
     **/
    fun openGiftPendingScreen(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse)


    /**
     * Function to open gift success screen
     **/
    fun openGiftSuccessScreen(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse)


    /**
     * Function to open gift failure screen
     **/
    fun openGiftFailureScreen(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse)


    /**
     * Function to redirect to post order screen based on gifting status(SUCCESS, PENDING, FAILURE)
     **/
    fun redirectToGiftStatusScreen(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse)

    /**
     * Function to open gift received screen
     **/
    fun openViewGiftScreen(goldGiftReceivedResponse: GoldGiftReceivedResponse)

    /**
     * Function to share gift image with deeplink to download app
     **/
    fun shareGift(
        uiScope: CoroutineScope,
        contextRef: WeakReference<Context>,
        isExistingReceiver: Boolean, // Whether the receiver is already a jar user or not
        shouldShareOnlyOnWA: Boolean = false
    )


}