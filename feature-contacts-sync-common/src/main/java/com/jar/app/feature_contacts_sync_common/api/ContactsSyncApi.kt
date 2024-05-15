package com.jar.app.feature_contacts_sync_common.api

import androidx.annotation.IdRes
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType

//Exposed to outer world to trigger the contacts sync flow
interface ContactsSyncApi {
    fun initiateContactsSyncFlow(
        featureType: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType,
        infoDeeplink: String,
        shouldAnimate: Boolean = false,
        @IdRes popUpToId: Int? = null
    )

    fun openPermissionFlow(
        featureType: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType,
        deeplink: String? = null
    )
}