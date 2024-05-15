package com.jar.app.feature_round_off.impl.ui.save_method

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_round_off.databinding.FeatureRoundOffCellStepsBinding

class RoundOffStepsAdapter : ListAdapter<String, RoundOffStepsViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RoundOffStepsViewHolder(
        FeatureRoundOffCellStepsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: RoundOffStepsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setTitle(it, currentList.size)
        }
    }

}