package com.jar.app.feature_homepage.impl.ui.first_gold_coin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_homepage.databinding.FeatureHomepageFirstCoinDeliveryBinding
import com.jar.app.feature_homepage.shared.domain.model.RefreshFirstCoinEvent
import com.jar.app.feature_homepage.shared.util.EventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class FirstCoinDeliveryFragment :
    BaseFragment<FeatureHomepageFirstCoinDeliveryBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    private val viewModel by viewModels<FirstCoinTransitionViewModel> { defaultViewModelProviderFactory }

    private val args: FirstCoinDeliveryFragmentArgs by navArgs()

    private var pageName = ""

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureHomepageFirstCoinDeliveryBinding
        get() = FeatureHomepageFirstCoinDeliveryBinding::inflate

    private var deeplink = ""
    private var status = com.jar.app.feature_homepage.shared.domain.model.DeliveryStatus.FAILED

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupListener()
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchTransitionPageData()
    }

    private fun setupListener() {
        binding.btnStartNow.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_FirstCoin_Card,
                mapOf(
                    EventKey.PageName to pageName,
                    EventKey.ButtonType to binding.btnStartNow.getText()
                )
            )
            if (deeplink.isNullOrEmpty()) {
                EventBus.getDefault().post(RefreshFirstCoinEvent())
                EventBus.getDefault().post(
                    GoToHomeEvent(
                        FirstCoinDeliveryFragment::class.java.name,
                        BaseConstants.HomeBottomNavigationScreen.HOME
                    )
                )
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                if (browserIntent.resolveActivity(requireContext().packageManager) != null)
                    startActivity(browserIntent)
            }
        }
        binding.tvSupport.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_FirstCoin_Card,
                mapOf(
                    EventKey.PageName to pageName,
                    EventKey.ButtonType to EventKey.Contact_Support
                )
            )
            val number = remoteConfigManager.getWhatsappNumber()
            if (status == com.jar.app.feature_homepage.shared.domain.model.DeliveryStatus.FAILED) {
                val message =
                    getString(com.jar.app.feature_daily_investment.R.string.feature_first_coin_delivery_failed, number, args.orderId)
                it.context.openWhatsapp(number, message)
            } else {
                val message =
                    getString(
                        com.jar.app.feature_daily_investment.R.string.feature_first_coin_delivery_pending,
                        number,
                        args.orderId
                    )
                it.context.openWhatsapp(number, message)
            }

        }
        binding.ivCross.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_FirstCoin_Card,
                mapOf(
                    EventKey.PageName to pageName,
                    EventKey.ButtonType to EventKey.Cross
                )
            )
            viewModel.sendOrderId(args.orderId)
            EventBus.getDefault().post(
                RefreshFirstCoinEvent()
            )
            EventBus.getDefault().post(
                GoToHomeEvent(
                    FirstCoinDeliveryFragment::class.java.name,
                    BaseConstants.HomeBottomNavigationScreen.HOME
                )
            )
        }
    }

    fun observeLiveData() {
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
                setData(it)
            },
            onError = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
            }
        )
    }

    private fun setData(data: com.jar.app.feature_homepage.shared.domain.model.FirstCoinTransitionData) {
        deeplink = data.deeplink ?: ""
        when (data.getDeliveryStatus()) {
            com.jar.app.feature_homepage.shared.domain.model.DeliveryStatus.FAILED -> {
                binding.clResources.isVisible = true
                binding.tvProgressHeading.isVisible = false
                binding.tvProgressSubHeading.isVisible = false
                binding.ivProgressTransitionImage.isVisible = false
                binding.clResources.isVisible = true
                binding.tvStatusHeading.isVisible = true
                binding.tvStatusHeading.text = data.title
                binding.tvStatusSubHeading.isVisible = true
                binding.tvStatusSubHeading.text = data.description
                binding.animViewFail.isVisible = true
                binding.animViewFail.playLottieWithUrlAndExceptionHandling(
                    requireContext(), BaseConstants.LottieUrls.ERROR_EXCLAMATION
                )
                binding.animViewFail.playAnimation()
                binding.tvSupport.isVisible = true

                analyticsHandler.postEvent(
                    EventKey.ShownFirstCoinCard,
                    mapOf(EventKey.PageName to EventKey.Delivery_Failure)
                )

                pageName = EventKey.Delivery_Failure

                viewModel.sendOrderId(args.orderId)
                EventBus.getDefault().post(
                    com.jar.app.feature_homepage.shared.domain.event.RefreshFirstCoinCardEvent()
                )

            }
            com.jar.app.feature_homepage.shared.domain.model.DeliveryStatus.DELIVERED -> {
                binding.tvProgressHeading.isVisible = false
                binding.tvProgressSubHeading.isVisible = false
                binding.ivProgressTransitionImage.isVisible = false
                binding.clResources.isVisible = true
                binding.tvStatusHeading.isVisible = true
                binding.tvStatusHeading.text = data.title
                binding.tvStatusSubHeading.isVisible = true
                binding.tvStatusSubHeading.text = data.description
                binding.animViewSuccess.isVisible = true
                binding.animViewSuccess.playLottieWithUrlAndExceptionHandling(
                    requireContext(), BaseConstants.LottieUrls.SMALL_CHECK
                )
                binding.animViewSuccess.playAnimation()

                analyticsHandler.postEvent(
                    EventKey.ShownFirstCoinCard,
                    mapOf(EventKey.PageName to EventKey.Delivery_Success)
                )

                pageName = EventKey.Delivery_Success

                viewModel.sendOrderId(args.orderId)
                EventBus.getDefault().post(
                    com.jar.app.feature_homepage.shared.domain.event.RefreshFirstCoinCardEvent()
                )
            }
            com.jar.app.feature_homepage.shared.domain.model.DeliveryStatus.PENDING -> {
                binding.clResources.isVisible = false
                binding.tvStatusSubHeading.isVisible = false
                binding.tvStatusHeading.isVisible = false
                binding.animViewSuccess.isVisible = false
                binding.animViewFail.isVisible = false
                binding.tvStatusSubHeading.isVisible = true
                binding.tvProgressHeading.isVisible = true
                binding.tvProgressHeading.text = data.title
                binding.tvProgressSubHeading.isVisible = true
                binding.tvProgressSubHeading.text = data.description
                binding.ivProgressTransitionImage.isVisible = true
                Glide.with(requireContext())
                    .load(data.image)
                    .into(binding.ivProgressTransitionImage)
                binding.tvSupport.isVisible = true

                analyticsHandler.postEvent(
                    EventKey.ShownFirstCoinCard,
                    mapOf(EventKey.PageName to EventKey.Track_my_order)
                )

                pageName = EventKey.Track_my_order
            }
            else -> {
                binding.tvProgressHeading.isVisible = false
                binding.tvProgressSubHeading.isVisible = false
                binding.ivProgressTransitionImage.isVisible = false
                binding.tvStatusHeading.isVisible = true
                binding.tvStatusHeading.text = data.title
                binding.tvStatusSubHeading.isVisible = true
                binding.tvStatusSubHeading.text = data.description
                binding.animViewFail.isVisible = true
                binding.animViewFail.playLottieWithUrlAndExceptionHandling(
                    requireContext(), BaseConstants.LottieUrls.ERROR_EXCLAMATION
                )
                binding.animViewFail.playAnimation()
                binding.tvSupport.isVisible = true

                analyticsHandler.postEvent(
                    EventKey.ShownFirstCoinCard,
                    mapOf(EventKey.PageName to EventKey.Delivery_Failure)
                )

                pageName = EventKey.Delivery_Failure
            }
        }
        binding.btnStartNow.setText(data.btnText.orEmpty())
        if (!data.footer.isNullOrEmpty()) {
            binding.tvfooter.isVisible = true
            binding.tvfooter.text = data.footer.orEmpty()
        }
    }
}