@file:OptIn(ExperimentalGlideComposeApi::class, FlowPreview::class)

package com.jar.app.feature_sell_gold.impl.ui.amount.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.generateAnnotatedFromHtmlString
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@Composable
internal fun EnterAmount(
    amountState: AmountState,
    volumeFromAmount: Float,
    verificationStatusState: VerificationStatusState?,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    expanded: Boolean,
    onAcquireFocus: (Boolean) -> Unit,
    onAmountEntered: (Boolean, String) -> Unit,
    onKnowMoreClick: () -> Unit,
    onEnteredAmountChange: (Float) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        var enteredAmount by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(
                TextFieldValue("")
            )
        }
        var errorMessage by remember { mutableStateOf("") }
        val pattern = remember { Regex("^(?!\\.)\\d{0,7}(?:\\.\\d{0,2})?\$") }

        LaunchedEffect(Unit) {
            enteredAmount = enteredAmount.copy(
                selection = TextRange(enteredAmount.text.length)
            )
        }

        LaunchedEffect(enteredAmount) {
            snapshotFlow { enteredAmount }.debounce(300).collect { enteredText ->
                errorMessage = when {
                    enteredText.text == "-" || enteredText.text == "." || enteredText.text.isBlank() -> ""
                    enteredText.text.toFloat() <= 5f -> amountState.minAmountError
                    enteredText.text.toFloat() > amountState.withdrawalLimit -> amountState.maxAmountError
                    else -> ""
                }
                val isValidAmount = !enteredText.text.contains("-")
                        && enteredText.text.isNotBlank() && enteredText.text.toFloat() in 6f..amountState.withdrawalLimit
                onAmountEntered(isValidAmount, enteredAmount.text)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = amountState.headerText,
                style = JarTypography.h4.copy(lineHeight = 26.sp),
                color = Color.White
            )

            verificationStatusState?.let {
                VerificationStatusLabel(it)
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        AmountTextField(
            modifier = Modifier,
            rupeeSymbol = amountState.rupeeSymbol,
            goldWeight = volumeFromAmount,
            value = enteredAmount,
            onValueChange = {
                if (it.text.isEmpty() || it.text.matches(pattern)) {
                    enteredAmount = it
                    onEnteredAmountChange(it.text.toFloatOrZero())
                }
            },
            errorMessage = errorMessage,
            focusRequester = focusRequester,
            expanded = expanded,
            onAcquireFocus = onAcquireFocus
        )
        Spacer(modifier = Modifier.size(16.dp))
        Crossfade(
            targetState = errorMessage,
            animationSpec = tween(400),
            label = ""
        ) {
            when {
                it.isBlank() -> AmountCaptionInfo(
                    iconUrl = amountState.footerIconLink,
                    infoText = amountState.footerText
                )

                else -> AmountCaptionInfo(
                    iconUrl = amountState.errorIconLink,
                    infoText = errorMessage,
                    isClickable = true,
                    onKnowMoreClick = {
                        onKnowMoreClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun AmountCaptionInfo(
    iconUrl: String,
    infoText: String,
    isClickable: Boolean = false,
    onKnowMoreClick: (Int) -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        JarImage(
            modifier = Modifier.size(14.dp),
            imageUrl = iconUrl,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(4.dp))
        if (isClickable) {
            ClickableText(
                text = infoText.generateAnnotatedFromHtmlString(),
                style = JarTypography.caption,
                maxLines = 2,
                onClick = onKnowMoreClick
            )
        } else {
            Text(
                text = infoText.generateAnnotatedFromHtmlString(),
                style = JarTypography.caption,
                maxLines = 2
            )
        }
    }
}

@Stable
internal data class AmountState(
    val withdrawalLimit: Float,
    val headerText: String,
    val footerText: String,
    val rupeeSymbol: String,
    val minAmountError: String,
    val maxAmountError: String,
    val footerIconLink: String,
    val errorIconLink: String,
    val currentGoldPriceValue: Float
)
