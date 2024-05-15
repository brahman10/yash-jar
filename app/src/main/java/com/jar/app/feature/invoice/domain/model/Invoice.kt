package com.jar.app.feature.invoice.domain.model

import android.content.Context
import androidx.core.content.ContextCompat
import com.jar.app.R
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Invoice(
    @SerialName("invoiceId")
    val invoiceId: String,
    @SerialName("transactionType")
    val transactionType: String,
    @SerialName("invoiceLink")
    val invoiceLink: String,
    @SerialName("date")
    val date: Long,
    @SerialName("amount")
    val amount: Double
) {
    fun getColorForInvoiceType(context: Context): Int {
        val color = InvoiceType.valueOf(transactionType.uppercase()).color
        return ContextCompat.getColor(context, color)
    }
}

data class InvoiceResp(
    @SerialName("invoices")
    val invoice: List<Invoice>
)

enum class InvoiceType(val color: Int) {
    BUY(com.jar.app.core_ui.R.color.color_58DDC8),
    SELL(com.jar.app.core_ui.R.color.color_EA5252),
    DELIVERY(com.jar.app.core_ui.R.color.color_ebb46a),
    GIFTING(com.jar.app.core_ui.R.color.color_EEEAFF)
}