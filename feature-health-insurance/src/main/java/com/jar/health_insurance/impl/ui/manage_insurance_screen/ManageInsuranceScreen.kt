@file:OptIn(ExperimentalFoundationApi::class)

package com.jar.health_insurance.impl.ui.manage_insurance_screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.OpenHealthInsuranceMemberSubmitFormEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.ButtonType
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.convertToAnnotatedString
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_health_insurance.R
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceCTA
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceCTAAction
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceTransactionData
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.MetaData
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.PolicyCard
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.ui.ManageInsuranceState
import com.jar.app.feature_health_insurance.shared.ui.ManageScreenAnalyticsEvents
import com.jar.app.feature_health_insurance.shared.ui.ManageScreenEvent
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.health_insurance.impl.ui.common.components.PaymentStatusDescriptionCard
import com.jar.health_insurance.impl.ui.components.BackPressHandler
import com.jar.health_insurance.impl.ui.manage_insurance_screen.component.InsuranceStatusSection
import com.jar.health_insurance.impl.ui.manage_insurance_screen.component.ManageScreenCardSection
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.core_ui.R.color as uiColor

private val VIEW_TRANSACTIONS_PEEK_HEIGHT = 90.dp
@AndroidEntryPoint
//@RuntimePermissions
@ExperimentalGlideComposeApi
class ManageInsuranceScreen : BaseComposeFragment() {

    private var initiateOneTimePaymentJob: Job? = null

    @Inject
    lateinit var paymentManger: PaymentManager

