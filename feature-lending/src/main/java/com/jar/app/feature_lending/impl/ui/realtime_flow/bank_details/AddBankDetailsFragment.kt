package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_details


import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_lending.LendingNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.ui_event.AddBankDetailsEvent
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class AddBankDetailsFragment : BaseComposeFragment() {
    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val viewModelProvider by viewModels<AddBankDetailsViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    private fun handleBackPress() {
        popBackStack()
    }

    private fun goToNext() {
        navigateTo(
            LendingNavigationDirections.actionToUploadBankStatementFragment(),
            popUpTo = R.id.addBankDetailsFragment,
            inclusive = true,
            shouldAnimate = true
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postEvent(LendingEventKeyV2.BANK_DETAILS_SCREEN_LAUNCHED)
        viewModel.fetchSteps()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.updateBankDetails.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (it?.success == true) {
                            goToNext()
                        }
                    },
                    onError = { message, errorCode ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    @Composable
    override fun RenderScreen() {

        val state by viewModel.uiState.collectAsState()

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                ToolbarWithHelpButton(
                    onBackButtonClick = {
                        postEvent(LendingEventKeyV2.BACK_BUTTON_CLICKED)
                        handleBackPress()
                    },
                    title = stringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_bank_details.resourceId),
                    onHelpButtonClick = { openNeedHelp() }
                )
            },
            bottomBar = {
                JarPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_continue.resourceId),
                    isAllCaps = false,
                    isEnabled = (state.shouldEnableButton()),
                    onClick = {
                        postEvent(LendingEventKeyV2.BANK_DETAILS_CONTINUE_CLICKED)
                        viewModel.uiEvent(AddBankDetailsEvent.onButtonClick(state))
                    }
                )
            },
            backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.bgColor)
        ) { padding ->
            AddBankDetailsScreen(
                modifier = Modifier.padding(padding),
                uiState = state,
                onValueChangeOfAccountNo = viewModel::uiEvent,
                onValueChangeOfIfscCode = viewModel::uiEvent,
            )
        }

    }

    private fun openNeedHelp() {
        postEvent(LendingEventKeyV2.CONTACT_SUPPORT_CLICKED)
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getString(
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_kyc_contact_support_real_time_help_s_s.resourceId,
            name,
            number
        )
        requireContext().openWhatsapp(sendTo, message)
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeState()
    }

    private fun postEvent(action: String) {
        analyticsApi.postEvent(
            LendingEventKeyV2.RLENDING_INITIAL_FLOW,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_DETAILS_SCREEN,
                LendingEventKeyV2.action to action
            )
        )
    }

}




