package com.jar.health_insurance.impl.ui.add_details

import android.graphics.Typeface
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.ErrorToastMessage
import com.jar.app.core_compose_ui.component.ICON_GRAVITY_END
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.ComposeLinkText
import com.jar.app.core_compose_ui.utils.HtmlText
import com.jar.app.core_compose_ui.utils.convertToAnnotatedString
import com.jar.app.core_compose_ui.views.OverlappingProfileViewCompose
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_ui.R
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_health_insurance.shared.data.models.add_details.AddDetailsPageErrors
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Good_HealthDecClicked
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Insurance_EldestAgeEntered
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Member_Clicked
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents.Next_InsuranceDetailsClicked
import com.jar.app.feature_health_insurance.shared.util.Constants.ADD_DETAILS_SCREEN
import com.jar.health_insurance.impl.ui.common.components.TopBarNeedHelpWhatsapp
import com.jar.health_insurance.impl.ui.components.BackPressHandler
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class AddDetailsFragment : BaseComposeFragment() {

    private val viewModel by viewModels<AddDetailsViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
        analyticsHandler.postEvent(
            HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.Insurance_Select_Members_Shown
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.color_141021)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onTriggerEvent(AddDetailsFragmentEvents.OnLoadData)
    }

    @Composable
    override fun RenderScreen() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        AddDetailsScreen(uiState = uiState)
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun AddDetailsScreen(
        uiState: AddDetailsFragmentState,
    ) {
        val systemUiController = rememberSystemUiController()
        systemUiController.setNavigationBarColor(Color(0xFF141021))


        uiState.errorMessage?.let { errorMessage ->
            ErrorToastMessage(errorMessage = errorMessage) {
                viewModel.onTriggerEvent(AddDetailsFragmentEvents.ErrorMessageDisplayed)
            }
        }

        BackPressHandler(onBackPressed = {
            popBackStack()
        })

        Scaffold(
            topBar = {
                uiState.addDetailsScreenStaticDataResponse?.toolBarText?.let { toolBarText ->
                    RenderBaseToolBar(onBackClick = {
                        popBackStack()
                    }, title = toolBarText, RightSection = {
                        TopBarNeedHelpWhatsapp(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .debounceClickable {
                                    analyticsHandler.postEvent(
                                        HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                            HealthInsuranceEvents.EVENT_NAME to HealthInsuranceEvents.Insurance_Need_Help_Clicked
                                        )
                                    )
                                    val initialMessage =
                                        uiState.addDetailsScreenStaticDataResponse.needHelp?.whatsappText
                                    val whatsappNumber =
                                        uiState.addDetailsScreenStaticDataResponse.needHelp?.whatsappNumber
                                    whatsappNumber?.let { message ->
                                        requireContext().openWhatsapp(
                                            message, initialMessage
                                        )
                                    }
                                },
                            iconLeftText = stringResource(id = com.jar.app.feature_health_insurance.R.string.need_help)
                        )
                    })
                }
            },
            bottomBar = {
                Column {
                    uiState.addDetailsScreenStaticDataResponse?.ctaText?.let {
                        JarPrimaryButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 20.dp),
                            text = it,
                            icon = R.drawable.ic_arrow_right,
                            isAllCaps = false,
                            onClick = {
                                analyticsHandler.postEvent(
                                    HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                        HealthInsuranceEvents.EVENT_NAME to Next_InsuranceDetailsClicked,
                                        HealthInsuranceEvents.Age to uiState.maximumAgeEntered,
                                        HealthInsuranceEvents.ADULT_CNT to viewModel.getAdultCnt(
                                            uiState.selectedMembers
                                        ),
                                        HealthInsuranceEvents.KID_CNT to viewModel.getKidsCnt(
                                            uiState.selectedMembers
                                        ),
                                    )
                                )

                                if (uiState.selectedMembers.contains(1)) {
                                    analyticsHandler.postEvent(
                                        HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                            HealthInsuranceEvents.Age to uiState.maximumAgeEntered,
                                            HealthInsuranceEvents.EVENT_NAME to Insurance_EldestAgeEntered
                                        )
                                    )
                                }

                                viewModel.onTriggerEvent(AddDetailsFragmentEvents.OnNextButtonClicked { orderId ->
                                    navigateTo(
                                        AddDetailsFragmentDirections.actionAddDetailsFragmentToSelectHealthInsurancePlanScreen(
                                            orderId
                                        )
                                    )
                                })
                            },
                            iconGravity = ICON_GRAVITY_END,
                            isEnabled = uiState.isButtonEnabled.value
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    uiState.addDetailsScreenStaticDataResponse?.footer?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
                        ) {
                            OverlappingProfileViewCompose(
                                it.images,
                                18.dp
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = convertToAnnotatedString(it.text, " ")
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Divider(
                                color = colorResource(id = R.color.color_3F365C),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            JarImage(
                                imageUrl = it.providerIcon,
                                contentDescription = null,
                                modifier = Modifier.height(16.dp)
                            )

                        }
                    }
                }
            },
            backgroundColor = Color(0xFF141021),
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(start = 16.dp, end = 16.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),
            ) {

                HeaderSection(
                    currentPageNumber = uiState.addDetailsScreenStaticDataResponse?.currentPageNumber,
                    totalPageNumber = uiState.addDetailsScreenStaticDataResponse?.totalPageNumber,
                    header = uiState.addDetailsScreenStaticDataResponse?.header
                )

                RenderHorizontalFilterList(
                    memberList = uiState.addDetailsScreenStaticDataResponse?.premiumValuesMemberDetailsList,
                    selectedMembersList = uiState.selectedMembers
                )

                GetMaximumAgeSection(
                    shouldKidsPolicyBeShown = uiState.shouldKidsPolicyBeShown.value,
                    shouldHintForSpouseAndMyselfBeShown = uiState.shouldHintForMyselfAndSpouseBeShown.value,
                    isKidSelected = uiState.isKidSelected.value,
                    ageEntered = uiState.maximumAgeEntered,
                    kidsInfoIcon = uiState.addDetailsScreenStaticDataResponse?.kidsInfoIcon,
                    kidsPolicyInfoText = uiState.addDetailsScreenStaticDataResponse?.kidsPolicyInfoText,
                    hintForMyselfAndSpouse = uiState.addDetailsScreenStaticDataResponse?.hintForMyselfAndSpouse,
                    hintForMyself = uiState.addDetailsScreenStaticDataResponse?.hintForMyself,
                    errorInfoIcon = uiState.addDetailsScreenStaticDataResponse?.errorInfoIcon,
                    defaultInfoIcon = uiState.addDetailsScreenStaticDataResponse?.defaultInfoIcon,
                    defaultInfoText = uiState.addDetailsScreenStaticDataResponse?.defaultInfoText,
                    kidsInfoText = uiState.addDetailsScreenStaticDataResponse?.kidsInfoText,
                    textFieldErrors = uiState.addDetailsScreenStaticDataResponse?.errors.orEmpty(),
                    insuranceMaxAge = uiState.addDetailsScreenStaticDataResponse?.insuranceMaxAge
                        ?: 45,
                    insuranceMinAge = uiState.addDetailsScreenStaticDataResponse?.insuranceMinAge
                        ?: 18,
                    kidsPolicyInfoIcon = uiState.addDetailsScreenStaticDataResponse?.kidsPolicyInfoIcon.orEmpty()
                )

                uiState.addDetailsScreenStaticDataResponse?.mandatoryContextText?.let {
                    GoodHealthDeclarationCard(
                        isGoodHealthDeclarationChecked = uiState.isGoodHealthDeclarationChecked,
                        mandatoryContextText = uiState.addDetailsScreenStaticDataResponse.mandatoryContextText,
                        mandatoryHeader = uiState.addDetailsScreenStaticDataResponse.mandatoryHeader
                    )
                }
            }
        }
    }


    @Composable
    fun HeaderSection(
        currentPageNumber: Int?,
        totalPageNumber: Int?,
        header: String?,
    ) {

        val context = LocalContext.current
        val typeface = try {
            ResourcesCompat.getFont(context, R.font.inter_bold) ?: Typeface.DEFAULT_BOLD
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }

        header?.let {
            HtmlText(
                text = it,
                textSize = 32f,
                typeface = typeface,
                modifier = Modifier.padding(bottom = 24.dp),
            )
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun GetMaximumAgeSection(
        shouldKidsPolicyBeShown: Boolean,
        shouldHintForSpouseAndMyselfBeShown: Boolean,
        isKidSelected: Boolean,
        ageEntered: String,
        kidsInfoIcon: String?,
        kidsPolicyInfoText: String?,
        hintForMyselfAndSpouse: String?,
        hintForMyself: String?,
        errorInfoIcon: String?,
        defaultInfoIcon: String?,
        defaultInfoText: String?,
        kidsInfoText: String?,
        textFieldErrors: Map<String, String>,
        insuranceMaxAge: Int,
        insuranceMinAge: Int,
        kidsPolicyInfoIcon: String
    ) {
        if (shouldKidsPolicyBeShown) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 40.dp),
            ) {
                GlideImage(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 2.dp)
                        .size(16.dp),
                    model = kidsPolicyInfoIcon,
                    contentDescription = null
                )

                kidsPolicyInfoText?.let {
                    Text(
                        text = it,
                        color = Color(0xFFACA1D3),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                }
            }
        }

        (if (shouldHintForSpouseAndMyselfBeShown) hintForMyselfAndSpouse else hintForMyself)?.let {
            Text(
                text = it,
                color = Color(0xFFEEEAFF),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(
                        top = if (shouldKidsPolicyBeShown) 0.dp else 40.dp
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color(0xFF2E2942),
                    unfocusedBorderColor = Color(0xFF2E2942),
                    focusedBorderColor = Color(0xFF7745FF),
                    textColor = Color.White,
                    cursorColor = Color(0xFFACA1D3),
                    errorBorderColor = Color(0xFFEB6A6E),
                    errorCursorColor = Color(0xFFACA1D3)
                ),
                value = ageEntered,
                onValueChange = { textEntered ->
                    if (textEntered.trim().isNotEmpty() && textEntered.isDigitsOnly()) {
                        if (textEntered.toInt() <= 99) {
                            viewModel.onTriggerEvent(
                                AddDetailsFragmentEvents.OnMaximumAgeChanged(
                                    textEntered
                                )
                            )
                        }
                    } else {
                        if (textEntered.isEmpty()) {
                            viewModel.onTriggerEvent(
                                AddDetailsFragmentEvents.OnMaximumAgeChanged(
                                    textEntered
                                )
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                ),
                isError = (ageEntered.isNotEmpty() && ageEntered.toInt() > 45 || ageEntered.isNotEmpty() && ageEntered.toInt() < 18),
            )

            (if (shouldHintForSpouseAndMyselfBeShown) hintForMyselfAndSpouse else hintForMyself)?.let {
                if (ageEntered.isEmpty()) {
                    Text(
                        text = it,
                        color = Color(0xFF776E94),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterStart)
                    )
                } else {
                    Text(
                        text = "years",
                        color = Color(0xFFDFD4FF),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 18.dp)
                    )
                }
            }
        }

        if (ageEntered.isNotEmpty()) {
            if (ageEntered.toInt() > insuranceMaxAge || ageEntered.toInt() < insuranceMinAge) {
                if (ageEntered.toInt() < insuranceMinAge) {
                    Row(
                        modifier = Modifier.padding(bottom = 40.dp)
                    ) {
                        errorInfoIcon?.let {
                            GlideImage(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(16.dp),
                                model = it,
                                contentDescription = null,
                            )
                        }

                        textFieldErrors[AddDetailsPageErrors.FE_MIN_AGE_ERROR.error]?.let {
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                color = Color(0xFFEB6A6E)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.padding(bottom = 40.dp)
                    ) {
                        errorInfoIcon?.let {
                            GlideImage(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(16.dp),
                                model = it,
                                contentDescription = null
                            )
                        }

                        (if (shouldHintForSpouseAndMyselfBeShown) textFieldErrors[AddDetailsPageErrors.FE_MAX_AGE_SPOUSE_ERROR.error] else textFieldErrors[AddDetailsPageErrors.FE_MAX_AGE_ERROR.error])?.let {
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                color = Color(0xFFEB6A6E)
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = if (isKidSelected) 12.dp else 40.dp
                        ),
                ) {
                    GlideImage(
                        modifier = Modifier
                            .padding(end = 8.dp, top = 2.dp)
                            .size(16.dp),
                        model = defaultInfoIcon,
                        contentDescription = null
                    )

                    defaultInfoText?.let {
                        Text(
                            text = it,
                            color = Color(0xFFACA1D3),
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Top)
                        )
                    }
                }

                if (isKidSelected) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlideImage(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                            model = kidsInfoIcon,
                            contentDescription = null
                        )

                        kidsInfoText?.let {
                            Text(
                                text = it, color = Color(0xFFACA1D3), fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (isKidSelected) 12.dp else 40.dp),
            ) {
                GlideImage(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 2.dp)
                        .size(16.dp),
                    model = defaultInfoIcon,
                    contentDescription = null
                )

                defaultInfoText?.let {
                    Text(
                        text = it,
                        color = Color(0xFFACA1D3),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Top)
                    )
                }
            }

            if (isKidSelected) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(16.dp),
                        model = kidsInfoIcon,
                        contentDescription = null
                    )

                    kidsInfoText?.let {
                        Text(
                            text = it, color = Color(0xFFACA1D3), fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun GoodHealthDeclarationCard(
        mandatoryContextText: String?,
        mandatoryHeader: String?,
        isGoodHealthDeclarationChecked: Boolean,
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color(0xFF272239),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                mandatoryHeader?.let {
                    Text(
                        text = it,

                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 20.dp), verticalAlignment = Alignment.Top
                ) {

                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        Checkbox(
                            checked = isGoodHealthDeclarationChecked,
                            onCheckedChange = { isChecked ->
                                viewModel.onTriggerEvent(
                                    AddDetailsFragmentEvents.OnGoodHealthDeclarationCheckedChanged(
                                        isChecked
                                    )
                                )
                                analyticsHandler.postEvent(
                                    HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                                        HealthInsuranceEvents.EVENT_NAME to Good_HealthDecClicked
                                    )
                                )
                            },
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 8.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1EA787),
                                uncheckedColor = Color(0xFFEEEAFF),
                                checkmarkColor = Color.White
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.padding(end = 12.dp))

                    mandatoryContextText?.let { mandatoryContextText ->
                        ComposeLinkText(string = mandatoryContextText,
                            textStyle = com.jar.app.feature_health_insurance.R.style.PolicyDeclarationTextStyle,
                            textSize = 13f,
                            linkTextColor = R.color.white,
                            clickHandler = {
                                webPdfViewerApi.openPdf(it)
                            })
                    }
                }
            }
        }
    }

    @Composable
    fun RenderHorizontalFilterList(
        modifier: Modifier = Modifier,
        memberList: List<String>?,
        selectedMembersList: List<Int>,
    ) {

        val newSelectedMemberList = mutableListOf<Int>()
        newSelectedMemberList.addAll(selectedMembersList)

        val onSelection: (Int) -> Unit = {
            newSelectedMemberList.add(it)

            var selectedMember = ""

            when (it) {
                1 -> {
                    selectedMember = HealthInsuranceEvents.Member_Spouse
                }

                2 -> {
                    selectedMember = HealthInsuranceEvents.Member_Kid1
                }

                3 -> {
                    selectedMember = HealthInsuranceEvents.Member_Kid2
                }
            }

            if (selectedMember.isNotEmpty()) {
                analyticsHandler.postEvent(
                    HealthInsuranceEvents.Health_Insurance_Event, mapOf(
                        HealthInsuranceEvents.Member to selectedMember,
                        HealthInsuranceEvents.EVENT_NAME to Member_Clicked
                    )
                )
            }
            viewModel.onTriggerEvent(
                AddDetailsFragmentEvents.OnSelectedMembersChanged(
                    newSelectedMemberList
                )
            )
        }

        val onDeselection: (Int) -> Unit = {
            if (it != 0) {
                newSelectedMemberList.remove(it)
                viewModel.onTriggerEvent(
                    AddDetailsFragmentEvents.OnSelectedMembersChanged(
                        newSelectedMemberList
                    )
                )
            }
        }

        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFF141021))
                .padding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            memberList?.let {
                itemsIndexed(it) { index, title ->
                    if (selectedMembersList.contains(index)) {
                        SelectedCard(title, index, onDeselection)
                    } else {
                        UnSelectedCard(title, index, onSelection)
                    }
                }
            }
        }
    }

    @Composable
    fun UnSelectedCard(title: String?, index: Int, function: (Int) -> Unit) {
        Text(text = title.orEmpty(),
            style = JarTypography.body1,
            color = Color.White,
            modifier = Modifier
                .background(
                    Color(0xFF141021), shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.color_3C3357),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .debounceClickable { function(index) })
    }

    @Composable
    fun SelectedCard(title: String?, index: Int, onDeselection: (Int) -> Unit) {
        Card(
            shape = RoundedCornerShape(8.dp), backgroundColor = Color(0xFF7745FF)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .debounceClickable { onDeselection(index) },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick_green),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Text(
                    text = title.orEmpty(),
                    modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, end = 16.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