    private val CALL_PERMISSION_REQUEST_CODE = 1
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    private val viewModelProvider by viewModels<ManageScreenViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private val args by navArgs<ManageInsuranceScreenArgs>()


    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(uiColor.color_141021)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onTriggerEvent(ManageScreenEvent.LoadManageScreenData(args.insuranceId))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlow()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.initiatePaymentFlow.collect(onLoading = {

                }, onSuccess = {
                    initiateOneTimePaymentJob?.cancel()
                    initiateOneTimePaymentJob =
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            if (it != null) {
                                it.screenSource = HealthInsuranceEvents.Health_Insurance
                                paymentManger.initiateOneTimePayment(it)
                                    .collectUnwrapped(onLoading = {
                                        showProgressBar()
                                    }, onSuccess = {
                                        dismissProgressBar()
                                        withContext(Dispatchers.Main) {
                                            navigateTo(
                                                "android-app://com.jar.app/healthInsurance/paymentStatusPage/${args.insuranceId}",
                                                popUpTo = R.id.insuranceLandingPage,
                                                inclusive = true
                                            )
                                        }

                                    }, onError = { errorMessage, _ ->
                                        dismissProgressBar()
                                        if (errorMessage.isNotEmpty()) {
                                            Toast.makeText(
                                                requireContext(),
                                                errorMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        }
                }, onError = { errorMessage, _ ->
                    if (errorMessage.isNotEmpty()) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    @Composable
    override fun RenderScreen() {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        if (!uiState.value.isLoading) {
            RenderManageScreen(uiState.value)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun RenderManageScreen(uiState: ManageInsuranceState) {
        val scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.PartiallyExpanded,
                confirmValueChange = { it != SheetValue.Hidden },
                skipHiddenState = false
            )

        )
        val state: LazyListState = rememberLazyListState()
        val items = viewModel.loadInsuranceTransactions(args.insuranceId).collectAsLazyPagingItems()
        var viewPosition by remember { mutableStateOf(-1) }
        BackPressHandler(
            onBackPressed = {
                handleBackPress(uiState, viewPosition)
            })

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetShape = RoundedCornerShape(12.dp),
            sheetContent = {
                InsuranceTransactionsBottomSheet(
                    header = uiState.transactionHeader,
                    transactionsDataList = items,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(id = uiColor.color_272239))
                )
            },
            sheetPeekHeight = VIEW_TRANSACTIONS_PEEK_HEIGHT,
            sheetDragHandle = {},
            sheetContainerColor = Color.Transparent,
            containerColor = colorResource(id = uiColor.color_272239),
            topBar = {
                Column {
                    RenderBaseToolBar(
                        modifier = Modifier
                            .background(color = colorResource(id = uiColor.color_141021)),
                        onBackClick = {
                            handleBackPress(uiState, viewPosition)
                        },
                        title = "Your Insurance",
                        /*RightSection = {
                            Image(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(24.dp),
                                painter = painterResource(id = R.drawable.ic_share),
                                contentDescription = "share icon"
                            )
                        }*/
                    )
                    Divider(
                        thickness = 1.dp,
                        color = colorResource(id = uiColor.color_ACA1D3_opacity_10)
                    )

                }
            }
        ) { contentPadding ->
            LazyColumn(modifier = Modifier.padding(contentPadding), state = state) {

                item(key = "Insurance_status_section") {
                    Box(
                        modifier = Modifier
                            .heightIn(min = if (uiState.manageScreenData?.insuranceStatusDetails != null) 200.dp else 0.dp)
                            .background(
                                color = colorResource(id = uiColor.color_141021),
                                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                            )
                    ) {
                        uiState.manageScreenData?.policyCards?.let { policyCards ->
                            Column {
                                Spacer(modifier = Modifier.height(30.dp))
                                CarouselView(
                                    policyCards
                                )
                            }
                        }

                        uiState.manageScreenData?.insuranceStatusDetails?.let { insuranceStatusDetails ->

                            InsuranceStatusSection(
                                modifier = Modifier.align(Alignment.Center),
                                insuranceStatusDetails
                            ) {
                                uiState.manageScreenData?.metaData?.let { it1 ->
                                    handleButtonClick(
                                        cta = it,
                                        viewPosition = viewPosition,
                                        uiState.manageScreenData?.isInsuranceExpired ?: false,
                                        metaData = it1,
                                        sectionType = uiState.manageScreenData?.sections?.get(viewPosition)?.sectionType
                                    )
                                }
                            }

                        }


                    }
                }

                uiState.manageScreenData?.insuranceExpiredScreen?.let { expiredScreen ->
                    item {
                        val configuration = LocalConfiguration.current
                        val screenHeight = configuration.screenHeightDp.dp
                        Box(
                            modifier = Modifier
                                .height(screenHeight * 0.7f)
                                .fillMaxWidth()
                                .background(color = colorResource(id = uiColor.color_6038CE))
                                .offset(y = (-12).dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .height(IntrinsicSize.Min)
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val headerText =
                                    expiredScreen.header?.let { convertToAnnotatedString(it) }
                                val descriptionText =
                                    expiredScreen.description?.let { convertToAnnotatedString(it) }
                                headerText?.let {
                                    Text(
                                        text = it, textAlign = TextAlign.Center
                                    )
                                }
                                descriptionText?.let {
                                    Text(
                                        text = it, textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                                expiredScreen.infoGraphic?.let {
                                    JarImage(
                                        modifier = Modifier.size(150.dp),
                                        imageUrl = it.url,
                                        contentDescription = "infographic"
                                    )
                                }
                                expiredScreen.cta?.let {
                                    JarButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = it.text,
                                        onClick = {
                                            uiState.manageScreenData?.metaData?.let { it1 ->
                                                handleButtonClick(
                                                    cta = it,
                                                    viewPosition =viewPosition,
                                                    uiState.manageScreenData?.isInsuranceExpired.orFalse(),
                                                    it1,
                                                    uiState.manageScreenData?.sections?.get(
                                                        viewPosition
                                                    )?.sectionType
                                                )
                                            }
                                            popBackStack()
                                        },
                                        textColor = colorResource(id = uiColor.color_6038CE),
                                        color = Color.White,
                                        buttonType = ButtonType.valueOf(it.ctaType)
                                    )
                                }
                            }
                        }

                    }
                }

                uiState.manageScreenData?.sections?.let { sections ->

                    sections.forEachIndexed { index, section ->
                        item {

                            Column {
                                ManageScreenCardSection(sectionData = section,
                                    onViewBenefitButtonClicked = {
                                        uiState.manageScreenData?.metaData?.let {
                                            sendOnClickEvent(
                                                InsuranceCTAAction.VIEW_BENEFITS.name,
                                                false,
                                                uiState.manageScreenData?.isInsuranceExpired.orFalse(),
                                                viewPosition,
                                                it,
                                                uiState.manageScreenData?.sections?.get(viewPosition)?.sectionType
                                            )
                                        }
                                        navigateTo(
                                            ManageInsuranceScreenDirections.actionManageInsuranceScreenToBenefitsPage(
                                                args.insuranceId
                                            )
                                        )
                                    },
                                    onKycVerifyClicked = {
                                        EventBus.getDefault()
                                            .post(HandleDeepLinkEvent(deepLink = it))
                                        uiState.manageScreenData?.metaData?.let { it1 ->
                                            sendOnClickEvent(
                                                InsuranceCTAAction.VERIFY.name,
                                                false,
                                                uiState.manageScreenData?.isInsuranceExpired.orFalse(),
                                                viewPosition,
                                                it1,
                                                uiState.manageScreenData?.sections?.get(viewPosition)?.sectionType
                                            )
                                        }
                                    },
                                    onButtonClicked = {
                                        uiState.manageScreenData?.metaData?.let { it1 ->
                                            handleButtonClick(
                                                it,
                                                viewPosition,
                                                uiState.manageScreenData?.isInsuranceExpired.orFalse(),
                                                it1,
                                                uiState.manageScreenData?.sections?.get(viewPosition)?.sectionType
                                            )
                                        }
                                    }
                                )
                                Divider(
                                    thickness = 4.dp,
                                    color = colorResource(id = uiColor.color_2F2943)
                                )


                            }

                        }

                    }

                }

                item {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        uiState.manageScreenData?.needHelp?.let { heedHelp ->
                            JarImage(
                                modifier = Modifier.size(24.dp),
                                imageUrl = heedHelp.icon,
                                contentDescription = "help icon"
                            )
                            heedHelp.text?.let { helpText ->
                                Text(
                                    modifier = Modifier.padding(start = 12.dp),
                                    text = helpText,
                                    style = JarTypography.h5.copy(color = Color.White)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))

                        uiState.manageScreenData?.contactUs?.let { contactUs ->
                            contactUs.text?.let {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .border(
                                            width = 1.dp,
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0x0FFFFFFF),
                                                    Color.Transparent,

                                                    )
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clip(shape = RoundedCornerShape(8.dp))
                                        .background(
                                            color = colorResource(
                                                id = uiColor.color_3C3357
                                            )
                                        )
                                        .debounceClickable {
                                            uiState.manageScreenData?.metaData?.let { it1 ->
                                                sendOnClickEvent(
                                                    InsuranceCTAAction.CONTACT_US.name,
                                                    false,
                                                    uiState.manageScreenData?.isInsuranceExpired.orFalse(),
                                                    viewPosition,
                                                    it1,
                                                    uiState.manageScreenData?.sections?.get(
                                                        viewPosition
                                                    )?.sectionType
                                                )
                                            }
                                            val initialMessage =
                                                contactUs.whatsappText
                                            val whatsappNumber =
                                                contactUs.whatsappNumber
                                            whatsappNumber?.let { message ->
                                                requireContext().openWhatsapp(
                                                    message, initialMessage
                                                )
                                            }
                                        },

                                    ) {
                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 22.dp, vertical = 12.dp),
                                        text = it,
                                        style = JarTypography.body2.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

            }
            LaunchedEffect(state) {
                snapshotFlow {
                    state.firstVisibleItemIndex
                }.debounce(200)
                    .collectLatest { index ->
                        if (index > viewPosition && index <= uiState.manageScreenData?.sections?.size.orZero() - 1) {
                            if (!uiState.isLoading) {
                                if (uiState.manageScreenData?.isInsuranceExpired.orFalse()) {
                                    viewModel.onTriggerEvent(
                                        ManageScreenEvent.TriggerAnalyticEvent(
                                            ManageScreenAnalyticsEvents.ManageScreenShownEvent(
                                                mapOf(
                                                    HealthInsuranceEvents.Insurance_Status to HealthInsuranceEvents.INSURANCE_EXPIRED
                                                )
                                            )
                                        )
                                    )
                                } else {
                                    viewModel.onTriggerEvent(
                                        ManageScreenEvent.TriggerAnalyticEvent(
                                            ManageScreenAnalyticsEvents.ManageScreenShownEvent(
                                                mapOf(
                                                    HealthInsuranceEvents.Insurance_Status to HealthInsuranceEvents.INSURANCE_ACTIVE,
                                                    HealthInsuranceEvents.Premium_Payment_Status to if (uiState.manageScreenData?.metaData?.isTransactionSuccessful.orFalse()) HealthInsuranceEvents.SUCCESSFUL else HealthInsuranceEvents.FAILED,
                                                    HealthInsuranceEvents.Insured_Type to if (uiState.manageScreenData?.metaData?.membersInsured?.size.orZero() > 1) HealthInsuranceEvents.MULTIPLE_PERSON else HealthInsuranceEvents.SINGLE_PERSON,
                                                    HealthInsuranceEvents.Vertical_Card_Position to index.toString(),
                                                    HealthInsuranceEvents.Identity_Verification_Status to if (uiState.manageScreenData?.metaData?.isKycVerified.orFalse()) HealthInsuranceEvents.YES else HealthInsuranceEvents.NO,
                                                    HealthInsuranceEvents.Vertical_Card_Title to uiState.manageScreenData?.sections?.get(
                                                        index
                                                    )?.sectionType.orEmpty(),
                                                )
                                            )
                                        )
                                    )
                                    viewPosition = index
                                }
                            }
                        }
                    }
            }

        }


    }

    private fun handleBackPress(uiState: ManageInsuranceState, viewPosition: Int) {
        uiState.manageScreenData?.metaData?.let {
            sendOnClickEvent(
                InsuranceCTAAction.BACK.name,
                false,
                uiState.manageScreenData?.isInsuranceExpired.orFalse(),
                viewPosition,
                it,
                uiState.manageScreenData?.sections?.get(viewPosition)?.sectionType
            )
        }
        popBackStack()
    }

    private fun handleButtonClick(
        cta: InsuranceCTA,
        viewPosition: Int,
        isInsuranceExpired: Boolean,
        metaData: MetaData,
        sectionType: String?
    ) {
        val action = cta.action?.let { InsuranceCTAAction.valueOf(it) }
        when (action) {
            InsuranceCTAAction.CALL -> {
                    makeCall(cta.link)

                sendOnClickEvent(
                    InsuranceCTAAction.CALL.name,
                    false,
                    isInsuranceExpired,
                    viewPosition,
                    metaData,
                    sectionType
                )
            }

            InsuranceCTAAction.DEEP_LINK -> {
                EventBus.getDefault().post(HandleDeepLinkEvent(cta.link))
                sendOnClickEvent(
                    InsuranceCTAAction.DEEP_LINK.name,
                    false,
                    isInsuranceExpired,
                    viewPosition,
                    metaData,
                    sectionType
                )
            }

            InsuranceCTAAction.PAY_MANUALLY -> {
                viewModel.onTriggerEvent(ManageScreenEvent.InitiateManualPayment(args.insuranceId))
                sendOnClickEvent(
                    cta.text,
                    true,
                    isInsuranceExpired,
                    viewPosition,
                    metaData,
                    sectionType
                )
            }

            InsuranceCTAAction.EMAIL -> {
                sendEmail(cta.link)
                sendOnClickEvent(
                    InsuranceCTAAction.EMAIL.name,
                    false,
                    isInsuranceExpired,
                    viewPosition,
                    metaData,
                    sectionType
                )
            }

            InsuranceCTAAction.GO_HOME -> {
                sendOnClickEvent(
                    InsuranceCTAAction.GO_HOME.name,
                    false,
                    isInsuranceExpired,
                    viewPosition,
                    metaData,
                    sectionType
                )
                popBackStack()
            }

            InsuranceCTAAction.ADD_MEMBER_DETAILS -> {
                sendOnClickEvent(
                    InsuranceCTAAction.ADD_MEMBER_DETAILS.name,
                    false,
                    isInsuranceExpired,
                    viewPosition,
                    metaData,
                    sectionType
                )
                EventBus.getDefault().post(
                    OpenHealthInsuranceMemberSubmitFormEvent(
                        cta.link
                    )
                )
            }

            InsuranceCTAAction.BACK -> {
                sendOnClickEvent(
                    InsuranceCTAAction.BACK.name,
                    false,
                    isInsuranceExpired,
                    viewPosition,
                    metaData,
                    sectionType
                )
            }

            else -> {
            }
        }
    }

    private fun sendOnClickEvent(
        ctaClicked: String,
        isPayManuallyClicked: Boolean,
        isInsuranceExpired: Boolean,
        viewPosition: Int,
        metaData: MetaData,
        sectionType: String?
    ) {
        var insuredMembers = ""

        for (member in metaData.membersInsured) {
            insuredMembers = "$insuredMembers $member"
        }

        if (isInsuranceExpired) {
            viewModel.onTriggerEvent(
                ManageScreenEvent.TriggerAnalyticEvent(
                    ManageScreenAnalyticsEvents.ManageScreenClickedEvent(
                        mapOf(
                            HealthInsuranceEvents.Insurance_Status to HealthInsuranceEvents.INSURANCE_ACTIVE,
                            HealthInsuranceEvents.CTA_Clicked to ctaClicked
                        )
                    )
                )
            )
        } else {
            if (isPayManuallyClicked) {
                viewModel.onTriggerEvent(
                    ManageScreenEvent.TriggerAnalyticEvent(
                        ManageScreenAnalyticsEvents.ManageScreenClickedEvent(
                            mapOf(
                                HealthInsuranceEvents.Insurance_Status to HealthInsuranceEvents.INSURANCE_ACTIVE,
                                HealthInsuranceEvents.Premium_Payment_Status to if (metaData.isTransactionSuccessful) HealthInsuranceEvents.SUCCESSFUL else HealthInsuranceEvents.FAILED,
                                HealthInsuranceEvents.Insured_Type to if (metaData.membersInsured.size > 1) HealthInsuranceEvents.MULTIPLE_PERSON else HealthInsuranceEvents.SINGLE_PERSON,
                                HealthInsuranceEvents.Vertical_Card_Position to viewPosition.toString(),
                                HealthInsuranceEvents.Identity_Verification_Status to if (metaData.isKycVerified) HealthInsuranceEvents.YES else HealthInsuranceEvents.NO,
                                HealthInsuranceEvents.Vertical_Card_Title to sectionType.orEmpty(),
                                HealthInsuranceEvents.Base_Plan to metaData.planType,
                                HealthInsuranceEvents.Premium_Amount to metaData.premiumAmount.toString(),
                                HealthInsuranceEvents.Payment_Frequency to metaData.premiumFrequency,
                                HealthInsuranceEvents.Policy_Start_Date to metaData.policyStartDate,
                                HealthInsuranceEvents.Policy_End_Date to metaData.policyEndDate,
                                HealthInsuranceEvents.Members_Insured to insuredMembers,
                                HealthInsuranceEvents.Premium_Due_Date to metaData.premiumDueData,
                                HealthInsuranceEvents.GRACE_PERIOD_STATUS to metaData.isOnGracePeriod.toString(),
                                HealthInsuranceEvents.CTA_Clicked to ctaClicked
                            )
                        )
                    )
                )
            } else {
                viewModel.onTriggerEvent(
                    ManageScreenEvent.TriggerAnalyticEvent(
                        ManageScreenAnalyticsEvents.ManageScreenClickedEvent(
                            mapOf(
                                HealthInsuranceEvents.Insurance_Status to HealthInsuranceEvents.INSURANCE_ACTIVE,
                                HealthInsuranceEvents.Premium_Payment_Status to if (metaData.isTransactionSuccessful) HealthInsuranceEvents.SUCCESSFUL else HealthInsuranceEvents.FAILED,
                                HealthInsuranceEvents.Insured_Type to if (metaData.membersInsured.size > 1) HealthInsuranceEvents.MULTIPLE_PERSON else HealthInsuranceEvents.SINGLE_PERSON,
                                HealthInsuranceEvents.Vertical_Card_Position to viewPosition.toString(),
                                HealthInsuranceEvents.Identity_Verification_Status to if (metaData.isKycVerified) HealthInsuranceEvents.YES else HealthInsuranceEvents.NO,
                                HealthInsuranceEvents.Vertical_Card_Title to sectionType.orEmpty(),
                                HealthInsuranceEvents.Base_Plan to metaData.planType,
                                HealthInsuranceEvents.Premium_Amount to metaData.premiumAmount.toString(),
                                HealthInsuranceEvents.Payment_Frequency to metaData.premiumFrequency,
                                HealthInsuranceEvents.Policy_Start_Date to metaData.policyStartDate,
                                HealthInsuranceEvents.Policy_End_Date to metaData.policyEndDate,
                                HealthInsuranceEvents.Members_Insured to insuredMembers,
                                HealthInsuranceEvents.Premium_Due_Date to metaData.premiumDueData,
                                HealthInsuranceEvents.CTA_Clicked to ctaClicked
                            )
                        )
                    )
                )
            }
        }
    }

    private fun sendEmail(emailId: String) {
        val intent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailId, null
            )
        )
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }

    private fun makeCall(number: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST_CODE
            )
        } else {
            initiateCall(number)
        }
    }

    private fun initiateCall(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$number")
        startActivity(callIntent)
    }

    @Composable
    private fun CarouselView(
        healthCards: List<PolicyCard>,
    ) {
        val pagerState = rememberPagerState(0)

        Column(
            modifier = Modifier.height(280.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                pageCount = healthCards.size
            ) { page ->
                CarouselItem(healthCards[page])
            }
            if (healthCards.size > 1) {
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    indicatorShape = CircleShape,
                    activeColor = colorResource(id = uiColor.color_D5CDF2),
                    inactiveColor = colorResource(id = uiColor.color_3C3357),
                    count = healthCards.size
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }


    @Composable
    fun CarouselItem(
        healthCard: PolicyCard
    ) {
        Box(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(12.dp))
                .background(color = colorResource(id = R.color.color_3C4BCC))
        ) {
            Image(
                painter = painterResource(id = R.drawable.health_card_overlay),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
            )
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(id = uiColor.black_opacity_12)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HEALTH INSURANCE",
                        style = JarTypography.body2.copy(color = Color.White, fontSize = 12.sp),
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .padding(vertical = 20.dp),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    JarImage(
                        modifier = Modifier
                            .height(18.dp)
                            .padding(end = 16.dp),
                        imageUrl = healthCard.providerIcon,
                        contentDescription = "Insurance provider icon"
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 23.dp, start = 16.dp),
                    text = healthCard.name.orEmpty().uppercase(),

                    style = JarTypography.h5.copy(color = Color.White)
                )
                Text(
                    text = healthCard.dob.orEmpty(),
                    style = JarTypography.body2.copy(color = Color.White, fontSize = 12.sp),
                    modifier = Modifier.padding(start = 16.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Column {
                        Text(
                            text = "POLICY NO.", style = JarTypography.body2.copy(
                                color = colorResource(id = uiColor.color_CFDDEB), fontSize = 12.sp
                            )
                        )
                        Text(
                            text = healthCard.policyNoValue.orEmpty(),
                            style = JarTypography.body2.copy(color = Color.White, fontSize = 12.sp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Column {
                        Text(
                            text = "VALID TILL", style = JarTypography.body2.copy(
                                color = colorResource(id = uiColor.color_CFDDEB), fontSize = 12.sp
                            )
                        )
                        Text(
                            text = healthCard.validity.orEmpty(),
                            style = JarTypography.body2.copy(color = Color.White, fontSize = 12.sp),
                        )

                    }

                }


            }
        }
    }

    @Composable
    fun InsuranceTransactionsBottomSheet(
        modifier: Modifier = Modifier,
        transactionsDataList: LazyPagingItems<InsuranceTransactionData>,
        header: String?
    ) {
        val listState: LazyListState = rememberLazyListState()
        val dragHandlerStrokeColor = colorResource(id = uiColor.color_776e94)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.drawBehind {
                val dragHandlerHeight = 20.dp.toPx()
                val dragHandlerStrokeWidth = 1.dp.toPx()
                drawArc(
                    color = dragHandlerStrokeColor,
                    startAngle = 180f,
                    sweepAngle = 90F,
                    useCenter = false,
                    size = Size(dragHandlerHeight * 2, dragHandlerHeight * 2),
                    style = Stroke(width = 1.dp.toPx())
                )
                drawLine(
                    color = dragHandlerStrokeColor,
                    start = Offset(dragHandlerHeight, dragHandlerStrokeWidth / 2),
                    end = Offset(size.width - dragHandlerHeight, 0F),
                    strokeWidth = dragHandlerStrokeWidth
                )
                drawArc(
                    color = dragHandlerStrokeColor,
                    startAngle = 0F,
                    sweepAngle = -90F,
                    useCenter = false,
                    size = Size(dragHandlerHeight * 2, dragHandlerHeight * 2),
                    topLeft = Offset(size.width - 40.dp.toPx(), 0F),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_sandwich),
                contentDescription = "",
                modifier = Modifier.padding(top = 4.dp)
            )
            header?.let {
                Text(
                    text = it,
                    style = JarTypography.body2.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.padding(top = 8.dp),
                contentPadding = PaddingValues(bottom = 34.dp, top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(transactionsDataList.itemCount) {
                    val insuranceTransactionsData = transactionsDataList[it]
                    insuranceTransactionsData?.let { transactionData ->
                        Box(
                            modifier = modifier
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = colorResource(id = uiColor.color_3E3856),
                                    shape = RoundedCornerShape(size = 12.dp)
                                )
                        ) {
                            PaymentStatusDescriptionCard(
                                modifier = Modifier
                                    .clickable {
                                        navigateTo(
                                            ManageInsuranceScreenDirections.actionManageInsuranceScreenToInsuranceTransactionDetailsScreen(
                                                transactionData.id
                                            )
                                        )
                                    }
                                    .padding(
                                        start = 12.dp,
                                        end = 16.dp,
                                        top = 16.dp,
                                        bottom = 16.dp
                                    ),
                                icon = transactionData.icon,
                                statusIcon = transactionData.statusIcon,
                                header = transactionData.header,
                                amount = transactionData.amount,
                                date = transactionData.date,
                                status = transactionData.status,
                                statusText = transactionData.statusText
                            )
                        }
                    }

                }
            }
        }
    }
    private fun Boolean?.orFalse() = this ?: false
}


@Composable
fun HorizontalPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    inactiveColor: Color = activeColor.copy(ContentAlpha.disabled),
    indicatorWidth: Dp = 8.dp,
    indicatorHeight: Dp = indicatorWidth,
    spacing: Dp = indicatorWidth,
    indicatorShape: Shape = CircleShape,
    count: Int
) {

    val indicatorWidthPx = LocalDensity.current.run { indicatorWidth.roundToPx() }
    val spacingPx = LocalDensity.current.run { spacing.roundToPx() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val indicatorModifier = Modifier
                .size(width = indicatorWidth, height = indicatorHeight)
                .background(color = inactiveColor, shape = indicatorShape)

            repeat(count) {
                Box(indicatorModifier)
            }
        }

        Box(
            Modifier
                .offset {
                    val scrollPosition =
                        (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                            .coerceIn(
                                0f,
                                (count - 1)
                                    .coerceAtLeast(0)
                                    .toFloat()
                            )
                    IntOffset(
                        x = ((spacingPx + indicatorWidthPx) * scrollPosition).toInt(),
                        y = 0
                    )
                }
                .size(width = indicatorWidth, height = indicatorHeight)
                .background(
                    color = activeColor,
                    shape = indicatorShape,
                )
        )
    }
}