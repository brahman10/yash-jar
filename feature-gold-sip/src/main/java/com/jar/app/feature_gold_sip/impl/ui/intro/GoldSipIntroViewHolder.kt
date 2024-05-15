package com.jar.app.feature_gold_sip.impl.ui.intro

import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellIntroBinding
import com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData

internal class GoldSipIntroViewHolder(private val binding: FeatureGoldSipCellIntroBinding) :
    BaseViewHolder(binding.root) {

    fun setGoldSipIntroData(goldSipData: com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData) {
        Glide.with(binding.root)
            .load(goldSipData.iconUrl)
            .into(binding.ivIntro)

        binding.tvIntroTitle.text = goldSipData.title
    }
}