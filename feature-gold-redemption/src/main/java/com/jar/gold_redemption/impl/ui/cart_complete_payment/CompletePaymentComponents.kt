@file:OptIn(ExperimentalGlideComposeApi::class)
package com.jar.gold_redemption.impl.ui.cart_complete_payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.GradientSeperator
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.core_compose_ui.views.LabelValueComposeView
import com.jar.app.core_compose_ui.views.RenderDashedLine
import com.jar.app.core_compose_ui.views.SingleExpandableCard
import com.jar.app.core_ui.R

@Composable
@Preview
fun RenderBottomCardPreview() {
    RenderBottomCard()
}
@Composable
fun RenderBottomCard() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            .padding(horizontal = 12.dp)
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_3c3357),
                shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
            )
            .height(30.dp)
    ) {

    }
}


@Composable
@Preview
fun RenderHelpSupportSectionPreview() {
    RenderHelpSupportSection {

    }
}
@Composable
fun RenderHelpSupportSection(function: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.color_272239)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(24.dp))
        GradientSeperator(Modifier)
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(24.dp))
        Text(
            text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_need_help),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            style = JarTypography.h6,
            color = colorResource(id = R.color.white),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.clickable {
                function()
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_whatsapp),
                tint = Color.Unspecified,
                contentDescription = ""
            )
            Text(
                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_contact_support),
                modifier = Modifier,
                style = JarTypography.body1.copy(textDecoration = TextDecoration.Underline),
                color = colorResource(id = R.color.color_EEEAFF),
                textAlign = TextAlign.Center
            )
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_right_chevron_bold),
                tint = colorResource(id = R.color.color_EEEAFF),
                contentDescription = ""
            )
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(30.dp))
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun RenderRowWithDashedLine(modifier: Modifier = Modifier) {
    val color: Color = colorResource(id = com.jar.app.core_ui.R.color.color_272239)
    val boxSize: Dp = 20.dp
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.semi_circle_fixe),
            "",
            modifier = Modifier
                .zIndex(10f)
                .height(boxSize)
                .width(boxSize/2),
            alignment = Alignment.CenterStart,
            colorFilter = ColorFilter.tint(color)
        )
        RenderDashedLine(
            Modifier
                .weight(1f))
        Image(
            painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.semi_circle_fixe),
            "",
            modifier = Modifier
                .zIndex(10f)
                .height(boxSize)
                .width(boxSize/2)
                .rotate(180f),
            alignment = Alignment.CenterEnd,
            colorFilter = ColorFilter.tint(color),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun RenderRefundDetails(defaultModifier: Modifier, list: List<LabelAndValueCompose>, isExpanded: MutableState<Boolean>) {
    Card(modifier = defaultModifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),) {
        SingleExpandableCard(isExpanded, {
            Text(
                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_refund_details),
                style = JarTypography.body1,
                modifier = Modifier.padding(16.dp),
                color = colorResource(id = R.color.color_ACA1D3)
            )
        }) {
            LabelValueComposeView(modifier = Modifier.padding(horizontal = 16.dp), list = list)
        }
    }
}

@Composable
@Preview
fun RenderValueAdapterCardPReview() {
    RenderValueAdapterCard(Modifier, listOf(
        LabelAndValueCompose("Label", "Value"),
        LabelAndValueCompose("Label", "Value"),
        LabelAndValueCompose("Label", "Value"),
    ))
}
@Composable
fun RenderValueAdapterCard(modifier: Modifier = Modifier, value: List<LabelAndValueCompose>) {
    Card(
        modifier
            .padding(horizontal = 16.dp)
            .border(
                width = 0.01.dp, brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0x4DFFFFFF),
                        1f to Color(0x00FFFFFF)
                    ),
                    tileMode = TileMode.Mirror
                ), shape = RoundedCornerShape(20.dp)
            ),
        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_352F4F),
        shape = RoundedCornerShape(20.dp)
    ) {
        LabelValueComposeView(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 2.dp),
            list = value
        )
    }
}