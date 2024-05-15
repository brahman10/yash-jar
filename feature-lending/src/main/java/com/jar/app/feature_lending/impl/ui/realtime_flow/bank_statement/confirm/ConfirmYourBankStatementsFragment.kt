package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_statement.confirm


import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.CtaType
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.impl.ui.realtime_flow.bank_statement.upload.UploadBankStatementViewModel
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants.MAX_PDFS_ALLOWED_TO_UPLOAD
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class ConfirmYourBankStatementsFragment : BaseComposeFragment() {
    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args by navArgs<ConfirmYourBankStatementsFragmentArgs>()

    private val viewModel by navGraphViewModels<UploadBankStatementViewModel>(R.id.lending_navigation) { defaultViewModelProviderFactory }
    private lateinit var filePickerLauncher: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>


    private fun openFilePicker() {
        val filePickerOptions = "application/pdf"
        filePickerLauncher.launch(filePickerOptions)
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setCtaType(args.ctaType)
    }

    private fun navigateToFindingBestOffer() {
        navigateTo(
            "android-app://com.jar.app/findingBestOfferFragment",
            shouldAnimate = true,
            popUpTo = R.id.confirmBankDetailFragment,
            inclusive = true
        )
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun RenderScreen() {
        val uiState by viewModel.uploadStatementUiState.collectAsState()
        filePickerLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                if (!uris.isNullOrEmpty()) {
                    viewModel.addPdfUris(uris, true)
                }
            }

        analyticsApi.postEvent(
            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                LendingEventKeyV2.action to if (uiState.ctaType == CtaType.CONFIRM) LendingEventKeyV2.BANKSTATEMENT_CONFIRMATION_SCREEN_LAUNCHED else LendingEventKeyV2.BANKSTATEMENT_VIEW_SCREEN_SHOWN
            )
        )

        val scope = rememberCoroutineScope()
        val keyboardController = LocalSoftwareKeyboardController.current
        val modalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            scrimColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
            sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            sheetContent = {
                EnterBankStatementPasswordBottomSheet(
                    password = uiState.statementPassword,
                    onCrossClick = {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_PASSWORD_BS,
                                LendingEventKeyV2.action to LendingEventKeyV2.PASSWORDBS_CROSS_BUTTON_CLICKED
                            )
                        )
                        scope.launch {
                            modalBottomSheetState.hide()
                            keyboardController?.hide()
                        }
                    },
                    onSubmitButtonClick = {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_PASSWORD_BS,
                                LendingEventKeyV2.action to LendingEventKeyV2.PASSWORDBS_SUBMIT_CLICKED
                            )
                        )
                        viewModel.submitBankStatements()
                    },
                    onValueChange = {
                        if (it.length <= 1) {
                            analyticsApi.postEvent(
                                LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                                mapOf(
                                    LendingEventKeyV2.screen_name to LendingEventKeyV2.ENTER_PASSWORD_BS,
                                    LendingEventKeyV2.action to LendingEventKeyV2.PASSWORD_ENTERED
                                )
                            )
                        }
                        viewModel.onPasswordChange(it)
                    }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (uiState.shouldHideToolbar().not()) {
                        ToolbarWithHelpButton(
                            onBackButtonClick = { handleBackPress() },
                            title = when (uiState.ctaType) {
                                CtaType.CONFIRM -> stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_PDFs_onfirmation.resourceId)
                                CtaType.SUBMIT -> stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_bank_statement_view.resourceId)
                            },
                            onHelpButtonClick = { openNeedHelp() }
                        )
                    }
                },
                bottomBar = {
                    if (uiState.shouldShowCta) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            JarPrimaryButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                isEnabled = uiState.isPrimaryButtonEnabled,
                                isAllCaps = false,
                                text = when (uiState.ctaType) {
                                    CtaType.CONFIRM -> stringResource(id = com.jar.app.core_ui.R.string.core_ui_confirm)
                                    CtaType.SUBMIT -> stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_submit.resourceId)
                                },
                                onClick = {
                                    when (uiState.ctaType) {
                                        CtaType.CONFIRM -> {
                                            analyticsApi.postEvent(
                                                LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                                                mapOf(
                                                    LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                                                    LendingEventKeyV2.action to LendingEventKeyV2.BANKSTATEMENT_CONFIRM_CLICKED,
                                                    LendingEventKeyV2.bank_statement_count to uiState.getFinalList().size
                                                )
                                            )
                                            viewModel.uploadBankStatementsPdf()
                                        }

                                        CtaType.SUBMIT -> {
                                            analyticsApi.postEvent(
                                                LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                                                mapOf(
                                                    LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_VIEW_SCREEN,
                                                    LendingEventKeyV2.action to LendingEventKeyV2.BANKSTATEMENT_VIEW_SUBMIT_CLICKED
                                                )
                                            )
                                            scope.launch {
                                                modalBottomSheetState.show()
                                            }
                                        }
                                    }
                                })
                            Text(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .clickable {
                                        analyticsApi.postEvent(
                                            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                                            mapOf(
                                                LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                                                LendingEventKeyV2.action to LendingEventKeyV2.UPLOAD_MORE_CLICKED
                                            )
                                        )
                                        openFilePicker()
                                    },
                                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_upload_more.resourceId),
                                fontSize = 12.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight(600),
                                color = Color(0xFFD5CDF2),
                                textAlign = TextAlign.Center,
                                textDecoration = TextDecoration.Underline
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                },
                backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.bgColor)
            ) { paddingValues ->

                if (uiState.selectedPdfs.size > MAX_PDFS_ALLOWED_TO_UPLOAD) {
                    LaunchedEffect(key1 = Unit) {
                        analyticsApi?.postEvent(
                            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                                LendingEventKeyV2.action to LendingEventKeyV2.UPLOAD_COMPLETED_SCREEN_SHOWN
                            )
                        )
                    }
                }
                ConfirmYourBankStatementsFragmentComposable(
                    modifier = Modifier.padding(paddingValues),
                    bankStatementList = uiState.selectedPdfs,
                    uploadedBankStatementList = uiState.uploadedPdfs,
                    ctaType = uiState.ctaType,
                    showWarning = uiState.totalFiles > MAX_PDFS_ALLOWED_TO_UPLOAD,
                    warningString = stringResource(
                        id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_you_can_upload_a_maximum_of_pdf.resourceId,
                        MAX_PDFS_ALLOWED_TO_UPLOAD
                    ),
                    onCrossClick = { uri, index ->
                        analyticsApi.postEvent(
                            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                            mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_STATEMENT_CONFIRMATION_SCREEN,
                                LendingEventKeyV2.action to LendingEventKeyV2.FILE_DELETED
                            )
                        )
                        viewModel.removePdfUri(uri, index)
                    },
                    showUploading = uiState.shouldShowUploading,
                    uploadPercent = "${uiState.uploadPercent}%",
                    showUploadError = uiState.shouldShowUploadError,
                    showUploadSuccess = uiState.shouldShowUploadSuccess,
                    analyticsApi = analyticsApi
                )
            }
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeFlowData()
    }

    private fun observeFlowData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.submitPassword.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateToFindingBestOffer()
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                    }
                )

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.noOfItemsLeft.collectLatest {
                    if (it == 0) {
                        handleBackPress()
                    }
                }
            }
        }
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

    override fun onDestroy() {
        viewModel.clearItems()
        super.onDestroy()
    }


}
