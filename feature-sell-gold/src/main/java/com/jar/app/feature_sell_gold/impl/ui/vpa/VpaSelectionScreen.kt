@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.jar.app.feature_sell_gold.impl.ui.vpa

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_1C192A
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_1F1B2E
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_272239
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_ACA1D31A
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.GoldPriceTicker
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_sell_gold.impl.ui.amount.component.TopAppBar
import com.jar.app.feature_sell_gold.impl.ui.vpa.component.AddNewUpiIdButton
import com.jar.app.feature_sell_gold.impl.ui.vpa.component.BreakdownBottomSheet
import com.jar.app.feature_sell_gold.impl.ui.vpa.component.BreakdownSection
import com.jar.app.feature_sell_gold.impl.ui.vpa.component.EnterNewUpiBottomSheet
import com.jar.app.feature_sell_gold.impl.ui.vpa.component.UpiDeletionMessage
import com.jar.app.feature_sell_gold.impl.ui.vpa.component.UpiIdRadioGroup
import com.jar.app.feature_sell_gold.shared.MR.strings.select_upi_id
import com.jar.app.feature_sell_gold.shared.MR.strings.upi_id_verified_successfully
import com.jar.app.feature_sell_gold.shared.domain.models.GoldPriceState
import com.jar.app.feature_sell_gold.shared.domain.models.UpiVerificationStatus
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawRequest
import com.jar.app.feature_settings.domain.model.VpaChips
import com.jar.app.feature_user_api.domain.model.SavedVPA
import com.jar.app.feature_user_api.domain.model.SavedVpaResponse
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.launch

