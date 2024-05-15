package com.jar.app.feature_gold_lease.impl.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseV2SplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class GoldLeaseV2SplashFragment : BaseFragment<FragmentGoldLeaseV2SplashBinding>(){

    private val args by navArgs<GoldLeaseV2SplashFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseV2SplashBinding
        get() = FragmentGoldLeaseV2SplashBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        uiScope.launch {
            delay(1500)
            if (isActive) {
                navigateTo(
                    GoldLeaseV2SplashFragmentDirections.actionGoldLeaseV2SplashFragmentToGoldLeaseLandingFragment(
                        flowType = args.flowType,
                        tabPosition = args.tabPosition,
                        isNewLeaseUser = args.isNewLeaseUser,
                        clickTime = args.clickTime
                    ),
                    popUpTo = R.id.goldLeaseV2SplashFragment,
                    inclusive = true
                )
            }
        }
    }
}