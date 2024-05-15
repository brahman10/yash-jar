package com.jar.app.feature_goal_based_saving.impl.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.HeaderCircleIconComponentWithUrl
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.core_compose_ui.views.LabelValueComposeView
import com.jar.app.core_compose_ui.views.RenderDashedLine
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.impl.ui.transaction.TransactionFragmentAction
import com.jar.app.feature_goal_based_saving.impl.ui.transaction.TransactionFragmentViewModel
import com.jar.app.feature_goal_based_saving.shared.data.model.ManualPaymentStatus
import com.jar.app.feature_goal_based_saving.shared.data.model.OrderDetails
import com.jar.app.feature_goal_based_saving.shared.data.model.StatusDetails

@Composable
internal fun PaymentStatusScreen(viewModel: TransactionFragmentViewModel) {
    val state = viewModel.state.collectAsState()
    val data = state.value.OnData
    val cardBg = Color(android.graphics.Color.parseColor("#3c3357"))
    val paymentStatus = ManualPaymentStatus.fromString(data?.status)
    val isExpanded = remember { mutableStateOf<Boolean>(true) }
    val defaultModifier: Modifier = Modifier
        .padding(
            horizontal = 12.dp,
        )
        .background(cardBg)
    if (state.value.OnData != null) {
        LazyColumn(
            modifier = Modifier.background(color = Color(0xFF272239))
        ) {
            item {
                HeaderCircleIconComponentWithUrl(
                    defaultModifier,
                    data?.statusIcon ?: "",
                    topPadding = 20.dp
                ) {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                }
            }
            data?.title?.let { title ->
                item {
                    Box(modifier = defaultModifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier
                                .padding(start = 50.dp, end = 50.dp)
                                .fillMaxWidth()
                                .background(colorResource(id = com.jar.app.core_ui.R.color.color_3c3357)),
                            text = "${title}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                TopSectionDetails(
                    modifier = defaultModifier,
                    statusDetails = data?.statusDetails,
                    data?.statusIcon ?: "",
                    paymentStatus,
                    viewModel,
                    data?.statusDetails?.invoiceLink
                )
            }
            item {
                RenderRowWithDashedLine(
                    defaultModifier
                        .background(cardBg)
                        .padding(top = 10.dp, bottom = 16.dp)
                )
            }

            data?.setupSavingsGoalDetails?.goalDetailList?.let {
                item {
                    PolicyDetails(
                        modifier = defaultModifier.padding(top = 10.dp),
                        "${data?.setupSavingsGoalDetails?.header}",
                        it.map {
                            LabelAndValueCompose(
                                it.key ?: "",
                                it.value ?: "",
                                it.copy ?: false,
                                valueTextStyle = JarTypography.body1.copy(
                                    color = Color.White
                                )
                            )
                        }
                    )
                }
            }


            data?.orderDetails?.let { orderDetails ->
                item {
                    TransactionDetailsView(
                        modifier = defaultModifier.padding(vertical = 16.dp),
                        orderDetails,
                        viewModel
                    )
                }
            }

            item {
                GoToHomeSection(modifier = Modifier, viewModel, "${data?.buttonCta?.text}",paymentStatus)
            }
        }
    }
}

@Composable
internal fun RenderCelebratationLottie() {
    val composition = rememberLottieComposition(LottieCompositionSpec.Url(BaseConstants.LottieUrls.CONFETTI_FROM_TOP))
    val progress = animateLottieCompositionAsState(composition.value, iterations = 1)

    LottieAnimation(
        modifier = Modifier.fillMaxSize(),
        composition = composition.value,
        progress = { progress.value },
        contentScale = ContentScale.FillWidth,
    )
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun RenderRowWithDashedLine(modifier: Modifier = Modifier) {
    val color: Color = colorResource(id = com.jar.app.core_ui.R.color.color_272239)
    val boxSize: Dp = 20.dp
    Row(
        modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = com.jar.app.core_ui.R.drawable.semi_circle_fixed),
            "",
            modifier = Modifier
                .zIndex(10f)
                .height(20.dp)
                .width(boxSize / 2),
            alignment = Alignment.CenterStart,
            colorFilter = ColorFilter.tint(color)
        )
        RenderDashedLine(
            Modifier.weight(1f)
        )
        Image(
            painter = painterResource(id = com.jar.app.core_ui.R.drawable.semi_circle_fixed),
            "",
            modifier = Modifier
                .zIndex(10f)
                .height(boxSize)
                .width(boxSize / 2)
                .rotate(180f),
            alignment = Alignment.CenterEnd,
            colorFilter = ColorFilter.tint(color),
            contentScale = ContentScale.FillBounds
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun TopSectionDetails(
    modifier: Modifier = Modifier,
    statusDetails: StatusDetails?,
    statusIcon: String,
    paymentStatus: ManualPaymentStatus,
    viewModel: TransactionFragmentViewModel,
    invoiceLink: String?
) {
    PaymentInfoCard(modifier = modifier.padding(top = 24.dp)) {
        Column(
            modifier = Modifier
                .padding(16.dp)
            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ImageWithBadge(imageUrl = "${statusDetails?.goalImage}", badgeUrl = statusIcon)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 15.dp)
                ) {
                    Text(
                        text = "${statusDetails?.goalName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Start,
                    )
                    statusDetails?.details?.let {
                        if (it.isNotEmpty() && it.size == 1) {
                            Text(
                                text = "${it.get(0).key}",
                                modifier = Modifier,
                                fontSize = 12.sp,
                                color = Color(0xFFACA1D3),
                                textAlign = TextAlign.Start,
                            )
                        }
                    }

                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 15.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.End),
                        text = "${statusDetails?.amount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.End,
                    )
                    statusDetails?.details?.let {
                        if (it.isNotEmpty() && it.size == 1) {
                            Text(
                                modifier = Modifier.align(Alignment.End),
                                text = "${it.get(0).value}",
                                fontSize = 12.sp,
                                color = Color(0xFFACA1D3),
                                textAlign = TextAlign.End,
                            )
                        }
                    }
                }
            }
        }
        if (paymentStatus == ManualPaymentStatus.SUCCESS && invoiceLink != null) {
            Divider(
                color = Color(0xFF463C69),
                modifier = modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(top = 20.dp)
            )
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 20.dp, top = 20.dp)
                    .debounceClickable {
                        viewModel.handleActions(
                            TransactionFragmentAction.OnClickOnDownloadInvoice
                        )
                    }
            ) {
                Image(modifier = Modifier
                    .size(30.dp)
                    .padding(start = 12.dp)
                    .align(Alignment.CenterVertically),
                    painter = painterResource(id = R.drawable.download_icon), contentDescription =  "")
                val underlinedText = AnnotatedString.Builder("Download Invoice")
                    .apply {
                        addStyle(
                            style = SpanStyle(textDecoration = TextDecoration.Underline),
                            start = 0,
                            end = "Download Invoice".length
                        )
                    }
                    .toAnnotatedString()
                Text(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .align(Alignment.CenterVertically),
                    text = underlinedText,
                    color = Color.White,
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageWithBadge(
    imageUrl: String, badgeUrl: String, badgeOffset: Dp = 6.dp
) {
    Box() {
        CircularLayout() {
            GlideImage(
                modifier = Modifier
                    .size(40.dp)
                    .padding(3.dp),
                model = imageUrl,
                contentDescription = "shield"
            )
        }
        CircularLayout(
            modifier = Modifier
                .offset(x = badgeOffset, y = badgeOffset)
                .align(Alignment.BottomEnd)
                .zIndex(10f)
                .clipToBounds(),
            backgroundColor = Color(0xFF2E2942)
        ) {
            GlideImage(
                model = badgeUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(2.dp)
                    .size(19.dp),
                contentScale = ContentScale.Fit
            )
        }

    }
}

@Composable
fun CircularLayout(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF423C5C),
    content: @Composable () -> Unit
) {
    Layout(
        content = {
            content()
        },
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
    ) { measurable, constraints ->
        val placeable = measurable.first().measure(constraints)
        val size = maxOf(placeable.width, placeable.height)
        layout(size, size) {
            placeable.place((size - placeable.width) / 2, (size - placeable.height) / 2)
        }
    }
}

@Composable
fun PaymentInfoCard(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .background(
                color = Color(0xFF2E2942), shape = RoundedCornerShape(size = 10.dp)
            )
    ) {
        content()
    }
}

