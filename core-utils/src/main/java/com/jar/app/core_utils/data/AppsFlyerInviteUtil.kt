package com.jar.app.core_utils.data

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.appsflyer.share.LinkGenerator
import com.appsflyer.share.ShareInviteHelper
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_utils.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppsFlyerInviteUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: PrefsApi
) {
    private val appsFlyerAppInviteListener = object : LinkGenerator.ResponseListener {
        override fun onResponse(p0: String?) {
            if (!p0.isNullOrEmpty()) {
                val now = System.currentTimeMillis()
                prefs.setUserInviteLink(p0)
                prefs.setInviteLinkExpiry(now + (1000 * 60 * 60 * 24 * 20))     //20 days in millis
            }
        }

        override fun onResponseError(p0: String?) {
            prefs.setInviteLinkExpiry(0)
        }
    }

    fun getAppsFlyerInviteLink(): String? = prefs.getUserInviteLink()

    private fun generateAppsFlyerLink(
        userId: String,
        deepLink: String,
        listener: LinkGenerator.ResponseListener
    ) {
        AppsFlyerLib.getInstance().setAppInviteOneLink(BuildConfig.APPS_FLYER_TEMPLATE_ID)
        val linkGenerator = ShareInviteHelper.generateInviteUrl(context)
        linkGenerator.addParameter("deep_link_value", deepLink)
        linkGenerator.addParameter("deep_link_sub1", userId)
        linkGenerator.addParameter("is_retargeting", "false")
        linkGenerator.addParameter("pid", deepLink)
        // Optional; makes the referrer ID available in the installs raw-data report
        linkGenerator.addParameter("af_sub1", userId)
        linkGenerator.generateLink(context, listener)
    }

    fun initAppsFlyerInviteLink(userId: String) {
        if (shouldReGenerateInviteLink()) {
            generateAppsFlyerLink(userId, "referrals", appsFlyerAppInviteListener)
        }
    }
    private fun shouldReGenerateInviteLink(): Boolean {
        val now = System.currentTimeMillis()
        return if (prefs.getInviteLinkExpiry() == 0L)
            true
        else
            prefs.getInviteLinkExpiry() < now
    }
}