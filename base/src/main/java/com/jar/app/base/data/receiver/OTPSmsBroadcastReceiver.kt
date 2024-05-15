package com.jar.app.base.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.jar.app.base.data.event.OTPReceivedEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

abstract class HiltOTPBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
    }
}

@AndroidEntryPoint
class OTPSmsBroadcastReceiver() : HiltOTPBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
//                     Get SMS message contents
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as? String ?: return
                    EventBus.getDefault().postSticky(OTPReceivedEvent(message))
                }
            }
        }
    }

}