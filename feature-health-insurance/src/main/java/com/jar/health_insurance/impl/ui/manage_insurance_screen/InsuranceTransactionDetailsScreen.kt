package com.jar.health_insurance.impl.ui.manage_insurance_screen

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.ErrorToastMessage
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.convertToAnnotatedString
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.core_compose_ui.views.LabelValueComposeView
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_compose_ui.views.SingleExpandableCard
import com.jar.app.core_compose_ui.views.payments.PaymentTimelineView
import com.jar.app.core_compose_ui.views.payments.TimelineViewData
import com.jar.app.core_compose_ui.views.payments.TransactionStatus
import com.jar.app.core_ui.R
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.NeedHelp
import com.jar.app.feature_health_insurance.shared.data.models.payment_status.PaymentStatus
import com.jar.app.feature_health_insurance.shared.data.models.transaction_details.Notification
import com.jar.app.feature_health_insurance.shared.data.models.transaction_details.OrderDetail
import com.jar.app.feature_health_insurance.shared.data.models.transaction_details.PaymentPlanDescriptionCard
import com.jar.app.feature_health_insurance.shared.ui.InsuranceTransactionDetailScreenEvent
import com.jar.app.feature_health_insurance.shared.ui.InsuranceTransactionDetailsState
import com.jar.health_insurance.impl.ui.common.components.PaymentInfoCard
import com.jar.health_insurance.impl.ui.common.components.PaymentPlanDescriptionCard
import com.jar.health_insurance.impl.ui.common.components.TopBarNeedHelpWhatsapp
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
@ExperimentalGlideComposeApi
class InsuranceTransactionDetailsScreen : BaseComposeFragment() {
    private val viewModelProvider by viewModels<InsuranceTransactionDetailsViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }
    private val args by navArgs<InsuranceTransactionDetailsScreenArgs>()
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onTriggerEvent(
            InsuranceTransactionDetailScreenEvent.LoadInsuranceTransactionDetails(
                args.transactionId
            )
        )
    }


    @Preview
    @Composable
    override fun RenderScreen() {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        if (!uiState.value.isLoading) {
            RenderTransactionDetailsScreen(uiState.value)
            dismissProgressBar()
        } else {
            showProgressBar()
        }
    }

    @Composable
    fun RenderTransactionDetailsScreen(uiState: InsuranceTransactionDetailsState) {
       uiState.errorMessage?.let {
           ErrorToastMessage(errorMessage = it) {}
       }
       Scaffold(topBar = {
           uiState.insuranceTransactionDetails?.toolbarTitle?.let {
               RenderBaseToolBar(
                   onBackClick = { popBackStack() },
                   title = it
               )
           }
       }, containerColor = colorResource(id = R.color.color_272239)) { paddingValues ->
           Column(
               modifier = Modifier
                   .padding(paddingValues)
                   .fillMaxSize()
                   .verticalScroll(rememberScrollState()),
               verticalArrangement = Arrangement.spacedBy(16.dp)
           ) {
               Divider(
                   thickness = 1.dp,
                   color = colorResource(id = R.color.color_ACA1D3_opacity_10)
               )
               uiState.insuranceTransactionDetails?.paymentDescriptionCard?.let {
                   RenderPaymentPlanDescriptionSection(paymentPlanDescriptionCard = it)
               }

               uiState.insuranceTransactionDetails?.paymentStatusesData?.paymentStatusDataList?.let {
                   val timelineViewDataList = it.map { data ->
                       val transactionStatus: TransactionStatus? = getTransactionStatus(data.status)
                       TimelineViewData(
                           status = transactionStatus,
                           title = data.label,
                           date = data.date,
                           refreshText = data.description,
                           refreshTextTypography = JarTypography.body2.copy(
                               fontSize = 12.sp,
                               color = statusTextColorFromStatus(transactionStatus)
                           ),
                       )
                   }
                   uiState.insuranceTransactionDetails?.paymentStatusesData?.title?.let { it1 ->
                       RenderTransactionStatusSection(
                           heading = it1,
                           timelineViewDataList = timelineViewDataList
                       )
                   }
               }
               uiState.insuranceTransactionDetails?.orderDetails?.orderDetailsList?.let {
                  PaymentInfoCard {
                      RenderOrderDetailsSection(
                          orderDetailsDataList = it,
                          heading = uiState.insuranceTransactionDetails?.orderDetails?.title
                      )
                  }
               }
               uiState.insuranceTransactionDetails?.contactUs?.let {
                   RenderContactUs(
                       modifier = Modifier.align(Alignment.End),
                       contactUs = it
                   )
               }
           }

       }
    }

    @Composable
    private fun RenderPaymentPlanDescriptionSection(paymentPlanDescriptionCard: PaymentPlanDescriptionCard) {
        paymentPlanDescriptionCard.run {
            PaymentInfoCard {
                PaymentPlanDescriptionCard(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 18.dp,
                    ),
                    headerIcon = headerIcon,
                    statusIcon = statusIcon,
                    headerLabelText = headerLabelText,
                    headerValueText = headerValueText,
                    subHeaderLabelText = subHeaderLabelText,
                    subHeaderValueText = subHeaderValueText,
                )
            }
        }
    }

    @Composable
    private fun RenderTransactionStatusSection(
        heading: String,
        timelineViewDataList: List<TimelineViewData>
    ) {
        PaymentInfoCard {
            Column(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = heading,
                    modifier = Modifier.padding(bottom = 20.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.color_D5CDF2)
                )

                PaymentTimelineView(modifier = Modifier,
                    timelineViewDataList = timelineViewDataList,
                    bottomText = null,
                    retryButtonPressed = null,
                    shouldShowDividerAtLast = { true })

            }
        }
    }

    @Composable
    fun RenderOrderDetailsSection(
        orderDetailsDataList: List<OrderDetail>,
        heading: String?
    ) {
        SingleExpandableCard(remember { mutableStateOf(true) }, {
            Text(
                text = heading.orEmpty(),
                style = JarTypography.body2.copy(
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.color_D5CDF2),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(16.dp),
            )
        }) {
            RenderExpandedSection(orderDetailsDataList)
        }
    }

    @Composable
    private fun RenderExpandedSection(
        orderDetailsDataList: List<OrderDetail>
    ) {
        Column(modifier = Modifier.padding(horizontal = 15.dp)) {
            orderDetailsDataList.forEachIndexed { index, orderDetail ->
                Spacer(modifier = Modifier.height(height = 24.dp + (3.dp.takeIf { index == 0 }
                    ?: 0.dp)))
                Text(
                    text = orderDetail.title,
                    style = JarTypography.caption.copy(color = colorResource(id = R.color.color_D5CDF2))
                )
                val labelAndValueComposeDataList = orderDetail.orderDetailsInfoList.map { detailsInfo ->
                    val labelTextStyle = JarTypography.body2.copy(
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor(detailsInfo.labelColor))
                    )
                    val valueTextStyle =
                        (if (detailsInfo.bold.orFalse()) JarTypography.body2.copy(
                            fontWeight = FontWeight.Bold
                        ) else JarTypography.body2.copy(
                            fontSize = 12.sp
                        )).copy(color = Color(android.graphics.Color.parseColor(detailsInfo.valueColor)))
                    detailsInfo.run {
                        LabelAndValueCompose(
                            label = label.orEmpty(),
                            value = value.orEmpty(),
                            showCopyToClipBoardIconAndTruncate = valueTruncate.orFalse(),
                            valueTextStyle = valueTextStyle,
                            labelTextStyle = labelTextStyle,
                        )
                    }
                }

                LabelValueComposeView(
                    modifier = Modifier.padding(top = 16.dp),
                    list = labelAndValueComposeDataList
                )

                orderDetail.notification?.let {
                    PolicyExpiredFooter(notification = it)
                }

                if (index != orderDetailsDataList.lastIndex) Divider(
                    modifier = Modifier.padding(top = 16.dp),
                    thickness = 1.dp,
                    color = colorResource(id = R.color.color_ACA1D3_opacity_10)
                )
            }
        }
    }

    @Composable
    fun RenderContactUs(modifier: Modifier = Modifier, contactUs: NeedHelp) {
        contactUs.text?.let {
            TopBarNeedHelpWhatsapp(
                modifier = modifier
                    .padding(end = 16.dp)
                    .debounceClickable {
                        val initialMessage = contactUs.whatsappText
                        val whatsappNumber = contactUs.whatsappNumber
                        whatsappNumber?.let { message ->
                            requireContext().openWhatsapp(
                                message, initialMessage
                            )
                        }
                    },
                iconResId = com.jar.app.feature_health_insurance.R.drawable.ic_contact_us,
                iconLeftText = it
            )
        }

    }

    @Composable
    fun PolicyExpiredFooter(modifier: Modifier = Modifier, notification: Notification) {
        Row(
            modifier = modifier
                .background(
                    color = colorResource(id = R.color.color_3C3357),
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .height(38.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            JarImage(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp, start = 16.dp)
                    .size(18.dp),
                imageUrl = notification.icon,
                contentDescription = "Notification icon"
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                text = convertToAnnotatedString(notification.text),
                style = JarTypography.body2.copy(color = colorResource(id = R.color.color_EB6A6E))
            )
        }
    }

    @Composable
    fun statusTextColorFromStatus(status: TransactionStatus?): Color {
        return when (status) {
            TransactionStatus.SUCCESS -> colorResource(id = R.color.color_58DDC8)
            TransactionStatus.PENDING, null -> colorResource(id = R.color.color_EBB46A)
            TransactionStatus.FAILED -> colorResource(id = R.color.color_EB6A6E)
        }
    }

    private fun getTransactionStatus(insurancePaymentStatus: String?): TransactionStatus? = when {
        insurancePaymentStatus.equals(PaymentStatus.SUCCESS.name, true) -> TransactionStatus.SUCCESS
        insurancePaymentStatus.equals(PaymentStatus.FAILURE.name, true) -> TransactionStatus.FAILED
        insurancePaymentStatus.equals(PaymentStatus.PENDING.name, true) || insurancePaymentStatus.equals(PaymentStatus.INITIATED.name, true) -> TransactionStatus.PENDING
        else -> null
    }


    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.color_141021)
    }
}