@file:OptIn(ExperimentalGlideComposeApi::class)

package com.jar.health_insurance.impl.ui.manage_insurance_screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.convertToAnnotatedString
import com.jar.app.core_ui.R
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.NotificationCard

@Composable
fun NotificationView(
    modifier: Modifier = Modifier,
    notification: NotificationCard
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
        JarImage(
            modifier = Modifier.size(24.dp),
            imageUrl = notification.icon,
            contentDescription = null
        )



        Text(
            modifier = Modifier
                .padding(start = 9.dp)
                .weight(1f),
            text = convertToAnnotatedString(notification.text, separator = " "),
            style = JarTypography.body2.copy(
                color = colorResource(id = R.color.color_EBB46A),
                textAlign = TextAlign.Start,
                lineHeight = 18.sp
            )
        )
    }
}
