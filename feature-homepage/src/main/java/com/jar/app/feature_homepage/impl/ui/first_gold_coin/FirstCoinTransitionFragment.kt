package com.jar.app.feature_homepage.impl.ui.first_gold_coin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.CacheEvictionUtil
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.databinding.FeatureHomepageFirstCoinTransitionBinding
import com.jar.app.feature_homepage.shared.util.EventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class FirstCoinTransitionFragment :
    BaseFragment<FeatureHomepageFirstCoinTransitionBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var cacheEvictionUtil: CacheEvictionUtil

    private val viewModel by viewModels<FirstCoinTransitionViewModel> { defaultViewModelProviderFactory }

    private var pageName = EventKey.Introduction_Screen

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureHomepageFirstCoinTransitionBinding
        get() = FeatureHomepageFirstCoinTransitionBinding::inflate


    override fun setup(savedInstanceState: Bundle?) {
        viewModel.fetchTransitionPageData()
        observeLiveData()
    }

    private fun setData(data: com.jar.app.feature_homepage.shared.domain.model.FirstCoinTransitionData){
        binding.tvHeading.text = data.title
        binding.tvSubHeading.text = data.description
        binding.btnStartNow.setText(data.btnText.orEmpty())
        binding.btnGetItDelivered.setText(data.btnText.orEmpty())
        Glide.with(requireContext())
            .load(data.image)
            .into(binding.ivTransitionImage)

        binding.btnStartNow.setDebounceClickListener {
            viewModel.sendFirstCoinOnboardingStatus()
            analyticsHandler.postEvent(
                EventKey.Clicked_FirstCoin_Card,
                mapOf(
                    EventKey.PageName to pageName,
                    EventKey.ButtonType to EventKey.Proceed)
            )
            if(data.deeplink.isNullOrEmpty()){
                EventBus.getDefault().post(
                    GoToHomeEvent(
                        FirstCoinDeliveryFragment::class.java.name,
                        BaseConstants.HomeBottomNavigationScreen.HOME
                    )
                )
            }else{
                EventBus.getDefault().post(HandleDeepLinkEvent(data.deeplink!!))
            }
        }

        binding.btnGetItDelivered.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_FirstCoin_Card,
                mapOf(
                    EventKey.PageName to pageName,
                    EventKey.ButtonType to EventKey.Proceed)
            )
            if(data.deeplink.isNullOrEmpty()){
                EventBus.getDefault().post(
                    GoToHomeEvent(
                        FirstCoinDeliveryFragment::class.java.name,
                        BaseConstants.HomeBottomNavigationScreen.HOME
                    )
                )
            }else{
                EventBus.getDefault().post(HandleDeepLinkEvent(data.deeplink!!))
            }
        }

        binding.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_FirstCoin_Card,
                mapOf(
                    EventKey.PageName to pageName,
                    EventKey.ButtonType to EventKey.Back)
            )
            EventBus.getDefault().post(
                GoToHomeEvent(
                    FirstCoinDeliveryFragment::class.java.name,
                    BaseConstants.HomeBottomNavigationScreen.HOME
                )
            )
        }
        if(data.getDeliveryStatus() == null){
            binding.animView.isVisible = true
            binding.btnStartNow.setDrawableEnd(com.jar.app.core_ui.R.drawable.ic_arrow_right)
        }

        if(data.btnText != "Start Now"){
            pageName = EventKey.Get_it_delivered
            binding.btnGetItDelivered.isVisible = true
        }else{
            pageName = EventKey.Introduction_Screen
            binding.btnStartNow.isVisible = true
        }

        analyticsHandler.postEvent(
            EventKey.ShownFirstCoinCard,
            mapOf(EventKey.PageName to pageName)
        )
    }

    fun observeLiveData(){
        viewModel.transitionPageLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                binding.shimmerPlaceholder.isVisible = true
                binding.clContainer.isVisible = false
                binding.shimmerPlaceholder.startShimmer()
                        },
            onSuccess = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
                viewModel.sendFirstCoinOnboardingStatus()
                cacheEvictionUtil.refreshFirstCoinData()
                setData(it)
            },
            onError = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
            }
        )
    }

}