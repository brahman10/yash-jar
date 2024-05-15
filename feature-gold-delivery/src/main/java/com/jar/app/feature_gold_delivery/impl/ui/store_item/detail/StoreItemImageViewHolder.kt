package com.jar.app.feature_gold_delivery.impl.ui.store_item.detail

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.base.util.dp
import com.jar.app.feature_gold_delivery.databinding.CellStoreItemImageBinding
import javax.inject.Inject

class StoreItemImageViewHolder @Inject constructor(private val binding: CellStoreItemImageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setImage(url: String) {
        Glide.with(itemView)
            .load(url)
            .transform(RoundedCorners(10.dp))
            .into(binding.ivImage)
    }
}