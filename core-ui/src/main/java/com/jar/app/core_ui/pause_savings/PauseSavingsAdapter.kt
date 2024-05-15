package com.jar.app.core_ui.pause_savings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.databinding.CoreUiCellPauseDurationBinding
import com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper


class PauseSavingsAdapter(private val onClick: (pauseSavingOptionWrapper: PauseSavingOptionWrapper, position: Int) -> Unit) :
    ListAdapter<PauseSavingOptionWrapper,PauseSavingsViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PauseSavingOptionWrapper>() {
            override fun areItemsTheSame(
                oldItem: PauseSavingOptionWrapper,
                newItem: PauseSavingOptionWrapper
            ): Boolean {
                return oldItem.pauseSavingOption.timeValue == newItem.pauseSavingOption.timeValue
            }

            override fun areContentsTheSame(
                oldItem: PauseSavingOptionWrapper,
                newItem: PauseSavingOptionWrapper
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PauseSavingsViewHolder(
        CoreUiCellPauseDurationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onClick
    )

    override fun onBindViewHolder(holder: PauseSavingsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setPauseOption(it)
        }
    }
}