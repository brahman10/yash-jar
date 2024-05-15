package com.jar.health_insurance.impl.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.ImageWithBadge
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.PaymentStatus


@Composable
fun PaymentInfoCard(
    modifier: Modifier = Modifier, content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .background(
                color = Color(0xFF2E2942), shape = RoundedCornerShape(size = 10.dp)
            )
    ) {
        content()
    }
}

@Composable
fun PaymentStatusDescriptionCard(
    modifier: Modifier = Modifier,
    icon: String? = null,
    statusIcon: String? = null,
    header: String? = null,
    amount: String? = null,
    date: String? = null,
    status: String? = null,
    statusText: String? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let { insuranceUrl ->
            statusIcon?.let { statusUrl ->
                ImageWithBadge(imageUrl = insuranceUrl, badgeUrl = statusUrl,
                    imageBackgroundColor = com.jar.app.core_ui.R.color.color_4F466B,
                    badgeBackgroundColor = com.jar.app.core_ui.R.color.color_3E3856
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically),
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = header.orEmpty(),
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = amount.orEmpty(),
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.End,
                )
            }
            Spacer(
                modifier = Modifier
                    .height(5.dp)
                    .background(Color.Red)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = statusText.orEmpty(),
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = colorFromPaymentStatus(paymentStatus = status),
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = date.orEmpty(),
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                    textAlign = TextAlign.End
                )
            }
        }
    }

}

@Composable
fun PaymentPlanDescriptionCard(
    modifier: Modifier = Modifier,
    headerIcon: String? = null,
    statusIcon: String? = null,
    headerLabelText: String? = null,
    headerValueText: String? = null,
    subHeaderLabelText: String? = null,
    subHeaderValueText: String? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        headerIcon?.let { insuranceUrl ->
            statusIcon?.let { statusUrl ->
                ImageWithBadge(imageUrl = insuranceUrl, badgeUrl = statusUrl,
                    imageBackgroundColor = com.jar.app.core_ui.R.color.color_3C3357,
                    badgeBackgroundColor = com.jar.app.core_ui.R.color.color_2E2942
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically),
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (headerLabelText != null) {
                    Text(
                        text = headerLabelText,
                        modifier = Modifier.weight(1f),
                        style = JarTypography.body2.copy(fontWeight = FontWeight.Bold, color = Color.White),
                        textAlign = TextAlign.Start,
                    )
                }
                if (headerValueText != null) {
                    Text(
                        text = headerValueText,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.End,
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .height(5.dp)
                    .background(Color.Red)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (subHeaderLabelText != null) {
                    Text(
                        text = subHeaderLabelText,
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        textAlign = TextAlign.Start,
                    )
                }
                if (subHeaderValueText != null) {
                    Text(
                        text = subHeaderValueText,
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }

}

@Composable
internal fun colorFromPaymentStatus(paymentStatus:String?): Color = when {
    PaymentStatus.PENDING.name.equals(paymentStatus, true) || PaymentStatus.INITIATED.name.equals(paymentStatus, true) -> colorResource(id = com.jar.app.core_ui.R.color.color_EBB46A)
    PaymentStatus.FAILURE.name.equals(paymentStatus,true) -> colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E)
    PaymentStatus.SUCCESS.name.equals(paymentStatus,true) -> colorResource(id = com.jar.app.core_ui.R.color.color_1EA787)
    else -> colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
}

@Preview
@Composable
fun PreviewPaymentStatusDescriptionCard() {
    PaymentStatusDescriptionCard(
        modifier = Modifier,
        icon = null,
        statusIcon = null,
        header = "Monthly Premium",
        amount = "₹2,800",
        date = "3 May’22, 3:43pm",
        status = "Pending",
        statusText = "In Progress"
    )
}

@Preview
@Composable
fun PreviewPaymentPlanDescriptionCard() {
    PaymentPlanDescriptionCard(
        modifier = Modifier,
        headerIcon = null,
        statusIcon = null,
        headerLabelText = "Premium Amount",
        headerValueText = "₹2,800",
        subHeaderLabelText = "For March, 2023 ",
        subHeaderValueText = null,
        )
}


