package com.myjar.app.feature_exit_survey.impl.util

import android.app.Activity
import android.content.Intent
import android.net.Uri

fun Activity.openDialerWithPhoneNumber(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
    startActivity(intent)
}