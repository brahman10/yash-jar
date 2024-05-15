package com.jar.app.feature_vasooli.impl.domain.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jar.app.feature_vasooli.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class VasooliConfirmation(
    @StringRes
    val title: Int,

    @DrawableRes
    val iconRes: Int?,

    @StringRes
    val actionText: Int,

    @StringRes
    val dismissText: Int,

    val showPaymentMode: Boolean,
): Parcelable {
    MARK_AS_PAID(
        R.string.feature_vasooli_mark_as_paid_prompt,
        R.drawable.feature_vasooli_mark_as_paid,
        R.string.feature_vasooli_yes_paise_vasool,
        R.string.feature_vasooli_nahi_yaar,
        true
    ),
    MARK_DEFAULT(
        R.string.feature_vasooli_mark_as_default_prompt,
        null,
        R.string.feature_vasooli_yes,
        R.string.feature_vasooli_dismiss,
        false
    ),
    DELETE_RECORD(
        R.string.feature_vasooli_delete_prompt,
        null,
        R.string.feature_vasooli_yes,
        R.string.feature_vasooli_dismiss,
        false
    )
}