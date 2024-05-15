package com.jar.app.feature_jar_duo.shared.domain.model

import com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData

@kotlinx.serialization.Serializable
data class DuoContactsMetaData(
    val totalContactsToBeInvited:Int?,
    val totalPendingInvites:List<PendingInviteData>?,
    val totalGroupCount:List<DuoGroupData>?,
    val userName:String?
)
