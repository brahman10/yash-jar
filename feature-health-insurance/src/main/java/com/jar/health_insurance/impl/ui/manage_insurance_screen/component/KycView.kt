package com.jar.health_insurance.impl.ui.manage_insurance_screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.ButtonType
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.KycData

@Composable
fun KycView(
    modifier: Modifier = Modifier,
    onButtonClicked: (String) -> Unit = {},
    kycData: KycData
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.color_2e2942),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            kycData.header?.let {
                Text(
                    text = it,
                    style = JarTypography.body2.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            kycData.description?.let {
                Text(
                    text = it,
                    style = JarTypography.body2.copy(
                        color = colorResource(id = R.color.color_ACA1D3),
                        fontSize = 12.sp
                    )
                )
            }
        }
        kycData.cta?.let {
            JarButton(
                text = it.text,
                onClick = { onButtonClicked(it.link) },
                buttonType = ButtonType.valueOf(it.ctaType),
                minHeight = 36.dp
            )

        }
    }
}