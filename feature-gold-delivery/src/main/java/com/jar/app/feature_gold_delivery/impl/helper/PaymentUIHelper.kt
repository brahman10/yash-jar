package com.jar.app.feature_gold_delivery.impl.helper

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.jar.app.feature_gold_delivery.R
import java.lang.ref.WeakReference

internal object PaymentUIHelper {

    fun setIconStatus(
        status: com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus,
        view: WeakReference<AppCompatImageView>
    ) {
        val imageView = view.get()
        when (status) {
            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> {
                imageView?.setImageResource(R.drawable.icon_check_filled)
                imageView?.colorFilter = null
            }

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING -> {
                imageView?.setImageResource(R.drawable.alert_warning_padded)
                imageView?.colorFilter = null
            }

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> {
                imageView?.setImageResource(R.drawable.feature_gold_delivery_ic_circle_close)
                imageView?.setColorFilter(
                    ContextCompat.getColor(
                        imageView.context,
                        com.jar.app.core_ui.R.color.color_EB6A6E
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    fun setPillStatus(
        status: com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus,
        view: WeakReference<AppCompatTextView>
    ) {
        val textView = view.get()
        val context = textView?.context
        context ?: return

        when (status) {
            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> {
                textView.background =
                    ContextCompat.getDrawable(textView.context, R.drawable.round_273442_bg_8dp)
                textView.setTextColor(
                    ContextCompat.getColor(
                        textView.context,
                        com.jar.app.core_ui.R.color.color_58DDC8
                    )
                )
                textView.text = context.getString(R.string.success)
            }

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING -> {
                textView.background =
                    ContextCompat.getDrawable(textView.context, R.drawable.round_1aebb46a_bg_8dp)
                textView.setTextColor(
                    ContextCompat.getColor(
                        textView.context,
                        com.jar.app.core_ui.R.color.color_ebb46a
                    )
                )
                textView.text = context.getString(R.string.in_progress)
            }

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> {
                textView.background =
                    ContextCompat.getDrawable(textView.context, R.drawable.round_light_round_bg_8dp)
                textView.setTextColor(
                    ContextCompat.getColor(
                        textView.context,
                        com.jar.app.core_ui.R.color.color_EB6A6E
                    )
                )
                textView.text = context.getString(R.string.failed)
            }
        }
    }
}