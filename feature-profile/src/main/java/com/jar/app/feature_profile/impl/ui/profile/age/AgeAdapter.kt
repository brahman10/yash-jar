package com.jar.app.feature_profile.impl.ui.profile.age

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_profile.databinding.CellProfileAgeItemBinding

class AgeAdapter: ListAdapter<Int, AgeAdapter.AgeViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgeViewHolder {
        val binding = CellProfileAgeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgeViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class AgeViewHolder(
        private val binding: CellProfileAgeItemBinding
    ) : BaseViewHolder(binding.root) {

        fun bindData(data: Int) {
            binding.tvAge.text = data.toString()
        }
    }
}