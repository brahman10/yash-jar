package com.jar.app.feature_transactions_common.shared

import com.jar.app.core_base.shared.CoreBaseMR
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.ImageResource

enum class CommonTransactionStatus {
    PROCESSING,
    IN_PROCESS,
    DEFAULT,
    SUCCESS,
    COMPLETED,
    INVESTED,
    SCHEDULED,
    REVERSED,
    INACTIVE,
    INITIATED,
    FAILED,
    FAILURE,
    SENT,
    PENDING,
    DETECTED,
    EXPIRED;

    companion object {
        fun getValueForString(status: String?): CommonTransactionStatus {
            val transactionStatus = status?.uppercase()
            return CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: DEFAULT
        }

    }

    fun getIcon(): ImageResource {
        return when (this) {
            PROCESSING -> CommonTransactionMR.images.ic_pending_svg
            IN_PROCESS -> CommonTransactionMR.images.ic_pending_svg
            DEFAULT -> CommonTransactionMR.images.ic_pending_svg
            SUCCESS -> CommonTransactionMR.images.ic_tick_svg
            COMPLETED -> CommonTransactionMR.images.ic_tick_svg
            INVESTED -> CommonTransactionMR.images.ic_tick_svg
            SCHEDULED -> CommonTransactionMR.images.ic_pending_svg
            REVERSED -> CommonTransactionMR.images.ic_reversed_svg
            INACTIVE -> CommonTransactionMR.images.ic_transaction_inactive
            INITIATED -> CommonTransactionMR.images.ic_pending_svg
            FAILED -> CommonTransactionMR.images.ic_failure_svg
            FAILURE -> CommonTransactionMR.images.ic_failure_svg
            SENT -> CommonTransactionMR.images.ic_tick_svg
            PENDING -> CommonTransactionMR.images.ic_pending_svg
            DETECTED -> CommonTransactionMR.images.ic_pending_svg
            EXPIRED -> CommonTransactionMR.images.ic_failure_svg
        }
    }

    fun getColor(): ColorResource {
        return when (this) {
            PROCESSING -> CoreBaseMR.colors.color_EBB46A
            IN_PROCESS -> CoreBaseMR.colors.color_EBB46A
            DEFAULT -> CoreBaseMR.colors.color_EBB46A
            SUCCESS -> CoreBaseMR.colors.color_58DDC8
            COMPLETED -> CoreBaseMR.colors.color_58DDC8
            INVESTED -> CoreBaseMR.colors.color_58DDC8
            SCHEDULED -> CoreBaseMR.colors.color_789BDE
            REVERSED -> CoreBaseMR.colors.color_789BDE
            INACTIVE -> CoreBaseMR.colors.color_4D436A
            INITIATED -> CoreBaseMR.colors.color_ACA1D3FF
            FAILED -> CoreBaseMR.colors.color_FF4D52
            FAILURE -> CoreBaseMR.colors.color_FF4D52
            SENT -> CoreBaseMR.colors.color_EBB46A
            PENDING -> CoreBaseMR.colors.color_EBB46A
            DETECTED -> CoreBaseMR.colors.color_ACA1D3FF
            EXPIRED -> CoreBaseMR.colors.color_FF4D52
        }
    }

    fun getTitleColor(): ColorResource {
        return when (this) {
            INACTIVE -> CoreBaseMR.colors.color_776E94
            else -> CoreBaseMR.colors.color_FFFFFF
        }
    }

    fun getDescriptionColor(): ColorResource {
        return when (this) {
            INACTIVE -> CoreBaseMR.colors.color_776E94
            PROCESSING, IN_PROCESS, PENDING -> CoreBaseMR.colors.color_EBB46A
            FAILED, FAILURE -> CoreBaseMR.colors.color_EB6A6E
            else -> CoreBaseMR.colors.color_FFFFFF
        }
    }
}