package com.jar.app.feature_vasooli.impl.domain

internal object VasooliEventKey {

    //Props
    const val Screen = "Screen"
    const val Button = "Button"
    const val FromScreen = "FromScreen"
    const val Skip = "Skip"
    const val Start = "Start"
    const val IsEmpty = "isEmpty"
    const val Yes = "Yes"
    const val No = "No"
    const val AddNew = "AddNew"
    const val Back = "Back"
    const val Card = "Card"
    const val PhoneNumber = "PhoneNumber"
    const val Name = "Name"
    const val Amount = "Amount"
    const val LoanDate = "LoanDate"
    const val DueDate = "DueDate"
    const val NewRepayment = "NewRepayment"
    const val SendReminder = "SendReminder"
    const val Edit = "Edit"
    const val EditDetails = "EditDetails"
    const val FullyPaid = "FullyPaid"
    const val Default = "Default"
    const val Delete = "Delete"
    const val Date = "Date"
    const val PaymentMode = "PaymentMode"
    const val Cancel = "Cancel"
    const val ChangeImage = "ChangeImage"
    const val Send = "Send"
    const val Medium = "vasooli_reminder_medium"
    const val Image = "Image"
    const val FullVasool = "FullVasool"

    //Screens
    const val LoanDetails = "LoanDetails"
    const val Vasooli_Home = " Vasooli_Home"
    const val Carousal = "Carousal"

    //Common Events
    const val Shown_Screen_Vasooli = "Shown_Screen_Vasooli"

    //Screen Specific Events
    object Intro {
        const val Clicked_CarousalScreen_Vasooli = "Clicked_CarousalScreen_Vasooli"
    }

    object VasooliHome {
        const val Clicked_Vasooli_Homescreen = "Clicked_Vasooli_Homescreen"
    }

    object VasooliEntry {
        const val Clicked_SaveNewLoan_Vasooli = "Clicked_SaveNewLoan_Vasooli"
        const val ClickedPermission_Vasooli = "ClickedPermission_Vasooli"
    }

    object VasooliDetails {
        const val Clicked_LoanDetailsScreen_Vasooli = "Clicked_LoanDetailsScreen_Vasooli"
        const val Clicked_EditLoanDetails_Vasooli = "Clicked_EditLoanDetails_Vasooli"
    }

    object VasooliRepayment {
        const val Clicked_SaveRepayment_Vasooli = "Clicked_SaveRepayment_Vasooli"
    }

    object VasooliReminder {
        const val Clicked_SendReminderScreen_Vasooli = "Clicked_SendReminderScreen_Vasooli"
    }

    object VasooliConfirmation {
        const val Clicked_FullyPaidScreen_Vasooli = "Clicked_FullyPaidScreen_Vasooli"
    }
}