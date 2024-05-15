package com.jar.app.feature_spin.impl.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.feature_spin.databinding.FragmentSpinIntroBinding
import com.jar.app.feature_spin.impl.custom.util.fromHtml
import com.jar.app.feature_spin.impl.custom.util.getWidthAndHeight
import com.jar.app.feature_spin.shared.util.SpinsEventKeys
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class SpinIntroFragment : BaseDialogFragment<FragmentSpinIntroBinding>() {

    private val args by navArgs<SpinIntroFragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSpinIntroBinding
        get() = FragmentSpinIntroBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(
            true,
            shouldShowFullScreen = true
        )

    override fun setup() {
        setUpData()
        setupClickListeners()
        analyticsHandler.postEvent(
            com.jar.app.feature_spin.shared.util.SpinsEventKeys.Shown_HowtoPlayScreen,
            mapOf(
                com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureType to com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin
            )
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val widthAndHeight = getWidthAndHeight(this.requireActivity())
        val verticalMargin = widthAndHeight.first * 0.1
        val horizontalMargin = widthAndHeight.second * 0.2
        dialog?.window?.setLayout(
            widthAndHeight.first - verticalMargin.toInt(),
            widthAndHeight.second - horizontalMargin.toInt()
        )
    }


    private fun setUpData() {
        binding.apply {
            playButton.text = args.spinIntro.playCta.text

            Glide.with(tvSpinToWin).load(args.spinIntro.header).into(tvSpinToWin)

            Glide.with(goldBrick).load(args.spinIntro.spinIntroPageDetailsObjects?.get(0)?.iconLink).into(goldBrick)
            Glide.with(icOne).load(args.spinIntro.spinIntroPageDetailsObjects?.get(0)?.index).into(icOne)
            tvOne.text = fromHtml(args.spinIntro.spinIntroPageDetailsObjects?.get(0)?.firstText + " " + (args.spinIntro.spinIntroPageDetailsObjects?.get(0)?.secondText
                ?: ""))

            Glide.with(goldBrick2).load(args.spinIntro.spinIntroPageDetailsObjects?.get(1)?.iconLink).into(goldBrick2)
            Glide.with(icOne2).load(args.spinIntro.spinIntroPageDetailsObjects?.get(1)?.index).into(icOne2)
            tvTwo.text = fromHtml(
                args.spinIntro.spinIntroPageDetailsObjects?.get(1)?.firstText + " " + (args.spinIntro.spinIntroPageDetailsObjects?.get(1)?.secondText
                ?: ""))

            Glide.with(goldBrick3).load(args.spinIntro.spinIntroPageDetailsObjects?.get(2)?.iconLink).into(goldBrick3)
            Glide.with(ic).load(args.spinIntro.spinIntroPageDetailsObjects?.get(2)?.index).into(ic)
            tvThree.text = fromHtml(args.spinIntro.spinIntroPageDetailsObjects?.get(2)?.firstText + " " + (args.spinIntro.spinIntroPageDetailsObjects?.get(2)?.secondText
                ?: ""))
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            playButton.setOnClickListener {
                analyticsHandler.postEvent(
                    com.jar.app.feature_spin.shared.util.SpinsEventKeys.Clicked_Button_HowtoPlayScreen,
                    mapOf(
                        com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureType to com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin
                    )
                )
                dismissAllowingStateLoss()
            }
        }
    }
}