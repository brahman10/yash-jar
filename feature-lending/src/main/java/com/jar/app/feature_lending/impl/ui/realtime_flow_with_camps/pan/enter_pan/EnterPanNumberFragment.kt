package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.enter_pan

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_base.util.isSpecialCharacters
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.ui.common_component.OutlinedTextFieldWithIcon
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.enter_pan.EnterPanNumberFragment.Companion.PAN_LENGTH
import com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.error_screen.PanErrorStatesArguments
import com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.pan_preview.IdentityConfirmationScreenArguments
import com.jar.app.feature_lending.impl.util.PanVisualTransformation
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class EnterPanNumberFragment : BaseComposeFragment() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider by viewModels<EnterPanNumberViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    companion object {
        const val PAN_LENGTH = 10
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    override fun RenderScreen() {
        EnterPanNumberScreen(
            onBackButtonClick = {
                popBackStack()
            },
            onHelpButtonClick = {
                openHelpSection()
            },
            onConfirmButtonClick = { panNumber ->
                viewModel.manualFetchPANDetails(
                    ManualKycRequest(
                        panNumber, null, null
                    )
                )
            }
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeFlow()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiStateFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it.data?.let {
                            it.panData?.let { panDetail ->
                                val name = panDetail.name.split(" ")
                                val firstName = if (name.isNotEmpty()) name[0] else ""
                                val lastName = if ((name.size
                                        ?: 0) > 1
                                ) name.filterIndexed { index, s -> index != 0 }
                                    .joinToString(" ") else ""

                                val creditReportPAN =
                                    CreditReportPAN(
                                        panDetail.panNumber,
                                        firstName,
                                        lastName,
                                        panDetail.dob
                                    )

                                val args = encodeUrl(
                                    serializer.encodeToString(
                                        IdentityConfirmationScreenArguments(
                                            creditReportPan = creditReportPAN
                                        )
                                    )
                                )
                                //navigate to preview screen.
                                navigateTo(
                                    EnterPanNumberFragmentDirections.actionEnterPanNumberFragmentToIdentityConfirmationFragment(
                                        args
                                    )
                                )
                            }
                        }
                    },
                    onError = { _, errorCode ->
                        dismissProgressBar()
                        when (errorCode) {
                            BaseConstants.ErrorCodesLendingKyc.PAN.INVALID_PAN_CARD -> {
                                navigateTo(
                                    EnterPanNumberFragmentDirections.actionEnterPanNumberFragmentToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            heading = getString(com.jar.app.feature_lending.shared.MR.strings.feature_lending_PAN_status.resourceId),
                                            title = getString(com.jar.app.feature_lending.shared.MR.strings.feature_lending_invalid_pan.resourceId),
                                            subTitle = getString(com.jar.app.feature_lending.shared.MR.strings.feature_leanding_please_re_try_with_valid_PAN.resourceId),
                                            imageId = R.drawable.feature_lending_ic_invalid_card,
                                            isInvalidPan = true
                                        )
                                    )
                                )
                            }

                            BaseConstants.ErrorCodesLendingKyc.PAN.PAN_ENTRY_LIMIT_EXCEEDED,
                            BaseConstants.ErrorCodesLendingKyc.PAN.PAN_ENTRY_LIMIT_EXHAUSTED -> {
                                navigateTo(
                                    EnterPanNumberFragmentDirections.actionEnterPanNumberFragmentToPanErrorStatesFragment(
                                        PanErrorStatesArguments(
                                            heading = getString(com.jar.app.feature_lending.shared.MR.strings.feature_lending_PAN_limit_reached.resourceId),
                                            title = getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_attempt_limit_exceeded.resourceId),
                                            subTitle = getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow.resourceId),
                                            imageId = com.jar.app.feature_lending.R.drawable.feature_lending_ic_error_i
                                        )
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    private fun openHelpSection() {
        requireContext().openWhatsapp(
            remoteConfigApi.getWhatsappNumber(), ""
        )
    }
}

@Composable
private fun EnterPanNumberScreen(
    onBackButtonClick: () -> Unit = {},
    onHelpButtonClick: () -> Unit = {},
    onConfirmButtonClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val panTextFieldState = rememberSaveable { mutableStateOf("") }
    val errorMessageState = rememberSaveable { mutableStateOf("") }
    val isErrorState = rememberSaveable { mutableStateOf(false) }

    Column {
        ToolbarWithHelpButton(
            onBackButtonClick = onBackButtonClick,
            title = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_enter_pan.resourceId),
            onHelpButtonClick = onHelpButtonClick
        )
        Column(
            Modifier
                .fillMaxSize()
                .background(color = JarColors.bgColor)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_you_are_almost_there.resourceId),
                    fontSize = 20.sp,
                    fontFamily = jarInterFontFamily,
                    fontWeight = FontWeight(700),
                    color = colorResource(id = com.jar.app.core_ui.R.color.white)
                )

                Image(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(16.dp),
                    painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick),
                    contentDescription = ""
                )
            }

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_please_verify_your_PAN_details.resourceId),
                fontSize = 16.sp,
                fontFamily = jarInterFontFamily,
                color = colorResource(id = com.jar.app.core_ui.R.color.white)
            )

            Text(
                modifier = Modifier.padding(top = 40.dp),
                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_enter_PAN_number.resourceId),
                fontSize = 16.sp,
                fontFamily = jarInterFontFamily,
                color = colorResource(id = com.jar.app.core_ui.R.color.white)
            )
            OutlinedTextFieldWithIcon(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                value = panTextFieldState.value,
                onValueChange = {
                    if (it.length <= PAN_LENGTH) {
                        panTextFieldState.value = it.uppercase()
                        checkForValidPAN(
                            it.uppercase(),
                            errorMessageState,
                            isErrorState,
                            context
                        )
                    }
                },
                placeholderText = stringResource(id = com.jar.app.core_ui.R.string.core_ui_pan_hint),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = true,
                    keyboardType = if (panTextFieldState.value.length < 5) KeyboardType.Text else if (panTextFieldState.value.length < 9) KeyboardType.Number else KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = PanVisualTransformation(),
                showError = isErrorState.value,
                errorMessage = errorMessageState.value
            )

            JarPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 48.dp, end = 16.dp),
                iconPadding = 0.dp,
                text = stringResource(id = com.jar.app.core_ui.R.string.core_ui_confirm),
                isAllCaps = false,
                onClick = { onConfirmButtonClick(panTextFieldState.value) },
                isEnabled = (panTextFieldState.value.length == PAN_LENGTH) && !isErrorState.value
            )
        }
    }
}

private fun checkForValidPAN(
    value: String,
    errorMessageState: MutableState<String>,
    isErrorState: MutableState<Boolean>,
    context: Context
) {
    if (value == "") {
        isErrorState.value = false
    } else if (value.isSpecialCharacters()) {
        errorMessageState.value =
            context.getString(com.jar.app.core_ui.R.string.core_ui_pan_cannot_have_special_character)
        isErrorState.value = true
    } else if (value.length >= 4 && value[3].equals(Char(80/*ASCII for Char P*/), true).not()) {
        errorMessageState.value =
            context.getString(com.jar.app.core_ui.R.string.core_ui_incorrect_pan_format)
        isErrorState.value = true
    } else if (getRawText(value).length != PAN_LENGTH) {
        errorMessageState.value =
            context.getString(com.jar.app.core_ui.R.string.core_ui_pan_number_should_be_10_char)
        isErrorState.value = true
    } else {
        isErrorState.value = false
    }
}

private fun getRawText(value: String) = value.replace(" ", "")


@Preview
@Composable
private fun EnterPanNumberScreenPreview() {
    EnterPanNumberScreen()
}