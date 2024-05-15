package com.jar.app.feature.bottom_sheet_prompt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.FragmentHomeScreenPromptBottomSheetBinding
import com.jar.app.feature_homepage.shared.domain.model.HomeScreenPrompt
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class HomeScreenPromptBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentHomeScreenPromptBottomSheetBinding>(){

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeScreenPromptBottomSheetBinding
        get() = FragmentHomeScreenPromptBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false, isDraggable = false)

    private val args by navArgs<HomeScreenPromptBottomSheetFragmentArgs>()

    private val homeScreenPrompt by lazy {
        serializer.decodeFromString<HomeScreenPrompt>(decodeUrl(args.homeScreenPromptDataStr))
    }

    override fun setup() {
        setupUI()
        setupListeners()
        analyticsApi.postEvent(
            EventKey.Bottomsheet_HomeScreen_PromptShown,
            mapOf(
                EventKey.AMOUNT to homeScreenPrompt.amount.orZero(),
                EventKey.FeatureType to homeScreenPrompt.featureType.orEmpty(),
                EventKey.TIME_STAMP to homeScreenPrompt.timeStamp.orEmpty()
            )
        )
    }

    private fun setupUI() {
        Glide.with(requireContext()).load(homeScreenPrompt.icon.orEmpty())
            .into(binding.ivIcon)
        binding.tvTitle.setHtmlText(homeScreenPrompt.title.orEmpty())
        binding.tvDescription.setHtmlText(homeScreenPrompt.description.orEmpty())
        binding.btnCta.setText(homeScreenPrompt.ctaText.orEmpty())
    }

    private fun setupListeners() {
        binding.ivCancel.setDebounceClickListener {
            analyticsApi.postEvent(
                EventKey.Bottomsheet_HomeScreen_PromptClosed,
                mapOf(
                    EventKey.AMOUNT to homeScreenPrompt.amount.orZero(),
                    EventKey.FeatureType to homeScreenPrompt.featureType.orEmpty(),
                    EventKey.TIME_STAMP to homeScreenPrompt.timeStamp.orEmpty()
                )
            )
            dismiss()
        }

        binding.btnCta.setDebounceClickListener {
            homeScreenPrompt.deeplink?.let {
                analyticsApi.postEvent(
                    EventKey.Bottomsheet_HomeScreen_PromptClicked,
                    mapOf(
                        EventKey.AMOUNT to homeScreenPrompt.amount.orZero(),
                        EventKey.FeatureType to homeScreenPrompt.featureType.orEmpty(),
                        EventKey.TIME_STAMP to homeScreenPrompt.timeStamp.orEmpty()
                    )
                )
                dismiss()
                EventBus.getDefault().post(HandleDeepLinkEvent(it))
            }
        }
    }
}