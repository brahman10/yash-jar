package com.jar.health_insurance.impl.ui.landing_page

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalAbsoluteElevation
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.base.util.isChromeInstalled
import com.jar.app.base.util.openUrlInChromeTabOrExternalBrowser
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.ErrorToastMessage
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.hindFontFamily
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_health_insurance.R
import com.jar.app.feature_health_insurance.shared.data.models.landing1.BenefitsDetails
import com.jar.app.feature_health_insurance.shared.data.models.landing1.BenefitsX
import com.jar.app.feature_health_insurance.shared.data.models.landing1.HiSubHeaderInsuranceBenefits
import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingFooter
import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingFooterCTA
import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingVideo
import com.jar.app.feature_health_insurance.shared.data.models.landing1.LandingVideoSection
import com.jar.app.feature_health_insurance.shared.data.models.landing1.ReturnScreen
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Back_ButtonClicked
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Clicked_InsuranceLanding_Top_Video
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Get_InsuredClicked
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Health_Insurance_Event_Ts
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Insurance_BenefitsShown
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Insurance_FAQClicked
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Insurance_LandingPageShown
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Leaving_CTAClicked
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Leaving_InsuranceShown
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.ThumbNailText
import com.jar.health_insurance.impl.ui.components.BackPressHandler
import com.jar.health_insurance.impl.ui.components.HtmlText
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.core_ui.R.color as uiColor

@AndroidEntryPoint
class InsuranceLandingPage : BaseComposeFragment() {

