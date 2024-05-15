package com.jar.app.feature_lending.impl.ui.credit_report.credit_summary_reports

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_lending.impl.ui.common_component.RealTimeGenericLoading
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.impl.ui.credit_report.components.CellCreditScoreHeaderCard
import com.jar.app.feature_lending.impl.ui.credit_report.components.CheckNowCreditReportCard
import com.jar.app.feature_lending.impl.ui.credit_report.components.CreditLimitAndUsageCard
import com.jar.app.feature_lending.impl.ui.realtime_flow.components.CreditScoreCard
import com.jar.app.feature_lending.impl.ui.realtime_flow.landing.RealTimeLandingError
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditSummaryDataResponse
import com.jar.app.feature_lending.shared.domain.model.creditReport.Performance
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class CreditSummaryFragment : BaseComposeFragment() {
    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val viewModelProvider by viewModels<CreditSummaryViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCreditSummaryData()
    }

    @Composable
    override fun RenderScreen() {

        val screenData by viewModel.creditSummaryData.collectAsState()
        when (screenData.status) {
            RestClientResult.Status.NONE,
            RestClientResult.Status.LOADING -> {
                RealTimeGenericLoading(modifier = Modifier)
            }

            RestClientResult.Status.SUCCESS -> {
                dismissProgressBar()
                LaunchedEffect(key1 = Unit) {
                    viewModel.sendAnalyticsDetailedReportShown(LendingEventKeyV2.shown)
                }
                CreditSummaryScreen(
                    creditSummaryData = screenData.data?.data,
                    onBackButtonClick = {
                        handleBackPress()
                    },
                    onHelpButtonClick = {
                        openNeedHelp()
                    },
                    inPerformanceCardClicked = {
                        handlePerformanceClicked(it)
                    },
                    onCheckNowClicked = {
                        showProgressBar()
                        viewModel.sendAnalyticsDetailedReportShown(LendingEventKeyV2.check_now_clicked)
                        viewModel.sendRequestForRefresh()
                    }
                )
            }
            RestClientResult.Status.ERROR -> {
                RealTimeLandingError(title = screenData.message.orEmpty())
            }
        }

    }

    private fun handlePerformanceClicked(data: Performance) {
        viewModel.sendAnalyticsDetailedReportShown(data.type?.lowercase() + "_clicked")
        navigateTo(
            CreditSummaryFragmentDirections.actionCreditSummaryFragmentToCreditRepaymentHistoryFragment(
                performance = data,
            ),
            shouldAnimate = true
        )
    }

    private fun handleBackPress() {

        viewModel.sendAnalyticsBackButtonEvent()
        popBackStack()
    }

    private fun openNeedHelp() {
        viewModel.sendAnalyticsNeedHelpEvent()
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getCustomStringFormatted(
            MR.strings.feature_credit_contact_support,
            name.orEmpty(),
            number.orEmpty()
        )
        requireContext().openWhatsapp(sendTo, message)
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}

@Composable
internal fun CreditSummaryScreen(
    modifier: Modifier = Modifier,
    creditSummaryData: CreditSummaryDataResponse?,
    onBackButtonClick: () -> Unit = {},
    onHelpButtonClick: () -> Unit = {},
    inPerformanceCardClicked: (Performance) -> Unit = {},
    onCheckNowClicked: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            ToolbarWithHelpButton(
                onBackButtonClick = onBackButtonClick,
                title = stringResource(id = MR.strings.feature_lending_credit_report_title.resourceId),
                onHelpButtonClick = onHelpButtonClick
            )
        },
        bottomBar = {
        },
        floatingActionButtonPosition = FabPosition.Center,
        backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.bgColor)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            item(key = "header_card") { //header card
                creditSummaryData?.creditScore?.let {
                    CreditScoreCard(
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        creditScoreCard = com.jar.app.feature_lending.shared.domain.model.realTimeFlow.CreditScoreCard(
                            backgroundColor = "#FF492B9D",
                            footerText = "",
                            creditScore = creditSummaryData.creditScore.orZero(),
                            creditScoreResult = creditSummaryData.creditScoreResult.orEmpty()
                        ), shouldShowDivider = false
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp, bottom = 4.dp),
                        text = stringResource(id = MR.strings.feature_lending_credit_performance.resourceId),
                        style = JarTypography.h6.copy(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = com.jar.app.core_ui.R.color.commonTxtColor),
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
            items(creditSummaryData?.performance.orEmpty(), key = { it.name.orEmpty() }) {
                CreditLimitAndUsageCard(it,
                    inPerformanceCardClicked = { data -> inPerformanceCardClicked(data) }
                )
            }
            item(key = "note_text") {//note text
                if (!creditSummaryData?.refreshCreditReport?.refreshCreditScore.orFalse()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        text = stringResource(id = MR.strings.feature_lending_credit_updated_on.resourceId) + " ${creditSummaryData?.refreshCreditReport?.lastUpdated}",
                        style = JarTypography.body1.copy(
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                } else {
                    CheckNowCreditReportCard(creditSummaryData?.refreshCreditReport?.lastUpdated.orEmpty(),
                        onCheckNowClick = { onCheckNowClicked() }
                    )
                }
            }
            creditSummaryData?.impactFactors?.factors?.let {
                item(key = "factors_card") { //factors card
                    CellCreditScoreHeaderCard(creditSummaryData.impactFactors?.factors ?: listOf())
                }
            }
        }

    }
}

