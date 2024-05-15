package com.jar.app.feature_lending.impl.ui.repayments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentRepaymentHistoryBinding
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class RepaymentHistoryFragment : BaseFragment<FragmentRepaymentHistoryBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<RepaymentHistoryFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRepaymentHistoryBinding
        get() = FragmentRepaymentHistoryBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))

        binding.toolBar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Repay_BackButtonClicked,
                values = mapOf(LendingEventKeyV2.screen_name to LendingEventKeyV2.Emi_Txn_Tab_Screen)
            )
            popBackStack()
        }

        binding.toolBar.tvTitle.text = getCustomString(MR.strings.feature_lending_emi_schedule_transactions)
        binding.toolBar.separator.isVisible = false
        binding.toolBar.ivTitleImage.isVisible = false
        binding.toolBar.lottieView.isVisible = false
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        val adapter = RepaymentHistoryPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, args.loanId)
        binding.viewPager2.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> getCustomString(MR.strings.feature_lending_emi_details)
                1 -> getCustomString(MR.strings.feature_lending_transactions)
                else -> throw Exception("Invalid tab position")
            }
        }.attach()
    }
}