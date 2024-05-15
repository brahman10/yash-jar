package com.jar.app.feature_calculator.impl.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.isNullOrZero
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.toIntOrZero
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_compose_ui.views.renderExpandableFaqList
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_calculator.impl.ui.components.CalculationDetailCard
import com.jar.app.feature_calculator.impl.ui.components.CalculatorSliderItem
import com.jar.app.feature_calculator.impl.ui.components.ToolbarWithHelpButton
import com.jar.app.feature_calculator.shared.domain.model.CalculatorDataRes
import com.jar.app.feature_calculator.shared.domain.model.CalculatorType
import com.jar.app.feature_calculator.shared.domain.model.SliderData
import com.jar.app.feature_calculator.shared.domain.model.SliderSubType
import com.jar.app.feature_calculator.shared.domain.model.SliderType
import com.jar.app.feature_calculator.shared.ui.CalculatedData
import com.jar.app.feature_calculator.shared.ui.CalculatorViewModel
import com.jar.app.feature_calculator.shared.util.CalculatorConstants
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.api.LendingApi
import com.jar.app.feature_lending.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
internal class CalculatorFragment : BaseComposeFragment() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    private val args by navArgs<CalculatorFragmentArgs>()

    @Inject
    lateinit var lendingApi: LendingApi

    private val viewModelProvider by viewModels<CalculatorViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }

    private fun handleBackPress() {
        viewModel.syncAnalyticsEvent(
            CalculatorConstants.EventKey.EmiCalculator_BackButtonClicked,
            data = mapOf(
                CalculatorConstants.EventKey.screen_name to CalculatorConstants.EventKey.calculator_screen,
            ),
            false
        )
        popBackStack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchData(CalculatorType.valueOf(args.calculatorType))
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
    }

    @Composable
    override fun RenderScreen() {
        CalculatorScreenRoot(
            viewModel = viewModel,
            calculatorType = CalculatorType.valueOf(args.calculatorType),
            onBackButtonClick = {
                viewModel.syncAnalyticsEvent(
                    CalculatorConstants.EventKey.EmiCalculator_BackButtonClicked,
                    mapOf(
                        CalculatorConstants.EventKey.screen_name to CalculatorConstants.EventKey.calculator_screen,
                    ),
                    false
                )
                handleBackPress()
            },
            onHelpButtonClick = {
                viewModel.syncAnalyticsEvent(
                    CalculatorConstants.EventKey.EmiCalculator_NeedHelpClicked,
                    mapOf(
                        CalculatorConstants.EventKey.screen_name to CalculatorConstants.EventKey.calculator_screen,
                    ),
                    false
                )
                openHelpSection()
            },
            onRedirectButtonClick = { redirectToDeeplink(it) },
            onAnyValueChange = { amount, tenure, interest ->
                viewModel.onValueChanged(
                    amount,
                    tenure,
                    interest,
                    CalculatorType.valueOf(args.calculatorType)
                )
            },
            onAnalyticsEvent = { eventName, data, syncOncePerSession ->
                viewModel.syncAnalyticsEvent(eventName, data, syncOncePerSession)
            }
        )
    }

    private fun openHelpSection() {
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getCustomStringFormatted(
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_emi_calculator_help,
            name.orEmpty(),
            number.orEmpty()
        )
        requireContext().openWhatsapp(sendTo, message)
    }

    private fun redirectToDeeplink(deepLink: String) {
        if (CalculatorType.valueOf(args.calculatorType) == CalculatorType.SAVINGS_CALCULATOR) {
            EventBus.getDefault().post(HandleDeepLinkEvent(deepLink, args.calculatorType))
        } else {
            if (deepLink.contains(BaseConstants.ExternalDeepLinks.LENDING_ONBOARDING, true)) {
                lendingApi.openLendingFlowV2(
                    flowType = "EMI_CALCULATOR",
                    apiCallback = { _, isLoading ->
                        if (isLoading) showProgressBar()
                        else dismissProgressBar()
                    })
            } else {
                lendingApi.openEmiCalculatorLaunchingSoonScreen()
            }
        }
    }
}

