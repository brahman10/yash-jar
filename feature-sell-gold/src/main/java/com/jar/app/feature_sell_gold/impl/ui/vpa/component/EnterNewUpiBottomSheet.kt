@file:OptIn(FlowPreview::class, FlowPreview::class)

package com.jar.app.feature_sell_gold.impl.ui.vpa.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_272239
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_7745FF
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_776E9433
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_789BDE
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_ACA1D3FF
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_EB6A6E
import com.jar.app.core_base.util.isFirstCharacterSpecial
import com.jar.app.core_base.util.isValidVpa
import com.jar.app.core_compose_ui.component.JarCommonBoldText
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_sell_gold.impl.ui.vpa.UpiVerificationLottie
import com.jar.app.feature_sell_gold.shared.MR.strings.cancel
import com.jar.app.feature_sell_gold.shared.MR.strings.enter_new_upi_id
import com.jar.app.feature_sell_gold.shared.MR.strings.incorrect_upi_format
import com.jar.app.feature_sell_gold.shared.MR.strings.please_add_registered_upi
import com.jar.app.feature_sell_gold.shared.MR.strings.upi_cant_have_special_chars
import com.jar.app.feature_sell_gold.shared.MR.strings.verify_and_add
import com.jar.app.feature_sell_gold.shared.domain.models.UpiVerificationStatus
import com.jar.app.feature_settings.domain.model.VpaChips
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@Composable
internal fun EnterNewUpiBottomSheet(
    vpaChips: VpaChips?,
    upiVerificationStatus: UpiVerificationStatus?,
    modifier: Modifier = Modifier,
    onCancelClick: () -> Unit,
    onVerifyClick: (String) -> Unit,
    onLottieEnd: () -> Unit
) {
    var isLottiePlayedSuccessfully by remember(upiVerificationStatus?.verifyUpiResponse) {
        mutableStateOf(false)
    }

    var enteredUpiId by remember { mutableStateOf("") }
    var isValidVpa by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .background(
                colorResource(id = color_272239.resourceId),
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
            .fillMaxWidth()
            .heightIn(max = 308.dp)
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            var isVpaFieldCleared by remember(upiVerificationStatus) { mutableStateOf(false) }

            LaunchedEffect(vpaChips) { enteredUpiId = "" }

            Row(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                JarCommonBoldText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = enter_new_upi_id.resourceId),
                    style = JarTypography.h5,
                    color = Color.White
                )
                TextButton(
                    onClick = onCancelClick
                ) {
                    Text(
                        text = stringResource(id = cancel.resourceId),
                        style = JarTypography.caption,
                        color = colorResource(id = color_ACA1D3FF.resourceId)
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            UpiInputTextField(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                value = enteredUpiId,
                onValueChange = { newVpa, isValid ->
                    enteredUpiId = newVpa
                    isValidVpa = isValid
                },
                onClearClick = {
                    errorMessage = ""
                    enteredUpiId = ""
                    isVpaFieldCleared = true
                }
            )

            val context = LocalContext.current
            LaunchedEffect(enteredUpiId, upiVerificationStatus) {
                snapshotFlow { enteredUpiId }.debounce(300).collect { enteredText ->
                    errorMessage = when {
                        enteredText.isBlank() -> {
                            isVpaFieldCleared = true
                            ""
                        }
                        upiVerificationStatus?.isError == true && !isVpaFieldCleared ->
                            please_add_registered_upi.getString(context)

                        enteredText.isFirstCharacterSpecial -> upi_cant_have_special_chars
                            .getString(context)

                        !enteredUpiId.contains("@") && !enteredUpiId.isValidVpa ->
                            incorrect_upi_format.getString(context)

                        else -> ""
                    }
                }
            }

            Crossfade(
                modifier = Modifier.animateContentSize(),
                targetState = errorMessage,
                label = ""
            ) { error ->
                when {
                    error.isBlank() -> vpaChips?.vpaChips?.let { vpas ->
                        LazyRow(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(vpas.filter { it.contains(enteredUpiId.substringAfter("@")) }) { vpaText ->
                                VpaChip(
                                    modifier = Modifier.clickable {
                                        enteredUpiId =
                                            enteredUpiId.replaceAfter("@", vpaText.drop(1))
                                    },
                                    text = vpaText
                                )
                            }
                        }
                    }

                    else -> Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Image(
                            painterResource(id = R.drawable.core_ui_ic_error),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = errorMessage,
                            style = JarTypography.caption,
                            color = colorResource(id = color_EB6A6E.resourceId),
                            maxLines = 1
                        )
                    }
                }
            }


            JarPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = stringResource(id = verify_and_add.resourceId),
                onClick = {
                    isVpaFieldCleared = false
                    onVerifyClick(enteredUpiId)
                },
                isAllCaps = false,
                isEnabled = enteredUpiId.isValidVpa
            )
        }

        Crossfade(
            modifier = Modifier
                .background(colorResource(id = color_272239.resourceId))
                .fillMaxWidth()
                .heightIn(max = 308.dp),
            targetState = upiVerificationStatus, label = ""
        ) { status ->
            when {
                status?.isLoading == true -> Box(
                    modifier = Modifier
                        .background(colorResource(id = color_272239.resourceId))
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 48.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                }

                status?.verifyUpiResponse != null && !isLottiePlayedSuccessfully ->
                    UpiVerificationLottie(
                        modifier = Modifier
                            .background(colorResource(id = color_272239.resourceId))
                            .fillMaxSize()
                    ) {
                        isLottiePlayedSuccessfully = true
                        enteredUpiId = ""
                        onLottieEnd()
                    }

            }
        }
    }
}

@Composable
private fun UpiInputTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String, Boolean) -> Unit,
    onClearClick: () -> Unit
) {
    var currentText by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        BasicTextField(
            value = value,
            onValueChange = { newText ->
                currentText = newText
                onValueChange(currentText, currentText.isValidVpa)
            },
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    spotColor = when {
                        value.isValidVpa || value.isBlank() -> colorResource(id = color_7745FF.resourceId)
                        else -> colorResource(id = color_EB6A6E.resourceId)
                    },
                    ambientColor = when {
                        value.isValidVpa || value.isBlank() -> colorResource(id = color_7745FF.resourceId)
                        else -> colorResource(id = color_EB6A6E.resourceId)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = when {
                        value.isValidVpa || value.isBlank() -> colorResource(id = color_7745FF.resourceId)
                        else -> colorResource(id = color_EB6A6E.resourceId)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    color = colorResource(id = color_2E2942.resourceId),
                    shape = RoundedCornerShape(size = 12.dp)
                )
                .padding(
                    start = 12.dp,
                    top = 22.dp,
                    end = 36.dp,
                    bottom = 22.dp
                ),
            singleLine = true,
            cursorBrush = SolidColor(colorResource(id = color_789BDE.resourceId)),
            textStyle = JarTypography.body2.copy(
                color = Color.White
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() },
            )
        )

        IconButton(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(16.dp)
                .align(Alignment.CenterEnd),
            onClick = onClearClick
        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ic_cross_small),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun VpaChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = colorResource(id = color_776E9433.resourceId),
                shape = RoundedCornerShape(size = 8.dp)
            )
            .height(34.dp)
            .background(
                color = colorResource(id = color_2E2942.resourceId),
                shape = RoundedCornerShape(size = 8.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = JarTypography.caption,
            color = colorResource(id = color_ACA1D3FF.resourceId)
        )
    }
}