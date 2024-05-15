package com.jar.app.feature_weekly_magic.impl.ui.info_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.feature_weekly_magic.databinding.FragmentWeeklyChallengeInfoBinding
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeInfo
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class WeeklyChallengeInfoBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentWeeklyChallengeInfoBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val viewModelProvider by viewModels<WeeklyChallengeInfoViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWeeklyChallengeInfoBinding
        get() = FragmentWeeklyChallengeInfoBinding::inflate

    override val bottomSheetConfig = BottomSheetConfig(
        isHideable = false,
        shouldShowFullHeight = true,
        isCancellable = false,
        isDraggable = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchWeeklyChallengeInfo()
    }

    override fun setup() {
        setClickListeners()
        setObservers()
        analyticsApi.postEvent(WeeklyMagicConstants.AnalyticsKeys.WeeklyMagic_BSShown)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
    }

    private fun setClickListeners() {
        binding.ivCancel.setDebounceClickListener {
            dismiss()
        }
    }

    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyChallengeInfoFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setDataInUI(it)
                        }
                        markInfoAsViewed()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(getRootView())
                    }
                )
            }
        }
    }

    private fun setDataInUI(weeklyChallengeInfo: WeeklyChallengeInfo) {
        binding.tvTitle.setHtmlText(weeklyChallengeInfo.cardTitle)
        binding.tvDescription.setHtmlText(weeklyChallengeInfo.cardDescription)
        binding.btnCta.setText(weeklyChallengeInfo.ctaText.orEmpty())
        weeklyChallengeInfo.icon?.takeIf { it.isEmpty().not() }?.let {
            Glide.with(requireContext()).load(it).into(binding.ivIcon)
        }
        binding.btnCta.setDebounceClickListener {
            analyticsApi.postEvent(
                WeeklyMagicConstants.AnalyticsKeys.WeeklyMagic_BSClicked,
                mapOf(
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.clickaction to binding.btnCta.getText()
                )
            )
            dismiss()
            EventBus.getDefault().post(HandleDeepLinkEvent(weeklyChallengeInfo.ctaDeeplink.orEmpty()))
        }
    }

    private fun markInfoAsViewed() {
        viewModel.weeklyChallengeInfoFlow.value.data?.data?.challengeId?.let {
            viewModel.markWeeklyChallengeAsViewed(it)
        }
    }

}