package com.jar.app.feature_daily_investment.impl.ui.education

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentCellEducationDsBinding
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingEducation

internal class DailySavingEducationAdapter(
    private val onClick: (Int, DailySavingEducation, Boolean) -> Unit
) :
    ListAdapter<DailySavingEducation, DailySavingEducationViewHolder>(DIFF_UTIL) {

    var selectedPosition = 0

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<DailySavingEducation>() {
            override fun areItemsTheSame(
                oldItem: DailySavingEducation, newItem: DailySavingEducation
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: DailySavingEducation, newItem: DailySavingEducation
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = DailySavingEducationViewHolder(
        FeatureDailyInvestmentCellEducationDsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onClick
    )

    override fun onBindViewHolder(holder: DailySavingEducationViewHolder, position: Int) {
        getItem(position)?.let { holder.setStepsData(it) }
    }
}