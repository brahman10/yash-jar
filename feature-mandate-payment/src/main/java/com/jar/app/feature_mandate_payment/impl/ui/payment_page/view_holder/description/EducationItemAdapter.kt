package com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder.description

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellMandateEducationItemBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help.MandateEducationItem

internal class EducationItemAdapter :
    ListAdapter<MandateEducationItem, EducationItemViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MandateEducationItem>() {
            override fun areItemsTheSame(
                oldItem: MandateEducationItem,
                newItem: MandateEducationItem
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: MandateEducationItem,
                newItem: MandateEducationItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EducationItemViewHolder(
        FeatureMandatePaymentCellMandateEducationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: EducationItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setMandateEducationItem(it, currentList.size)
        }
    }

}