@Composable
private fun CalculatorScreenRoot(
    viewModel: CalculatorViewModel,
    calculatorType: CalculatorType,
    onBackButtonClick: () -> Unit,
    onHelpButtonClick: () -> Unit,
    onRedirectButtonClick: (deeplink: String) -> Unit,
    onAnyValueChange: (amount: Int, tenure: Int, interest: Float) -> Unit,
    onAnalyticsEvent: (eventName: String, data: Map<String, Any>?, syncOncePerSession: Boolean) -> Unit
) {

    val uiState by viewModel.uiStateFlow.collectAsState()

    if (uiState.isLoading) {
        if(calculatorType == CalculatorType.EMI_CALCULATOR) {
            onAnalyticsEvent(
                CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                mapOf(
                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.loading_screen_shown,
                ),
                false
            )
        }else{
            onAnalyticsEvent(
                CalculatorConstants.EventKey.SavingsCalculator_Shown,
                mapOf(
                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.loading_screen_shown,
                ),
                false
            )
        }
        CalculatorLottieTransition(
            modifier = Modifier
                .fillMaxSize(),
            lottieUrl = CalculatorConstants.LottieUrls.EMI_CALCULATOR
        )
    } else if (uiState.errorString.isNullOrBlank().not()) {
        ErrorScreen(
            title = uiState.errorString
                ?: stringResource(id = MR.strings.feature_lending_something_went_wrong.resourceId)
        )
    } else {
        uiState.data?.let {
            CalculatorMainScreen(
                calculatorDataRes = it,
                calculatorType = calculatorType,
                sliderList = uiState.sliderList.orEmpty(),
                calculatedData = uiState.calculatedData,
                onBackButtonClick = onBackButtonClick,
                onHelpButtonClick = onHelpButtonClick,
                onAnyValueChange = onAnyValueChange,
                onRedirectButtonClick = onRedirectButtonClick,
                onToggleTenure = { subType, amount, interest ->
                    viewModel.onTenureChange(subType, amount, interest, calculatorType)
                },
                onAnalyticsEvent = onAnalyticsEvent
            )
        }
    }
}

@Composable
private fun CalculatorLottieTransition(
    modifier: Modifier,
    lottieUrl: String
) {

    val composition = rememberLottieComposition(LottieCompositionSpec.Url(lottieUrl))

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        com.airbnb.lottie.compose.LottieAnimation(
            modifier = Modifier
                .size(56.dp),
            composition = composition.value,
            contentScale = ContentScale.FillBounds,
            iterations = LottieConstants.IterateForever,
            speed = 2.5f
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_calculator_loading.resourceId),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight(700),
                color = Color(0xFFEEEAFF),
                textAlign = TextAlign.Center,
            )
        )
    }
}

