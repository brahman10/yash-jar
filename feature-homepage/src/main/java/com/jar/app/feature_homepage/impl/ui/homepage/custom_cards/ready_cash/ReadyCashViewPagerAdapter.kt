package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.ready_cash

import android.text.Spannable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.core_base.domain.model.card_library.FooterCarousel
import com.jar.app.core_base.domain.model.card_library.TextList
import com.jar.app.core_ui.util.convertToString
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_homepage.databinding.FooterCarouselViewBinding
import java.lang.ref.WeakReference

class ReadyCashViewPagerAdapter(
    private val textList: List<TextList>,
) : RecyclerView.Adapter<ReadyCashViewPagerAdapter.ReadyCashViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReadyCashViewPagerViewHolder(
        FooterCarouselViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: ReadyCashViewPagerViewHolder, position: Int) {
        holder.setData(textList[position % textList.size])
    }

    inner class ReadyCashViewPagerViewHolder(
        private val binding: FooterCarouselViewBinding
    ) : BaseViewHolder(binding.root) {
        fun setData(data: TextList) {
            val contextRef = WeakReference(binding.root.context)
            binding.tvCarouselText.text = data.convertToString(contextRef)
            Glide.with(context)
                .load(data.icon)
                .into(binding.ivCarouselImage)
        }
    }
}