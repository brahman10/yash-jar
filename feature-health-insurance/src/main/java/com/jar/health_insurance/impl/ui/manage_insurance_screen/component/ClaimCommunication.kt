package com.jar.health_insurance.impl.ui.manage_insurance_screen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.ButtonType
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceCTA


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WayToCommunicateCtaItem(
    modifier: Modifier = Modifier,
    ctaData: InsuranceCTA?,
    label: String?,
    value: String?,
    labelIcon: String? = null,
    handleButtonClicked: (InsuranceCTA) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            labelIcon?.let { icon ->
                JarImage(
                    modifier = Modifier
                        .size(16.dp),
                    imageUrl = icon,
                    contentDescription = "labelIcon"
                )
            }
            Column(
                modifier = Modifier.padding(start = 12.dp)

            ) {
                label?.let {
                    Text(
                        text = it, style = JarTypography.h6.copy(
                            fontSize = 14.sp, color = Color.White
                        ),
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                    )
                }

                value?.let {
                    Text(
                        text = it,
                        style = JarTypography.body2.copy(
                            fontSize = 12.sp,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 25.dp)
                    )
                }
            }

        }

        ctaData?.let { cta ->
            JarButton(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxWidth(),
                text = cta.text,
                onClick = { handleButtonClicked(cta) },
                isAllCaps = false,
                buttonType = ButtonType.valueOf(cta.ctaType),
                minHeight = 36.dp,

                )

        }
    }
}
