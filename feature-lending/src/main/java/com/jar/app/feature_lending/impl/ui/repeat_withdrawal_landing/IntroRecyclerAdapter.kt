package com.jar.app.feature_lending.impl.ui.repeat_withdrawal_landing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.feature_lending.databinding.CellLendingRepeatWithdrawalIntroBinding
import com.jar.app.feature_lending.impl.domain.model.IntroItem

internal class IntroRecyclerAdapter :
    ListAdapter<IntroItem, IntroRecyclerAdapter.IntroHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<IntroItem>() {
            override fun areItemsTheSame(oldItem: IntroItem, newItem: IntroItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: IntroItem, newItem: IntroItem): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroHolder {
        val binding = CellLendingRepeatWithdrawalIntroBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntroHolder(binding)
    }

    override fun onBindViewHolder(holder: IntroHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class IntroHolder(
        private val binding: CellLendingRepeatWithdrawalIntroBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: IntroItem) {
            Glide.with(binding.root.context).load(item.icon).into(binding.ivUspIcon)
            binding.tvUspTitle.text = binding.root.context.getString(item.title)

        }

    }
}