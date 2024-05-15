package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_statement.upload


import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.navGraphViewModels
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.CtaType
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class UploadBankStatementFragment : BaseComposeFragment() {
    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    lateinit var filePickerLauncher: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>

    private val viewModel by navGraphViewModels<UploadBankStatementViewModel>(R.id.lending_navigation) { defaultViewModelProviderFactory }
    private fun openFilePicker() {
        val filePickerOptions = "application/pdf"
        filePickerLauncher.launch(filePickerOptions)
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    private fun navigateToConfirmation(uris: List<Uri>) {
        viewModel.addPdfUris(uris, false)
        navigateTo(
            uri = "android-app://com.jar.app/confirmYourBankStatementsFragment/${CtaType.CONFIRM.name}",
            shouldAnimate = true
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            LendingEventKeyV2.RLENDING_INITIAL_FLOW,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_SCREEN,
                LendingEventKeyV2.action to LendingEventKeyV2.BANK_STATEMENT_SCREEN_LAUNCHED
            )
        )
        viewModel.fetchBankDetail()
    }

    @Composable
    override fun RenderScreen() {
        filePickerLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                if (!uris.isNullOrEmpty()) {
                    navigateToConfirmation(uris)
                }
            }
        val uiState by viewModel.uploadStatementUiState.collectAsState()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ToolbarWithHelpButton(
                    onBackButtonClick = { handleBackPress() },
                    title = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_bank_statement.resourceId),
                    onHelpButtonClick = { openNeedHelp() }
                )
            },
            backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.bgColor)
        ) { paddingValues ->
            UploadBankStatementComposable(
                modifier = Modifier.padding(paddingValues),
                bankDetail = uiState.bankDetail,
                time = "6 months",
                onSubmitClick = {
                    analyticsApi.postEvent(
                        LendingEventKeyV2.RLENDING_INITIAL_FLOW,
                        mapOf(
                            LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_SCREEN,
                            LendingEventKeyV2.action to LendingEventKeyV2.UPLOAD_BANK_STATEMENT_CLICKED
                        )
                    )
                    openFilePicker()
                }
            )
        }

    }

    override fun setup(savedInstanceState: Bundle?) {

    }

    private fun openNeedHelp() {
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getCustomStringFormatted(
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_kyc_contact_support_real_time_help_s_s,
            name.orEmpty(),
            number.orEmpty()
        )
        requireContext().openWhatsapp(sendTo, message)
    }

    private fun handleBackPress() {
        popBackStack()
    }

}
