package com.jar.app.feature_transaction.impl.ui.breakdown

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellGoldBreakdownBinding
import com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown

class UserGoldBreakdownAdapter : ListAdapter<com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown, UserGoldBreakdownViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown>() {
            override fun areItemsTheSame(oldItem: com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown, newItem: com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown, newItem: com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserGoldBreakdownViewHolder {
        val binding = FeatureTransactionCellGoldBreakdownBinding.inflate(
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