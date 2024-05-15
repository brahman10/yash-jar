package com.jar.app.feature_jar_duo.api

interface DuoApi {

    fun openDuoFeature(fromScreen: String, hasContactPermission: Boolean)

    fun openInvitationPopupIfPendingInvites()

    fun openDuoContactList(orZero: Int, size: Int)

    fun openPendingInviteList()

    fun openDuoIntroStory(fromScreen: String,pendingInvites:Int,duoGroups:Int,hasContactSynced: Boolean)

    fun openDuoList()


}