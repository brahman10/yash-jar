package com.jar.app.feature_contact_sync_common.shared.utils

object ContactsSyncConstants {
    const val SOURCE_SENT_INVITE = "SOURCE_SENT_INVITE"
    internal object Endpoints {
        const val FETCH_CONTACT_LIST = "v1/api/contacts/list"
        const val FETCH_CONTACT_LIST_STATIC_DATA = "v1/api/contacts/list/static"
        const val FETCH_CONTACT_PROCESSING_STATUS = "v1/api/contacts/processing/check"
        const val PROCESS_INVITE = "v1/api/duo/invite/process"
        const val FETCH_PENDING_INVITES = "v1/api/contacts/pending/invitesSent/list"
        const val FETCH_SENT_INVITES = "v1/api/contacts/pending/invitesSent/list"
        const val ADD_CONTACTS = "v1/api/contacts/add"
        const val SEND_DUO_INVITE = "v1/api/contacts/invite/send"
        const val SEND_DUO_REMINDER = "v1/api/contacts/remind"
        const val SEND_MULTIPLE_INVITES = "v1/api/duo/invite/multiple/send"
    }

    object AnalyticKeys {
        const val Refresh_contacts: String = "Refresh_contacts"
        const val Clear_search_text: String = "Clear_search_text"
        const val ErrorMessage = "ErrorMessage"
        const val Cancelled = "Cancelled"
        const val SCREEN = "Screen"

        const val Cancel_sync="Cancel_Sync"
        const val CTA = "CTA"
        const val invitetype = "invitetype"
        const val SECTION = "Section"
        const val Key = "Key"


        const val duo_list = "duo list"
        const val Contact_list = "Contact_list"
        const val rename = "Rename"
        const val delete = "Delete"

        const val share_invite = "share invite"
        const val back = "back"
        const val Button = "Button"
        const val FromScreen = "FromScreen"
        const val SCREEN_CONTACT_ACCESS = "Screen=Contact access"
        const val SCREEN_CONTACT_SYNC_SUCCESS = "Screen= Contacts sync success"
        const val Skip = "Skip"
        const val Proceed = "Proceed"
        const val Replay = "Replay intro"
        const val ALLOW_ACCESS = "Allow access"
        const val ILL_DO_IT_LATER = "I’ll Do later"
        const val SINGLE_INVITE = "Single Invite"
        const val MULTIPLE_INVITE = "Multiple Invite"
        const val Sent_Invites = "sent Invites"
        const val Back_button_clicked = "Back_button_clicked"
        const val Remind = "Remind"
        const val SELECT_ALL = "Select All"
        const val VIEW_ALL = "View All"
        const val Sync_contacts = "Sync contacts"
        const val Invite_friends = "Invite friends"
        const val Rename = "Rename"
        const val Delete = "Delete"
        const val Open = "Open"
        const val Top_banner = "Top banner"
        const val searchaction = "searchaction"
        const val Feature_Type = "Feature_Type"
        const val Contact_Permission_Given = "Contact_Permission_Given"
        const val Permission_screen = "Permission_screen"
        const val screenstatus = "screenstatus"
        const val Sent_Invites_clicked = "Sent_Invites_clicked"

        const val ContactsScreenShown = "Contacts_ScreenShown"
        const val ContactsScreenClicked = "Contacts_ScreenClicked"
        const val ContactsInviteClicked = ContactsScreenClicked
        const val SentInviteScreenLaunched = ContactsScreenShown
        const val SentInviteRemindClicked = ContactsScreenClicked

        const val InviteContactsScreenShown = ContactsScreenShown
        const val InviteContactsScreenAllowAccessClicked = ContactsScreenClicked
        const val InviteContactsStatusScreenShown = ContactsScreenShown
    }
}