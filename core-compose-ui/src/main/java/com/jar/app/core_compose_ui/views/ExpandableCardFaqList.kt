package com.jar.app.core_compose_ui.views

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R

/**
 * Data class representing an expandable card model.
 * @param id The unique identifier of the card.
 * @param faqHeaderText The question associated with the card.
 * @param faqExpandableContentText The answer associated with the card (nullable).
 * @param data For customExpandableContent function in the renderExpandableFaqList function - any custom data can be passed here.
 * @param drawableRes The drawable resource ID associated with the card (nullable).
 */
data class ExpandableCardModel(
    val id: Int,
    val faqHeaderText: String,
    val faqExpandableContentText: String? = null,
    val data: Any? = null,
    @DrawableRes val drawableRes: Int? = null
)

@Composable
@Preview
fun RenderExpandableListPReview() {
    val faqSelectedIndex = remember { mutableStateOf<Int>(-1) }
    LazyColumn {
        renderExpandableFaqList(
            this,
            listOf(
                ExpandableCardModel(0, "Hello what's up?", "Big Answer here\nBig Answer here", R.drawable.ic_add),
                ExpandableCardModel(1, "Hello what's up?", "Big Answer here\nBig Answer here", R.drawable.ic_add),
                ExpandableCardModel(2, "Hello what's up?", "Big Answer here\nBig Answer here", R.drawable.ic_add),
                ExpandableCardModel(3, "Hello what's up?", "Big Answer here\nBig Answer here", R.drawable.ic_add),
            ),
            faqSelectedIndex,
            R.color.color_272239,
            R.color.color_272239,
            addSeperator = true,
            paddedSeparator = true
        )
    }

}

/**
 * Renders a list of expandable cards in a lazy list.
 * If customExpandableContent that allows to render custom view for the expanded layout
 *
 * @param lazyListScope The scope used for the lazy list.
 * @param cards The list of expandable card models to render.
 * @param selectedIndex The mutable state holding the index of the selected card.
 * @param listBackgroundColor The color resource for the background of the card list.
 * @param cardBackgroundColor The color resource for the background of the individual cards.
 * @param addSeperator Specifies whether to add a separator between the cards.
 * @param paddedSeparator Specifies whether the separator should have padding.
 * @param content The composable content to be displayed in each card. It takes a data parameter.
 * @param elevation The elevation of the cards.
 * @param onClick The click listener for the cards. It takes the index of the clicked card.
 * @param questionTextColor The color resource for the text color of the question in the cards.
 * @param answerTextColor The color resource for the text color of the answer in the cards.
 * @param questionTextSize The question text size in the cards.
 * @param answerTextSize The answer text size in the cards.
*/
fun renderExpandableFaqList(
    lazyListScope: LazyListScope,
    cards: List<ExpandableCardModel>,
    selectedIndex: MutableState<Int>,
    @ColorRes listBackgroundColor: Int? = null,
    @ColorRes cardBackgroundColor: Int? = null,
    addSeperator: Boolean = false,
    paddedSeparator: Boolean = false,
    customExpandableContent: @Composable ((data: Any?) -> Unit)? = null,
    elevation: Dp = 1.dp,
    onClick: ((Int) -> Unit)? = null,
    @ColorRes questionTextColor: Int = R.color.color_D5CDF2,
    @ColorRes answerTextColor: Int = com.jar.app.core_ui.R.color.white,
    questionTextSize: TextUnit = 16.sp,
    answerTextSize:TextUnit = 16.sp,
    columnWrapper: Modifier = Modifier,
    cardHorizontalPadding: Dp = 16.dp,
    cardVerticalPadding:Dp = 4.dp
) {
    lazyListScope.itemsIndexed(cards) { index, card ->
        Column(modifier = columnWrapper) {
            ExpandableFaqCard(
                card = card,
                onCardArrowClick = {
                    onClick?.invoke(index)
                    selectedIndex.value = if (selectedIndex.value == index) -1 else index
                },
                expanded = selectedIndex.value == index,
                listBackgroundColor,
                cardBackgroundColor,
                customExpandableContent,
                elevation,
                questionTextColor = colorResource(id = questionTextColor),
                answerTextColor = colorResource(id = answerTextColor),
                questionTextSize = questionTextSize,
                answerTextSize = answerTextSize,
                horizontalPadding = cardHorizontalPadding,
                verticalPadding = cardVerticalPadding
            )
            if (addSeperator && index != cards.lastIndex) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .then(if (cardBackgroundColor != null) Modifier.background(colorResource(id = cardBackgroundColor)) else Modifier)
                        .then(if (paddedSeparator) Modifier.padding(horizontal = 16.dp) else Modifier)
                        .background(colorResource(id = com.jar.app.core_ui.R.color.color_3B3355))
                ) {
                    // no-op, used as divider
                }
            }
        }
    }

}


const val EXPAND_ANIMATION_DURATION = 500
const val EXPANSTION_TRANSITION_DURATION = 500

