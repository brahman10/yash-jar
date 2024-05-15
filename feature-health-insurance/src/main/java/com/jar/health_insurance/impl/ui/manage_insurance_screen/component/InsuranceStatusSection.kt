package com.jar.health_insurance.impl.ui.manage_insurance_screen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.ButtonType
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceCTA
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceStatusDetails
import com.jar.app.core_ui.R.color as uiColor


@Composable
@ExperimentalGlideComposeApi
fun InsuranceStatusSection(
    modifier: Modifier = Modifier,
    insuranceStatusDetails: InsuranceStatusDetails,
    onButtonClick: (InsuranceCTA) -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        JarImage(
            modifier = Modifier.size(45.dp),
            imageUrl = insuranceStatusDetails.icon,
            contentDescription = "status icon"
        )

        insuranceStatusDetails.header?.let {
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = it,
                style = JarTypography.h5.copy(color = colorResource(id = uiColor.white))
            )
        }
        Spacer(modifier = Modifier.height(if (insuranceStatusDetails.header == null) 16.dp else 8.dp))
        insuranceStatusDetails.description?.let {
            Text(
                text = it, style = JarTypography.body2.copy(
                    color = try {
                        Color(android.graphics.Color.parseColor(insuranceStatusDetails.descriptionColor))
                    } catch (e: Exception) {
                        colorResource(id = uiColor.color_ACA1D3)
                    }, textAlign = TextAlign.Center,
                    lineHeight = 18.sp

                )
            )
        }


        insuranceStatusDetails.cta?.forEach { cta ->
            JarButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                text = cta.text,
                onClick = { onButtonClick(cta) },
                buttonType = ButtonType.valueOf(cta.ctaType)
            )
        }
    }
}