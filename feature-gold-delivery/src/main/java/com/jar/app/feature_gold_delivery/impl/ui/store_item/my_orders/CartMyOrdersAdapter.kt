package com.jar.app.feature_gold_delivery.impl.ui.store_item.my_orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.ItemCartMyOrderBinding
import com.jar.app.feature_gold_delivery.databinding.ItemGoldDeliveryFaqHeaderBinding
import com.jar.app.feature_gold_delivery.impl.helper.Utils.getColorForCommonTransactionStatus
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.MyOrdersData

internal class CartMyOrdersAdapter(
    private val onTransactionSelected: (transactionData: TransactionData) -> Unit
) : PagingDataAdapter<MyOrdersData, ViewHolder>(DIFF_CALLBACK) {

    companion object {
        const val MY_ORDERS_HEADER = 1
        const val MY_ORDERS_DETAIL = 2
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MyOrdersData>() {
            override fun areItemsTheSame(
                oldItem: MyOrdersData,
                newItem: MyOrdersData
            ): Boolean {
                return ((oldItem is MyOrdersData.MyOrdersHeader && newItem is MyOrdersData.MyOrdersHeader && oldItem.title == newItem.title) ||
                        (oldItem is MyOrdersData.MyOrdersBody && newItem is MyOrdersData.MyOrdersBody && oldItem.body.orderId == newItem.body.orderId))
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: MyOrdersData,
                newItem: MyOrdersData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MyOrdersData.MyOrdersHeader -> MY_ORDERS_HEADER
            is MyOrdersData.MyOrdersBody -> MY_ORDERS_DETAIL
            else -> throw java.lang.RuntimeException("Item View holder isn't correct")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            MY_ORDERS_HEADER -> {
                val binding = ItemGoldDeliveryFaqHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return NewCartMyOrderHeaderVH(binding)
            }

            MY_ORDERS_DETAIL -> {
                val binding =
                    ItemCartMyOrderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return NewCartMyOrderVH(binding)
            }

            else -> {
                throw java.lang.RuntimeException("Item View holder isn't correct")
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        when (itemViewType) {
            MY_ORDERS_HEADER -> {
                getItem(position)?.let {
                    (holder as NewCartMyOrderHeaderVH).bindData(it as MyOrdersData.MyOrdersHeader)
                }
            }
            MY_ORDERS_DETAIL -> {
                getItem(position)?.let {
                    (holder as NewCartMyOrderVH).bindData(it as MyOrdersData.MyOrdersBody)
                }
            }
        }
    }

    inner class NewCartMyOrderHeaderVH(
        private val binding: ItemGoldDeliveryFaqHeaderBinding
    ) : BaseViewHolder(binding.root) {
        fun bindData(it: MyOrdersData.MyOrdersHeader) {
            binding.tvHeader.text = it.title
        }
    }


    inner class NewCartMyOrderVH(
        private val binding: ItemCartMyOrderBinding
    ) : BaseViewHolder(binding.root) {

        private var transactionData: TransactionData? = null

        init {
            binding.root.setDebounceClickListener {
                transactionData?.let {
                    onTransactionSelected(it)
                }
            }
        }

        fun bindData(data: MyOrdersData.MyOrdersBody) {
            transactionData = data.body
            binding.dateTv.text = data.body.date
            binding.nameTv.text = data.body.title
            binding.statusTv.text = data.body.currentStatus
            binding.quantityTv.text = context.getString(R.string.item_quantity_gm, 1, data.body.volume)
            data.body.currentStatus?.let {
                binding.statusTv.setTextColor(
                    getColorForCommonTransactionStatus(data.body.statusEnum).getColor(context)
                )
            }

            data.body?.let {
                it.iconLink?.let {
                    Glide.with(binding.root.context).load(it).into(binding.imageView)
                }
            }
        }
    }
}
