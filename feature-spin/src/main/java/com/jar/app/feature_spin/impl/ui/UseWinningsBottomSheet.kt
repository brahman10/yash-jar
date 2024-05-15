package com.jar.app.feature_spin.impl.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_spin.R
import com.jar.app.feature_spin.databinding.FragmentUseWinningsBinding
import com.jar.app.feature_spin.impl.custom.util.fromHtml
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.CTA
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.Clicked_button_HowtoUseWinningsScreen
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.Close
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureType
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.Shown_HowtoUseWinningsScreen
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.UseWinnings
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class UseWinningsBottomSheet: BaseBottomSheetDialogFragment<FragmentUseWinningsBinding>() {

    private val args by navArgs<UseWinningsBottomSheetArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        const val TAG = "UseWinningsBottomSheet"
        fun show(supportFragmentManager: FragmentManager) {
            UseWinningsBottomSheet().show(supportFragmentManager, TAG)
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUseWinningsBinding
        get() = FragmentUseWinningsBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        context?.let {
            ContextCompat.getColor(it, R.color.status_bar_color_use_winning)
        }?.let { setNavigationBarColor(it) }
        setViewWithData()
        setUpClickListeners()
        analyticsHandler.postEvent(
            Shown_HowtoUseWinningsScreen,
            mapOf(
                FeatureType to FeatureTypeSpinAndWin
            )
        )
    }

    private fun setViewWithData() {
        binding.apply {
            Glide.with(icon).load(args.useWinning.url).into(icon)
            tvQuestion.text = args.useWinning.title?.let { fromHtml(it) }
            useWinning.text = args.useWinning.description?.let { fromHtml(it) }
            playButton.text = args.useWinning.ctaText
        }
    }


    private fun setUpClickListeners() {
        with(binding) {
            ivClose.setDebounceClickListener {
                analyticsHandler.postEvent(
                    Clicked_button_HowtoUseWinningsScreen,
                    mapOf(
                        FeatureType to FeatureTypeSpinAndWin,
                        CTA to Close
                    )
                )
                popBackStack()
            }
            playButton.setDebounceClickListener {
                analyticsHandler.postEvent(
                    Clicked_button_HowtoUseWinningsScreen,
                    mapOf(
                        FeatureType to FeatureTypeSpinAndWin,
                        CTA to UseWinnings
                    )
                )
                EventBus.getDefault().post(
                    args.useWinning.ctaDeeplink?.let { it1 ->
                        HandleDeepLinkEvent(
                            it1
                        )
                    }
                )
            }
        }
    }

    private fun setNavigationBarColor(color: Int) {
        dialog?.window?.navigationBarColor = color
    }
}