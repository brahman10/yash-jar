package com.jar.health_insurance.impl.ui

import android.graphics.Bitmap
import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getPhonePeVersionCode
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeBottomSheetDialogFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_health_insurance.shared.domain.events.HealthInsuranceEvents
import com.jar.app.feature_health_insurance.shared.util.Constants.INSURANCE_DETAILS_MANDATE
import com.jar.app.feature_health_insurance.shared.util.Constants.INSURANCE_FLOWTYPE
import com.jar.app.feature_health_insurance.shared.util.Constants.INSURANCE_MANDATE_TITLE
import com.jar.app.feature_health_insurance.shared.util.Constants.MANDATE_PAYMENT
import com.jar.app.feature_health_insurance.shared.util.Constants.MANUAL_PAYMENT
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_mandate_payment_common.impl.ui.PaymentPageFragmentViewModelAndroid
import com.jar.app.feature_mandate_payments_common.shared.MandatePaymentBuildKonfig
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments.shared.domain.event.AvailableAppEvent
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateGetAvailableUpiAppWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateUpiIntentWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.GetAvailableUpiApps
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.InitiateUpiIntent
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.OneTimePaymentMethodType
import com.jar.app.feature_payment.impl.ui.payment_option.PaymentOptionPageFragmentViewModelAndroid
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.UUID
import javax.inject.Inject
import com.jar.app.feature_mandate_payment_common.impl.model.UpiApp as MandateUpiApp
import com.jar.app.core_ui.R.color as uiColor


@AndroidEntryPoint
class HealthInsurancePaymentBottomSheet : BaseComposeBottomSheetDialogFragment(), BaseResources{

    private var initiatePaymentResponse: InitiatePaymentResponse? = null

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    private val viewModel by viewModels<PaymentBottomSheetViewModel> { defaultViewModelProviderFactory }

    private val viewModelProvider by viewModels<PaymentPageFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val mandatePaymentViewModel by lazy {
        viewModelProvider.getInstance()
    }

    @Inject
    lateinit var serializer: Serializer

    private val paymentPageFragmentViewModelProvider by viewModels<PaymentOptionPageFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val paymentPageFragmentViewModel by lazy {
        paymentPageFragmentViewModelProvider.getInstance()
    }

    private val args by navArgs<HealthInsurancePaymentBottomSheetArgs>()

    private val uuid by lazy {
        UUID.randomUUID().toString()
    }

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    private val onProceed: (UpiApp) -> Unit = { upiApp ->
        viewModel.onClickedAnalyticsEvent(HealthInsuranceEvents.BUTTON_TYPE_PROCEED)
        EventBus.getDefault().post(InitiateUpiIntentWithJuspay(
            InitiateUpiIntent(
                requestId = UUID.randomUUID().toString(),
                orderId = initiatePaymentResponse?.juspay?.orderId.orEmpty(),
                payWithApp = upiApp.packageName,
                clientAuthToken = initiatePaymentResponse?.juspay?.clientAuthToken.orEmpty()
            )
        ))
    }

    private val onMandateProceed: (MandateUpiApp) -> Unit = { upiApp ->
        viewModel.onClickedAnalyticsEvent(HealthInsuranceEvents.BUTTON_TYPE_PROCEED)
        val initiateMandatePaymentRequest = InitiateMandatePaymentRequest(
            mandateAmount = args?.amount?.toFloat().orZero() ,
            authWorkflowType = MandateWorkflowType.TRANSACTION,
            subscriptionType = INSURANCE_FLOWTYPE,
            insuranceId = args.insuranceId,
        )
        mandatePaymentViewModel.fetchInitiateMandatePaymentData(
            mandatePaymentGateway = mandatePaymentApi.getMandatePaymentGateway(),
            packageName = upiApp.packageName,
            initiateMandatePaymentRequest = initiateMandatePaymentRequest!!,
            fetchPhonePeVersionCode = {
                requireContext().getPhonePeVersionCode(MandatePaymentBuildKonfig.PHONEPE_PACKAGE)?.toString()
            }
        )
    }

