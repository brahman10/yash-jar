package com.jar.app.feature_calculator.impl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_calculator.shared.domain.model.CalculatorCardData

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CalculationDetailCard(
    modifier: Modifier,
    cardData: CalculatorCardData,
    field1Value: String,
    field2Value: String,
    field3Value: String,
    field4Value: String,
    onButtonClick: (deeplink: String) -> Unit,
) {
    Column(
        modifier = modifier.then(
            other = Modifier
                .background(
                    color = Color(android.graphics.Color.parseColor(cardData.backgroundColor)),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(
                    horizontal = 16.dp, vertical = 24.dp
                )
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cardData.title1,
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontFamily = jarFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFFFFFFF)
                    )
                )

                Row(modifier = Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹",
                        style = TextStyle(
                            fontSize = 32.sp,
                            lineHeight = 36.sp,
                            fontFamily = jarFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = JarColors.color_white_80,
                        ),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = field1Value,
                        style = TextStyle(
                            fontSize = 32.sp,
                            lineHeight = 36.sp,
                            fontFamily = jarFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            GlideImage(
                modifier = Modifier
                    .size(width = 92.dp, height = 56.dp),
                model = cardData.imageUrl,
                contentScale = ContentScale.FillBounds,
                contentDescription = ""
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = JarColors.color_4DFFFFFF)

        Row(modifier = Modifier.fillMaxWidth()) {
            KeyValueDetails(modifier = Modifier.weight(1f), key = cardData.title2, value = field2Value)
            Spacer(modifier = Modifier.width(16.dp))

            KeyValueDetails(modifier = Modifier.weight(1f), key = cardData.title3, value = field3Value)
            Spacer(modifier = Modifier.width(16.dp))

            KeyValueDetails(modifier = Modifier.weight(1f), key = cardData.title4, value = field4Value)
        }

        JarPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = cardData.buttonText,
            onClick = { onButtonClick(cardData.deepLink) },
            color = Color(android.graphics.Color.parseColor(cardData.buttonBackgroundColor)),
            textColor = Color(android.graphics.Color.parseColor(cardData.buttonTextColor)),
            isAllCaps = false,
            borderBrush = null
        )
    }
}