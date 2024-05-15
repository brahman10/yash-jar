package com.jar.app.feature_gold_sip.impl.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellIntroBinding
import com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData

internal class GoldSipIntroAdapter : ListAdapter<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData, GoldSipIntroViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData>() {
            override fun areItemsTheSame(
                oldItem: com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData,
                newItem: com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData
            ): Boolean {
                return oldItem.iconUrl == newItem.iconUrl
            }

            override fun areContentsTheSame(
                oldItem: com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData,
                newItem: com.jar.app.feature_gold_sip.shared.domain.model.GoldSipData
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GoldSipIntroViewHolder(
        FeatureGoldSipCellIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GoldSipIntroViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setGoldSipIntroData(it)
        }
    }
}