    @Composable
    override fun RenderBottomSheet() {

        val uiState by viewModel.uiState.collectAsState()

        if (uiState.upiAppsList.isNotEmpty() || uiState.mandateUpiList.isNotEmpty()) {

            if(args.paymentType == MANUAL_PAYMENT){
                LaunchedEffect(key1 = Unit){
                    viewModel.onPaymentScreenShown()
                }
                MandateBottomSheet(
                    navController = findNavController(),
                    onProceed = onProceed,
                    selectedIndex = uiState.selectedIndex,
                    selectedAppPackageName = uiState.selectedAppPackageName,
                    appsList = uiState.upiAppsList,
                    mandateUpiList = uiState.mandateUpiList,
                    amount = if(args.paymentType == MANUAL_PAYMENT) (initiatePaymentResponse?.amount?.toInt().toString()) else args.amount!!,
                    paymentType = args.paymentType!!,
                    onSelection = { index ->
                        viewModel.onClickedAnalyticsEvent(HealthInsuranceEvents.BUTTON_TYPE_UPI_APP)
                        viewModel.onTriggerEvent(MandateBottomSheetEvents.OnSelectedIndexChanged(index))
                    }) {
                    viewModel.onClickedAnalyticsEvent(HealthInsuranceEvents.BUTTON_TYPE_CANCEL)
                    dismiss()
                }
            }else{
                LaunchedEffect(key1 = Unit){
                    viewModel.onPaymentScreenShown()
                }
                MandateBottomSheet(
                    navController = findNavController(),
                    onMandateProceed = onMandateProceed,
                    selectedIndex = uiState.selectedIndex,
                    selectedAppPackageName = uiState.selectedAppPackageName,
                    appsList = uiState.upiAppsList,
                    mandateUpiList = uiState.mandateUpiList,
                    amount = if(args.paymentType == MANUAL_PAYMENT) (initiatePaymentResponse?.amount?.toInt().toString()) else args.amount.orEmpty(),
                    paymentType = args.paymentType!!,
                    onSelection = { index ->
                        viewModel.onClickedAnalyticsEvent(HealthInsuranceEvents.BUTTON_TYPE_UPI_APP)
                        viewModel.onTriggerEvent(MandateBottomSheetEvents.OnSelectedIndexChanged(index))
                    }) {
                    viewModel.onClickedAnalyticsEvent(HealthInsuranceEvents.BUTTON_TYPE_CANCEL)
                    dismiss()
                }
            }
        }
    }

    override fun setup() {
        observeData()
    }

