package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.shared.domain.model.Steps
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentIntroBottomsheetCellBinding


internal class DailySavingsV2IntroductionBottomSheetAdapter(
) :
    ListAdapter<Steps, DailySavingsIntroductionViewHolder>(DIFF_CALLBACK){

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Steps>() {
            override fun areItemsTheSame(oldItem: Steps, newItem: Steps): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Steps, newItem: Steps): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailySavingsIntroductionViewHolder {
        val binding = FeatureDailyInvestmentIntroBottomsheetCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailySavingsIntroductionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailySavingsIntroductionViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

internal class DailySavingsIntroductionViewHolder(
    private val binding: FeatureDailyInvestmentIntroBottomsheetCellBinding,
) :
    BaseViewHolder(binding.root) {

    fun bind(data: Steps){
        binding.tvTitle.text = data.title
        Glide.with(context).load(data.imageUrl)
            .into(binding.imIcon)
    }

}