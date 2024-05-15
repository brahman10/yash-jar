package com.jar.app.feature_payment.impl.ui.payment_option

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_payment.databinding.CellAvailableAppBinding
import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp

internal class UpiAppAdapter(
    private val onClick: (app: UpiApp) -> Unit
) : ListAdapter<UpiApp, UpiAppViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UpiApp>() {
            override fun areItemsTheSame(oldItem: UpiApp, newItem: UpiApp): Boolean {
                return oldItem.packageName == newItem.packageName
            }

            override fun areContentsTheSame(oldItem: UpiApp, newItem: UpiApp): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpiAppViewHolder {
        val binding =
            CellAvailableAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpiAppViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holderUpi: UpiAppViewHolder, position: Int) {
        getItem(position)?.let {
            holderUpi.setApp(it)
        }
    }
}