@file:OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)

package com.jar.app.feature_sell_gold.impl.ui.amount.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.onTouchHeld
import kotlinx.coroutines.delay

@Composable
fun ValuePropCarousel(
    pagerState: PagerState,
    carouselCards: List<ValuePropCarouselCard>,
    modifier: Modifier = Modifier
) {
    val fillWidthAnim = remember { Animatable(0f) }
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.onTouchHeld { isPressed = it },
        colors = CardDefaults.cardColors(
            containerColor = carouselCards[pagerState.currentPage].backgroundColor.toComposeColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        HorizontalPager(
            modifier = Modifier.padding(top = 16.dp),
            pageCount = carouselCards.size,
            state = pagerState
        ) { page ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = carouselCards[page].description,
                    style = JarTypography.body2,
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(16.dp))
                JarImage(
                    modifier = Modifier.size(72.dp),
                    imageUrl = carouselCards[page].iconLink,
                    contentDescription = null
                )
            }
        }
        Row(
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {

            LaunchedEffect(isPressed) {
                val currentValue = fillWidthAnim.value
                if (isPressed) fillWidthAnim.stop() else fillWidthAnim.snapTo(currentValue)
            }

            LaunchedEffect(pagerState.currentPage) {
                fillWidthAnim.snapTo(0f)
                fillWidthAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = LinearEasing
                    )
                )
            }

            repeat(carouselCards.size) { iteration ->
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .drawBehind {
                            if (pagerState.currentPage == iteration) {
                                drawRoundRect(
                                    color = Color.White,
                                    size = Size(
                                        width = size.width * fillWidthAnim.value,
                                        height = size.height
                                    ),
                                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                                )
                            }
                        }
                        .size(
                            width = if (pagerState.currentPage == iteration) 14.dp else 4.dp,
                            height = 4.dp
                        )
                )
            }

            var key by remember { mutableStateOf(0) }

            val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

            if (!isDragged && !isPressed) {
                LaunchedEffect(key) {
                    delay(2000)
                    val nextPage = (pagerState.currentPage + 1) % carouselCards.size
                    pagerState.animateScrollToPage(nextPage)
                    key = nextPage
                }
            }
        }
    }
}

@Stable
data class ValuePropCarouselCard(
    val description: String,
    val iconLink: String,
    val backgroundColor: String
)

val String.toComposeColor: Color
    get() = Color(android.graphics.Color.parseColor(this))