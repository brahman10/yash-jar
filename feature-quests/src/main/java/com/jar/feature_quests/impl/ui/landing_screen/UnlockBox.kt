package com.jar.feature_quests.impl.ui.landing_screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.jar.feature_quests.shared.domain.model.LandingViewItems
import kotlin.math.roundToInt

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalAnimationApi::class)
@Composable
fun UnlockBox(
    modifier: Modifier = Modifier,
    data: LandingViewItems?,
    onSwipedCallBack: () -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_7029CC),
                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 30.dp, vertical = 20.dp)
    ) {
        Row (
            Modifier
                .padding(bottom = 20.dp)
                .height(IntrinsicSize.Min)) {
            GlideImage(
                model = data?.bottomSheetImage,
                contentDescription = "",
                modifier = Modifier.fillMaxHeight()
            )
            Column(
                Modifier
                    .padding(start = 20.dp)) {
                Text(
                    text = data?.bottomSheetTitle.orEmpty(),
                    style = JarTypography.h3,
                    color = Color.White,
                )
                Text(
                    text = data?.bottomSheetDesc.orEmpty(),
                    style = JarTypography.body1,
                    color = colorResource(id = R.color.color_D5CDF2),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
        SlideTodoView(
            endIcon = {},
            text = data?.bottomSheetSliderText.orEmpty(),
            navigationIcon = {
                Image(
                    painterResource(id = R.drawable.ic_right_chevron),
                    "",
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.color_121127))
                )
            },
            slideColor = Color.White,
            textStyle = JarTypography.body1.copy(color = colorResource(id = R.color.color_121127)),
            onSwipedCallBack = onSwipedCallBack,
            slideHeight = 60.dp
        )
    }
}


enum class state {
    Start, End
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalAnimationApi
@Composable
fun SlideTodoView(
    modifier: Modifier = Modifier,
    slideHeight: Dp = 60.dp,
    slideWidth: Dp = 400.dp,
    slideColor: Color,
    navigationIcon: @Composable () -> Unit,
    navigationIconPadding: Dp = 2.dp,
    endIcon: @Composable () -> Unit,
    widthAnimationMillis: Int = 1000,
    text: String? = null,
    textStyle: androidx.compose.ui.text.TextStyle? = null,
    elevation: Dp = 0.dp,
    onSwipedCallBack: (() -> Unit)
) {

    val iconSize = slideHeight - 10.dp
    val slideDistance = with(LocalDensity.current) {
        (slideWidth - iconSize - 50.dp).toPx()
    }

    val swipeableState = rememberSwipeableState(initialValue = state.Start)

    var flag by remember { mutableStateOf(iconSize) }

    if (swipeableState.currentValue == state.End) {
        onSwipedCallBack()
    }

    val iconSizeAnimation by animateDpAsState(targetValue = flag, tween(1000), label = "")

    val textAlpha by animateFloatAsState(
        targetValue = if (swipeableState.offset.value != 0f) (1 - swipeableState.progress.fraction) else 1f,
        label = ""
    )

    val width by animateDpAsState(
        targetValue = if (iconSizeAnimation == 0.dp) slideHeight else slideWidth,
        tween(widthAnimationMillis), label = ""
    )

    AnimatedVisibility(
        visible = width != slideHeight,
        exit = fadeOut(
            targetAlpha = 0f,
            animationSpec = tween(1000, easing = LinearEasing, delayMillis = 1000)
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .height(slideHeight)
                .width(width),
            color = slideColor,
            elevation = elevation
        ) {
            Box(
                modifier = Modifier
                    .padding(5.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    text?.let { it ->
                        if (textStyle != null) {
                            Text(
                                text = it,
                                style = textStyle.copy(color = textStyle.color.copy(alpha = textAlpha))
                            )
                        } else Text(it)
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF4C430),
                        modifier = Modifier
                            .size(iconSizeAnimation)
                            .padding(navigationIconPadding)
                            .aspectRatio(1f)
                            .swipeable(
                                state = swipeableState,
                                anchors = mapOf(
                                    0f to state.Start,
                                    slideDistance to state.End
                                ),
                                thresholds = { _, _ -> FractionalThreshold(0.9f) },
                                orientation = Orientation.Horizontal
                            )
                            .offset {
                                IntOffset(swipeableState.offset.value.roundToInt(), 0)
                            },
                    ) {
                        navigationIcon()
                    }
                }
                AnimatedVisibility(visible = width == slideHeight) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(iconSize),
                            color = Color.Transparent
                        ) {
                            endIcon()
                        }
                    }
                }
            }
        }
    }
}