/**
 * Composable function that represents an expandable card.
 * In the default if no @param customExpandableContent is passed it will render a expandable faq
 * @param card The model representing the expandable card.
 * @param onCardArrowClick The callback function to be invoked when the arrow on the card is clicked.
 * @param expanded Indicates whether the card is expanded or collapsed.
 * @param listBackgroundColor The color resource for the background of the card list.
 * @param cardBackgroundColor The color resource for the background of the individual cards.
 * @param customExpandableContent The composable content to be displayed inside the expandable card.
 * @param elevation The elevation of the card.
 * @param questionTextColor The color of the text for the question in the card.
 *  @param questionTextSize The question text size in the cards.
 * @param answerTextSize The answer text size in the cards.
*/
@Composable
fun ExpandableFaqCard(
    card: ExpandableCardModel,
    onCardArrowClick: () -> Unit,
    expanded: Boolean,
    @ColorRes listBackgroundColor: Int? = null,
    @ColorRes cardBackgroundColor: Int? = null,
    customExpandableContent: @Composable ((data: Any?) -> Unit)? = null,
    elevation: Dp = 1.dp,
    questionTextColor: Color = colorResource(id = R.color.color_D5CDF2),
    answerTextColor: Color = Color.White,
    questionTextSize: TextUnit = 16.sp,
    answerTextSize:TextUnit = 16.sp,
    horizontalPadding:Dp = 16.dp,
    verticalPadding:Dp = 4.dp
) {
    val transition = updateTransition(
        targetState = expanded,
        label = "Rotate"
    )

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "") {
        if (it) 0f else 180f
    }

    Card(
        backgroundColor = if (cardBackgroundColor != null) colorResource(id = cardBackgroundColor) else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (listBackgroundColor != null) Modifier.background(colorResource(id = listBackgroundColor)) else Modifier)
            .padding(
                vertical = verticalPadding, horizontal = horizontalPadding
            ),
        elevation = elevation
    ) {
        Column {
            ExpandableFaqHeader(
                onCardArrowClick = onCardArrowClick,
                card = card,
                questionTextColor = questionTextColor,
                arrowRotationDegree = arrowRotationDegree,
                questionTextSize = questionTextSize
            )
            ExpandableFaqContent(
                isExpanded = expanded,
                initialVisibility = expanded,
                answer = card.faqExpandableContentText,
                customExpandableContent = customExpandableContent,
                data = card.data,
                answerTextColor = answerTextColor,
                answerTextSize = answerTextSize
            )
        }
    }
}

@Composable
private fun ExpandableFaqHeader(
    onCardArrowClick: () -> Unit,
    card: ExpandableCardModel,
    questionTextColor: Color,
    arrowRotationDegree: Float,
    questionTextSize: TextUnit = 16.sp
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
        onCardArrowClick()
    }) {
        card.drawableRes?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "",
                colorFilter = ColorFilter.tint(colorResource(id = R.color.color_D5CDF2))
            )
        }
        Text(
            card.faqHeaderText,
            style = JarTypography.body1.copy(lineHeight = 20.sp, fontWeight = FontWeight.Bold),
            color = questionTextColor,
            fontSize = questionTextSize,
            modifier = Modifier
                .padding(bottom = 20.dp, top = 20.dp, start = 16.dp)
                .weight(10f),
            lineHeight = 26.sp,
        )
        Spacer(modifier = Modifier.weight(0.5f))
        CardArrow(
            degrees = arrowRotationDegree,
            onClick = onCardArrowClick,
            tintColor = colorResource(id = R.color.color_D5CDF2)
        )
    }
}

@Preview
@Composable
fun CardArrowPreview() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CardArrow(degrees = 0f, onClick = {})
        CardArrow(degrees = 180f, onClick = {})
        CardArrow(degrees = 270f, onClick = {})
        CardArrow(degrees = 90f, onClick = {})
    }
}
@Composable
fun CardArrow(
    degrees: Float,
    onClick: () -> Unit,
    tintColor: Color = Color.White
) {
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_up),
                tint = tintColor,
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees),
            )
        }
    )
}

/**
 * Composable function that represents the content of an expandable FAQ.
 * @param isExpanded Indicates whether the content is currently visible.
 * @param initialVisibility Indicates the initial visibility state of the content.
 * @param answer The answer text to be displayed (nullable).
 * @param customExpandableContent The composable content to be displayed instead of the answer text (nullable).
 * @param answerTextColor The color of the answer text.
 * @param data Additional data to be passed to the customExpandableContent composable.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandableFaqContent(
    isExpanded: Boolean = true,
    initialVisibility: Boolean = false,
    answer: String? = null,
    customExpandableContent: @Composable() ((data: Any?) -> Unit)? = null,
    answerTextColor: Color = Color.White,
    answerTextSize:TextUnit = 16.sp,
    data: Any?
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Bottom,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Bottom,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    AnimatedVisibility(
        visible = isExpanded,
        initiallyVisible = initialVisibility,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            answer?.let {
                Text(
                    text = it,
                    color = answerTextColor,
                    modifier = Modifier.offset(y = -20.dp),
                    lineHeight = 26.sp,
                    fontSize = answerTextSize,
                    style = JarTypography.body1
                )
            } ?: run {
                customExpandableContent?.invoke(data)
            }
        }

    }
}