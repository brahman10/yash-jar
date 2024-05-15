package com.jar.app.feature_calculator.impl.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_ui.R


@Composable
fun ToolbarWithHelpButton(
    title: String,
    onBackButtonClick: () -> Unit,
    onHelpButtonClick: () -> Unit,
    helpButtonText: String,
    haveIcon: Boolean = true,
    shouldShowTitle: Boolean = true,
    shouldShowBackArrow: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
                .background(color = colorResource(id = com.jar.app.core_ui.R.color.bgColor))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (shouldShowBackArrow) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .debounceClickable { onBackButtonClick() },

                    painter = painterResource(id = R.drawable.ic_arrow_back_small),
                    contentDescription = "Back Button",
                    contentScale = ContentScale.Inside
                )
            }

            if (shouldShowTitle) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    text = title,
                    fontSize = 14.sp,
                    fontFamily = jarFontFamily,
                    fontWeight = FontWeight(700),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    color = colorResource(id = R.color.white)
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
            }

            OutlinedButton(
                text = helpButtonText,
                onHelpButtonClick = { onHelpButtonClick() },
                icon = painterResource(id = R.drawable.ic_whatsapp_help_support_12dp),
                haveIcon = haveIcon
            )

        }
        Divider(
            color = colorResource(id = R.color.color_3C3357), thickness = 1.dp
        )
    }
}

@Composable
fun OutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter,
    haveIcon: Boolean = true,
    onHelpButtonClick: () -> Unit
) {
    Row(
        modifier = modifier
            .border(
                1.dp,
                colorResource(id = com.jar.app.core_ui.R.color.color_776e94),
                shape = RoundedCornerShape(15)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .debounceClickable { onHelpButtonClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (haveIcon) {
            Image(
                modifier = Modifier
                    .size(12.dp),
                painter = icon,
                colorFilter = ColorFilter.tint(JarColors.color_EEEAFF),
                contentDescription = "Help"
            )
        }

        Text(
            text = text, modifier = Modifier
                .wrapContentSize()
                .padding(start = 8.dp),
            fontSize = 12.sp,
            fontFamily = jarFontFamily,
            color = JarColors.color_EEEAFF,
            textAlign = TextAlign.Center
        )
    }
}