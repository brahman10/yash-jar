package com.jar.app.feature_gifting.impl

import android.content.Context
import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.shareOnWhatsapp
import com.jar.app.core_utils.data.AppsFlyerInviteUtil
import com.jar.app.core_utils.data.FileUtils
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.api.GiftingApi
import com.jar.app.feature_gifting.shared.domain.model.GoldGiftReceivedResponse
import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments_common.shared.GiftingStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_utils.data.WhatsAppUtil
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject

internal class GiftingApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val serializer: Serializer,
    private val fileUtils: FileUtils,
    private val appsFlyerInviteUtil: AppsFlyerInviteUtil,
    private val dispatcherProvider: DispatcherProvider,
    private val deviceUtils: WhatsAppUtil
) : GiftingApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openSendGiftScreen(fromScreen: String, sendGiftGoldRequest: SendGiftGoldRequest?) {
        val data = encodeUrl(serializer.encodeToString(sendGiftGoldRequest))
        navController.navigate(
            Uri.parse("android-app://com.jar.app/sendGiftFragment/$data/$fromScreen"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openGiftSuccessScreen(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        val data = encodeUrl(serializer.encodeToString(fetchManualPaymentStatusResponse))
        navController.navigate(
            Uri.parse("android-app://com.jar.app/giftGoldSuccessFragment/$data"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openGiftPendingScreen(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        val data = encodeUrl(serializer.encodeToString(fetchManualPaymentStatusResponse))
        navController.navigate(
            Uri.parse("android-app://com.jar.app/giftGoldPendingFragment/$data"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openGiftFailureScreen(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        val data = encodeUrl(serializer.encodeToString(fetchManualPaymentStatusResponse))
        navController.navigate(
            Uri.parse("android-app://com.jar.app/giftGoldFailureFragment/$data"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun redirectToGiftStatusScreen(fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse) {
        when (fetchManualPaymentStatusResponse.sendGiftResponse?.getGiftingStatus()) {
            GiftingStatus.SENT -> {
                openGiftSuccessScreen(fetchManualPaymentStatusResponse)
            }
            GiftingStatus.PENDING -> {
                openGiftPendingScreen(fetchManualPaymentStatusResponse)
            }
            GiftingStatus.FAILURE -> {
                openGiftFailureScreen(fetchManualPaymentStatusResponse)
            }
            else -> {
                // Do Nothing..
            }
        }
    }

    override fun openViewGiftScreen(goldGiftReceivedResponse: GoldGiftReceivedResponse) {
        val data = encodeUrl(serializer.encodeToString(goldGiftReceivedResponse))
        navController.navigate(
            Uri.parse("android-app://com.jar.app/viewReceivedGiftFragment/$data"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun shareGift(
        uiScope: CoroutineScope,
        contextRef: WeakReference<Context>,
        isExistingReceiver: Boolean,
        shouldShareOnlyOnWA: Boolean
    ) {
        val context = contextRef.get()
        if (context != null) {
            uiScope.launch {
                val shareMsg =
                    if (isExistingReceiver)
                        context.getString(
                            R.string.feature_gifting_gift_share_message_existing_user,
                            getShareAppUrl()
                        )
                    else
                        context.getString(
                            R.string.feature_gifting_gift_share_message_new_user,
                            getShareAppUrl()
                        )

                fileUtils.copyDrawable(
                    R.drawable.feature_gifting_gift_share_image, "gift_share_image"
                )?.let {
                    withContext(dispatcherProvider.main) {
                        if (shouldShareOnlyOnWA) {
                            context.shareOnWhatsapp(deviceUtils.getWhatsappPackageName(), shareMsg, image = it)
                        } else {
                            fileUtils.shareImage(
                                context,
                                it,
                                shareMsg
                            )
                        }
                    }
                } ?: kotlin.run {
                    if (shouldShareOnlyOnWA) {
                        context.shareOnWhatsapp(deviceUtils.getWhatsappPackageName(), shareMsg)
                    } else {
                        fileUtils.shareText(shareMsg, "")
                    }
                }
            }
        }
    }

    private fun getShareAppUrl(): String {
        return appsFlyerInviteUtil.getAppsFlyerInviteLink()
            ?: BaseConstants.PLAY_STORE_URL
    }
}