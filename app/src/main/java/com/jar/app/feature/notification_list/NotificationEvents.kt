package com.jar.app.feature.notification_list

object NotificationEvents {

    const val State = "state"
    const val Count = "count"
    const val FromScreen = "fromScreen"
    const val TimeSpent = "timeSpent"
    const val Category = "category"
    const val Trigger = "trigger"
    const val ClickedNotifications_Homescreen ="ClickedNotifications_Homescreen"
    const val Shown_NotificationScreen ="Shown_NotificationScreen"
    const val Clicked_BackButton_NotificationScreen ="Clicked_BackButton_NotificationScreen"
    const val Clicked_Notification_NotificationScreen ="Clicked_Notification_NotificationScreen"

    object NotificationState{
        const val Read = "read"
        const val UnRead = "Unread"
    }
}