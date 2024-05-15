package com.jar.app.feature_lending.impl.ui.step_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_lending.databinding.CellLendingHorizontalStepBinding
import com.jar.app.feature_lending.databinding.CellLendingVerticalStepBinding
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep

internal class LendingStepAdapter(
    private val stepViewType: LendingStepView.StepViewType
): ListAdapter<LendingProgressStep, LendingBaseStepHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LendingProgressStep>() {
            override fun areItemsTheSame(
                oldItem: LendingProgressStep,
                newItem: LendingProgressStep
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: LendingProgressStep,
                newItem: LendingProgressStep
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LendingBaseStepHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (stepViewType) {
            LendingStepView.StepViewType.HORIZONTAL -> LendingHorizontalStepViewHolder(
                CellLendingHorizontalStepBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                this
            )
            LendingStepView.StepViewType.VERTICAL -> LendingVerticalStepViewHolder(
                CellLendingVerticalStepBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                this
            )
        }
    }

    override fun onBindViewHolder(holder: LendingBaseStepHolder, position: Int) {
        getItem(position)?.let {
            holder.bindStep(it)
        }
    }


}