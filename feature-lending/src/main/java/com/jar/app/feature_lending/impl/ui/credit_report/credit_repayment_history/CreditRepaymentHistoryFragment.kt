package com.jar.app.feature_lending.impl.ui.credit_report.credit_repayment_history

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_lending.impl.ui.common_component.RealTimeGenericLoading
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.impl.ui.credit_report.components.MonthlyRepaymentsCard
import com.jar.app.feature_lending.impl.ui.credit_report.components.RepaymentHistoryTab
import com.jar.app.feature_lending.impl.ui.realtime_flow.landing.RealTimeLandingError
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditDetailedReportResponse
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class CreditRepaymentHistoryFragment : BaseComposeFragment() {
    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args by navArgs<CreditRepaymentHistoryFragmentArgs>()
    private val viewModelProvider by viewModels<CreditRepaymentViewModelAndroid> { defaultViewModelProviderFactory }
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
        viewModel.getCreditDetailedReport(args.performance.type.orEmpty())
    }

    @Composable
    override fun RenderScreen() {
        val screenData by viewModel.creditDetailedReportData.collectAsState()

        when (screenData.status) {
            RestClientResult.Status.NONE,
            RestClientResult.Status.LOADING -> {
                RealTimeGenericLoading(modifier = Modifier)
            }

            RestClientResult.Status.SUCCESS -> {
                LaunchedEffect(key1 = Unit){
                    viewModel.sendAnalyticsScreenLaunchedEvent(isCreditTab = false,args.performance)
                }
                screenData.data?.data?.let {
                    RepaymentHistoryScreen(
                        creditDetailedReportData = screenData.data?.data,
                        onBackButtonClick = {
                            handleBackPress()
                        },
                        onHelpButtonClick = {
                            openNeedHelp()
                        },
                        onTabClick = {
                            onTabSelected(it)
                        },
                        args = args,
                    )
                }
            }

            RestClientResult.Status.ERROR -> {
                RealTimeLandingError(title = screenData.message.orEmpty())
            }
        }
    }

    private fun handleBackPress() {
        viewModel.sendAnalyticsBackButtonEvent(args.performance.type.orEmpty())
        popBackStack()
    }
    private fun onTabSelected(isCredit:Boolean) {
        viewModel.sendAnalyticsTabClickedLaunchedEvent(isCredit,args.performance)
        viewModel.sendAnalyticsScreenLaunchedEvent(isCredit,args.performance)
    }
    private fun openNeedHelp() {
        viewModel.sendAnalyticsNeedHelpEvent( args.performance.type.orEmpty())
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getCustomStringFormatted(MR.strings.feature_credit_contact_support,
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
internal fun RepaymentHistoryScreen(
    modifier: Modifier = Modifier,
    creditDetailedReportData: CreditDetailedReportResponse?,
    onBackButtonClick: () -> Unit = {},
    onHelpButtonClick: () -> Unit = {},
    onTabClick: (Boolean) -> Unit = {},
    args: CreditRepaymentHistoryFragmentArgs,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            args.performance.name?.let {
                ToolbarWithHelpButton(
                    onBackButtonClick = onBackButtonClick,
                    title = it.replace("\n", " "),
                    onHelpButtonClick = onHelpButtonClick
                )
            }
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
                creditDetailedReportData.let {
                    MonthlyRepaymentsCard(creditDetailedReportData)
                }
            }
            item(key = "tab") { //tab card
                creditDetailedReportData?.let {
                    RepaymentHistoryTab(
                        creditDetailedReportData,onTabClick
                    )
                }
            }

        }

    }
}

