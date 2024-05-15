package com.jar.app.core_ui.step

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.databinding.CoreUiItemStepBinding

class StepAdapter : ListAdapter<Step, StepViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Step>() {
            override fun areItemsTheSame(oldItem: Step, newItem: Step): Boolean {
                return oldItem.textRes == newItem.textRes
            }

            override fun areContentsTheSame(oldItem: Step, newItem: Step): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = CoreUiItemStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setStep(it)
        }
    }

}