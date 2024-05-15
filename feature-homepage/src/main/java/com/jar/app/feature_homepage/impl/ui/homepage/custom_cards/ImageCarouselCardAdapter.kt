package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_homepage.databinding.HomepageImageCardsLayoutBinding
import com.jar.app.feature_homepage.shared.domain.model.ImageCards

class ImageCarouselCardAdapter(
    private val imageList: List<ImageCards>,
    private val onClick: () -> Unit
) : RecyclerView.Adapter<ImageCarouselCardAdapter.ImageCarouselCardViewHolder>(){

    inner class ImageCarouselCardViewHolder(
        private val binding: HomepageImageCardsLayoutBinding
    ): BaseViewHolder(binding.root) {
        fun setData(image: ImageCards){
            Glide.with(context)
                .load(image.imageUrl)
                .into(binding.ivImage)

            binding.root.setOnClickListener {
                onClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCarouselCardViewHolder {
        return ImageCarouselCardViewHolder(
            HomepageImageCardsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: ImageCarouselCardViewHolder, position: Int) {
        holder.setData(imageList[position % imageList.size])
    }
}