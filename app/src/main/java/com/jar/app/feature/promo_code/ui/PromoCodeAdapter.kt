package com.jar.app.feature.promo_code.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.jar.app.databinding.CellPromoCodeBinding
import com.jar.app.feature.promo_code.domain.data.PromoCode
import kotlinx.coroutines.CoroutineScope

class PromoCodeAdapter(
    private val uiScope: CoroutineScope,
    private val onApplyClick: (Int, PromoCode) -> Unit
) : PagingDataAdapter<PromoCode, PromoCodeViewHolder>(DIFF_UTIL) {

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<PromoCode>() {
            override fun areItemsTheSame(oldItem: PromoCode, newItem: PromoCode): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PromoCode, newItem: PromoCode): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PromoCodeViewHolder(
        CellPromoCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        uiScope,
        onApplyClick
    )

    override fun onBindViewHolder(holder: PromoCodeViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setPromoCode(it)
        }
    }


    override fun onViewAttachedToWindow(holder: PromoCodeViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.startTimer()
    }

    override fun onViewDetachedFromWindow(holder: PromoCodeViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.stopTimer()
    }
}