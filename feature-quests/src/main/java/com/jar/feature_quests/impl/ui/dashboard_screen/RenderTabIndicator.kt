package com.jar.feature_quests.impl.ui.dashboard_screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.orFalse
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.GradientAngle
import com.jar.app.core_compose_ui.utils.GradientOffset
import com.jar.app.core_compose_ui.utils.angledCircularGradientBackground
import com.jar.feature_quests.shared.domain.model.CardDetails
import com.jar.feature_quests.shared.domain.model.Indicator
import com.jar.feature_quests.shared.domain.model.Quest
import com.jar.feature_quests.shared.domain.model.QuestStatus

class QuestPreviewProvider : PreviewParameterProvider<Quest> {
    val quest = Quest(
        type = "TXN_GAME",
        locked = true,
        indicator = Indicator(
            iconUrl = "https://cdn.myjar.app/quest/indicatorLockedIcon.webp",
            text = "3",
            bgColorStart = "#FF94E4",
            bgColorEnd = "#FCFDFF"
        ),
        cardDetails = CardDetails(
            title = "Quest Locked",
            description = "Complete previous level to unlock this",
            progress = null,
            image = "https://cdn.myjar.app/quest/homeCardLockedImage.webp",
            bgOverlayImage = "https://cdn.myjar.app/quest/overlayLocked.webp",
            bgColorStart = "#4FA15F",
            bgColorEnd = "#FCFDFF",
            questCardCta = null
        ),
        status = "LOCKED"
    )
    override val values: Sequence<Quest> = sequenceOf(
        quest,
        // You can add other Quest instances here to test different states.
    )
}

@Composable
@Preview
fun RenderTabIndicatorPreview2(
    @PreviewParameter(QuestPreviewProvider::class) quest: Quest, // Step 2: Accept the preview parameter
) {
    RenderTabIndicator(quest, isSelected = true, 1, 3)
}
@Composable
@Preview
fun RenderTabIndicatorPreview(
    @PreviewParameter(QuestPreviewProvider::class) quest: Quest, // Step 2: Accept the preview parameter
) {
    RenderTabIndicator(quest, isSelected = false, 1,2)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RenderTabIndicator(quest: Quest, isSelected: Boolean, index: Int, selectedIndex: Int) {
    val isLocked = quest.locked.orFalse()
    val size = if (isSelected) 75.dp else 40.dp
    val borderWidth = if (isSelected) 6.dp else 1.dp
    val sizeDp =  (size - (if (isSelected) borderWidth else 0.dp))
    val sizeDpPx = with(LocalDensity.current) { (size - (if (isSelected) borderWidth else 0.dp)).toPx() }
    val gradientOffset = remember { GradientOffset(GradientAngle.CW45) }
    Box(
        modifier = Modifier
            .padding(start = 2.dp, end = 2.dp, bottom = 5.dp)
            .size(sizeDp)
            .background(Color(0xFF100C24), shape = CircleShape)
            .aspectRatio(1f)
            .then(
                if (isSelected || isLocked)
                    Modifier
                        .padding(if (isLocked) 0.dp else 0.dp)
                        .border(
                            width = borderWidth, color =
                            if (isSelected) colorResource(id = com.jar.app.core_ui.R.color.color_4B175B) else Color.White,
                            shape = CircleShape
                        )
                else
                    Modifier
            )
            .then(
                if (isSelected || (selectedIndex > index))
                Modifier
                    .advancedShadow(
                        shadowBlurRadius = 15.dp,
                        cornersRadius = sizeDp*1.2f,
                        transparentColor = Color(0xFF100C24).toArgb(),
                        offsetY = 10.dp,
                        color = Color(0xFF4B175B)
                    )
                else Modifier
            )
            .then(
                if (!isLocked || isSelected) Modifier
                    .angledCircularGradientBackground(
                        colors = listOf(
                            fromHex(quest.indicator?.bgColorStart.orEmpty()),
                            fromHex(quest.indicator?.bgColorEnd.orEmpty())
                        ),
                        angle = 315f,
                        circleRadius = sizeDpPx / 2
                    ).padding(4.dp) else Modifier
            )
    ) {
        if (!quest.indicator?.iconUrl.isNullOrBlank() && (!isSelected || quest.getStatusEnum() == QuestStatus.COMPLETED)) {
            val padding = getPadding(isSelected, isLocked)
            GlideImage(
                model = quest.indicator?.iconUrl.orEmpty(),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(padding)
            )
        } else {
            Text(
                text = quest.indicator?.text.orEmpty(),
                color = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                modifier = Modifier.align(Alignment.Center),
                style = if (isSelected) JarTypography.h3 else JarTypography.h5
            )
        }
    }
}

fun getPadding(selected: Boolean, locked: Boolean): Dp {
    if (selected) {
        return 8.dp
    } else if (locked) {
        return 12.dp
    } else {
        return 6.dp
    }
}