@Composable
private fun CalculatorMainScreen(
    calculatorDataRes: CalculatorDataRes,
    calculatorType: CalculatorType,
    sliderList: List<SliderData>,
    calculatedData: CalculatedData?,
    onBackButtonClick: () -> Unit,
    onHelpButtonClick: () -> Unit,
    onRedirectButtonClick: (deeplink: String) -> Unit,
    onAnyValueChange: (amount: Int, tenure: Int, interest: Float) -> Unit,
    onToggleTenure: (tenureType: SliderSubType, amount: Int, interest: Float) -> Unit,
    onAnalyticsEvent: (eventName: String, data: Map<String, Any>?, oncePerSession: Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            if(calculatorType == CalculatorType.EMI_CALCULATOR){
                ToolbarWithHelpButton(
                    onBackButtonClick = onBackButtonClick,
                    title = calculatorDataRes.title,
                    onHelpButtonClick = onHelpButtonClick,
                    helpButtonText = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_need_help.resourceId)
                )
            }else {
                RenderBaseToolBar(onBackClick = onBackButtonClick, title =calculatorDataRes.title)
            }
        },
        content = {

            var amount by rememberSaveable {
                mutableStateOf(
                    sliderList.findLast { it.getSliderSubType() == SliderSubType.AMOUNT }?.min.orZero()
                        .toInt()
                )
            }
            var tenure by rememberSaveable {
                mutableStateOf(
                    sliderList.findLast { it.getSliderSubType() == SliderSubType.YEAR }?.min.orZero()
                        .toInt()
                )
            }
            var interest by rememberSaveable {
                mutableStateOf(
                    sliderList.findLast { it.getSliderSubType() == SliderSubType.PERCENTAGE }?.min
                        ?: 0f
                )
            }

            var state by rememberSaveable { mutableStateOf(SliderSubType.YEAR) }
            val faqSelectedIndex = remember { mutableStateOf<Int>(-1) }
            LaunchedEffect(Unit) {
                if(calculatorType == CalculatorType.EMI_CALCULATOR){
                    onAnalyticsEvent(
                        CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                        mapOf(
                            CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.calculator_screen_shown,
                            CalculatorConstants.EventKey.amount to amount,
                            CalculatorConstants.EventKey.tenure to tenure,
                            CalculatorConstants.EventKey.interest to interest,
                            CalculatorConstants.EventKey.tenure_type to state.name,
                        ),
                        false
                    )
                }else{
                    onAnalyticsEvent(
                        CalculatorConstants.EventKey.SavingsCalculator_Shown,
                        mapOf(
                            CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.calculator_screen_shown,
                            CalculatorConstants.EventKey.amount to amount,
                            CalculatorConstants.EventKey.tenure to tenure,
                            CalculatorConstants.EventKey.interest to interest,
                            CalculatorConstants.EventKey.tenure_type to state.name,
                        ),
                        false
                    )
                }
                onAnyValueChange(amount, tenure, interest)
            }

            LaunchedEffect(key1 = calculatedData?.finalAmount, block = {
                delay(1000)
                if ((calculatedData?.finalAmount ?: 0f) > 0f) {
                    onAnalyticsEvent(
                        CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                        mapOf(
                            CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.calculator_screen_updated,
                            CalculatorConstants.EventKey.amount to (if (calculatedData?.amount.isNullOrZero()) "NaN" else calculatedData?.amount?.orZero()
                                .toString()),
                            CalculatorConstants.EventKey.tenure to (if (calculatedData?.tenure.isNullOrZero()) "NaN" else calculatedData?.tenure?.orZero()
                                .toString()),
                            CalculatorConstants.EventKey.interest to (if (calculatedData?.interest.isNullOrZero()) "NaN" else calculatedData?.interest?.orZero()
                                .toString()),
                            CalculatorConstants.EventKey.tenure_type to calculatedData?.tenureType?.name.orEmpty(),
                            CalculatorConstants.EventKey.monthly_emi to (if (calculatedData?.finalAmount.isNullOrZero()) "NaN" else calculatedData?.finalAmount.orZero()
                                .toInt().toString()),
                        ),
                        false
                    )
                }
            })

            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
            ) {

                item(key = "space") {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                items(sliderList.size, key = {
                    sliderList[it].getSliderType().name + sliderList[it].getSliderSubType().name
                }) { index ->
                    val sliderData = sliderList[index]
                    if(calculatorType != CalculatorType.SAVINGS_CALCULATOR || sliderData.getSliderType() != SliderType.PERCENTAGE) {
                        CalculatorSliderItem(
                            modifier = Modifier.padding(top = 20.dp),
                            calculatorType = calculatorType,
                            title = sliderData.title,
                            currentValue = when (sliderData.getSliderType()) {
                                SliderType.AMOUNT -> amount.toFloat()
                                SliderType.TENURE -> tenure.toFloat()
                                SliderType.PERCENTAGE -> interest
                                SliderType.NONE -> 0f
                            },
                            minValue = sliderData.min,
                            maxValue = sliderData.max,
                            stepCount = sliderData.stepCount,
                            minTitle = if (sliderData.getSliderType() == SliderType.AMOUNT)
                                "${sliderData.getSliderSubType().affix}${
                                    sliderData.min.toInt().getFormattedAmount(0)
                                }"
                            else if (sliderData.getSliderType() == SliderType.PERCENTAGE)
                                "${sliderData.min}${sliderData.getSliderSubType().affix}"
                            else
                                "${sliderData.min.toInt()} ${sliderData.getSliderSubType().affix}",
                            maxTitle = if (sliderData.getSliderType() == SliderType.AMOUNT)
                                "${sliderData.getSliderSubType().affix}${
                                    sliderData.max.toInt().getFormattedAmount(0)
                                }"
                            else if (sliderData.getSliderType() == SliderType.PERCENTAGE)
                                "${sliderData.max}${sliderData.getSliderSubType().affix}"
                            else
                                "${sliderData.max.toInt()} ${sliderData.getSliderSubType().affix}",
                            onSliderChange = { newValue ->
                                when (sliderData.getSliderType()) {
                                    SliderType.AMOUNT -> {
                                        if (calculatorType == CalculatorType.EMI_CALCULATOR) {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.amount_changed,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.slider
                                                ),
                                                true
                                            )
                                        } else {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.SavingsCalculator_Clicked,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.amount_changed,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.slider,
                                                    CalculatorConstants.EventKey.amount to newValue.roundToInt()
                                                ),
                                                true
                                            )
                                        }
                                        amount = newValue.roundToInt()
                                    }

                                    SliderType.TENURE -> {
                                        if (calculatorType == CalculatorType.EMI_CALCULATOR) {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.tenure_changed,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.slider,
                                                ),
                                                true
                                            )
                                        } else {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.SavingsCalculator_Clicked,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.Time_Period,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.slider,
                                                    CalculatorConstants.EventKey.tenure_type to calculatedData?.tenureType?.name.orEmpty(),
                                                    CalculatorConstants.EventKey.Time_Period to newValue.toInt(),
                                                ),
                                                true
                                            )
                                        }
                                        tenure = newValue.toInt()
                                    }

                                    SliderType.PERCENTAGE -> {
                                        if (calculatorType == CalculatorType.EMI_CALCULATOR) {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.interest_changed,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.slider
                                                ),
                                                true
                                            )
                                        } else {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.SavingsCalculator_Clicked,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.Growth_Rate,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.slider,
                                                    CalculatorConstants.EventKey.Growth_Rate to newValue
                                                ),
                                                true
                                            )
                                        }
                                        interest = newValue
                                    }

                                    SliderType.NONE -> {}
                                }
                                onAnyValueChange(amount, tenure, interest)
                            },
                            onTextChange = { newValue ->
                                when (sliderData.getSliderType()) {
                                    SliderType.AMOUNT -> {
                                        amount =
                                            if (newValue.isNullOrBlank()) sliderData.min.toInt() else {
                                                val newValueInInt = newValue.toIntOrZero()
                                                if (newValueInInt > sliderData.max.toInt()) {
                                                    sliderData.max.toInt()
                                                } else {
                                                    newValueInInt
                                                }
                                            }
                                        if (calculatorType == CalculatorType.EMI_CALCULATOR) {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.amount_changed,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.typed
                                                ),
                                                true
                                            )
                                        } else {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.SavingsCalculator_Clicked,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.amount_changed,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.typed,
                                                    CalculatorConstants.EventKey.amount to amount
                                                ),
                                                true
                                            )
                                        }
                                    }

                                    SliderType.TENURE -> {
                                        tenure =
                                            if (newValue.isNullOrBlank()) 0 else newValue.toIntOrZero()

                                        if (calculatorType == CalculatorType.EMI_CALCULATOR) {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.tenure_changed,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.typed,
                                                ),
                                                true
                                            )
                                        } else {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.SavingsCalculator_Clicked,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.Time_Period,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.typed,
                                                    CalculatorConstants.EventKey.tenure_type to calculatedData?.tenureType?.name.orEmpty(),
                                                    CalculatorConstants.EventKey.Time_Period to tenure,
                                                ),
                                                true
                                            )
                                        }
                                    }

                                    SliderType.PERCENTAGE -> {
                                        interest = if (newValue.isNullOrBlank()) 0f else {
                                            if (newValue.contains(".", true))
                                                newValue.toFloat()
                                            else
                                                (newValue.toFloat() / 100)
                                        }
                                        if (calculatorType == CalculatorType.EMI_CALCULATOR) {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.interest_changed,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.typed
                                                ),
                                                true
                                            )
                                        } else {
                                            onAnalyticsEvent(
                                                CalculatorConstants.EventKey.SavingsCalculator_Clicked,
                                                mapOf(
                                                    CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.Growth_Rate,
                                                    CalculatorConstants.EventKey.type to CalculatorConstants.EventKey.typed,
                                                    CalculatorConstants.EventKey.Growth_Rate to interest
                                                ),
                                                true
                                            )
                                        }
                                    }

                                    SliderType.NONE -> {}
                                }
                                onAnyValueChange(amount, tenure, interest)
                            },
                            showToggle = sliderData.getSliderType() == SliderType.TENURE,
                            toggleState = state,
                            onToggle = { defaultValue, subType ->
                                onAnalyticsEvent(
                                    CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                                    mapOf(
                                        CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.tenure_type_changed,
                                        CalculatorConstants.EventKey.from to state.name,
                                        CalculatorConstants.EventKey.to to subType.name,
                                    ),
                                    false
                                )
                                state = subType
                                tenure = defaultValue.toInt()
                                onToggleTenure(subType, amount, interest)
                            },
                            type = sliderData.getSliderType()
                        )
                    }

                }
                if (calculatorType != CalculatorType.SAVINGS_CALCULATOR || calculatedData?.finalAmount.orZero() != 0f) {
                    item(key = "calculator_card") {
                        CalculationDetailCard(
                            modifier = Modifier.padding(top = 20.dp),
                            cardData = calculatorDataRes.cardDetails,
                            field1Value = if (calculatedData?.finalAmount.isNullOrZero()) "NaN" else
                                calculatedData?.finalAmount?.roundToInt().orZero().getFormattedAmount(0),

                            field2Value = if (calculatedData?.amount.isNullOrZero()) "NaN" else
                                "₹ ${calculatedData?.amount.orZero().getFormattedAmount(0)}",

                            field3Value = if (calculatedData?.tenure.isNullOrZero()) "NaN" else
                                "${calculatedData?.tenure} ${calculatedData?.tenureType?.affix.orEmpty()}",

                            field4Value = if (calculatedData?.interest.isNullOrZero()) "NaN" else
                                stringResource(
                                    id = MR.strings.feature_lending_float_prefix_interest.resourceId,
                                    (calculatedData?.interest ?: 0f)
                                ),

                            onButtonClick = {
                                onAnalyticsEvent(
                                    CalculatorConstants.EventKey.EmiCalculator_MainScreenLaunched,
                                    mapOf(
                                        CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.get_instant_loan_clicked,
                                        CalculatorConstants.EventKey.amount to (calculatedData?.amount
                                            ?: 0),
                                        CalculatorConstants.EventKey.tenure to (calculatedData?.tenure
                                            ?: 0),
                                        CalculatorConstants.EventKey.interest to (calculatedData?.interest
                                            ?: 0f),
                                        CalculatorConstants.EventKey.tenure_type to calculatedData?.tenureType?.name.orEmpty(),
                                        CalculatorConstants.EventKey.monthly_emi to (calculatedData?.finalAmount
                                            ?: 0f),
                                    ),
                                    false
                                )
                                onRedirectButtonClick(it)
                            }
                        )
                    }
                }

                item(key = "disclaimer_text") {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 32.dp),
                        text = calculatorDataRes.disclaimerText,
                        style = TextStyle(
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            fontFamily = jarFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFACA1D3),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
                if (calculatorType == CalculatorType.SAVINGS_CALCULATOR) {
                    calculatedData?.let { data ->
                        item(key = "saving_details_view") {
                            SavingsDetailsView(data)
                        }
                    }

                    calculatorDataRes.faqResponse?.let { faqResponse ->
                        item(key = "FAQ") {
                            Spacer(modifier = Modifier.height(40.dp))
                            Text(
                                text = faqResponse.title,
                                style = JarTypography.h2,
                                color = Color(0xFFE9E9EB),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        val faqs = calculatorDataRes.faqResponse?.faqs?.mapIndexed { index, faq ->
                            ExpandableCardModel(
                                id = index,
                                faqHeaderText = faq.question,
                                faqExpandableContentText = faq.answer
                            )
                        }.orEmpty()
                        renderExpandableFaqList(
                            this, faqs, faqSelectedIndex,
                            listBackgroundColor = com.jar.app.core_ui.R.color.color_221D32,
                            cardBackgroundColor = com.jar.app.core_ui.R.color.color_3C3357,
                            questionTextColor = com.jar.app.core_ui.R.color.white,
                            answerTextColor = com.jar.app.core_ui.R.color.color_D5CDF2,
                            onClick = { index ->
                                faqs.getOrNull(index)?.let { faq ->
                                    onAnalyticsEvent(
                                        CalculatorConstants.EventKey.SavingsCalculator_Clicked,
                                        mapOf(
                                            CalculatorConstants.EventKey.action to CalculatorConstants.EventKey.FAQ,
                                        ),
                                        false
                                    )
                        }
                            },
                            cardHorizontalPadding = 0.dp
                        )
                    }
                }
            }
        },
        backgroundColor = JarColors.color_221D32
    )
}

