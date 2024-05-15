package com.jar.app.feature_sms_sync.impl.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.jar.app.base.data.event.OnNewMessageEvent
import com.jar.app.feature_sms_sync.impl.utils.SmsSyncConstants
import com.jar.app.feature_sms_sync.impl.worker.SmsSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.*

class NewSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            GlobalScope.launch(Dispatchers.IO) {
                val bundle = intent.extras
                if (bundle != null) {
                    val pdusObj = bundle["pdus"] as? Array<*>? ?: return@launch
                    val sms = arrayOfNulls<SmsMessage>(pdusObj.size)
                    val sb = StringBuilder()
                    var sender = ""
                    for (i in pdusObj.indices) {
                        val format = bundle.getString("format")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            sms[i] = SmsMessage.createFromPdu(pdusObj[i] as ByteArray?, format)
                        } else {
                            sms[i] = SmsMessage.createFromPdu(pdusObj[i] as ByteArray?)
                        }
                        try {
                            if (i == 0) {
                                sender = sms[i]?.originatingAddress!!
                                    .uppercase(Locale.getDefault()).replace("['`]*".toRegex(), "")
                                    .trim { it <= ' ' }
                            }
                        } catch (ex: Exception) {
                            /*
                        Ignore
                         */
                        }
                        if (sender.equals(sms[i]?.originatingAddress, ignoreCase = true)) {
                            sb.append(sms[i]?.messageBody)
                        }
                    }
                    val message = sb.toString().replace("['`\"]*".toRegex(), "").replace("\n", "")
                    if (sender.length <= SmsSyncConstants.NON_PERSONAL_SENDER_LENGTH) {
                        EventBus.getDefault().postSticky(OnNewMessageEvent(message))

                        val work = OneTimeWorkRequestBuilder<SmsSyncWorker>()
                            .addTag(SmsSyncWorker.SMS_SYNC_WORKER_TAG)
                            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                            .build()
                        WorkManager.getInstance(context).enqueueUniqueWork(
                            SmsSyncWorker.SMS_SYNC_WORKER_NAME,
                            ExistingWorkPolicy.REPLACE,
                            work
                        )
                    }
                }
            }
        }
    }
}