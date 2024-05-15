package com.jar.app.feature_gold_delivery.impl.ui

import androidx.annotation.ColorRes

@kotlinx.serialization.Serializable
enum class GoldDeliveryTransactionStatus(@ColorRes val color: Int) {
    PROCESSING(com.jar.app.core_ui.R.color.color_ebb46a),
    IN_PROCESS(com.jar.app.core_ui.R.color.color_ebb46a),
    DEFAULT(com.jar.app.core_ui.R.color.color_ebb46a),
    SUCCESS(com.jar.app.core_ui.R.color.color_58DDC8),
    COMPLETED(com.jar.app.core_ui.R.color.color_58DDC8),
    INVESTED(com.jar.app.core_ui.R.color.color_58DDC8),
    SCHEDULED(com.jar.app.core_ui.R.color.color_789BDE),
    REVERSED(com.jar.app.core_ui.R.color.color_789BDE),
    INACTIVE(com.jar.app.core_ui.R.color.color_789BDE),
    INITIATED(com.jar.app.core_ui.R.color.color_ACA1D3),
    FAILED(com.jar.app.core_ui.R.color.color_FF4D52),
    FAILURE(com.jar.app.core_ui.R.color.color_FF4D52),
    SENT(com.jar.app.core_ui.R.color.color_ebb46a),
    PENDING(com.jar.app.core_ui.R.color.color_ebb46a),
    DETECTED(com.jar.app.core_ui.R.color.color_ACA1D3),
}