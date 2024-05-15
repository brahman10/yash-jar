package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.pan_preview

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.components.IdentityDetailCard
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class IdentityConfirmationFragment : BaseComposeFragment() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<IdentityConfirmationFragmentArgs>()

    private val identityConfirmationScreenArguments by lazy {
        serializer.decodeFromString<IdentityConfirmationScreenArguments>(
            decodeUrl(args.identityReportArguments)
        )
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    override fun RenderScreen() {
        IdentityConfirmationScreen(
            identityConfirmationScreenArguments.creditReportPan,
            onBacKButtonClicked = {
                popBackStack()
            },
            onHelpButtonClicked = {
                openHelpSection()
            },
            navigateToBestOfferScreen = {
                navigateTo(
                    "android-app://com.jar.app/findingBestOfferFragment",
                    popUpTo = com.jar.app.feature_lending.R.id.realtimeSelectBankFragment,
                    inclusive = false
                )
            },
            navigateToPanEntry = {
                popBackStack()
            }
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
    }

    private fun openHelpSection() {
        requireContext().openWhatsapp(
            remoteConfigApi.getWhatsappNumber(), ""
        )
    }
}

@Composable
private fun IdentityConfirmationScreen(
    creditReportPAN: CreditReportPAN? = null,
    onBacKButtonClicked: () -> Unit = {},
    onHelpButtonClicked: () -> Unit = {},
    navigateToBestOfferScreen: () -> Unit = {},
    navigateToPanEntry: () -> Unit = {}
) {
    Column {
        ToolbarWithHelpButton(
            onBackButtonClick = onBacKButtonClicked,
            title = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_confirm_PAN.resourceId),
            onHelpButtonClick = onHelpButtonClicked
        )
        Column(
            Modifier
                .fillMaxSize()
                .background(color = JarColors.bgColor)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_confirm_your_pan.resourceId),
                fontSize = 24.sp,
                fontFamily = jarInterFontFamily,
                fontWeight = FontWeight(700),
                color = colorResource(id = R.color.white)
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(id = com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_following_pan_is_associated_with_your_credit_report.resourceId),
                fontSize = 14.sp,
                fontFamily = jarInterFontFamily,
                color = colorResource(id = R.color.color_D5CDF2)
            )

            IdentityDetailCard(
                modifier = Modifier.padding(top = 24.dp),
                panNumber = creditReportPAN?.panNumber.orEmpty(),
                panHolderName = creditReportPAN?.firstName.plus(" " + creditReportPAN?.lastName),
                panHolderDOB = creditReportPAN?.dob.orEmpty()
            )

            Spacer(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .weight(1f, false)
                    .fillMaxSize()
            )

            JarPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.core_ui_confirm),
                isAllCaps = false,
                onClick = {
                    navigateToBestOfferScreen.invoke()
                }
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
                    .debounceClickable {
                        navigateToPanEntry.invoke()
                    },
                text = stringResource(id = com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_no_this_is_not_my_pan.resourceId),
                fontSize = 12.sp,
                fontFamily = jarInterFontFamily,
                fontWeight = FontWeight(600),
                color = colorResource(id = R.color.color_D5CDF2),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}


@Preview(device = Devices.NEXUS_5)
@Composable
private fun IdentityConfirmationScreenPreview() {
    IdentityConfirmationScreen(
        creditReportPAN = CreditReportPAN(
            firstName = "Mohammed Sulaimaan",
            lastName = "Khan Durrani",
            dob = "20/02/1994",
            panNumber = "FGHCD2345L"
        )
    )
}