@Composable
@Preview
fun SavingGoalDetails(modifier: Modifier = Modifier) {
    PaymentInfoCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent)
        ) {
            Text(
                text = "Saving goal Details",
                fontSize = 12.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFFD5CDF2)
            )
            LabelValueComposeView(
                modifier = Modifier.padding(top = 24.dp),
                listOf(
                    LabelAndValueCompose("Saving for", "iphone")
                )
            )
        }
    }
}

@Composable
fun PolicyDetails(
    modifier: Modifier = Modifier,
    header: String,
    list: List<LabelAndValueCompose>
) {
    PaymentInfoCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Transparent)
        ) {
            Text(
                text = "$header",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD5CDF2)
            )
            LabelValueComposeView(
                modifier = Modifier.padding(top = 24.dp),
                list
            )
        }
    }
}

@Composable
internal fun TransactionDetailsView(
    modifier: Modifier = Modifier,
    orderDetails: OrderDetails,
    viewModel: TransactionFragmentViewModel,
) {
    PaymentInfoCard(modifier = modifier.fillMaxWidth()) {
        TransactionCardContent(orderDetails, viewModel)
    }
}


@Composable
private fun TransactionCardContent(
    orderDetails: OrderDetails,
    viewModel: TransactionFragmentViewModel
) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Column {
        Row(modifier = Modifier
            .padding(20.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable(
                enabled = true, indication = null, interactionSource = interactionSource
            ) {
                expanded.value = !expanded.value
            }) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Top),
                text = "${orderDetails.header}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD5CDF2)
            )

            Icon(
                modifier = Modifier.clickable {
                    expanded.value = !expanded.value
                    viewModel.handleActions(
                                                 TransactionFragmentAction.OnOrderSectionChevronClicked
                                             )
                },
                tint = Color(0xFFD5CDF2),
                imageVector = if (expanded.value) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded.value) {
                    "SHOW LESS"
                } else {
                    "SHOW MORE"
                },
            )

        }
        if (expanded.value) {
            Column {
                if (orderDetails.priceSubHeader != null) {
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .align(Alignment.Start),
                        text = "${orderDetails.priceSubHeader}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFD5CDF2)
                    )
                    orderDetails?.priceDetailList?.let {
                        LabelValueComposeView(
                            modifier = Modifier.padding(start = 20.dp, top = 15.dp, end = 20.dp),
                            list = it.map {
                                LabelAndValueCompose(
                                    it.key ?: "",
                                    it.value ?: "",
                                    it.copy ?: false,
                                    valueTextStyle = JarTypography.body1.copy(
                                        color = Color.White
                                    ),
                                )
                            }
                        )
                    }
                }


                if (orderDetails.txnSubHeader != null) {
                    if (orderDetails.priceSubHeader != null){
                        Divider(
                            color = Color(0xFF463C69),
                            modifier = Modifier
                                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
                                .height(1.dp)
                        )
                    }
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 15.dp)
                            .align(Alignment.Start),
                        text = "${orderDetails.txnSubHeader}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFD5CDF2)
                    )
                    orderDetails.txnDetailList?.let {
                        LabelValueComposeView(
                            modifier = Modifier.padding(start = 20.dp, top = 15.dp, end = 20.dp),
                            list = it.map {
                                LabelAndValueCompose(
                                    it.key ?: "",
                                    it.value ?: "",
                                    it.copy ?: false,
                                    valueTextStyle = JarTypography.body1.copy(
                                        color = Color.White
                                    ),
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun GoToHomeSection(
    modifier: Modifier = Modifier,
    viewModel: TransactionFragmentViewModel,
    buttonCta: String,
    paymentStatus: ManualPaymentStatus
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        if (paymentStatus == ManualPaymentStatus.SUCCESS) {
            JarSecondaryButton(modifier = Modifier.fillMaxWidth(), isAllCaps = false, color = Color(
                android.graphics.Color.parseColor("#6637E4")
            ), borderColor = Color(
                android.graphics.Color.parseColor("#845EE9")
            ), text = buttonCta, onClick = {
                viewModel
                    .handleActions(
                        TransactionFragmentAction.OnTrackMyGoal
                    )
            })
        } else if (paymentStatus == ManualPaymentStatus.PENDING){
            JarSecondaryButton(modifier = Modifier.fillMaxWidth(), text = buttonCta, onClick = {
                viewModel
                    .handleActions(
                        TransactionFragmentAction.OnClickOnGoToHome
                    )
            },isAllCaps = false, color = Color(
                android.graphics.Color.parseColor("#6637E4")
            ), borderColor = Color(
                android.graphics.Color.parseColor("#855FE9")
            ))
        } else if (paymentStatus == ManualPaymentStatus.FAILED){
            JarSecondaryButton(modifier = Modifier.fillMaxWidth(), text = buttonCta, onClick = {
                viewModel
                    .handleActions(
                        TransactionFragmentAction.OnClickOnGoToHome
                    )
            }, isAllCaps = false)
        }

        if (paymentStatus != ManualPaymentStatus.SUCCESS) {
            Divider(
                color = Color(0xFF463C69),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(1.dp)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Need help?",
                fontSize = 16.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
                    .debounceClickable {
                        viewModel.handleActions(
                            TransactionFragmentAction.OnClickOnContactUs
                        )
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.contact_us_icon),
                    contentDescription = "chat",
                )
                Text(
                    text = "Contact Support",
                    fontSize = 14.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
                Image(
                    modifier = Modifier.size(25.dp),
                    painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_right_chevron),
                    contentDescription = "arrow",
                )
            }
        }

    }
}

@Composable
internal fun RenderLeftIconContent(status: TxnRoutineState, strokedBorderVisible: Boolean = true) {
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f)
    )

    Box(Modifier
        .size(32.dp)
        .then(if (strokedBorderVisible) {
            Modifier
                .drawBehind {
                    drawCircle(color = Color.White, radius = 34.dp.value, style = stroke)
                }
        } else Modifier),
        contentAlignment = Alignment.Center) {
        Row {
            Icon(
                painter = painterResource(id = getIconForStatus(status)),
                contentDescription = "",
                tint = Color.Unspecified,
                modifier = Modifier
                    .then(
                        if (strokedBorderVisible) Modifier
                            .size(26.dp)
                            .padding(2.dp) else Modifier.fillMaxSize()
                    )

            )
        }

        
    }
}

