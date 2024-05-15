package com.jar.app.feature_lending_kyc.impl.ui.step_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycCellHorizontalStepBinding
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycCellVerticalStepBinding
import com.jar.app.feature_lending_kyc.impl.data.KycStep

internal class KycStepsAdapter(
    private val stepViewType: KycStepView.StepViewType
) : ListAdapter<KycStep, BaseStepHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<KycStep>() {
            override fun areItemsTheSame(oldItem: KycStep, newItem: KycStep): Boolean {
                return oldItem.text == newItem.text
            }

            override fun areContentsTheSame(oldItem: KycStep, newItem: KycStep): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseStepHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (stepViewType) {
            KycStepView.StepViewType.HORIZONTAL -> HorizontalStepViewHolder(
                FeatureLendingKycCellHorizontalStepBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                this
            )
            KycStepView.StepViewType.VERTICAL -> VerticalStepViewHolder(
                FeatureLendingKycCellVerticalStepBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                this
            )
        }
    }

    override fun onBindViewHolder(holder: BaseStepHolder, position: Int) {
        getItem(position)?.let {
            holder.bindStep(it)
        }
    }

    fun getStep(position: Int): KycStep = getItem(position)
}