@file:OptIn(ExperimentalFoundationApi::class)

package com.jar.app.feature_sell_gold.impl.ui.amount

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_000000B2
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_272239
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_27223900
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_ACA1D31A
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.GoldPriceTicker
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.generateAnnotatedFromHtmlString
import com.jar.app.core_compose_ui.utils.keyboardAsState
import com.jar.app.feature_sell_gold.impl.ui.amount.component.AmountState
import com.jar.app.feature_sell_gold.impl.ui.amount.component.EnterAmount
import com.jar.app.feature_sell_gold.impl.ui.amount.component.PendingIdVerificationCard
import com.jar.app.feature_sell_gold.impl.ui.amount.component.TopAppBar
import com.jar.app.feature_sell_gold.impl.ui.amount.component.ValuePropCarousel
import com.jar.app.feature_sell_gold.impl.ui.amount.component.ValuePropCarouselCard
import com.jar.app.feature_sell_gold.impl.ui.amount.component.VerificationBottomSheet
import com.jar.app.feature_sell_gold.impl.ui.amount.component.VerificationStatusState
import com.jar.app.feature_sell_gold.impl.ui.amount.component.WithdrawalDetails
import com.jar.app.feature_sell_gold.impl.ui.amount.component.toComposeColor
import com.jar.app.feature_sell_gold.shared.MR.strings.proceed
import com.jar.app.feature_sell_gold.shared.MR.strings.withdraw
import com.jar.app.feature_sell_gold.shared.domain.models.DrawerDetailsResponse
import com.jar.app.feature_sell_gold.shared.domain.models.GoldPriceState
import com.jar.app.feature_sell_gold.shared.domain.models.KycDetailsResponse
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AmountEntryScreen(
    goldPriceState: GoldPriceState?,
    volumeFromAmount: Float,
    drawerDetailsResponse: DrawerDetailsResponse?,
    kycDetails: KycDetailsResponse?,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onFaqClick: (String) -> Unit,
    onProceedClick: (String, Boolean, Boolean, Int) -> Unit,
    onInitiateIdVerification: (String) -> Unit,
    onVerifyIdClick: () -> Unit,
    onContactUsClick: (String) -> Unit,
    onWithdrawDetailsToggle: () -> Unit,
    onEnteredAmountChange: (Float, Boolean) -> Unit,
    onVerificationBottomSheetToggled: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = colorResource(id = color_272239.resourceId)
    )

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
    var isVerificationBottomSheetVisible by remember { mutableStateOf(false) }
    var hasShownVerificationSheetAtLeastOnce by remember { mutableStateOf(false) }
    var enteredAmount by remember { mutableStateOf("") }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.background(
                        color = colorResource(id = color_272239.resourceId)
                    ),
                    title = stringResource(withdraw.resourceId),
                    onBackClick = onBackClick,
                    endIcon = {
                        Box(
                            modifier = Modifier
                                .size(70.dp, 32.dp)
                                .background(
                                    color = colorResource(id = color_2E2942.resourceId),
                                    shape = RoundedCornerShape(40.dp)
                                )
                                .clickable {
                                    onFaqClick(drawerDetailsResponse?.faqLink.orEmpty())
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = drawerDetailsResponse?.faqText.orEmpty(),
                                style = JarTypography.body2,
                                color = Color.White
                            )
                        }
                    }
                )
                Divider(
                    thickness = 1.dp,
                    color = colorResource(id = color_ACA1D31A.resourceId)
                )
            }
        },
        containerColor = colorResource(id = color_272239.resourceId),
        sheetContent = {
            VerificationBottomSheet(
                kycDetails = kycDetails,
                onCloseClick = {
                    scope.launch {
                        bottomSheetState.hide()
                        isVerificationBottomSheetVisible = false
                    }
                },
                onVerifyClick = {
                    onVerifyIdClick()
                    scope.launch {
                        bottomSheetState.hide()
                        onInitiateIdVerification(enteredAmount)
                    }
                },
                onContactUsClick = {
                    onContactUsClick(kycDetails?.bottomSheet?.contactLink.orEmpty())
                }
            )
        },
        sheetPeekHeight = 0.dp,
        sheetContainerColor = colorResource(id = color_272239.resourceId),
        sheetDragHandle = null,
    ) { contentPadding ->
        var expanded by remember { mutableStateOf(false) }
        var amountTextFieldHasFocus by remember { mutableStateOf(true) }
        var hasEnteredValidAmountOnce by rememberSaveable { mutableStateOf(false) }
        var isCurrentAmountValid by rememberSaveable { mutableStateOf(false) }
        val isKeyboardOpen by keyboardAsState()
        var hasViewedDrawerDetailsOnce by remember { mutableStateOf(false) }
        val valuePropCarouselPagerState = rememberPagerState()

        LaunchedEffect(expanded) {
            if (expanded) hasViewedDrawerDetailsOnce = true
        }

        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                val focusRequester = remember { FocusRequester() }
                drawerDetailsResponse?.drawer?.let { drawer ->
                    WithdrawalDetails(
                        drawer = drawer,
                        expanded = expanded,
                        onToggleExpand = {
                            onWithdrawDetailsToggle()
                            expanded = !expanded
                        }
                    )
                }

                EnterAmount(
                    modifier = Modifier.imePadding(),
                    volumeFromAmount = volumeFromAmount,
                    amountState = AmountState(
                        withdrawalLimit = drawerDetailsResponse?.drawer?.drawerItems
                            ?.find { it.priority == 2 }?.amount.toFloatOrZero(),
                        headerText = drawerDetailsResponse?.inputAmount?.headerText.orEmpty(),
                        footerText = drawerDetailsResponse?.inputAmount?.footerText.orEmpty(),
                        rupeeSymbol = drawerDetailsResponse?.inputAmount?.rupeeSymbol.orEmpty(),
                        minAmountError = drawerDetailsResponse?.inputAmount?.minAmountError.orEmpty(),
                        maxAmountError = drawerDetailsResponse?.inputAmount?.maxAmountError.orEmpty(),
                        errorIconLink = drawerDetailsResponse?.inputAmount?.errorIconLink.orEmpty(),
                        footerIconLink = drawerDetailsResponse?.inputAmount?.footerIconLink.orEmpty(),
                        currentGoldPriceValue = goldPriceState?.goldPrice.orZero()
                    ),
                    verificationStatusState = kycDetails?.verificationState?.let {
                        VerificationStatusState(
                            iconUrl = it.iconLink,
                            text = it.title.generateAnnotatedFromHtmlString(),
                            backgroundColor = it.backgroundColor?.toComposeColor ?: Color.White
                        )
                    },
                    focusRequester = focusRequester,
                    expanded = expanded,
                    onAcquireFocus = { hasFocus ->
                        amountTextFieldHasFocus = hasFocus
                        if (expanded && hasFocus) expanded = false
                    },
                    onAmountEntered = { isValid, amount ->
                        if (isValid) hasEnteredValidAmountOnce = true
                        isCurrentAmountValid = isValid
                        enteredAmount = amount
                    },
                    onKnowMoreClick = {
                        expanded = true
                    },
                    onEnteredAmountChange = { amount ->
                        onEnteredAmountChange(amount, hasShownVerificationSheetAtLeastOnce)
                    }
                )

                kycDetails?.kycStatusCards?.let { kycStatusCards ->
                    PendingIdVerificationCard(
                        modifier = Modifier.padding(16.dp),
                        title = kycStatusCards.title.orEmpty(),
                        description = kycStatusCards.description.orEmpty(),
                        verificationStatusState = VerificationStatusState(
                            iconUrl = kycStatusCards.verificationState?.iconLink.orEmpty(),
                            text = kycStatusCards.verificationState?.title?.generateAnnotatedFromHtmlString(),
                            backgroundColor = kycStatusCards.backgroundColor?.toComposeColor
                                ?: Color.White
                        ),
                        ctaIcon = kycStatusCards.buttonIcon,
                        ctaText = kycStatusCards.buttonText,
                        onCtaClick = { onInitiateIdVerification(enteredAmount) }
                    )
                } ?: run {
                    AnimatedVisibility(
                        visible = hasEnteredValidAmountOnce,
                        enter = fadeIn() + slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetX = { it }
                        )
                    ) {
                        drawerDetailsResponse?.withdrawalCards?.map {
                            ValuePropCarouselCard(
                                description = it.description,
                                iconLink = it.iconLink,
                                backgroundColor = it.backgroundColor
                            )
                        }?.takeIf { it.isNotEmpty() }?.let { cards ->
                            ValuePropCarousel(
                                pagerState = valuePropCarouselPagerState,
                                carouselCards = cards,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorResource(id = color_27223900.resourceId),
                                colorResource(id = color_272239.resourceId)
                            )
                        )
                    )
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.Bottom
            ) {
                GoldPriceTicker(
                    goldPrice = goldPriceState?.goldPrice.orZero(),
                    validityInSeconds = goldPriceState?.validityInSeconds.orZero(),
                    currentSecond = goldPriceState?.millisLeft?.milliseconds?.inWholeSeconds.orZero()
                )

                AnimatedVisibility(
                    visible = !isKeyboardOpen && isCurrentAmountValid,
                ) {
                    JarPrimaryButton(
                        modifier = Modifier
                            .background(colorResource(id = color_272239.resourceId))
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        text = stringResource(id = proceed.resourceId),
                        isAllCaps = false,
                        onClick = {
                            if (enteredAmount.toFloat() > MIN_WITHDRAWAL_AMOUNT_ALLOWED_WITHOUT_ID && kycDetails?.docType == null
                                || kycDetails?.docType == "ID" && enteredAmount.toFloat() > MAX_WITHDRAWAL_AMOUNT_ALLOWED_WITHOUT_PAN
                            ) {
                                scope.launch {
                                    bottomSheetState.expand()
                                }
                                onVerificationBottomSheetToggled()
                                isVerificationBottomSheetVisible = true
                                hasShownVerificationSheetAtLeastOnce = true
                            } else {
                                onProceedClick(
                                    enteredAmount,
                                    hasViewedDrawerDetailsOnce,
                                    hasEnteredValidAmountOnce,
                                    valuePropCarouselPagerState.currentPage
                                )
                            }
                        },
                        isEnabled = isCurrentAmountValid
                    )
                }
            }

            // Background Scrim
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = bottomSheetState.currentValue == SheetValue.Expanded
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = colorResource(id = color_000000B2.resourceId)
                        )
                )
            }
        }
    }
}