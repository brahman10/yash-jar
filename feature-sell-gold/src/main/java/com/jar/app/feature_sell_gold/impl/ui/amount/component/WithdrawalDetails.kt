@file:OptIn(ExperimentalGlideComposeApi::class)

package com.jar.app.feature_sell_gold.impl.ui.amount.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_272239
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_37314B
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_776E944D
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_ACA1D3FF
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.detectVerticalScroll
import com.jar.app.core_ui.R
import com.jar.app.feature_sell_gold.shared.domain.models.Drawer

@Composable
internal fun WithdrawalDetails(
    drawer: Drawer,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onToggleExpand: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggleExpand
            )
            .detectVerticalScroll(
                onScrollUp = { if (expanded) onToggleExpand() },
                onScrollDown = { if (!expanded) onToggleExpand() }
            )
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(colorResource(id = color_37314B.resourceId)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(color = colorResource(id = color_272239.resourceId))
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .clip(
                    shape = RoundedCornerShape(
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val drawerItems = drawer.drawerItems.sortedBy { it.priority }
            itemsIndexed(
                items = if (expanded) drawerItems else drawerItems.take(2)
            ) { index, drawerItem ->

                if (index == 2 || index == 4) {
                    Divider(
                        color = colorResource(id = color_776E944D.resourceId),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                ListItem(
                    modifier = Modifier.detectVerticalScroll(
                        onScrollUp = { if (expanded) onToggleExpand() },
                        onScrollDown = { if (!expanded) onToggleExpand() }
                    ),
                    iconUrl = drawerItem.iconLink,
                    text = drawerItem.keyText,
                    amount = drawerItem.amount.toFloatOrZero(),
                    goldWeight = drawerItem.volume,
                    unitPreference = drawerItem.unitPreference,
                    isAmountHighlighted = index in 0..1
                )
            }

            if (drawer.footerText.isNotBlank() && expanded) {
                item {
                    Divider(
                        modifier = Modifier.detectVerticalScroll(onScrollUp = onToggleExpand),
                        color = colorResource(id = color_776E944D.resourceId)
                    )
                }
                item {
                    VerificationRequiredItem(
                        modifier = Modifier.detectVerticalScroll(onScrollUp = onToggleExpand),
                        text = drawer.footerText
                    )
                }
            }
        }

        val arrowRotationAngle by animateFloatAsState(
            targetValue = if (expanded) 180f else 360f,
            label = "arrowRotationAngle"
        )

        val infiniteTransition = rememberInfiniteTransition(label = "")
        val pulseAnimation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        Image(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .clickable(onClick = onToggleExpand)
                .graphicsLayer {
                    rotationZ = arrowRotationAngle
                    translationY = 2.dp.toPx() * pulseAnimation
                },
            painter = painterResource(id = R.drawable.ic_down_arrow),
            contentDescription = null
        )
    }
}

@Composable
private fun ListItem(
    iconUrl: String,
    text: String,
    amount: Float,
    goldWeight: String,
    unitPreference: String,
    modifier: Modifier = Modifier,
    isAmountHighlighted: Boolean = false
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        JarImage(
            imageUrl = iconUrl,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = text,
            maxLines = 3,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            style = JarTypography.body2,
            color = colorResource(id = color_ACA1D3FF.resourceId)
        )
        Text(
            text = "₹${amount}",
            modifier = Modifier.padding(start = 4.dp),
            textAlign = TextAlign.End,
            maxLines = 1,
            style = with(JarTypography) {
                if (isAmountHighlighted) h4.copy(lineHeight = 26.sp) else h5.copy(lineHeight = 26.sp)
            },
            color = if (isAmountHighlighted) Color.White else colorResource(id = color_ACA1D3FF.resourceId)
        )
        Spacer(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .size(1.dp, 13.dp)
                .background(color = colorResource(id = color_776E944D.resourceId))
        )
        Text(
            text = "$goldWeight $unitPreference",
            style = JarTypography.caption,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.requiredWidth(64.dp),
            color = colorResource(id = color_ACA1D3FF.resourceId)
        )
    }
}

@Composable
private fun VerificationRequiredItem(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = text,
        style = JarTypography.caption,
        maxLines = 3,
        color = colorResource(id = color_ACA1D3FF.resourceId)
    )
}