    private val viewModel by viewModels<LandingPageViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<InsuranceLandingPageArgs>()
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.color_3C4BCC)
        viewModel.onTriggerEvent(LandingPageEvent.LoadLandingPageData)
        analyticsHandler.postEvent(
            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                HealthInsuranceEvents.FromScreen to args.fromScreen,
                HealthInsuranceEvents.EVENT_NAME to Insurance_LandingPageShown
            )
        )
    }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun RenderScreen() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        if (uiState.landingPageData != null) {
            RenderInsurancePage(uiState)
        }
        uiState.errorMessage?.let { errorMessage ->
            ErrorToastMessage(errorMessage = errorMessage) {
                viewModel.onTriggerEvent(LandingPageEvent.ErrorMessageDisplayed)
            }
        }


    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(
        ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
        ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class
    )
    @Composable
    fun RenderInsurancePage(uiState: LandingPageState) {
        val landingPageData = uiState.landingPageData
        val state: LazyListState = rememberLazyListState()
        val systemUiController = rememberSystemUiController()
        var viewPosition by remember { mutableStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        val rememberModalBottomSheetState =
            androidx.compose.material.rememberModalBottomSheetState(
                ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true
            )
        BackPressHandler(onBackPressed = {
            analyticsHandler.postEvent(
                HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                    HealthInsuranceEvents.FromScreen to HealthInsuranceEvents.Insurance_landing_page,
                    HealthInsuranceEvents.EVENT_NAME to Back_ButtonClicked
                )
            )
            landingPageData?.returnScreen?.let {
                coroutineScope.launch {
                    rememberModalBottomSheetState.show()
                }
            } ?: run {
                popBackStack()
            }
        })

        ModalBottomSheetLayout(
            sheetContent = {
                landingPageData?.returnScreen?.let { returnScreenData ->
                    AbandonPageBottomSheet(hideBottomSheet = {
                        landingPageData?.let {
                            coroutineScope.launch {
                                rememberModalBottomSheetState.hide()
                            }
                        } ?: run { popBackStack() }
                    }, returnScreenData)
                }
            },
            scrimColor = Color(0x9E000000),
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetState = rememberModalBottomSheetState
        ) {
            Scaffold(
                topBar = {
                    Column {
                        val topBarColor = remember { Animatable(Color(0xFF3C4BCC)) }
                        systemUiController.setStatusBarColor(topBarColor.value)
                        systemUiController.setNavigationBarColor(Color(0xFF272239))
                        LaunchedEffect(state) {
                            snapshotFlow {
                                state.firstVisibleItemIndex
                            }.debounce(200)
                                .collectLatest { index ->
                                    if (index > viewPosition) {
                                        analyticsHandler.postEvent(
                                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                                HealthInsuranceEvents.EVENT_NAME to Insurance_LandingPageShown,
                                                HealthInsuranceEvents.FromScreen to args.fromScreen,
                                                HealthInsuranceEvents.SCROLL_POSITION to index,
                                            )
                                        )
                                        viewPosition = index
                                    }
                                    if (index > 1) {
                                        topBarColor.animateTo(
                                            Color(0xFF141021),
                                            animationSpec = tween(400)
                                        )
                                    } else {
                                        topBarColor.animateTo(
                                            Color(0xFF3C4BCC),
                                            animationSpec = tween(400)
                                        )
                                    }
                                }
                        }

                        RenderBaseToolBar(
                            modifier = Modifier.background(topBarColor.value),
                            onBackClick = {
                                coroutineScope.launch {
                                    analyticsHandler.postEvent(
                                        HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                            HealthInsuranceEvents.FromScreen to HealthInsuranceEvents.Insurance_landing_page,
                                            HealthInsuranceEvents.EVENT_NAME to Back_ButtonClicked
                                        )
                                    )
                                    if(landingPageData?.returnScreen != null){
                                        rememberModalBottomSheetState.show()
                                    }else{
                                        popBackStack()
                                    }
                                }
                            },
                            title = "",
                            RightSection = {
                                Faq(
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .clickable {
                                            analyticsHandler.postEvent(
                                                HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                                    HealthInsuranceEvents.FromScreen to HealthInsuranceEvents.Insurance_landing_page,
                                                    HealthInsuranceEvents.EVENT_NAME to Insurance_FAQClicked
                                                )
                                            )

                                            EventBus
                                                .getDefault()
                                                .post(
                                                    HandleDeepLinkEvent(
                                                        BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.HELP_SUPPORT_HEALTH_INSURANCE
                                                    )
                                                )
                                        }
                                )
                            }
                        )
                    }
                },
                bottomBar = {
                    GetInsuredLayout(
                        data = landingPageData?.landingFooterCTA,
                        onClick = {
                            analyticsHandler.postEvent(
                                HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                    HealthInsuranceEvents.FromScreen to args.fromScreen,
                                    HealthInsuranceEvents.EVENT_NAME to Get_InsuredClicked
                                )
                            )
                            navigateTo(InsuranceLandingPageDirections.actionInsuranceLandingPageToAddDetailsFragment())
                        }
                    )
                },
                containerColor = Color(0xFF141021),
            ) { padding ->
                val screenWidth = LocalConfiguration.current.screenWidthDp
                val cardWidth = screenWidth.dp - 16.dp
                val cardHeight = cardWidth / 2.dp
                val offset = ((cardHeight / 1.75).dp)
                val remainingColumnHeight = cardHeight.dp - offset
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    state = state
                ) {
                    item(key = "header_image") {
                        Box(

                        ) {
                            Column(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            bottomStart = 16.dp,
                                            bottomEnd = 16.dp
                                        )
                                    )
                                    .background(Color(0xFF3C4BCC))
                                    .align(BottomCenter)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(8f)
                                            .padding(start = 18.dp, top = 14.dp)
                                    ) {
                                        landingPageData?.landingHeader?.header?.let { it1 ->
                                            Text(
                                                text = it1,
                                                color = Color.White,
                                                fontSize = 42.sp,
                                                lineHeight = 42.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        landingPageData?.landingHeader?.subHeader?.let { it1 ->
                                            val shouldUseOldHeader =
                                                landingPageData.landingHeader?.subHeaderNew.isNullOrEmpty()
                                            if (shouldUseOldHeader) {
                                                Text(
                                                    text = it1,
                                                    fontFamily = hindFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colorResource(id = uiColor.color_171A65),
                                                    lineHeight = 35.sp,
                                                    fontSize = 35.sp
                                                )
                                            }
                                        }
                                    }
                                    JarImage(
                                        modifier = Modifier
                                            .weight(3f)
                                            .width(40.dp)
                                            .height(60.dp)
                                            .padding(end = 16.dp),
                                        imageUrl = landingPageData?.landingHeader?.shieldIcon,
                                        contentDescription = ""
                                    )
                                }
                                landingPageData?.landingHeader?.subHeaderNew?.let { it1 ->
                                    Text(
                                        modifier = Modifier.padding(start = 20.dp, end = 35.dp),
                                        text = it1,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = uiColor.color_D8DCFF),
                                        fontSize = 16.sp,
                                        lineHeight = 21.5.sp
                                    )
                                }
                                Row {
                                    landingPageData?.landingHeader?.partnerText?.let {
                                        landingPageData.landingHeader?.partnerLogo?.let { it1 ->
                                            RenderHeaderPartnershipLabel(
                                                modifier = Modifier.padding(start = 16.dp, top = 32.dp),
                                                partnershipTitle = it,
                                                partnershipIcon = it1,
                                            )
                                        }
                                    }
                                    viewModel.apiExecutionCount += 1
                                    Spacer(modifier = Modifier.weight(1f))
                                    JarImage(
                                        modifier = Modifier
                                            .width(244.dp)
                                            .height(140.dp)
                                            .padding(end = 10.dp),
                                        imageUrl = landingPageData?.landingHeader?.familyImage,
                                        contentDescription = "",
                                        fromScreen = args.fromScreen,
                                        startTime = args.clickTime.toLong(),
                                        apiExecutionCount = viewModel.apiExecutionCount,
                                        afterLoading = {
                                            analyticsHandler.postEvent(
                                                Health_Insurance_Event_Ts,
                                                mapOf(
                                                    EventKey.IS_FROM_CACHE to it.isItFromCache,
                                                    EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(endTimeTime = it.endTime, startTime = it.startTime)
                                                )
                                            )
                                        }
                                    )
                                }
                            }

                            landingPageData?.landingHeader?.defaultVideo?.let { defaultVideo ->
                                JarImage(
                                    imageUrl = defaultVideo.thumbNailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            translationY = ((cardHeight / 1.75).dp + 50.dp).toPx()
                                        }
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp)
                                        .aspectRatio(2f)
                                        .background(
                                            color = Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .debounceClickable {
                                            analyticsHandler.postEvent(
                                                Clicked_InsuranceLanding_Top_Video
                                            )
                                            defaultVideo.videoUrl?.let { videoUrl ->
                                                val value = false
                                                val encodedUrl = encodeUrl(videoUrl)
                                                val title = ""
                                                val showToolbar = false
                                                navigateTo(
                                                    "android-app://com.jar.app/webView/${BaseConstants.WebViewFlowType.INSURANCE_LANDING}/$value/$encodedUrl/${title.ifEmpty { "Jar" }}/$showToolbar",
                                                    true
                                                )
                                            }
                                        }
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 38.dp)
                                    .padding(horizontal = 24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .align(BottomCenter),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ) {
                                landingPageData?.landingHeader?.infoIcon?.let { imageUrl ->
                                    JarImage(
                                        modifier = Modifier
                                            .padding(vertical = 9.dp)
                                            .padding(end = 6.dp)
                                            .size(24.dp),
                                        imageUrl = imageUrl,
                                        contentDescription = "infoIcon"
                                    )
                                }
                                landingPageData?.landingHeader?.infoTextNew?.let { it1 ->
                                    Text(
                                        modifier = Modifier
                                            .padding(vertical = 9.dp),
                                        text = it1,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        lineHeight = 10.sp,
                                        letterSpacing = 0.5.sp

                                    )
                                } ?: run {
                                    landingPageData?.landingHeader?.infoText?.let { it1 ->
                                        Text(
                                            modifier = Modifier
                                                .padding(vertical = 9.dp),
                                            text = it1,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            textAlign = TextAlign.Center

                                        )
                                    }
                                }
                                landingPageData?.landingHeader?.infoIconEnd?.let { imageUrl ->
                                    JarImage(
                                        modifier = Modifier
                                            .padding(top = 7.dp, bottom = 11.dp)
                                            .padding(start = 8.dp)
                                            .height(16.dp),
                                        imageUrl = imageUrl,
                                        contentDescription = "infoIconEnd"
                                    )
                                }
                            }
                        }


                    }

                    landingPageData?.landingVideoSection?.let { landingVideoSectionData ->
                        item(key = "insurance_video_section"){
                            LandingVideoSection(landingVideoData = landingVideoSectionData, modifier = Modifier.padding(top = remainingColumnHeight + 28.dp))
                            LandingPageDivider(width = 4.dp)
                        }
                    }

                    item(key = "insurance_buy_reason_label") {
                        landingPageData?.landingSubHeader?.headerText?.let { it1 -> LabelText(text = it1) }
                    }
                    item(key = "insurance_buy_reason_list") {
                        landingPageData?.landingSubHeader?.hiSubHeaderInsuranceBenefitsList?.let { insuranceBenefits ->
                            LazyRow(
                                modifier = Modifier.padding(top = 20.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                items(insuranceBenefits) { item ->
                                    item?.let { benefitItem ->
                                        InsuranceBuyReasonItem(benefitItem = benefitItem)
                                    }
                                }
                            }
                            LandingPageDivider(width = 4.dp)
                        }
                    }

                    item(key = "insurance_benefits_label") {
                        LabelText(text = landingPageData?.benefits?.headerText.orEmpty())
                    }
                    item(key = "insurance_benefits_list") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 20.dp)
                                .height(440.dp)
                        ) {

                            landingPageData?.benefits?.benefitsDetailsList?.let { benefitDetailsList ->
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .debounceClickable {
                                            analyticsHandler.postEvent(
                                                HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                                    HealthInsuranceEvents.Type to "Insurance Benefits",
                                                    HealthInsuranceEvents.EVENT_NAME to Insurance_BenefitsShown
                                                )
                                            )
                                            navigateTo(
                                                InsuranceLandingPageDirections.actionInsuranceLandingPageToBenefitsPage(
                                                    null
                                                )
                                            )
                                        }
                                ) {
                                    itemsIndexed(benefitDetailsList) { index, item ->
                                        item?.let {

                                            InsuranceBenefitItem(
                                                benefitItem = it,
                                                position = index,
                                                totalItemCount = benefitDetailsList.size,
                                                iconHeight = 64,
                                                iconWidth = 64,
                                                itemAspectRatio = 1F,
                                            )
                                        }
                                    }
                                }
                            }



                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = -5.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            bottomEnd = 12.dp,
                                            bottomStart = 12.dp
                                        )
                                    )
                                    .clipToBounds()
                                    .background(color = Color(0xFF3C3357))
                                    .padding(vertical = 20.dp)
                                    .debounceClickable {
                                        analyticsHandler.postEvent(
                                            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                                HealthInsuranceEvents.Type to "Insurance Benefits",
                                                HealthInsuranceEvents.EVENT_NAME to Insurance_BenefitsShown
                                            )
                                        )
                                        navigateTo(
                                            InsuranceLandingPageDirections.actionInsuranceLandingPageToBenefitsPage(
                                                null
                                            )
                                        )
                                    },
                                horizontalArrangement = Arrangement.Center

                            ) {
                                landingPageData?.benefits?.benefitsCTAText?.let { it1 ->
                                    Text(
                                        modifier = Modifier,
                                        text = it1,
                                        color = Color.White,
                                        textDecoration = TextDecoration.Underline,
                                        fontSize = 14.sp,
                                        lineHeight = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center

                                    )

                                }
                                Image(
                                    painterResource(id = R.drawable.ic_right_arrow),
                                    contentDescription = "Right arrow",
                                )
                            }
                        }
                    }
                    item {
                        Divider(
                            color = Color(0xFFFFFFFF).copy(alpha = 0.05f),
                            modifier = Modifier
                                .padding(top = 40.dp, bottom = 30.dp)
                                .fillMaxWidth()
                                .height(4.dp)

                        )
                    }
                    item {
                        landingPageData?.hospitals?.headerText?.let { it1 ->
                            HtmlText(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                //.padding(top = 40.dp, bottom = 28.dp),
                                text = it1,
                                fontSize = 28f,
                                customTypeface = Typeface.DEFAULT_BOLD,

                                )
                        }
                    }

                    item {
                        Spacer(Modifier.height(28.dp))
                        landingPageData?.hospitals?.hospitalList?.let { hospitalList ->
                            LazyHorizontalStaggeredGrid(
                                rows = StaggeredGridCells.Fixed(2),
                                modifier = Modifier
                                    .height(100.dp)
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalItemSpacing = 8.dp
                            ) {
                                items(hospitalList) { hospitalImage ->
                                    hospitalImage?.let { it1 -> HospitalIconChip(imageUrl = it1) }
                                }

                            }
                        }

                        /*AutoScrollLazyLayout(list = hospitalImages, content = {
                            HospitalIconChip(imageUrl = it)
                        })*/
                    }

                    landingPageData?.jarBenefits?.headerText?.let {
                        item {
                            LandingPageDivider(width = 4.dp)
                        }
                        item(key = "jar_benefits_label") {
                            LabelText(text = it)
                        }
                    }
                    landingPageData?.jarBenefits?.benefitsDetailsList?.let { benefitDetailsList ->
                        item(key = "jar_benefits_list") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 20.dp)
                            ) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    itemsIndexed(benefitDetailsList) { index, item ->
                                        item?.let {

                                            InsuranceBenefitItem(
                                                benefitItem = it,
                                                position = index,
                                                totalItemCount = benefitDetailsList.size,
                                                iconHeight = 33,
                                                iconWidth = 33,
                                                itemAspectRatio = 163F / 121F
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item("jar_benefits_list_divider") {
                        LandingPageDivider(width = 4.dp)
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            landingPageData?.partnerships?.headerTextNew?.let { it1 ->
                                LabelText(text = it1)
                                landingPageData.partnerships?.headerIcon?.let {
                                    JarImage(
                                        modifier = Modifier
                                            .height(20.dp)
                                            .padding(start = 8.dp),
                                        imageUrl = it,
                                        contentDescription = null
                                    )
                                }
                            } ?: run {
                                landingPageData?.partnerships?.headerText?.let { it1 -> LabelText(text = it1) }
                            }
                        }
                    }

                    landingPageData?.partnerships?.partnershipCardsList?.let { partnershipCards ->
                        partnershipCards.forEach { it ->
                            item {
                                PartnerShipCard(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 16.dp),
                                    text1 = it?.header ?: "",
                                    text2 = it?.subHeader ?: "",
                                    imageUrl = it?.icon ?: ""
                                )
                            }
                        }
                    }

                    item {
                        LandingPageDivider(width = 4.dp)
                    }
                    item {
                        landingPageData?.landingFooter?.let { footer ->
                            FooterLayout(
                                modifier = Modifier.padding(bottom = 40.dp),
                                footer = footer
                            )
                        }
                    }
                }

            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun RenderHeaderPartnershipLabel(
        modifier: Modifier,
        partnershipTitle: String,
        partnershipIcon: String
    ) {
        Column(modifier = modifier) {
            Text(
                text = partnershipTitle,
                fontSize = 8.sp,
                lineHeight = 8.sp,
                color = colorResource(id = uiColor.color_BBC1F6),
            )
            Spacer(modifier = Modifier.height(4.dp))
            JarImage(
                modifier = Modifier
                    .height(16.dp),
                imageUrl = partnershipIcon,
                contentDescription = null
            )
        }
    }

    @Composable
    private fun LandingVideoSection(
        modifier: Modifier = Modifier,
        landingVideoData: LandingVideoSection
    ) {
        Column(modifier = modifier) {
            landingVideoData.headerText?.let { headerText ->
                LabelText(text = headerText, modifier = Modifier.padding(end = 16.dp))
            }
            landingVideoData.videos?.let { landingVideos ->
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier
                        .padding(top = 20.dp)
                ){
                    items(landingVideos){video ->
                        ThumbNail(
                            landingVideo = video,
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun ThumbNail(
        modifier: Modifier = Modifier,
        landingVideo: LandingVideo? = null,
    ){

        val screenWidth = LocalConfiguration.current.screenWidthDp
        val cardWidth = screenWidth.dp - 52.dp

        Box(
            modifier = modifier
                .width(cardWidth / 2)
                .background(
                    colorResource(id = uiColor.color_272239),
                    shape = RoundedCornerShape(10.dp)
                )
                .debounceClickable {
                    analyticsHandler.postEvent(
                        Clicked_InsuranceLanding_Top_Video,
                        mapOf(
                            ThumbNailText to landingVideo?.header.orEmpty()
                        )
                    )
                    val value = false
                    val encodedUrl = encodeUrl(landingVideo?.videoUrl!!)
                    val title = ""
                    val showToolbar = false
                    navigateTo(
                        "android-app://com.jar.app/webView/${BaseConstants.WebViewFlowType.INSURANCE_LANDING}/$value/$encodedUrl/${title.ifEmpty { "Jar" }}/$showToolbar",
                        true
                    )
                }
        ){
            Image(
                painterResource(id = R.drawable.bg_video_card),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )

            Column(
                modifier = modifier
                    .fillMaxSize()

            ) {
                landingVideo?.header?.let { headerText ->
                    Text(
                        text = headerText,
                        style = JarTypography.caption.copy(color = Color.White),
                        modifier = Modifier
                            .padding(
                                top = 10.dp,
                                start = 10.dp,
                                end = 10.dp
                            )
                    )
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(top = 7.dp)
                ){
                    landingVideo?.leftIconUrl?.let { leftIconUrl ->
                        JarImage(
                            imageUrl = leftIconUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(36.dp)
                                .height(36.dp)
                                .padding(start = 10.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    landingVideo?.rightIconUrl?.let { rightIconUrl ->
                        JarImage(
                            imageUrl = rightIconUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(46.dp)
                                .height(48.dp)
                                .padding(end = 10.dp)
                        )
                    }
                }
            }
        }

    }

    @Composable
    private fun LabelText(modifier: Modifier = Modifier, text: String) {
        Text(
            modifier = modifier
                .padding(start = 16.dp),
            text = text,
            color = Color.White,
            style = JarTypography.h5.copy(
                fontSize = 20.sp,
                lineHeight = 28.sp,
            )
        )
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun InsuranceBuyReasonItem(
        modifier: Modifier = Modifier,
        benefitItem: HiSubHeaderInsuranceBenefits
    ) {
        Column(
            modifier = modifier
                .aspectRatio(1f)
                .background(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.05f),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            JarImage(
                modifier = Modifier
                    .padding(start = 16.dp, top = 20.dp)
                    .height(36.dp)
                    .width(36.dp),

                imageUrl = benefitItem.icon,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .widthIn(120.dp)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                text = benefitItem.text.orEmpty(),
                style = TextStyle(
                    fontFamily = jarFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.White,
                ),
                maxLines = 2
            )
        }

    }


    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    private fun InsuranceBenefitItem(
        modifier: Modifier = Modifier,
        benefitItem: BenefitsDetails,
        position: Int,
        totalItemCount: Int,
        iconHeight: Int,
        iconWidth: Int,
        itemAspectRatio: Float
    ) {
        Column(
            modifier = modifier
                .aspectRatio(itemAspectRatio)
                .background(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.05f),
                    shape = getInsuranceBenefitItemShape(position, totalItemCount)
                ),
            verticalArrangement = Arrangement.Center
        ) {
            JarImage(
                modifier = Modifier
                    .height(iconHeight.dp)
                    .width(iconWidth.dp)
                    .align(CenterHorizontally),
                imageUrl = benefitItem.icon,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .align(CenterHorizontally),
                text = benefitItem.text.orEmpty(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.White,
                maxLines = 2,
            )


        }

    }


    private fun getInsuranceBenefitItemShape(index: Int, itemCount: Int): RoundedCornerShape {
        return when {
            index < 2 -> {
                if (index == 0) {
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                } else {
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 12.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                }
            }

            index >= itemCount - 2 -> {
                if (index == itemCount - 2) {
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 0.dp
                    )
                } else {
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 12.dp
                    )
                }
            }

            else -> {
                RoundedCornerShape(0.dp)
            }
        }
    }


    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun HospitalIconChip(imageUrl: String) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.White)
        ) {
            GlideImage(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                model = imageUrl,
                contentDescription = null
            )
        }

    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable

    fun PartnerShipCard(
        modifier: Modifier = Modifier,
        text1: String,
        text2: String,
        imageUrl: String,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color(0xFFFFFFFF).copy(alpha = 0.05f))
                .padding(start = 16.dp, end = 29.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    text = text1,
                    color = Color(0xFFDFD4FF),
                    fontSize = 42.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold

                )
                Text(
                    text = text2,
                    color = Color(0xFFDFD4FF),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold

                )

            }

            JarImage(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically),
                imageUrl = imageUrl,
                contentDescription = null
            )

        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun FooterLayout(modifier: Modifier = Modifier, footer: LandingFooter) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(

            ) {
                footer.headerText?.let {
                    Text(
                        text = it,
                        color = Color(0xFF2E2942),
                        fontSize = 32.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                footer.subHeaderText?.let {
                    Text(
                        text = it,

                        color = Color(0xFF3C3357),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
            JarImage(
                modifier = Modifier
                    .width(71.dp)
                    .height(91.dp)
                    .align(Alignment.CenterVertically),
                imageUrl = footer.icon,
                contentDescription = "footerImage"
            )

        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun GetInsuredLayout(
        modifier: Modifier = Modifier,
        data: LandingFooterCTA?,
        onClick: () -> Unit,
    ) {
        data?.let { _data ->
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = uiColor.color_272239))
            ) {
                Divider(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.05f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
                JarPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 20.dp),
                    text = _data.ctaText.orEmpty(),
                    onClick = {

                        onClick()

                    },
                    isAllCaps = false
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(CenterHorizontally)
                        .padding(top = 12.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Bottom),
                        text = _data.inPartnershipText.orEmpty(),
                        fontSize = 10.sp,
                        lineHeight = 10.sp,
                        color = colorResource(id = uiColor.color_B4B4B4),
                    )

                    JarImage(
                        modifier = Modifier
                            .height(16.dp)
                            .padding(start = 8.dp),
                        imageUrl = _data.iciciIcon,
                        contentDescription = null
                    )
                }
            }
        }

    }

    @Composable
    fun LandingPageDivider(width: Dp) {
        Divider(
            color = Color(0xFFFFFFFF).copy(alpha = 0.05f),
            modifier = Modifier
                .padding(top = 40.dp, bottom = 35.dp)
                .fillMaxWidth()
                .height(width)

        )

    }

    @Composable
    fun AbandonPageBottomSheet(hideBottomSheet: () -> Unit = {}, returnScreen: ReturnScreen) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF272239))
        ) {
            LaunchedEffect(key1 = Unit) {

                analyticsHandler.postEvent(
                    HealthInsuranceEvents.Health_Insurance_Event,
                    mapOf(
                        HealthInsuranceEvents.EVENT_NAME to Leaving_InsuranceShown
                    )
                )
            }
            Image(painter = painterResource(id = com.jar.app.core_ui.R.drawable.lamp_logo), contentDescription = "lamp")

            Column() {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 73.dp),
                    text = returnScreen.headerText.orEmpty(),
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                    Box(modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(start = 15.dp, top = 16.dp, end = 15.dp, bottom = 16.dp)
                        .background(
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_492B9D),
                            shape = RoundedCornerShape(size = 8.dp)
                        )
                        .paint(
                            sizeToIntrinsics = true,
                            painter = painterResource(id = R.drawable.overlay)
                        )
                        .padding(start = 15.dp, top = 16.dp, end = 15.dp, bottom = 16.dp)
                        )
                    {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .align(Alignment.CenterStart),
                            verticalArrangement = Arrangement.Center
                        ) {
                            returnScreen.benefitsList?.forEachIndexed { index, benefit ->
                                BenefitRow(
                                    benefit = benefit,
                                    index = index,
                                    size = returnScreen.benefitsList?.size.orZero()
                                )
                            }

                        }


                }


                returnScreen.subText?.let {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 24.dp),
                        text = it,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TileCard(
                        modifier = Modifier.weight(1f),
                        labelText = returnScreen.comparisonFieldsList?.first()?.name.orEmpty(),
                        labelTextColor = Color(0xFF631818),
                        chipText = returnScreen.comparisonFieldsList?.first()?.value.orEmpty(),
                        chipTextColor = Color.White,
                        chipBgColor = Color(0xFFAC593C),
                        tileBgImage = returnScreen.comparisonFieldsList?.first()?.image.orEmpty(),
                        tileBgColor = Color(0xFFFFD0BF)

                    )
                    Text(
                        text = "vs",
                        fontSize = 12.sp,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center
                    )
                    TileCard(
                        modifier = Modifier.weight(1f),
                        labelText = returnScreen.comparisonFieldsList?.last()?.name.orEmpty(),
                        labelTextColor = Color.White,
                        chipText = returnScreen.comparisonFieldsList?.last()?.value.orEmpty(),
                        chipTextColor = Color.Black,
                        chipBgColor = Color(0xFFEBB46A),
                        tileBgImage = returnScreen.comparisonFieldsList?.last()?.image.orEmpty(),
                        tileBgColor = Color(0xFF3C4BCC)
                    )

                }

                JarPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 40.dp),
                    text = returnScreen.getQuoteCTAText.orEmpty(),
                    isAllCaps = false,
                    onClick = {
                        analyticsHandler.postEvent(
                            HealthInsuranceEvents.Health_Insurance_Event,
                            mapOf(
                                HealthInsuranceEvents.Button to returnScreen.getQuoteCTAText.orEmpty(),
                                HealthInsuranceEvents.EVENT_NAME to Leaving_CTAClicked
                            )

                        )
                        hideBottomSheet()
                    }
                )

                TextButton(onClick = {
                    analyticsHandler.postEvent(
                        HealthInsuranceEvents.Health_Insurance_Event,
                        mapOf(
                            HealthInsuranceEvents.Button to returnScreen.exitCTAText.orEmpty(),
                            HealthInsuranceEvents.EVENT_NAME to Leaving_CTAClicked
                        )

                    )
                    popBackStack()
                }) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = returnScreen.exitCTAText.orEmpty(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline
                    )
                }

            }

        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun TileCard(
        modifier: Modifier = Modifier,
        labelText: String,
        labelTextColor: Color,
        chipText: String,
        chipTextColor: Color,
        chipBgColor: Color,
        tileBgColor: Color,
        tileBgImage: String,
    ) {
        Column(
            modifier = modifier
                .height(123.dp)
                .background(color = tileBgColor, shape = RoundedCornerShape(size = 12.dp))
                .padding(top = 12.dp),

            horizontalAlignment = CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = labelText,
                    fontSize = 16.sp,
                    style = JarTypography.h6.copy(
                        color = labelTextColor,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1
                )

                Text(
                    modifier = Modifier
                        .background(
                            color = chipBgColor,
                            shape = RoundedCornerShape(size = 30.dp)
                        )
                        .padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
                    text = chipText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = chipTextColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            JarImage(
                modifier = Modifier.size(80.dp),
                imageUrl = tileBgImage,
                contentDescription = "chips"
            )

        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun BenefitRow(modifier: Modifier = Modifier, benefit: BenefitsX, index: Int, size: Int) {
        Row {
            JarImage(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.CenterVertically),
                imageUrl = benefit.icon,
                contentDescription = "image description",
                contentScale = ContentScale.None
            )
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = benefit.text.orEmpty(),
                fontSize = 14.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFFFFFFFF)
            )
        }
        if (index < size - 1) {
            Spacer(modifier = modifier.padding(6.dp))
        }
    }


    @Composable
    @Preview
    fun Faq(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = Color(0x30FFFFFF),
                    shape = RoundedCornerShape(size = 8.dp)
                )

                .background(color = Color(0x29FFFFFF), shape = RoundedCornerShape(size = 8.dp))
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
        ) {
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.ic_question_mark),
                    contentDescription = "question_mark"
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = "FAQs",
                    fontSize = 14.sp,
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Right
                )
            }

        }
    }

}


