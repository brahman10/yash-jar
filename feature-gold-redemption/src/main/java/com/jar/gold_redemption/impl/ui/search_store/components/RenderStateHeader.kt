package com.jar.gold_redemption.impl.ui.search_store.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.views.CardArrow
import com.jar.app.core_compose_ui.views.EXPAND_ANIMATION_DURATION
import com.jar.app.core_ui.R


@Composable
internal fun RenderStateHeader(
    it: String,
    count: Int,
    searchText: MutableState<String>? = null,
    childCount: Int? = null,
    CardArrowClick: () -> Unit
) {
    val size = LocalViewConfiguration.current.minimumTouchTargetSize
    val textColor =
        if (count == 0) colorResource(id = R.color.color_776E94) else colorResource(
            id = R.color.color_D5CDF2
        )
    val transition = updateTransition(
        targetState = logic(childCount, count),
        label = "Rotate"
    )
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "") { expanded ->
        if (expanded) 0f else 90f
    }

    Row(
        Modifier
            .fillMaxWidth()
            .debounceClickable {
                CardArrowClick()
            }
            .height(55.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            Modifier
                .fillMaxHeight()
                .width(16.dp)
        )
        MultiStyleText(text = it, modifier = Modifier.weight(1f), color = textColor, searchText = searchText?.value?.capitalize())
        Text(text = "${count.toString()} stores", color = textColor)
        if (count > 0)
            CardArrow(
                degrees = arrowRotationDegree,
                onClick = { CardArrowClick() },
                tintColor = colorResource(id = R.color.color_ACA1D3)
            )
        else
            Spacer(
                Modifier
                    .size(size)
            )
        }
        Spacer(
            Modifier
                .fillMaxHeight()
                .width(11.dp)
        )
    }

fun logic(childCount: Int?, count: Int): Boolean {
    return if (childCount != null)  childCount > 0
    else false
}

@Composable
fun MultiStyleText(text: String, modifier: Modifier, color: Color, searchText: String? = null) {
    Text(buildAnnotatedString {
        if (!searchText.isNullOrBlank()) {
            withStyle(style = SpanStyle(color = colorResource(id = com.jar.app.core_ui.R.color.color_789BDE))) {
                append(searchText)
            }
        }
        withStyle(style = SpanStyle(color = color)) {
            append(if (searchText.isNullOrBlank()) text else text.substring(IntRange(searchText.length, text.length - 1)))
        }
    }, modifier = modifier)
}
