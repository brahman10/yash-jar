package com.jar.app.feature_gifting.impl.ui.view_received_gift

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingFragmentViewReceivedGiftBinding
import com.jar.app.feature_gifting.shared.domain.model.GoldGiftReceivedResponse
import com.jar.app.feature_gifting.shared.util.EventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
internal class ViewReceivedGiftFragment :
    BaseFragment<FeatureGiftingFragmentViewReceivedGiftBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<ViewReceivedGiftFragmentArgs>()

    private val viewModel by viewModels<ViewReceivedGiftFragmentViewModel> { defaultViewModelProviderFactory }

    private val goldGiftReceivedResponse by lazy {
        serializer.decodeFromString<GoldGiftReceivedResponse>(
            decodeUrl(args.goldGiftReceivedResponse)
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGiftingFragmentViewReceivedGiftBinding
        get() = FeatureGiftingFragmentViewReceivedGiftBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            EventKey.Shown_Screen_GoldGiftingFlow,
            mapOf(EventKey.screenType to EventKey.tapToReveal)
        )
        val senderNameData = getString(
            R.string.feature_gifting_x_has_sent_a_gift_for_you,
            goldGiftReceivedResponse.senderName ?: getString(R.string.feature_gifting_someone)
        )
        binding.tvGiftForYouReveal.text = senderNameData
        binding.tvGiftForYou.text = senderNameData

        binding.goldAmount.text =
            getString(
                R.string.feature_gifting_x_gm_float_gold,
                goldGiftReceivedResponse.volume.orZero()
            )
        binding.message.text =
            goldGiftReceivedResponse.message
                ?: getString(R.string.feature_gifting_default_gifting_message)

        val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy | hh:mm a")
        val timeData = Instant.ofEpochMilli(goldGiftReceivedResponse.giftingDate)
            .atZone(ZoneId.systemDefault())
        binding.tvDate.text =
            "${getString(R.string.feature_gifting_gift_received_on)} ${timeData.format(formatter)}"
        Glide.with(this)
            .load(BaseConstants.ImageUrlConstants.SPARKLE_LIGHT_BG)
            .into(binding.ivSparkleLightBg)
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            popBackStack()
        }

        binding.ivCloseReveal.setDebounceClickListener {
            popBackStack()
        }
        binding.tvTapToReveal.setDebounceClickListener {
            showGiftDetailView()
        }
        binding.ivGiftIcon.setDebounceClickListener {
            showGiftDetailView()
        }
    }

    private fun showGiftDetailView() {
        binding.clRevealGift.slideToRevealNew(
            binding.clGiftDetail,
            onAnimationEnd = {
                analyticsHandler.postEvent(EventKey.ShownReceivedGiftScreen)
                viewModel.markGiftViewed(giftingId = goldGiftReceivedResponse.giftingId)
            })
    }
}