@Composable
internal fun VpaSelectionScreen(
    withdrawalPrice: String,
    volumeFromAmount: Float,
    userSavedVpas: SavedVpaResponse?,
    vpaChips: VpaChips?,
    upiVerificationStatus: UpiVerificationStatus?,
    goldPriceState: GoldPriceState?,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onVerifyUpiClick: (String) -> Unit,
    onConfirmWithdrawalClick: (withDrawRequest: WithdrawRequest, selectedVpa: SavedVPA) -> Unit
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(colorResource(id = color_2E2942.resourceId))
    systemUiController.setNavigationBarColor(colorResource(id = color_1C192A.resourceId))

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    var bottomSheetType by remember { mutableStateOf(BottomSheetType.AddNewUpiId) }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val focusRequester = remember { FocusRequester() }
    var selectedVpa by remember { mutableStateOf<SavedVPA?>(null) }

    BackHandler(bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            bottomSheetState.hide()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.background(colorResource(id = color_2E2942.resourceId)),
                    title = stringResource(id = select_upi_id.resourceId),
                    onBackClick = onBackClick
                )
                Divider(
                    thickness = 1.dp,
                    color = colorResource(id = color_ACA1D31A.resourceId)
                )
            }
        },
        containerColor = colorResource(id = color_1F1B2E.resourceId),
        sheetContent = {
            val focusManager = LocalFocusManager.current
            LaunchedEffect(bottomSheetState.currentValue) {
                if (bottomSheetState.currentValue == SheetValue.Hidden) {
                    focusManager.clearFocus()
                }
            }

            when (bottomSheetType) {
                BottomSheetType.AddNewUpiId -> EnterNewUpiBottomSheet(
                    vpaChips = vpaChips,
                    upiVerificationStatus = upiVerificationStatus,
                    modifier = Modifier
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .focusable(true)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.hasFocus) {
                                scope.launch { bringIntoViewRequester.bringIntoView() }
                            }
                        },
                    onCancelClick = {
                        scope.launch {
                            bottomSheetState.hide()
                            focusManager.clearFocus()
                        }
                    },
                    onVerifyClick = {
                        onVerifyUpiClick(it)
                        scope.launch {
                            focusManager.clearFocus()
                        }
                    },
                    onLottieEnd = {
                        scope.launch {
                            bottomSheetState.hide()
                        }
                    }
                )

                BottomSheetType.ViewPriceBreakDown -> BreakdownBottomSheet(
                    goldSellPrice = goldPriceState?.goldPrice.orZero(),
                    volumeFromAmount = volumeFromAmount,
                    withdrawalPrice = withdrawalPrice,
                    onCloseClick = {
                        scope.launch {
                            bottomSheetState.hide()
                        }
                    }
                )
            }
        },
        sheetPeekHeight = 0.dp,
        sheetContainerColor = colorResource(id = color_272239.resourceId),
        sheetDragHandle = null,
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                userSavedVpas?.payoutSavedVpas?.let { savedVpas ->
                    if (savedVpas.isNotEmpty()) {
                        LaunchedEffect(Unit) {
                            selectedVpa = savedVpas.first()
                        }
                        UpiIdRadioGroup(
                            upiIds = savedVpas,
                            onVpaSelected = { selectedVpa = it }
                        )
                    }
                    if (savedVpas.size < 5) {
                        AddNewUpiIdButton(
                            onClick = {
                                bottomSheetType = BottomSheetType.AddNewUpiId
                                scope.launch {
                                    bottomSheetState.expand()
                                    focusRequester.requestFocus()
                                }
                            }
                        )
                    } else if (savedVpas.size == 5) {
                        Spacer(modifier = Modifier.size(12.dp))
                        UpiDeletionMessage()
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                GoldPriceTicker(
                    goldPrice = goldPriceState?.goldPrice.orZero(),
                    validityInSeconds = goldPriceState?.validityInSeconds.orZero(),
                    currentSecond = goldPriceState?.millisLeft?.milliseconds?.inWholeSeconds.orZero()
                )

                BreakdownSection(
                    withdrawalPrice = withdrawalPrice,
                    modifier = Modifier,
                    isEnabled = goldPriceState?.millisLeft?.milliseconds?.inWholeSeconds.orZero() > 10,
                    onConfirmClick = {
                        selectedVpa?.let { vpa ->
                            onConfirmWithdrawalClick(
                                WithdrawRequest(
                                    amount = withdrawalPrice.toFloat(),
                                    apiResponse = FetchCurrentGoldPriceResponse(
                                        price = goldPriceState?.goldPrice.orZero(),
                                        rateId = goldPriceState?.rateId.orEmpty(),
                                        rateValidity = goldPriceState?.rateValidity.orEmpty(),
                                        isPriceDrop = goldPriceState?.isPriceDrop.orFalse(),
                                        validityInSeconds = goldPriceState?.validityInSeconds.orZero()
                                    ),
                                    instrumentType = "GOLD",
                                    savedVpaId = selectedVpa?.id,
                                    volume = volumeFromAmount,
                                    withDrawlType = "AMOUNT",
                                    type = "UPI"
                                ),
                                vpa
                            )
                        }
                    },
                    onViewBreakdownClick = {
                        bottomSheetType = BottomSheetType.ViewPriceBreakDown
                        scope.launch { bottomSheetState.expand() }
                    }
                )
            }

            // Background Scrim
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = bottomSheetState.currentValue == SheetValue.Expanded
            ) {
                Spacer(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures {
                                scope.launch {
                                    bottomSheetState.hide()
                                }
                            }
                        }
                        .fillMaxSize()
                        .background(Color(0xB2000000))
                )
            }

        }
    }
}


@Composable
fun UpiVerificationLottie(
    modifier: Modifier = Modifier,
    onLottieEnd: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.Url("${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json")
        )

        val lottieAnimationState = animateLottieCompositionAsState(
            composition = composition,
            isPlaying = true
        )

        if (lottieAnimationState.isAtEnd && lottieAnimationState.progress == 1f) {
            LaunchedEffect(Unit) {
                onLottieEnd()
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimation(
                modifier = Modifier,
                composition = composition,
                progress = { lottieAnimationState.progress }
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(id = upi_id_verified_successfully.resourceId),
                style = JarTypography.h5,
                color = Color.White
            )
        }
    }
}

private enum class BottomSheetType {
    ViewPriceBreakDown, AddNewUpiId
}