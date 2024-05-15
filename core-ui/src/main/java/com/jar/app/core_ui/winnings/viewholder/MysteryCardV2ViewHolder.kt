package com.jar.app.core_ui.winnings.viewholder

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_ui.databinding.MysteryCardViewHolderv2Binding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard


class MysteryCardV2ViewHolder(
    private val binding: MysteryCardViewHolderv2Binding,
) : BaseViewHolder(binding.root) {

    fun bind(
        postPaymentRewardCard: PostPaymentRewardCard,
        callback: (deepLink: String, featureType: String) -> Unit
    ) {
        binding.root.setDebounceClickListener {
            postPaymentRewardCard.deepLink?.let { deepLink ->
                callback.invoke(
                    deepLink,
                    postPaymentRewardCard.animationType.orEmpty()
                )
            }
        }
        postPaymentRewardCard.title?.let {
            binding.titleTv.text = it
        }
        postPaymentRewardCard.ctaText?.let {
            binding.descTv.text = it
        }
        postPaymentRewardCard.secondaryTitle?.let {
            binding.tvYou.text = it
        }
        postPaymentRewardCard.tertiaryTitle?.let {
            binding.rightContainer.isVisible = true
            binding.rightTv.text = it
        } ?: run {
            binding.rightContainer.isVisible = false
        }

        postPaymentRewardCard.bannerImage?.let {
            binding.bannerIv?.let { it1 ->
                Glide.with(context)
                    .load(it)
                    .into(it1)
            }
        }
    }
}