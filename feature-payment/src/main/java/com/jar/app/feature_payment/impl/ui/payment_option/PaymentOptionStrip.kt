package com.jar.app.feature_payment.impl.ui.payment_option

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp

@Composable
fun PayNowSection(
    modifier: Modifier = Modifier,
    oneTimeUpiApp: UpiApp? = null,
    mandateUpiApp: com.jar.app.feature_mandate_payment_common.impl.model.UpiApp? = null,
    isMandate: Boolean = false,
    payNowCtaText: String?,
    appChooserText: String?,
    onAppChooserClicked: (Boolean) -> Unit,
    onPayNowClicked: (Boolean) -> Unit,
    isCtaEnabled: Boolean = true,
    isAppChooserCtaEnabled: Boolean = true,
    showPaymentSecureFooter: Boolean = false,
    bgColor: Int = com.jar.app.core_ui.R.color.bgColor,

) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier.background(color = colorResource(id = bgColor)).padding(16.dp)

    ) {
        Column(
            modifier = Modifier
                .weight(0.5f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.debounceClickable {
                    if (isAppChooserCtaEnabled) {
                        onAppChooserClicked(isMandate)
                    }
                }) {

                if (isMandate) {
                    val packageManager = LocalContext.current.packageManager

                    val drawable = mandateUpiApp?.packageName?.let {
                        packageManager.getApplicationIcon(
                            it
                        )
                    }

                    drawable?.toBitmap(config = Bitmap.Config.ARGB_8888)?.asImageBitmap()
                        ?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                } else {
                    val packageManager = LocalContext.current.packageManager

                    val drawable = oneTimeUpiApp?.packageName?.let {
                        packageManager.getApplicationIcon(
                            it
                        )
                    }

                    drawable?.toBitmap(config = Bitmap.Config.ARGB_8888)?.asImageBitmap()
                        ?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                }

                appChooserText?.let {
                    Text(
                        text = it,
                        style = JarTypography.body2.copy(fontSize = 12.sp, color = Color.White),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .alpha(if (isAppChooserCtaEnabled) 1f else 0.5f)
                    )
                }

                Spacer(modifier = Modifier.padding(start = 8.dp))

                Image(
                    painter = painterResource(id = com.jar.app.core_compose_ui.R.drawable.ic_top_arrow_white),
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.padding(top = 4.dp))

            oneTimeUpiApp?.appName?.let {
                Text(
                    text = it, style = JarTypography.body2.copy(
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                )
            }

            mandateUpiApp?.appName?.let {
                Text(
                    text = it, style = JarTypography.body2.copy(
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                )
            }
        }

        payNowCtaText?.let {
            JarButton(
                text = it,
                onClick = {
                    if (isCtaEnabled) {
                        onPayNowClicked(isMandate)
                    }
                },
                isAllCaps = false,
                modifier = Modifier.weight(0.5f),
                isEnabled = isCtaEnabled
            )
        }
    }

    if (showPaymentSecureFooter) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(12.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_npci),
                contentDescription = "",
                modifier = Modifier
                    .height(18.dp)
                    .width(55.dp)
            )
            Spacer(Modifier.width(24.dp))
            Image(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_secure_with_shield_green),
                contentDescription = "",
                modifier = Modifier
                    .height(24.dp)
                    .width(94.dp)
            )
        }
    }
}