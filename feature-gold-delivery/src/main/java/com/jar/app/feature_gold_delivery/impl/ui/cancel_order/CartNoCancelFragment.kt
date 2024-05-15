package com.jar.app.feature_gold_delivery.impl.ui.cancel_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentNoOrderCancelBinding
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class CartNoCancelFragment : BaseFragment<FragmentNoOrderCancelBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNoOrderCancelBinding
        get() = FragmentNoOrderCancelBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getString(R.string.order_cancel), true, backFactorScale = BACK_FACTOR_SCALE
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
    }

    private fun setupListeners() {
        binding.btnConfirm.setDebounceClickListener {
            activity?.onBackPressed()
        }
    }

    private fun observeLiveData() {

    }

    private fun getData() {
    }
}
