package com.jar.app.core_ui.winnings

import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.core_ui.winnings.customAnimations.MysteryCardAnimation
import com.jar.app.core_ui.winnings.customAnimations.SpinsAnimation
import com.jar.app.core_ui.winnings.customAnimations.WeeklyMagicAnimation
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard

internal class SpinningWin(private val spinsAnimation: SpinsAnimation) :
    BaseViewHolder(spinsAnimation.rootView) {

    fun bind(postPaymentRewardCard: PostPaymentRewardCard, callback: (deepLink: String, feature: String) -> Unit) {
        spinsAnimation.setData(postPaymentRewardCard)
        spinsAnimation.rootView.setDebounceClickListener {
            postPaymentRewardCard.deepLink?.let { deepLink ->
                callback.invoke(
                    deepLink,
                    postPaymentRewardCard.animationType.orEmpty()
                )
            }
        }
    }

    fun onStart() {
        spinsAnimation.startAnimation()
    }

    fun onResume() {
        spinsAnimation.resumeAnimation()
    }

    fun onPause() {
        spinsAnimation.pauseAnimation()
    }

    fun onStop() {
        spinsAnimation.stopAnimation()
    }
}

internal class MysteryCard(private val mysteryCardAnimation: MysteryCardAnimation) :
    BaseViewHolder(mysteryCardAnimation) {
    fun bind(postPaymentRewardCard: PostPaymentRewardCard, callback: (deepLink: String, feature: String) -> Unit) {
        with(mysteryCardAnimation) {
            setData(postPaymentRewardCard)
            rootView.setDebounceClickListener {
                postPaymentRewardCard.deepLink?.let { deepLink ->
                    callback.invoke(
                        deepLink,
                        postPaymentRewardCard.animationType.orEmpty()
                    )
                }
            }
        }
    }
}

internal class WeeklyMagic(val binding: WeeklyMagicAnimation) :
    BaseViewHolder(binding.rootView) {
    fun bind(postPaymentRewardCard: PostPaymentRewardCard, callback: (deepLink: String, feature: String) -> Unit) {
        with(binding) {
            setData(postPaymentRewardCard)
            startAnimation()
            rootView.setDebounceClickListener {
                postPaymentRewardCard.deepLink?.let { deepLink ->
                    callback.invoke(
                        deepLink,
                        postPaymentRewardCard.animationType.orEmpty()
                    )
                }
            }
        }
    }

    fun onStart() {
        binding.startAnimation()
    }

    fun onResume() {
        binding.resumeAnimation()
    }

    fun onPause() {
        binding.pauseAnimation()
    }

    fun onStop() {
        binding.stopAnimation()
    }
}