package com.jar.app.feature_sell_gold.impl.ui.vpa

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.feature_sell_gold.impl.ui.model.VpaSelectionArgument
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class VpaSelectionFragment : BaseComposeFragment() {
    private val viewModelProvider by viewModels<VpaSelectionViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy { viewModelProvider.getInstance() }

    @Inject
    lateinit var serializer: Serializer

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    private val args: VpaSelectionFragmentArgs by navArgs()

    private val vpaSelectionArgs by lazy {
        serializer.decodeFromString<VpaSelectionArgument>(decodeUrl(args.vpaSelectionArgs))
    }

    @Composable
    override fun RenderScreen() {
        val goldPriceState by viewModel.currentGoldSellPriceFlow.collectAsStateWithLifecycle()
        val volumeFromAmount by viewModel.volumeFromAmountFlow.collectAsStateWithLifecycle()
        val userSavedVpas by viewModel.userSavedVpasFlow.collectAsStateWithLifecycle()
        val vpaChips by viewModel.vpaChipsFlow.collectAsStateWithLifecycle()
        val upiVerificationStatus by viewModel.upiVerificationStatusFlow.collectAsStateWithLifecycle()
        val withdrawalRequestStatus by viewModel.withdrawRequestStatusFlow.collectAsStateWithLifecycle()
        var hasGoldPriceUpdatedAtLeastOnce by remember { mutableStateOf(false) }

        LaunchedEffect(goldPriceState?.rateId) {
            goldPriceState?.rateId?.takeIf { it.isNotBlank() }?.also {
                hasGoldPriceUpdatedAtLeastOnce = true
            }
        }

        LaunchedEffect(Unit) {
            viewModel.postVpaListScreenShownEvent()
        }

        VpaSelectionScreen(
            withdrawalPrice = vpaSelectionArgs.withdrawalPrice,
            volumeFromAmount = volumeFromAmount.orZero(),
            vpaChips = vpaChips.data?.data,
            userSavedVpas = userSavedVpas.data?.data,
            upiVerificationStatus = upiVerificationStatus.data?.data,
            goldPriceState = goldPriceState,
            onBackClick = { popBackStack() },
            onVerifyUpiClick = viewModel::verifyUpiAddress,
            onConfirmWithdrawalClick = { withdrawRequest, selectedVpa ->
                viewModel.postWithdrawConfirmClickedEvent()
                navigateTo(
                    VpaSelectionFragmentDirections.actionUpiSelectionFragmentToWithdrawStatusFragment(
                        withdrawRequest = withdrawRequest,
                        orderId = vpaSelectionArgs.orderId
                            ?: withdrawalRequestStatus.data?.data?.orderId,
                        vpa = selectedVpa.vpaHandle,
                        isRetryFlow = vpaSelectionArgs.isRetryFlow
                    )
                )
            }
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.calculateVolumeFromAmount(vpaSelectionArgs.withdrawalPrice.toFloatOrZero())
    }
}