package com.jar.feature_quests.impl.ui.jackpot_popup

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.getDateShortMonthNameAndYear
import com.jar.app.core_base.domain.model.SpinBrandCouponOutcomeResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_quests.R
import com.jar.app.feature_quests.databinding.FragmentQuestsUnlockedBinding
import com.jar.app.feature_spin.impl.custom.util.dpToPx
import com.jar.app.feature_spin.impl.custom.util.getWidthAndHeight
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class JackpotPopupDialog : BaseDialogFragment<FragmentQuestsUnlockedBinding>() {
    private val args by navArgs<JackpotPopupDialogArgs>()

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val jackpotResponse: SpinBrandCouponOutcomeResponse by lazy {
        args.brandCouponOutcome
    }

    @Inject
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(), com.jar.app.core_ui.R.color.black_20
                )
            )
        )
        val widthAndHeight = getWidthAndHeight(this.requireActivity())
        dialog?.window?.setLayout(
            widthAndHeight.first,
            widthAndHeight.second
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentQuestsUnlockedBinding
        get() = FragmentQuestsUnlockedBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(
            isCancellable = false,
            shouldShowFullScreen = true
        )

    override fun setup() {
        setupUI()
        analyticsApi.postEvent(
            QuestEventKey.Events.Shown_QuestLevelRewards,
            mapOf(
                QuestEventKey.Properties.level to args.fromQuest
            )
        )
    }

    private fun setupUI() {
        with(binding) {
            tvHeading.text = jackpotResponse.header
            jackpotResponse.externalBrandCouponInfo?.brandIconLink?.let { imageUrl ->
                Glide.with(requireContext())
                    .load(imageUrl)
                    .override(56f.dpToPx(requireContext()))
                    .into(ivBrandIconCouponDetails)
            }
            tvCouponDetailsBrandTitle.text = jackpotResponse.externalBrandCouponInfo?.brandName
            tvCouponDetailsHeading.text = jackpotResponse.externalBrandCouponInfo?.title
            tvCouponDetailsDescription.text = jackpotResponse.externalBrandCouponInfo?.description

            btnCta2.setText(jackpotResponse.shareCta?.text.orEmpty())
            tvNavigationText.text = jackpotResponse.navigationText
            btnCta.setText(jackpotResponse.spinAgainCta?.text.orEmpty())
            jackpotResponse.externalBrandCouponInfo?.expiry?.let { expiryTime ->
                tvCouponDetailsValidTill.text = String.format(
                    getString(R.string.feature_quests_coupon_validity),
                    expiryTime.getDateShortMonthNameAndYear()
                )
            }
            tvNavigationText.text = jackpotResponse.navigationText

            btnCta2.setDebounceClickListener {
                postClickedEvent(btnCta2.getText())
                jackpotResponse.shareCta?.deeplink?.let { deepLink ->
                    postHandleDeepLinkEventAndDismiss(deepLink)
                }
            }
            ivClose.setDebounceClickListener {
                postClickedEvent(QuestEventKey.Values.cross)
                dismiss()
            }

            btnCta.setDebounceClickListener {
                postClickedEvent(btnCta.getText())
                jackpotResponse.spinAgainCta?.deeplink?.let { deepLink ->
                    dismiss()
                }
            }

        }
    }

    private fun postClickedEvent(buttonType: String) {
        analyticsApi.postEvent(
            QuestEventKey.Events.Clicked_QuestLevelRewards,
            mapOf(
                QuestEventKey.Properties.level to args.fromQuest,
                QuestEventKey.Properties.button_type to buttonType
            )
        )
    }

    private fun postHandleDeepLinkEventAndDismiss(deepLink: String) {
        dismiss()
        EventBus.getDefault().post(HandleDeepLinkEvent(deepLink))
    }
}