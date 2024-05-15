package com.jar.app.core_compose_ui.component

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_ui.R


@Composable
fun JarButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    isAllCaps: Boolean = false,
    @DrawableRes icon: Int? = null,
    @JarButtonIconGravity iconGravity: Int = ICON_GRAVITY_START,
    iconSize: DpSize = DpSize(16.dp, 16.dp),
    color: Color = JarColors.primaryButtonBgColor,
    iconPadding: Dp = 8.dp,
    elevation: Dp = 0.dp,
    fontWeight: FontWeight = FontWeight.W700,
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
    buttonType: ButtonType = ButtonType.PRIMARY,
    minHeight: Dp = 56.dp,
    secondaryBorderColor: Color = colorResource(id = R.color.color_846FC0),
    paddingValues: PaddingValues? = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    cornerRadius: Dp = 12.dp
) {
    when (buttonType) {
        ButtonType.PRIMARY -> {
            JarPrimaryButton(
                text = text,
                onClick = { onClick() },
                modifier = modifier,
                isEnabled = isEnabled,
                isAllCaps = isAllCaps,
                icon = icon,
                iconGravity = iconGravity,
                iconSize = iconSize,
                color = color,
                iconPadding = iconPadding,
                elevation = elevation,
                fontWeight = fontWeight,
                textColor = textColor,
                fontSize = fontSize,
                minHeight = minHeight,
                paddingValues = paddingValues,
                cornerRadius = cornerRadius
            )
        }

        ButtonType.SECONDARY -> {
            JarSecondaryButton(
                text = text,
                onClick = { onClick() },
                modifier = modifier,
                isEnabled = isEnabled,
                isAllCaps = isAllCaps,
                icon = icon,
                iconGravity = iconGravity,
                iconSize = iconSize,
                iconPadding = iconPadding,
                minHeight = minHeight,
                borderColor = secondaryBorderColor,
                textColor = textColor,
                cornerRadius = cornerRadius
            )
        }
    }

}

enum class ButtonType {
    PRIMARY,
    SECONDARY
}

/**
 * Jar themed primary button (Gradient Background)
 */
@Composable
fun JarPrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    isAllCaps: Boolean = true,
    icon: Any? = null,
    @JarButtonIconGravity iconGravity: Int = ICON_GRAVITY_START,
    iconSize: DpSize = DpSize(16.dp, 16.dp),
    color: Color = JarColors.primaryButtonBgColor,
    iconPadding: Dp = 8.dp,
    elevation: Dp = 0.dp,
    fontWeight: FontWeight = FontWeight.W700,
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
    minHeight: Dp = 56.dp,
    borderBrush: Brush? = Brush.verticalGradient(listOf(Color(0xFF845FE9), Color(0xFF6637E4))),
    paddingValues: PaddingValues? = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    cornerRadius: Dp = 12.dp
) {
    val buttonShape = remember { RoundedCornerShape(size = cornerRadius) }
    val backgroundModifier = remember {
        Modifier.background(
            color = color,
            shape = buttonShape
        )
    }
    InternalButtonWrapperImpl(
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        text = text,
        onClick = onClick,
        isEnabled = isEnabled,
        isAllCaps = isAllCaps,
        buttonShape = buttonShape,
        icon = icon,
        iconGravity = iconGravity,
        iconSize = iconSize,
        iconPadding = iconPadding,
        fontWeight = fontWeight,
        fontSize = fontSize,
        textColor = textColor,
        shadowElevation = elevation,
        borderBrush = borderBrush,
        borderWidth = 1.dp,
        minHeight = minHeight,
        paddingValues = paddingValues
    )

}

/**
 * Jar themed secondary button (bg Background color)
 */