@Composable
fun SavingsDetailsView(calculatedData: CalculatedData) {
    val interestEarned = calculatedData.amount ?: 1
    val principalAmount = (calculatedData.finalAmount ?: 1f).roundToInt() - interestEarned
    Column(modifier = Modifier.padding(top = 40.dp)) {
        Text(
            text = "Savings Details",
            style = JarTypography.h2,
            color = Color(0xFFE9E9EB),
            textAlign = TextAlign.Center
        )
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
        ) {
            TwoToneArcView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                calculatedData = calculatedData
            )

        }
        if (principalAmount.orZero() != 0) {
            SavingInfoRow(
                label = "Saving Amt",
                value = "₹ ${principalAmount.toInt()}",
                boxColor = Color(0xFFB59AFF)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (interestEarned.orZero() != 0) {
            SavingInfoRow(
                boxColor = Color(0xFF593AC2),
                label = "Estimated Growth",
                value = "₹ ${interestEarned.orZero()}"
            )
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = Color(0xFF3C3357)
            )
        }
        SavingInfoRow(
            boxColor = Color(0xFF593AC2),
            label = "Total Amt",
            value = "₹ ${(principalAmount + interestEarned)}",
            showColoredBox = false
        )
    }
}

@Composable
fun SavingInfoRow(
    label: String,
    value: String,
    boxColor: Color,
    showColoredBox:Boolean = true
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if(showColoredBox) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = boxColor, shape = RoundedCornerShape(size = 4.dp))
            )
        }
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            textAlign = TextAlign.Start,
            style = JarTypography.body1.copy(colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3))
        )
        Row(
            modifier = Modifier
                .weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = value,
                textAlign = TextAlign.End,
                style = JarTypography.body1.copy(colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3))
            )
        }

    }
}

