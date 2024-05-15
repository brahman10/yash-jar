package com.jar.health_insurance.impl.ui.common.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.feature_health_insurance.R

@Composable
fun TopBarNeedHelpWhatsapp(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int = R.drawable.whatsapp_icon,
    iconLeftText: String
) {
    Card(
        backgroundColor = Color(0xFF2E2942),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 8.dp, start = 16.dp, end = 8.dp, bottom = 8.dp)
                    .size(20.dp)
            )

            Text(
                text = iconLeftText,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight(600),
                modifier = Modifier
                    .padding(end = 16.dp)
            )
        }
    }
}

