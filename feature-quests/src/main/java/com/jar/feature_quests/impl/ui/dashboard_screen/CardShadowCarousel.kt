package com.jar.feature_quests.impl.ui.dashboard_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.component.ButtonType
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.angledGradientBackground
import com.jar.app.core_ui.R
import com.jar.feature_quests.shared.domain.model.Quest
import com.jar.feature_quests.shared.domain.model.QuestStatus
import com.jar.feature_quests.shared.domain.model.QuestsDashboardData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardShadowCarousel(
    modifier: Modifier,
    data: QuestsDashboardData,
    playCta: (quest: Quest) -> Unit,
    cardSwiped: () -> Unit
) {
    val list = data.quests
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemWidth = screenWidth / 1.3f
    val contentPadding = PaddingValues(
        start = 40.dp,
        end = 40.dp
    )
    val state = rememberLazyListState()
    val selectedIndex = remember {
        derivedStateOf<Int> {
            if (state.firstVisibleItemScrollOffset > 0) state.firstVisibleItemIndex + 1 else state.firstVisibleItemIndex
        }
    }
    LaunchedEffect(key1 = selectedIndex.value) {
        cardSwiped.invoke()
    }
    Column() {
        DotsIndicator(data.quests.size, selectedIndex.value, data.quests)
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(10.dp))
        LazyRow(
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = contentPadding,
        ) {
            itemsIndexed(list) {index, quest ->
                QuestCardContent(Modifier.width(itemWidth), quest, playCta, index, data.quests.size)
            }
        }

        val scope = rememberCoroutineScope()
        DisposableEffect(true) {
            val index =
                maxOf((data.quests.indexOfFirst { it.locked == true || (it.getStatusEnum() == QuestStatus.UNLOCKED || it.getStatusEnum() == QuestStatus.IN_PROGRESS) })
                    .coerceAtLeast(0),
                    (data.quests.indexOfLast { it.getStatusEnum() == QuestStatus.COMPLETED })
                )

            val launch = scope.launch {
                delay(300)
                state.animateScrollToItem(index)
            }
            onDispose {
                launch.cancel()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    quests: List<Quest>
) {
    val state = rememberLazyListState()
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        ),
        state = state,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        items(totalDots) { index ->
            RenderTabIndicator(quests[index], index == selectedIndex, index, selectedIndex)
        }
    }
}

fun getStartPadding(index: Int, size: Int): Dp {
    return when (index) {
        0 -> 20.dp
        size - 1 -> 0.dp
        else -> 10.dp
    }
}

fun getEndPadding(index: Int, size: Int): Dp {
    return when (index) {
        size - 1 -> 20.dp
        0 -> 0.dp
        else -> 10.dp
    }
}
@Composable
fun QuestCardContent(modifier: Modifier = Modifier, quest: Quest, playCta: (quest: Quest) -> Unit, index: Int, size: Int) {
    val borderColors = listOf(colorResource(id = R.color.color_D6DAE8), colorResource(id = R.color.color_7029CC))
    CardOverlay(
        modifier = modifier
            .padding(
                start = getStartPadding(index, size),
                top = 20.dp,
                bottom = 10.dp,
                end = getEndPadding(index, size)
            )
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(colors = borderColors),
                shape = RoundedCornerShape(36.dp)
            )
            .padding(bottom = 10.dp, top = 10.dp, start = 10.dp, end = 10.dp)
            .aspectRatio(4 / 5f)
            .ShadowModifier2(cornerRadius = 28.dp)
            .angledGradientBackground(
                colors = listOf(
                    fromHex(quest.cardDetails?.bgColorStart.orEmpty()),
                    fromHex(quest.cardDetails?.bgColorEnd.orEmpty())
                ),
                angle = 315f,
                cornerRadius = CornerRadius(with(LocalDensity.current) { 28.dp.toPx() })
            ),
        quest.cardDetails?.bgOverlayImage.orEmpty()
    ) {
        RenderCardContent(quest, playCta)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CardOverlay(modifier: Modifier, url: String, content: @Composable () -> Unit) {
    Box (modifier) {
        GlideImage(model = url, contentDescription = "", contentScale = ContentScale.FillBounds, modifier = Modifier.clip(
            RoundedCornerShape(28.dp)
        ))
        content()
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RenderCardContent(quest: Quest, playCta: (quest: Quest) -> Unit) {
    val textColor = if (quest.locked == true)  Color.White else colorResource(id = com.jar.app.core_ui.R.color.color_272239)
    Column(
        Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlideImage(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                .weight(1f)
                ,
            model = quest.cardDetails?.image,
            contentDescription = ""
        )
        Text(
            text = quest.cardDetails?.title.orEmpty(),
            style = JarTypography.h3,
            modifier = Modifier
                .padding(top = 18.dp, start = 12.dp, end = 12.dp)
                .heightIn(min = 48.dp),
            color = textColor,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2
        )
        quest.cardDetails?.description?.let {
            Text(
                text = it,
                style = JarTypography.body2.copy(fontSize = 12.sp),
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)
                    .heightIn(min = 20.dp),
                color = if (quest.locked == true) textColor else textColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2
            )
        }
        quest.cardDetails?.progress?.takeIf { it.isNotEmpty() }?.let {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 4.dp)
                    .height(20.dp), horizontalArrangement = Arrangement.Center) {
                it.forEach {
                    GlideImage(modifier = Modifier.size(20.dp), model = it, contentDescription = "")
                    Spacer(Modifier.width(10.dp))
                }
            }
        }
        quest.cardDetails?.questCardCta?.let {
            JarButton(
                text = it.title,
                onClick = { playCta(quest) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(48.dp),
                isAllCaps = false,
                buttonType = if (it.buttonType == "SECONDARY") ButtonType.SECONDARY else ButtonType.PRIMARY,
                secondaryBorderColor = colorResource(id = com.jar.app.core_ui.R.color.color_121127),
                textColor =  if (it.buttonType == "SECONDARY") colorResource(id = R.color.color_272239) else Color.White,
                minHeight = 48.dp,
                fontSize = 12.sp,
                paddingValues = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                cornerRadius = 10.dp
            )
        } ?: run {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp))
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(10.dp))
    }
}

fun Modifier.advancedShadow(
    color: Color = Color.Black,
    alpha: Float = 1f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    transparentColor: Int
) = drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()

    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowBlurRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
    }
}

fun fromHex(color: String) =
    Color(android.graphics.Color.parseColor(color))
