package com.jar.app.feature.user_gold_breakdown.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.databinding.CellGoldBreakdownBinding
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.UserGoldBreakdown

class UserGoldBreakdownAdapter : ListAdapter<UserGoldBreakdown, UserGoldBreakdownViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserGoldBreakdown>() {
            override fun areItemsTheSame(oldItem: UserGoldBreakdown, newItem: UserGoldBreakdown): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: UserGoldBreakdown, newItem: UserGoldBreakdown): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserGoldBreakdownViewHolder {
        val binding = CellGoldBreakdownBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserGoldBreakdownViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserGoldBreakdownViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setUserGoldBreakdown(it)
        }
    }
}