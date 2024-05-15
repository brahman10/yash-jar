package com.jar.app.feature_contacts_sync_common.impl

import android.net.Uri
import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.hasContactPermission
import com.jar.app.feature_contacts_sync_common.api.ContactsSyncApi
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import dagger.Lazy
import javax.inject.Inject

internal class ContactsSyncApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
) : BaseNavigation, ContactsSyncApi {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun initiateContactsSyncFlow(
        featureType: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType,
        infoDeeplink: String,
        shouldAnimate: Boolean,
        @IdRes popUpToId: Int?,
    ) {
        val deeplink = if (infoDeeplink.isBlank()) {
            "android-app://com.jar.app/contactsSync/${featureType.name}/"
        } else {
            "android-app://com.jar.app/contactsSync/${featureType.name}/${encodeUrl(infoDeeplink)}/"
        }
        if (navController.context.hasContactPermission()) {
            navController.navigate(Uri.parse(deeplink))
        } else {
            openPermissionFlow(featureType, deeplink)
        }
    }

    override fun openPermissionFlow(
        featureType: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType,
        deeplink: String?,
    ) {
        if (deeplink.isNullOrBlank()) {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/allowContactPermission/${featureType.name}/"),
            )
        } else {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/allowContactPermission/${featureType.name}/${encodeUrl(deeplink)}/"),
            )
        }

    }
}