package com.jar.app.feature_homepage.impl.ui.first_gold_coin

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.amountToString
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageFirstCoinProgressBinding
import com.jar.app.feature_homepage.shared.util.EventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class FirstCoinProgressFragment :
    BaseFragment<FeatureHomepageFirstCoinProgressBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    private val viewModel by viewModels<FirstCoinProgressViewModel> { defaultViewModelProviderFactory }

    val args by navArgs<FirstCoinProgressFragmentArgs>()

    private var progress = 0f

    private val lottieAnimation1Listener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it.animatedValue.toString().toFloat().orZero() * 100)
        uiScope.launch {
            if (100f <= progress) {
                playLottieAnimation2()
            }
        }
    }

    private val lottieAnimation2Listener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it.animatedValue.toString().toFloat().orZero() * 100)
        uiScope.launch {
            if (100f <= progress) {
                val directions =
                    FirstCoinProgressFragmentDirections.actionFirstGoldCoinProgressScreenToFirstCoinTransitionFragment()
                navigateTo(directions)
            }
        }
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureHomepageFirstCoinProgressBinding
        get() = FeatureHomepageFirstCoinProgressBinding::inflate


    override fun setup(savedInstanceState: Bundle?) {
        viewModel.fetchTransitionPageData()
        observeLiveData()
    }

    private fun setData(data: com.jar.app.feature_homepage.shared.domain.model.FirstCoinProgressData) {
        binding.tvHeading.text = data.header
        binding.tvSavingsLabel.text =
            requireContext().getString(R.string.feature_first_coin_progress_value_label)
        binding.tvGoldNeededLabel.text =
            requireContext().getString(R.string.feature_first_coin_progress_target_label)
        if (data.currentBalance == 0.0) {
            binding.tvSavingsValue.text = SpannableStringBuilder()
                .bold { append(data.currentBalance.toInt().toString()) }
                .append(getString(R.string.gm))
        } else {
            binding.tvSavingsValue.text = SpannableStringBuilder()
                .bold { append(data.currentBalance.amountToString(4)) }
                .append(getString(R.string.gm))
        }

        if (data.currentBalance == 0.0) {
            binding.tvGoldNeededValue.text = SpannableStringBuilder()
                .bold { append((data.target - data.currentBalance).toInt().toString()) }
                .append(getString(R.string.gm))
        } else {
            if ((data.target - data.currentBalance) < 0) {
                binding.tvGoldNeededValue.text = SpannableStringBuilder()
                    .bold { append("0 ") }
                    .append(getString(R.string.gm))
            } else {
                binding.tvGoldNeededValue.text = SpannableStringBuilder()
                    .bold { append((data.target - data.currentBalance).amountToString(4)) }
                    .append(getString(R.string.gm))
            }

        }

        if (data.autopayText.isNullOrEmpty()) {
            binding.vDivider.isVisible = false
            binding.tvDsStatus.isVisible = false
            binding.clAutoPay.isVisible = false
        } else {
            binding.clAutoPay.isVisible = true
            binding.vDivider.isVisible = true
            binding.tvDsStatus.text = buildSpannedString {
                append(data.autopayText)
                append(" ")
                bold {
                    append(
                        requireContext().getString(
                            R.string.rupee_x_in_string,
                            data.amount?.toInt().toString()
                        )
                    )
                }
            }
        }
        binding.btnCustom.setText(data.ctaText)
        binding.ivStar.setImageResource(R.drawable.first_coin_footer_star)
        binding.tvFooter.text = data.bottomText
        binding.ivHand.setImageResource(R.drawable.first_coin_hand)
        binding.btnCustom.setDebounceClickListener {
            analyticsHandler(binding.btnCustom.getText(), data)
            prefsApi.setUserLifeCycleForMandate(com.jar.app.core_analytics.EventKey.UserLifecycles.FirstCoin)
            EventBus.getDefault()
                .post(HandleDeepLinkEvent(data.ctaDeepLink, com.jar.app.core_analytics.EventKey.UserLifecycles.FirstCoin))
        }

        progress = data.percentageCompleted.toFloat()
        playLottieAnimation1()

        binding.btnBack.setDebounceClickListener {
            analyticsHandler(EventKey.Back, data)
            EventBus.getDefault().post(
                GoToHomeEvent(
                    FirstCoinProgressFragment::class.java.name,
                    BaseConstants.HomeBottomNavigationScreen.HOME
                )
            )
        }
        analyticsHandler.postEvent(
            EventKey.ShownFirstCoinCard,
            mapOf(
                EventKey.PageName to EventKey.Landing_page,
                EventKey.needed_gold_amount to (data.amount?.times(100))?.toInt().toString(),
                EventKey.autopay_amount to data.amount.toString(),
                EventKey.screen_type to data.ctaText,
                EventKey.success_percentage to data.percentageCompleted.toString(),
                EventKey.process_status to "Active"
            )
        )
    }

    private fun playLottieAnimation1() {
        binding.lottieProgressAnimation1.playAnimation()
        binding.lottieProgressAnimation1.addAnimatorUpdateListener(lottieAnimation1Listener)
        binding.lottieProgressAnimation1.setMinAndMaxFrame(
            0,
            (120 * (progress / 100)).toInt()
        ) //to play the first half
    }

    private fun playLottieAnimation2() {
        binding.lottieProgressAnimation1.visibility = View.INVISIBLE
        binding.lottieProgressAnimation2.isVisible = true
        binding.lottieProgressAnimation2.playAnimation()
        binding.lottieProgressAnimation2.addAnimatorUpdateListener(lottieAnimation2Listener)
    }

    private fun observeLiveData() {
        viewModel.firstCoinProgressLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                binding.shimmerPlaceholder.isVisible = true
                binding.clContainer.isVisible = false
                binding.shimmerPlaceholder.startShimmer()
            },
            onSuccess = {
                viewModel.apiResponseCount += 1
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
                setData(it)
                val currentTime = System.currentTimeMillis()
                if (viewModel.apiResponseCount == 1) {
                    analyticsHandler.postEvent(
                        EventKey.ShownFirstCoinCardTs,
                        mapOf(
                            com.jar.app.core_analytics.EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(
                                endTimeTime = currentTime,
                                startTime = args.clickTime.toLong()
                            )
                        )
                    )
                }
            },
            onError = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
            }
        )
    }

    private fun analyticsHandler(
        btnType: String,
        data: com.jar.app.feature_homepage.shared.domain.model.FirstCoinProgressData
    ) {
        analyticsHandler.postEvent(
            EventKey.ShownFirstCoinCard,
            mapOf(
                EventKey.PageName to EventKey.Landing_page,
                EventKey.ButtonType to btnType,
                EventKey.needed_gold_amount to (data.amount?.times(100))?.toInt().toString(),
                EventKey.autopay_amount to data.amount.toString(),
                EventKey.screen_type to data.ctaText,
                EventKey.success_percentage to data.percentageCompleted.toString(),
                EventKey.process_status to "Active"
            )
        )
    }

}