@Composable
fun JarSecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    isAllCaps: Boolean = true,
    @DrawableRes icon: Int? = null,
    @JarButtonIconGravity iconGravity: Int = ICON_GRAVITY_START,
    iconSize: DpSize = DpSize(16.dp, 16.dp),
    color: Color = Color.Transparent,
    iconPadding: Dp = 8.dp,
    borderColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_846FC0),
    minHeight: Dp = 56.dp,
    textColor: Color = Color.White,
    cornerRadius: Dp = 12.dp
) {
    val buttonShape = remember { RoundedCornerShape(size = cornerRadius) }
    val backgroundModifier = remember {
        Modifier.background(
            color = color,
            shape = buttonShape
        )
    }
    InternalButtonWrapperImpl(
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        text = text,
        onClick = onClick,
        isEnabled = isEnabled,
        isAllCaps = isAllCaps,
        buttonShape = buttonShape,
        icon = icon,
        iconGravity = iconGravity,
        iconSize = iconSize,
        iconPadding = iconPadding,
        shadowElevation = 0.dp,
        borderBrush = Brush.linearGradient(listOf(borderColor, borderColor)),
        borderWidth = 2.dp,
        minHeight = minHeight,
        textColor = textColor
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InternalButtonWrapperImpl(
    modifier: Modifier,
    backgroundModifier: Modifier,
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean,
    isAllCaps: Boolean,
    buttonShape: Shape,
    icon: Any?,
    @JarButtonIconGravity iconGravity: Int,
    iconSize: DpSize,
    iconPadding: Dp,
    fontWeight: FontWeight = FontWeight.W700,
    minHeight: Dp = 56.dp,
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
    shadowElevation: Dp = 8.dp,
    borderBrush: Brush? = Brush.linearGradient(
        listOf(
            JarColors.stroke30Color,
            JarColors.stroke30Color
        )
    ),
    borderWidth: Dp = 1.dp,
    paddingValues: PaddingValues? = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 300, easing = Ease),
        label = "buttonScaleAnimation"
    )
    Box(
        modifier = modifier
            .scale(scale.value)
            .defaultMinSize(minWidth = 48.dp, minHeight = minHeight)
            .shadow(elevation = shadowElevation, shape = buttonShape)
            .debounceClickable(enabled = isEnabled, onClick = onClick)
            .alpha(alpha = if (isEnabled) 1.0f else 0.3f)
            .then(backgroundModifier)
            .then(
                if (borderBrush != null) Modifier.border(
                    width = borderWidth,
                    brush = borderBrush,
                    shape = buttonShape
                ) else Modifier
            )
            .then(
                if (paddingValues != null) Modifier.padding(paddingValues)
                else Modifier
            )
            .pointerInput(key1 = isPressed) {
                awaitPointerEventScope {
                    isPressed = if (isPressed) {
                        waitForUpOrCancellation()
                        false
                    } else {
                        awaitFirstDown(false)
                        true
                    }
                }
            }
    ) {
        when (iconGravity) {
            ICON_GRAVITY_START,
            ICON_GRAVITY_END -> {
                HorizontalButtonContentImpl(
                    modifier = Modifier.align(Alignment.Center),
                    text = text,
                    isAllCaps = isAllCaps,
                    icon = icon,
                    iconGravity = iconGravity,
                    iconSize = iconSize,
                    iconPadding = iconPadding,
                    fontWeight = fontWeight,
                    textColor = textColor,
                    fontSize = fontSize
                )
            }

            ICON_GRAVITY_TOP,
            ICON_GRAVITY_BOTTOM -> {
                VerticalButtonContentImpl(
                    modifier = Modifier.align(Alignment.Center),
                    text = text,
                    isAllCaps = isAllCaps,
                    icon = icon,
                    iconGravity = iconGravity,
                    iconSize = iconSize,
                    iconPadding = iconPadding,
                    textColor = textColor,
                    fontSize = fontSize
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HorizontalButtonContentImpl(
    modifier: Modifier = Modifier,
    text: String,
    isAllCaps: Boolean,
    icon: Any?,
    @JarButtonIconGravity iconGravity: Int,
    iconSize: DpSize,
    iconPadding: Dp,
    fontWeight: FontWeight = FontWeight.W700,
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
) {
    Row(modifier = modifier) {
        if (iconGravity == ICON_GRAVITY_START) {
            icon?.let {
                JarImage(
                    imageUrl = it,
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.CenterVertically),
                    contentDescription = "Button Icon"
                )
                Spacer(modifier = Modifier.size(iconPadding))
            }

        }
        Text(
            text = if (isAllCaps) text.toUpperCase(Locale.current) else text,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = textColor,
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontFamily = jarFontFamily,
            lineHeight = fontSize,
            fontWeight = fontWeight
        )
        if (iconGravity == ICON_GRAVITY_END) {
            Spacer(modifier = Modifier.size(iconPadding))
            icon?.let {
                JarImage(
                    imageUrl = it,
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.CenterVertically),
                    contentDescription = "Button Icon"
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun VerticalButtonContentImpl(
    modifier: Modifier = Modifier,
    text: String,
    isAllCaps: Boolean,
    icon: Any?,
    @JarButtonIconGravity iconGravity: Int,
    iconSize: DpSize,
    iconPadding: Dp,
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp,
) {
    Column(modifier = modifier) {
        if (iconGravity == ICON_GRAVITY_TOP) {
            icon?.let {
                JarImage(
                    imageUrl = it,
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.CenterHorizontally),
                    contentDescription = "Button Icon"
                )
            }
            Spacer(
                modifier = Modifier
                    .size(iconPadding)
                    .align(Alignment.CenterHorizontally)
            )
        }
        Text(
            text = if (isAllCaps) text.toUpperCase(Locale.current) else text,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = textColor,
            textAlign = TextAlign.Center,
            fontSize = fontSize,
            fontFamily = jarFontFamily,
            lineHeight = fontSize,
            fontWeight = FontWeight.W700
        )
        if (iconGravity == ICON_GRAVITY_BOTTOM) {
            Spacer(
                modifier = Modifier
                    .size(iconPadding)
                    .align(Alignment.CenterHorizontally)
            )
            icon?.let {
                JarImage(
                    imageUrl = it,
                    modifier = Modifier
                        .size(iconSize)
                        .align(Alignment.CenterHorizontally),
                    contentDescription = "Button Icon"
                )
            }
        }
    }
}

@Preview
@Composable
fun JarPrimaryButtonPreview() {
    JarPrimaryButton(
        text = "Primary Button",
        onClick = {

        }
    )
}

@Preview
@Composable
fun JarSecondaryButtonPreview() {
    JarSecondaryButton(
        text = "Secondary Button",
        onClick = { }
    )
}

@Preview
@Composable
fun JarPrimaryButtonWithIconPreview() {
    JarPrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Primary Button",
        onClick = {},
        icon = com.jar.app.core_ui.R.drawable.core_ic_small_star,
        iconGravity = ICON_GRAVITY_END
    )
}

const val ICON_GRAVITY_START = 1
const val ICON_GRAVITY_END = 2
const val ICON_GRAVITY_TOP = 3
const val ICON_GRAVITY_BOTTOM = 4

@IntDef(
    ICON_GRAVITY_START,
    ICON_GRAVITY_END,
    ICON_GRAVITY_TOP,
    ICON_GRAVITY_BOTTOM
)
@Retention(AnnotationRetention.SOURCE)
annotation class JarButtonIconGravity

@Composable
fun RenderImagePillButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int? = null,
    text: String,
    @ColorRes bgColor: Int,
    @ColorRes textColor: Int,
    cornerRadius: Dp = 16.dp,
    colorFilter: ColorFilter? = null,
    biggerVerticalPadding: Boolean? = null,
    style: TextStyle = JarTypography.body1,
    smallerTextPadding: Boolean = false,
    borderColor: Int = bgColor,
    elevation: Dp = 0.dp,
    maxLines: Int = Int.MAX_VALUE,
    iconSize: Dp? = null
) {
    val textPadding = if (smallerTextPadding) 6.dp else 12.dp
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colorResource(id = borderColor),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(
                color = colorResource(id = bgColor),
                shape = RoundedCornerShape(cornerRadius)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        drawableRes?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .then(if (iconSize != null) Modifier.size(iconSize) else Modifier),
                colorFilter = colorFilter
            )
        }
        Text(
            text = text,
            modifier = Modifier.padding(
                if (drawableRes == null && biggerVerticalPadding == null)
                    PaddingValues(top = 4.dp, bottom = 4.dp, start = textPadding, end = 12.dp)
                else
                    PaddingValues(top = 8.dp, bottom = 8.dp, start = textPadding, end = 12.dp)
            ),
            style = style,
            color = colorResource(id = textColor),
            maxLines = maxLines
        )
    }
}

@Composable
@Preview
fun PreviewRenderImagePillButton() {
    RenderImagePillButton(
        drawableRes = android.R.drawable.arrow_down_float,
        text = "ASD",
        bgColor = android.R.color.black,
        textColor = android.R.color.white,
        maxLines = 1,
    )
}