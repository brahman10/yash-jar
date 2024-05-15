package com.jar.app.feature_sms_sync.impl.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.jar.app.feature_sms_sync.impl.di.SmsSyncPreferences
import com.jar.app.feature_sms_sync.impl.domain.model.SmsData
import com.jar.app.feature_sms_sync.impl.domain.model.SmsSyncRequest
import com.jar.app.feature_sms_sync.impl.domain.usecases.ISendSmsToServerUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
internal class SmsSyncUtils @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    @SmsSyncPreferences private val pref: SharedPreferences,
    private val smsToServerUseCase: ISendSmsToServerUseCase,
    private val analyticsApi: com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
) {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        analyticsApi.postEvent(
            SmsSyncConstants.EventKey.SMS_SYNC,
            values = mapOf(
                SmsSyncConstants.EventKey.EVENT_TYPE_KEY to SmsSyncConstants.EventKey.EVENT_TYPE_FAILURE,
                SmsSyncConstants.EventKey.FAILURE_REASON to (throwable.message ?: "Some Exception occurred"),
            )
        )
    }

    private val coroutineScope = GlobalScope + coroutineExceptionHandler + Dispatchers.IO

    /**
     * TimeStamp at which SMS reading was started. We will store this in preferences
     * Not storing System.currentTimeInMillis() as any new message between these two time periods might get skipped
     */
    private var triggerTimeStamp: Long = 0L

    private var syncJob: Job? = null

    /**
     * This will be the Entry Point for SMS sync
     * [numberOfDaysOfSms] Number of days of Past messages to be read
     */
    fun starSync(numberOfDaysOfSms: Int, skipLastSyncCheck: Boolean) {
        syncJob?.cancel()
        syncJob = coroutineScope.launch {
            /**
             * Skip the last sync check to do a forced sync for [numberOfDaysOfSms]
             */
            if (skipLastSyncCheck) {
                readMessages(getTimeStampFromDays(numberOfDaysOfSms))
            } else {
                val lastSyncTimeStamp = pref.getLong(SmsSyncConstants.PREF_KEY_LAST_SYNC_TIME, 0L)
                if (lastSyncTimeStamp > 0L)         //for reading only new messages after stored timeStamp
                    readMessages(lastSyncTimeStamp)
                else                                //for reading all past messages for [numberOfDaysOfSms] days
                    readMessages(getTimeStampFromDays(numberOfDaysOfSms))
            }
        }
    }

    /**
     * This will be called in case of a new message. It will sync all new messages from lastSyncTimeStamp
     * Functioning is same as above function, just that we don't pass number of days.
     */
    fun readRecentMessages() {
        val lastSyncTimeStamp = pref.getLong(SmsSyncConstants.PREF_KEY_LAST_SYNC_TIME, 0L)
        if (lastSyncTimeStamp > 0L)
            coroutineScope.launch {
                readMessages(lastSyncTimeStamp)
            }
    }

    private suspend fun readMessages(timeStamp: Long) {
        triggerTimeStamp = System.currentTimeMillis()
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_SMS)) {
            val smsList = ArrayList<SmsData>()
            val uri = Uri.parse(SMS_URI_SCHEME)
            val projection = arrayOf(ADDRESS_PROJECTION, BODY_PROJECTION, DATE_PROJECTION, DATE_SENT_PROJECTION)
            val selectionArgument = arrayOf(timeStamp.toString())
            applicationContext.contentResolver?.query(uri, projection, "date >?", selectionArgument, "date ASC")?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val senderAddressIndex = cursor.getColumnIndex(ADDRESS_PROJECTION)
                    val smsBodyIndex = cursor.getColumnIndex(BODY_PROJECTION)
                    val dateIndex = cursor.getColumnIndex(DATE_PROJECTION)                  //The date the message was received
                    val sendDateIndex = cursor.getColumnIndex(DATE_SENT_PROJECTION)         //The date the message was sent

                    var senderAddress: String?
                    var strBody: String?
                    do {
                        try {
                            senderAddress = cursor.getString(senderAddressIndex)
                            senderAddress = senderAddress.uppercase(Locale.getDefault()).replace("['`]*".toRegex(), "")
                            senderAddress = senderAddress.trim { it <= ' ' }
                        } catch (e: Exception) {
                            continue
                        }
                        if (senderAddress != null) {
                            strBody = cursor.getString(smsBodyIndex)
                            if (strBody != null) {
                                strBody = strBody.replace("\n", "")
                                var receivedDate = 0L
                                receivedDate = try {
                                    cursor.getLong(dateIndex)
                                } catch (e: Exception) {
                                    continue
                                }
                                var sentDate = 0L
                                sentDate = try {
                                    cursor.getLong(sendDateIndex)
                                } catch (e: Exception) {
                                    continue
                                }
                                //Not sure about this logic. Leaving it as it was in old code
                                //Modiji ne kiya hai to kuch soch samajh k hi kiya hoga
                                val oneDayMillis = 86400000L
                                if (sentDate > receivedDate - oneDayMillis && sentDate < receivedDate) {
                                    receivedDate = sentDate
                                }
                                //To ignore personal messages
                                if (senderAddress.length <= SmsSyncConstants.NON_PERSONAL_SENDER_LENGTH) {
                                    val smsData = SmsData(
                                        senderAddress,
                                        strBody,
                                        receivedDate,
                                    )
                                    smsList.add(smsData)
                                }
                            }
                        }
                    } while (cursor.moveToNext())
                } //Cursor will be automatically closed, since we are using .use{}
                /**
                 * Below line could have be inside the previous block also,
                 * but wanted to update the [SmsSyncConstants.PREF_KEY_LAST_SYNC_TIME] even in case of empty list
                 * so that we don't have to iterate over the timestamp again
                 */
                sendMessagesToServer(smsList)
            }
        } else {
            //Permission not granted
            analyticsApi.postEvent(
                SmsSyncConstants.EventKey.SMS_SYNC,
                values = mapOf(
                    SmsSyncConstants.EventKey.EVENT_TYPE_KEY to SmsSyncConstants.EventKey.EVENT_TYPE_FAILURE,
                    SmsSyncConstants.EventKey.FAILURE_REASON to "Permission not Granted"
                ),
            )
        }
    }

    @SuppressLint("ApplySharedPref")
    private suspend fun sendMessagesToServer(list: List<SmsData>) {
        if (list.isEmpty()) {
            //update the time stamp
            pref.edit().putLong(SmsSyncConstants.PREF_KEY_LAST_SYNC_TIME, triggerTimeStamp).commit()
            return
        }
        analyticsApi.postEvent(
            SmsSyncConstants.EventKey.SMS_SYNC,
            values = mapOf(
                SmsSyncConstants.EventKey.EVENT_TYPE_KEY to SmsSyncConstants.EventKey.EVENT_TYPE_STARTED
            )
        )
        smsToServerUseCase.sendSmsToServer(SmsSyncRequest(list)).collect {
            if (it.status == RestClientResult.Status.SUCCESS) {
                pref.edit().putLong(SmsSyncConstants.PREF_KEY_LAST_SYNC_TIME, triggerTimeStamp).commit()
                analyticsApi.postEvent(
                    SmsSyncConstants.EventKey.SMS_SYNC,
                    values = mapOf(
                        SmsSyncConstants.EventKey.EVENT_TYPE_KEY to SmsSyncConstants.EventKey.EVENT_TYPE_SUCCESS
                    )
                )
            } else if (it.status == RestClientResult.Status.ERROR) {
                analyticsApi.postEvent(
                    SmsSyncConstants.EventKey.SMS_SYNC,
                    values = mapOf(
                        SmsSyncConstants.EventKey.EVENT_TYPE_KEY to SmsSyncConstants.EventKey.EVENT_TYPE_FAILURE,
                        SmsSyncConstants.EventKey.FAILURE_REASON to (it.data?.errorMessage ?: "API Failure"),
                    )
                )
            }
        }
    }

    private fun getTimeStampFromDays(days: Int): Long {
        val calendar = Calendar.getInstance()
        //Reduce number of days
        calendar.add(Calendar.DAY_OF_MONTH, days * -1)
        // reset hour, minutes, seconds and millis, MIDNIGHT
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    companion object {
        private const val ADDRESS_PROJECTION = "address"
        private const val BODY_PROJECTION = "body"
        private const val DATE_PROJECTION = "date"
        private const val DATE_SENT_PROJECTION = "date_sent"

        private const val SMS_URI_SCHEME = "content://sms/inbox"
    }
}