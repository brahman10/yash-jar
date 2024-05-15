package com.jar.app.feature_jar_duo.impl.util

object DeeplinkUtils {
    fun generateStoryDeeplink(
        fromScreen: String,
        pendingInvites: String,
        duoGroups: String,
        hasContactSynced: String
    ): String {
        return "android-app://com.jar.app/duoIntroStory?fromScreen=$fromScreen&pendingInvites=${pendingInvites}&duoGroups=${duoGroups}&hasContactSynced=${hasContactSynced}"
    }
}