package com.jar.app.core_utils.data

import android.content.Context
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.isPackageInstalled
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhatsAppUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getWhatsappPackageName(): String? {
        return when {
            context.isPackageInstalled(BaseConstants.WHATSAPP_REGULAR_PACKAGE_NAME) -> BaseConstants.WHATSAPP_REGULAR_PACKAGE_NAME
            context.isPackageInstalled(BaseConstants.WHATSAPP_BUSINESS_PACKAGE_NAME) -> BaseConstants.WHATSAPP_BUSINESS_PACKAGE_NAME
            else -> null
        }
    }
}