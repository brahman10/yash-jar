package com.jar.app.core_compose_ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography

@Composable
@Preview
fun SingleExpandableCardPreview() {
    SingleExpandableCard(
        expanded = remember { mutableStateOf(true) },
        renderHeader = { ss ->
            Text(
                text = "Header",
                style = JarTypography.h6,
                modifier = Modifier.padding(16.dp)
            )
        },
        renderExpandedContent = {
            Text(
                text = "Content",
                style = JarTypography.body1,
                modifier = Modifier.padding(16.dp)
            )
        }
    )
}

/**
 * Composable function that represents a single expandable card.
 * @param expanded The mutable state indicating whether the card is expanded or collapsed.
 * @param renderHeader The composable function to render the header of the card.
 * @param renderExpandedContent The composable function to render the content of the card.
 */
@Composable
fun SingleExpandableCard(
    expanded: MutableState<Boolean>,
    renderHeader: @Composable (rowScope: RowScope) -> Unit,
    renderExpandedContent: @Composable () -> Unit,
) {
    val transition = updateTransition(
        targetState = expanded,
        label = "Rotate"
    )

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "") {
        if (it.value) 0f else 180f
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.debounceClickable {
            expanded.value = !expanded.value
        }) {
            renderHeader(this)
            Spacer(modifier = Modifier.weight(1f))
            CardArrow(degrees = arrowRotationDegree, onClick = { expanded.value = !expanded.value }, tintColor = colorResource(
                id = com.jar.app.core_ui.R.color.color_ACA1D3
            ))
        }
        SingleExpandableCard(visible = expanded.value, initialVisibility = expanded.value, renderExpandedContent)
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SingleExpandableCard(
    visible: Boolean = true,
    initialVisibility: Boolean = false,
    Content: @Composable () -> Unit,
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    AnimatedVisibility(
        visible = visible,
        initiallyVisible = initialVisibility,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Content()
    }
}