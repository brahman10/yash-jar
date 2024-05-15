package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.progress_states.success

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.orFalse
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class SetupSuccessStateFragment : BaseComposeFragment() {

    private val viewModelProvider by viewModels<SetupSuccessStateViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    override fun RenderScreen() {
        SetupSuccessStateScreen()
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        observeFlow()
        uiScope.launch {
            delay(2000)
            viewModel.fetchPanStatus()
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.panStatusData.collectUnwrapped(
                    onLoading = {

                    },
                    onSuccess = {
                        if (it.success.orFalse()) {
                            navigateTo(
                                "android-app://com.jar.app/enterPanNumberFragment",
                                popUpTo = com.jar.app.feature_lending.R.id.realtimeSelectBankFragment,
                                inclusive = false
                            )
                        } else {
                            navigateTo(
                                "android-app://com.jar.app/findingBestOfferFragment",
                                popUpTo = com.jar.app.feature_lending.R.id.realtimeSelectBankFragment,
                                inclusive = false
                            )

                        }
                    },
                    onError = { _, _ ->

                    }
                )
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}

@Composable
private fun SetupSuccessStateScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = JarColors.bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val composition by rememberLottieComposition(LottieCompositionSpec.Url(LendingConstants.LottieUrls.SMALL_CHECK))
        val progress by animateLottieCompositionAsState(composition)

        LottieAnimation(
            modifier = Modifier
                .size(88.dp),
            composition = composition,
            progress = { progress },
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_bank_setup_successful.resourceId),
            fontSize = 18.sp,
            fontFamily = jarInterFontFamily,
            fontWeight = FontWeight(700),
            color = colorResource(id = R.color.white)
        )
    }
}

@Preview
@Composable
fun SetupSuccessStateScreenPreview() {
    SetupSuccessStateScreen()
}