@DrawableRes
fun getIconForStatus(status: TxnRoutineState): Int {
    return when (status) {
        TxnRoutineState.COMPLETED -> com.jar.app.core_ui.R.drawable.core_ui_icon_check_filled
        TxnRoutineState.PROCESSING, TxnRoutineState.INACTIVE  -> R.drawable.feature_goal_based_saving_hourglass
        TxnRoutineState.FAILED, TxnRoutineState.INVALID -> R.drawable.feature_goal_based_gaving_failed
        TxnRoutineState.SCHEDULED -> R.drawable.feature_goal_based_saving_hourglass
    }
}

@Composable
fun colorForDivider(status: TxnRoutineState?, nextStatus: TxnRoutineState?): Color {
    if (nextStatus == null) return colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)
    return when (status) {
        TxnRoutineState.COMPLETED  -> colorResource(id = com.jar.app.core_ui.R.color.color_1EA787)
        TxnRoutineState.PROCESSING, TxnRoutineState.INACTIVE  -> colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)
        TxnRoutineState.FAILED, TxnRoutineState.INVALID   -> colorResource(id = com.jar.app.core_ui.R.color.color_EB6A6E)
        TxnRoutineState.SCHEDULED ->  colorResource(id = com.jar.app.core_ui.R.color.color_1EA787)
        else -> {
            colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)
        }
    }
}

enum class TxnRoutineState {
    COMPLETED, FAILED, PROCESSING, INACTIVE, INVALID, SCHEDULED;
    companion object {
        fun fromString(value: String?): TxnRoutineState {
            return TxnRoutineState.values()
                .find { it.name.equals(value, ignoreCase = true) } ?: FAILED
        }
    }
}