@Composable
internal fun ErrorScreen(
    modifier: Modifier = Modifier,
    title: String = "",
    subTitle: String = ""
) {

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Image(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.feature_leanding_ic_alert),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                text = title,
                style = JarTypography.h5,
                color = colorResource(id = com.jar.app.core_ui.R.color.white),
                lineHeight = 24.sp,
                fontSize = 18.sp,
                fontWeight = FontWeight(700),
                textAlign = TextAlign.Center
            )
            if (subTitle.isNotEmpty())
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    text = subTitle,
                    style = JarTypography.body2,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                    fontSize = 14.sp,
                    fontWeight = FontWeight(700),
                    textAlign = TextAlign.Center
                )
        }
    }

}

@Composable
fun TwoToneArcView(modifier: Modifier = Modifier, calculatedData: CalculatedData) {
    val interestEarned = calculatedData.amount ?: 1
    val principalAmount = (calculatedData.finalAmount ?: 1f) - interestEarned
    val totalAmount = principalAmount + interestEarned

    // Calculate the proportions
    val principalProportion = principalAmount / totalAmount
    val interestProportion = interestEarned / totalAmount

    Canvas(modifier = modifier)
    {
        val arcRadius = size.minDimension / 1.8f
        val arcCenter = Offset(size.width / 2, size.height / 1.17f)
        // Color for principal amount
        drawArc(
            color = Color(0xFFB59AFF),
            startAngle = 180f,
            sweepAngle = 180f * principalProportion,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = 100f)
        )

        // Color for interest earned
        drawArc(
            color = Color(0xFF593AC2),
            startAngle = 180f + 180f * principalProportion,
            sweepAngle = 180f * interestProportion,
            useCenter = false,
            topLeft = arcCenter - Offset(arcRadius, arcRadius),
            size = Size(arcRadius * 2, arcRadius * 2),
            style = Stroke(width = 100f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewText() {
    Text(
        text = "Savings Details",
        style = JarTypography.h2,
        color = Color(0xFFE9E9EB),
        textAlign = TextAlign.Center
    )
}