package com.jar.app.feature_round_off.impl.ui.calculation_loading

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentRoundOffCalculationLoadingBinding
import com.jar.app.feature_round_off.shared.util.RoundOffConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class RoundOffCalculationLoadingFragment :
    BaseFragment<FeatureRoundOffFragmentRoundOffCalculationLoadingBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentRoundOffCalculationLoadingBinding
        get() = FeatureRoundOffFragmentRoundOffCalculationLoadingBinding::inflate

    private val args by navArgs<RoundOffCalculationLoadingFragmentArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        binding.calculationRoundOffLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.HOUR_GLASS_LOADING
        )

        uiScope.launch {
            delay(2000)
            redirectToRoundOffsCalculatedFragment()
        }
    }

    private fun redirectToRoundOffsCalculatedFragment() {
        navigateTo(
            NavigationRoundOffDirections.actionToRoundOffCalculatedFragment(
                clickTime = args.clickTime,
                screenFlow = args.screenFlow
            ),
            popUpTo = R.id.roundOffCalculationLoadingFragment,
            inclusive = true
        )
    }
}