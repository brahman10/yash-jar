package com.jar.app.feature_sell_gold.impl.ui.amount.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_4B4366
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_EEEAFF
import com.jar.app.core_compose_ui.component.ICON_GRAVITY_END
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography

@Composable
fun PendingIdVerificationCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    ctaText: String?,
    ctaIcon: String?,
    onCtaClick: () -> Unit,
    verificationStatusState: VerificationStatusState
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colorResource(id = color_4B4366.resourceId),
                shape = RoundedCornerShape(size = 12.dp)
            )
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = JarTypography.body2.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            VerificationStatusLabel(verificationStatusState)
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = description,
            style = JarTypography.caption,
            color = colorResource(id = color_EEEAFF.resourceId)
        )
        if (ctaText != null && ctaIcon != null) {
            Spacer(modifier = Modifier.size(12.dp))
            JarPrimaryButton(
                modifier = Modifier,
                text = ctaText,
                fontSize = 12.sp,
                fontWeight = FontWeight.W600,
                onClick = onCtaClick,
                icon = ctaIcon,
                isAllCaps = false,
                iconSize = DpSize(12.dp, 12.dp),
                iconGravity = ICON_GRAVITY_END,
                minHeight = 36.dp
            )
        }
    }
}