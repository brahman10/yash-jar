package com.jar.app.feature.partnership_bonus.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jar.app.R
import com.jar.app.base.data.event.RefreshPartnerBonusEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.showToast
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.databinding.FragmentPartnershipBonusesBinding
import com.jar.app.feature.home.ui.adapter_delegates.PartnerBannerAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class PartnerShipBonusListFragment : BaseFragment<FragmentPartnershipBonusesBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var partnerBannerAdapter: PartnerBannerAdapter? = null

    private val viewModel by viewModels<PartnerShipBonusListFragmentViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPartnershipBonusesBinding
        get() = FragmentPartnershipBonusesBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getString(R.string.partnership_bonus),
                        true
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.fetchPartnerBanners()
        partnerBannerAdapter = PartnerBannerAdapter(
            onCardShown = {
//            analyticsHandler.postEvent(EventKey.ShownPartnershipBonusMessageHomescreen)
            },
            onCardClick = {
//            analyticsHandler.postEvent(EventKey.ClickedCrossPartnershipBonusMessageHomescreen)
                viewModel.claimBonus(it.orderId)
            }
        )
        binding.rvWithDrawl.adapter = partnerBannerAdapter
        observeData()
    }

    private fun observeData() {
        viewModel.claimedBonusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                context?.showToast(getString(R.string.claimed))
                viewModel.fetchPartnerBanners()
                EventBus.getDefault().post(RefreshPartnerBonusEvent())
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                context?.showToast(getString(R.string.claimed))
                viewModel.fetchPartnerBanners()
                EventBus.getDefault().post(RefreshPartnerBonusEvent())
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.partnerBannerLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                partnerBannerAdapter?.submitList(it.banners)
            },
            onError = {
                dismissProgressBar()
            }
        )
    }
}