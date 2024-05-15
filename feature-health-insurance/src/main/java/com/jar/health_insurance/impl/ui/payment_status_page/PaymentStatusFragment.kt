package com.jar.health_insurance.impl.ui.payment_status_page

import android.os.Bundle
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.OpenHealthInsuranceMemberSubmitFormEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.util.cast
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.base.BaseViewState
import com.jar.app.core_compose_ui.component.ButtonType
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.views.CircularLayout
import com.jar.app.core_compose_ui.views.HeaderCircleIconComponentWithUrl
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.core_compose_ui.views.LabelValueComposeView
import com.jar.app.core_compose_ui.views.RenderDashedLine
import com.jar.app.core_ui.R
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceCTAAction
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.InsurancePolicyDetails
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.PaymentStatus
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.PaymentStatusResponse
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.TransactionDetails
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Insurance_SuccessShown
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Post_Purchase
import com.jar.app.feature_health_insurance.shared.util.Constants
import com.jar.health_insurance.impl.ui.components.HtmlText
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@OptIn(ExperimentalGlideComposeApi::class)
@AndroidEntryPoint
class PaymentStatusFragment : BaseComposeFragment() {

    private val args by navArgs<PaymentStatusFragmentArgs>()

    private val viewModel by viewModels<PaymentStatusViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onTriggerEvent(PaymentStatusEvent.fetchPaymentStatus(args.insuranceId))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.color_141021)
    }

    @Composable
    @Preview
    override fun RenderScreen() {
        val paymentStatusState by viewModel.uiState.collectAsState()
        val systemUiController = rememberSystemUiController()
        systemUiController.setNavigationBarColor(Color(0xFF272239))

        when (paymentStatusState) {
            is BaseViewState.Data -> {
                val renderCelebrationLottie by remember { mutableStateOf(false) }
                Box(Modifier.fillMaxSize()) {
                    PaymentStatusScreen(paymentStatusState)
                    if (renderCelebrationLottie) RenderCelebratationLottie()
                }
            }

            is BaseViewState.Empty -> {
            }

            is BaseViewState.Error -> {
            }

            is BaseViewState.Loading -> {
            }
        }

    }

    @Composable
    fun PaymentStatusScreen(paymentStatusState: BaseViewState<*>) {

        val paymentStatus by remember {
            mutableStateOf(paymentStatusState.cast<BaseViewState.Data<PaymentStatusState>>().value.paymentStatus)
        }
        val planType = paymentStatus?.insurancePolicyDetails?.data?.get(0)?.value
        analyticsHandler.postEvent(
            HealthInsuranceEvents.Health_Insurance_Event,
            mapOf(
                HealthInsuranceEvents.Plan_Details to planType.orEmpty(),
                HealthInsuranceEvents.Payment_type to paymentStatus?.premiumType.orEmpty(),
                HealthInsuranceEvents.Status to paymentStatus?.status.orEmpty(),
                HealthInsuranceEvents.TRANSACTION_DETAILS to paymentStatus?.transactionDetails.toString(),
                HealthInsuranceEvents.EVENT_NAME to Insurance_SuccessShown,
            )
        )

        val cardBg = colorResource(id = R.color.color_3c3357)
        val isExpanded = remember { mutableStateOf<Boolean>(true) }
        val defaultModifier: Modifier = Modifier
            .padding(
                horizontal = 12.dp,
            )
            .background(cardBg)
        Scaffold(
            containerColor = Color(0xFF272239)
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)

            ) {
                paymentStatus?.statusImgUrl?.let { statusIcon ->
                    item {
                        HeaderCircleIconComponentWithUrl(
                            defaultModifier,
                            statusIcon,
                            topPadding = 20.dp,

                            ) {
                            if (paymentStatus?.status == PaymentStatus.SUCCESS.name) {
                                RenderCelebratationLottie()
                            }
                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            )
                        }
                    }
                }

                paymentStatus?.statusMessage?.let { statusMessage ->
                    item {
                        Text(
                            modifier = defaultModifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            text = statusMessage,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                    }
                }

                item {
                    paymentStatus?.let { TopSectionDetails(modifier = defaultModifier, it) }
                }
                item {
                    RenderRowWithDashedLine(
                        defaultModifier
                            .background(cardBg)
                            .padding(top = 16.dp, bottom = 16.dp)
                    )
                }
                paymentStatus?.insurancePolicyDetails?.let { policyDetails ->
                    item {
                        PolicyDetails(
                            modifier = defaultModifier.padding(top = 16.dp),
                            policyDetails
                        )
                    }
                }
                paymentStatus?.transactionDetails?.let { transactionDetails ->

                    item {
                        TransactionDetailsView(
                            modifier = defaultModifier.padding(vertical = 16.dp),
                            transactionDetails
                        )
                    }
                }
                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .padding(horizontal = 12.dp)
                            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                            .background(color = colorResource(id = R.color.color_3c3357))

                    ) {


                    }
                }


                item {
                    paymentStatus?.let {
                        GoToHomeSection(
                            modifier = Modifier.fillMaxWidth(),
                            it
                        )
                    }

                }

            }

        }

    }

    @Composable
    internal fun RenderCelebratationLottie() {
        val composition by rememberLottieComposition(LottieCompositionSpec.Url(BaseConstants.LottieUrls.CONFETTI_FROM_TOP))
        val progress by animateLottieCompositionAsState(composition, iterations = 1)

        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            composition = composition,
            progress = { progress },
            contentScale = ContentScale.FillWidth,
        )
    }


    @Composable
    @Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
    fun RenderRowWithDashedLine(modifier: Modifier = Modifier) {
        val color: Color = colorResource(id = R.color.color_272239)
        val boxSize: Dp = 20.dp
        Row(
            modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.semi_circle_fixed),
                "",
                modifier = Modifier
                    .zIndex(10f)
                    .height(boxSize)
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


    @Composable
    fun TopSectionDetails(modifier: Modifier = Modifier, paymentStatus: PaymentStatusResponse) {
        PaymentInfoCard(modifier = modifier.padding(top = 24.dp)) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp)
                    .padding(bottom = if (paymentStatus.status.orEmpty() != PaymentStatus.SUCCESS.name) 16.dp else 0.dp)
            ) {
                paymentStatus.insuranceImgUrl?.let { insuranceUrl ->
                    paymentStatus.statusImgUrl?.let { statusUrl ->
                        ImageWithBadge(imageUrl = insuranceUrl, badgeUrl = statusUrl)
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .align(Alignment.CenterVertically),

                    ) {
                    paymentStatus.amountPaidText?.let { amountText ->
                        paymentStatus.amountPaid?.let { amount ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = amountText,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Start,
                                )
                                Text(
                                    text = amount,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.End,
                                )
                            }
                        }
                    }

                    paymentStatus.premiumTypeText?.let { premiumTypeText ->
                        paymentStatus.premiumType?.let { premiumType ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = premiumTypeText,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 12.sp,
                                    color = Color(0xFFACA1D3),
                                    textAlign = TextAlign.Start,
                                )
                                Spacer(modifier = modifier.padding(vertical = 5.dp))
                                Text(
                                    text = premiumType,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 12.sp,
                                    color = Color(0xFFACA1D3),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }


                }
            }
            paymentStatus.ctaText?.let { ctaText ->
                JarPrimaryButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                    text = ctaText,
                    isAllCaps = false,
                    onClick = {
                        when (paymentStatus.status.orEmpty()) {
                            PaymentStatus.SUCCESS.name -> {

                                analyticsHandler.postEvent(
                                    HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                        HealthInsuranceEvents.FromScreen to Post_Purchase,
                                        HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.Add_Member_Details
                                    )
                                )
                                EventBus.getDefault().post(
                                    OpenHealthInsuranceMemberSubmitFormEvent(
                                        paymentStatus.ctaLink.orEmpty()
                                    )
                                )
                            }

                            PaymentStatus.FAILURE.name -> {
                                EventBus.getDefault().post(
                                    HandleDeepLinkEvent(
                                        paymentStatus.ctaLink.orEmpty()
                                    )
                                )
                            }
                        }

                    })
            }
        }
    }


    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun ImageWithBadge(
        imageUrl: String, badgeUrl: String, badgeOffset: Dp = 6.dp,
    ) {
        Box() {
            CircularLayout() {
                JarImage(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(25.dp),
                    imageUrl = imageUrl,
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
                JarImage(
                    imageUrl = badgeUrl,
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
    fun PaymentInfoCard(
        modifier: Modifier = Modifier, content: @Composable () -> Unit,
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
    fun PolicyDetails(modifier: Modifier = Modifier, policyDetails: InsurancePolicyDetails) {
        PaymentInfoCard(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Transparent)
            ) {
                policyDetails.title?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD5CDF2)
                    )
                }
                val finalList = policyDetails.data?.filter { it.label != null && it.value != null }
                    ?.map { attribute ->
                        LabelAndValueCompose(
                            label = attribute.label.orEmpty(),
                            value = attribute.value.orEmpty(),
                            valueTextStyle = TextStyle(
                                fontSize = 12.sp,
                                textAlign = TextAlign.End,
                                color = Color.White
                            ),
                            labelTextStyle = TextStyle(
                                fontSize = 12.sp,
                                textAlign = TextAlign.Start,
                                color = Color(0xFFACA1D3)
                            )
                        )
                    }
                finalList?.let {
                    LabelValueComposeView(
                        modifier = Modifier.padding(top = 24.dp),
                        list = it
                    )
                }

            }
        }
    }

    @Composable
    fun TransactionDetailsView(
        modifier: Modifier = Modifier,
        transactionDetails: TransactionDetails,
    ) {
        PaymentInfoCard(modifier = modifier.fillMaxWidth()) {
            TransactionCardContent(transactionDetails)
        }
    }


    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun TransactionCardContent(transactionDetails: TransactionDetails) {
        var expanded by rememberSaveable { mutableStateOf(false) }
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
                    expanded = !expanded
                }) {

                transactionDetails.title?.let {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.Top),
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD5CDF2)
                    )
                }



                Icon(
                    tint = Color(0xFFD5CDF2),
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        "SHOW LESS"
                    } else {
                        "SHOW MORE"
                    },
                )

            }
            if (expanded) {

                val finalList =
                    transactionDetails.data?.filter { it.label != null && it.value != null }
                        ?.map { attribute ->
                            LabelAndValueCompose(
                                label = attribute.label.orEmpty(),
                                value = attribute.value.orEmpty(),
                                valueTextStyle = TextStyle(
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.End,
                                    color = Color.White
                                ),
                                labelTextStyle = TextStyle(
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Start,
                                    color = Color(0xFFACA1D3)
                                ),

                                )
                        }
                finalList?.let { list ->
                    LabelValueComposeView(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        list = list.map {
                            if (it.label == "Transaction ID") {
                                it.copy(showCopyToClipBoardIconAndTruncate = true)
                            } else it
                        }
                    )
                }


            }
        }

    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun GoToHomeSection(modifier: Modifier = Modifier, paymentStatus: PaymentStatusResponse) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            paymentStatus.cta?.let { cta ->
                JarButton(
                    modifier = modifier,
                    text = cta.text,
                    onClick = {
                        if (InsuranceCTAAction.getInsuranceCTAAction(paymentStatus.cta?.action) == InsuranceCTAAction.GO_HOME) {
                            val currentTime = System.currentTimeMillis()
                            navigateTo(
                                BaseConstants.InternalDeepLinks.INSURANCE_LANDING_SCREEN + "/${Constants.PAYMENT_STATUS}"  + "/$currentTime",
                                popUpTo = com.jar.app.feature_health_insurance.R.id.completePaymentFragment,
                                inclusive = true
                            )
                        } else {
                            EventBus.getDefault().post(HandleDeepLinkEvent(cta.link))
                            popBackStack()
                        }
                    },
                    buttonType = ButtonType.valueOf(cta.ctaType)
                )
                Divider(
                    color = Color(0xFF463C69),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(1.dp)
                )
            }
            paymentStatus.needHelp?.text?.let { needHelpText ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = needHelpText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            paymentStatus.needHelp?.let { needHelp ->

                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                        .debounceClickable {
                            val initialMessage = needHelp.whatsappText.orEmpty()
                            val whatsappNumber = needHelp.whatsappNumber.orEmpty()
                            requireContext().openWhatsapp(whatsappNumber, initialMessage)

                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    JarImage(
                        modifier = Modifier.size(20.dp),
                        imageUrl = needHelp.icon,
                        contentDescription = "whatsapp icon"
                    )
                    paymentStatus.contactSupportText?.let { HtmlText(text = it) }
                    Icon(
                        tint = Color(0xFFD5CDF2),
                        imageVector = Icons.Filled.ArrowRight,
                        contentDescription = "chat",
                    )
                }
            }
        }

    }
}