    private fun observeData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mandatePaymentViewModel.paymentPageLiveData.collectLatest {
                    viewModel.createPaymentOptionList(it, requireContext().packageManager)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.paymentOptionFlow.collectLatest{ it ->

                    val mandateUpiAppList = mutableListOf<MandateUpiApp>()

                    it?.let {
                        for(data in it){
                            if(data.packageName == args.selectedUpiAppPackageName){
                                viewModel.onTriggerEvent(MandateBottomSheetEvents.OnChangeSelectedAppPackageName(data.packageName))
                            }
                            mandateUpiAppList.add(MandateUpiApp(data.packageName, data.optionIcon, data.optionName))
                        }
                    }

                    viewModel.onTriggerEvent(
                        MandateBottomSheetEvents.OnMandateUpiAppsChanged(
                            mandateUpiAppList
                        )
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mandatePaymentViewModel.fetchEnabledPaymentMethodsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        mandatePaymentViewModel.mergeApiResponse(
                            enabledPaymentMethodResponse = it
                        )
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
//                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mandatePaymentViewModel.initiateMandatePaymentDataLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let{
                            initiateMandatePayment(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(getRootView())
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                paymentPageFragmentViewModel.enabledPaymentMethodsFlow.collectUnwrapped(
                    onSuccess = {
                        it.forEach {
                            when (it) {
                                OneTimePaymentMethodType.UPI_INTENT -> {
                                    EventBus.getDefault().post(
                                        InitiateGetAvailableUpiAppWithJuspay(
                                            GetAvailableUpiApps(
                                                requestId = uuid,
                                                orderId = initiatePaymentResponse?.juspay?.orderId.orEmpty()
                                            )
                                        )
                                    )
                                }

                                else -> {
                                    // Other payment methods not supported in custom bottom sheet as of now..
                                }
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        popBackStack()
                    }
                )
            }
        }
    }

    private fun removeInitiateMandatePaymentData() {
        if (findNavController().isPresentInBackStack(args.containerId))
            findNavController().getBackStackEntry(args.containerId)
                .savedStateHandle[com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants.MANDATE_PAYMENT_RESPONSE_FROM_SDK] =
                null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeInitiateMandatePaymentData()
    }

    private fun initiateMandatePayment(
        initiateMandatePaymentApiResponse: InitiateMandatePaymentApiResponse,
    ) {
        if (findNavController().isPresentInBackStack(args.containerId))
            findNavController().getBackStackEntry(args.containerId!!)
                .savedStateHandle[com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants.MANDATE_PAYMENT_RESPONSE_FROM_SDK] =
                initiateMandatePaymentApiResponse
    }

    private fun getData() {
        paymentPageFragmentViewModel.fetchEnabledPaymentMethod(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)

        viewModel.paymentType = args.paymentType!!

        args.selectedUpiAppPackageName?.let {
            viewModel.onTriggerEvent(MandateBottomSheetEvents.OnChangeSelectedAppPackageName(it))
        }
        if(args.paymentType == MANDATE_PAYMENT){
            val paymentDetails = PaymentPageHeaderDetail(
                toolbarHeader = INSURANCE_MANDATE_TITLE ,
                title = INSURANCE_DETAILS_MANDATE,
                featureFlow = INSURANCE_FLOWTYPE,
                userLifecycle = HealthInsuranceEvents.Health_Insurance,
                savingFrequency = MandatePaymentCommonConstants.MandateStaticContentType.INSURANCE_AUTOPAY_SETUP.name,
                mandateSavingsType = MandatePaymentCommonConstants.MandateStaticContentType.INSURANCE_AUTOPAY_SETUP,
                toolbarIcon = com.jar.app.feature_health_insurance.R.drawable.bg_payment
            )
            mandatePaymentViewModel.paymentPageHeaderDetail = paymentDetails
            mandatePaymentViewModel.getData()
            viewModel.amount = args.amount.orEmpty()
        }else {
            getData()
            initiatePaymentResponse = args.initiatePaymentResponse?.let { decodeUrl(it) }?.let {
                serializer.decodeFromString<InitiatePaymentResponse>(
                    it
                )
            }
            viewModel.amount = initiatePaymentResponse?.amount?.toInt().toString()
            viewModel.onTriggerEvent(
                MandateBottomSheetEvents.OnInitiatePaymentResponseChanged(
                    initiatePaymentResponse
                )
            )
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAvailableAppsEvent(availableAppEvent: AvailableAppEvent) {
        val list = serializer.decodeFromString<List<UpiApp>>(availableAppEvent.upiApps.toString())
        viewModel.onTriggerEvent(MandateBottomSheetEvents.OnUpiAppListChanged(list))
    }
}

@Composable
fun MandateBottomSheet(
    modifier: Modifier = Modifier,
    selectedAppPackageName: String,
    selectedIndex: Int,
    appsList: List<UpiApp>,
    onSelection: (Int) -> Unit,
    onProceed: (UpiApp) -> Unit = {},
    onMandateProceed: (MandateUpiApp) -> Unit = {},
    amount: String,
    navController: NavController,
    mandateUpiList: List<MandateUpiApp>,
    paymentType: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                colorResource(id = R.color.color_2E2942),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
    ) {
        Image(
            painter = painterResource(id = com.jar.app.feature_payment.R.drawable.ic_white_cross),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 16.dp, end = 16.dp, bottom = 8.dp)
                .size(24.dp)
                .align(Alignment.End)
                .debounceClickable {
                    onClick()
                }
        )

        androidx.compose.material.Text(
            text = "Let's automate your premium",
            style = JarTypography.h3.copy(color = Color.White),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
        )

        Spacer(
            modifier = Modifier.padding(top = 28.dp)
        )

        Divider(
            color = colorResource(id = R.color.color_FF443E5E),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        )

        Spacer(
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material.Text(
                text = "Premium Amount",
                style = JarTypography.body2.copy(
                    color = colorResource(id = R.color.commonTxtColor),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            androidx.compose.material.Text(
                text = if(paymentType == MANUAL_PAYMENT) "₹$amount/year" else "₹$amount/month",
                style = JarTypography.h6.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .padding(end = 8.dp),
            )
        }

        Spacer(modifier = Modifier.padding(top = 8.dp))

        Divider(
            color = colorResource(id = R.color.color_FF443E5E),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp
            )
        )

        Spacer(modifier = Modifier.padding(top = 28.dp))

        androidx.compose.material.Text(
            text = "Select payment method",
            style = JarTypography.body2.copy(color = colorResource(id = R.color.smallTxtColor)),
            modifier = Modifier
                .padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.padding(top = 16.dp))

        val onSelection: (Int) -> Unit = { index ->
            onSelection(index)
        }

        LazyRow(
            modifier = Modifier
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp)
        ) {

            if(paymentType == MANUAL_PAYMENT){
                items(appsList.size) { index ->
                    UpiApp(
                        onSelection = index == selectedIndex,
                        upiApp = appsList[index],
                        onSelected = onSelection,
                        index = index,
                        mandateUpiApp = null,
                        paymentType = paymentType
                    )
                }
            }else{
                items(mandateUpiList.size) { index ->
                    UpiApp(
                        onSelection = index == selectedIndex,
                        upiApp = null,
                        onSelected = onSelection,
                        index = index,
                        mandateUpiApp = mandateUpiList[index],
                        paymentType = paymentType
                    )
                }
            }


        }

        Spacer(modifier = Modifier.padding(top = 48.dp))

        JarPrimaryButton(
            text = "Proceed",
            onClick = {
                if(paymentType == MANUAL_PAYMENT){
                    val selectedUpiApp = appsList[selectedIndex]
                    onProceed(selectedUpiApp)
                    navController.popBackStack()
                }else{
                    onMandateProceed(mandateUpiList[selectedIndex])
                }
            },
            isAllCaps = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.padding(top = 16.dp))

        Image(
            painter = painterResource(id = com.jar.app.feature_payment.R.drawable.guarantee),
            contentDescription = null,
            modifier = Modifier
                .size(width = 150.dp, height = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.padding(top = 24.dp))
    }

}

@Composable
fun UpiApp(
    modifier: Modifier = Modifier,
    onSelection: Boolean = false,
    upiApp: UpiApp? = UpiApp(packageName = "com.jar.app", appName = "Jar",isSelected = true),
    onSelected: (Int) -> Unit = {},
    index: Int = 0,
    mandateUpiApp: MandateUpiApp?,
    paymentType: String
) {

    Column(
        modifier = modifier
            .clickable {
                onSelected(index)
            }
    ) {
        Box(
            modifier = Modifier
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(
                        if (onSelection) BorderStroke(
                            width = 3.dp,
                            colorResource(id =  uiColor.color_1EA787)
                        ) else BorderStroke(width = 0.dp, Color.Transparent),
                        RoundedCornerShape(12.dp)
                    )


            ){

                if(paymentType == MANUAL_PAYMENT){
                    val packageManager = LocalContext.current.packageManager

                    val drawable = upiApp?.packageName?.let {
                        packageManager.getApplicationIcon(
                            it
                        )
                    }

                    drawable?.toBitmap(config = Bitmap.Config.ARGB_8888)?.asImageBitmap()
                        ?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                            )
                        }
                }else{
                    mandateUpiApp?.icon?.toBitmap(config = Bitmap.Config.ARGB_8888)
                        ?.asImageBitmap()
                        ?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                }

            }

            if (onSelection) {
                Image(
                    painter = painterResource(id = R.drawable.ic_tick_green),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .align(Alignment.TopEnd)
                        .border(
                            BorderStroke(width = 3.dp, Color(0xff1EA787)),
                            RoundedCornerShape(12.dp)
                        )

                )
            }
        }

        Spacer(modifier = Modifier.padding(top = 4.dp))

        androidx.compose.material.Text(
            text = if(paymentType == MANUAL_PAYMENT) upiApp?.appName.orEmpty() else mandateUpiApp?.appName.orEmpty() ,
            style = JarTypography.body2.copy(fontSize = 12.sp, color = Color(0xffEEEAFF)),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}

