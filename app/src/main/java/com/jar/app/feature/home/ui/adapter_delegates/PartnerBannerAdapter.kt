package com.jar.app.feature.home.ui.adapter_delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellPartnerBannerBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.Banner

class PartnerBannerAdapter(
    private val onCardShown: () -> Unit,
    private val onCardClick: (banner: Banner) -> Unit,
) : ListAdapter<Banner, PartnerBannerAdapter.PartnerBannerViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Banner>() {
            override fun areItemsTheSame(oldItem: Banner, newItem: Banner): Boolean {
                return oldItem.getUniqueKey() == newItem.getUniqueKey()
            }

            override fun areContentsTheSame(oldItem: Banner, newItem: Banner): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerBannerViewHolder {
        val binding =
            CellPartnerBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PartnerBannerViewHolder(binding, onCardClick)
    }

    override fun onBindViewHolder(holder: PartnerBannerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setBanner(it)
        }
    }

    override fun onViewAttachedToWindow(holder: PartnerBannerViewHolder) {
        super.onViewAttachedToWindow(holder)
        onCardShown.invoke()
    }

    inner class PartnerBannerViewHolder(
        private val binding: CellPartnerBannerBinding,
        private val onCardClick: (banner: Banner) -> Unit,
    ) :
        BaseViewHolder(binding.root) {

        fun setBanner(banner: Banner) {
            binding.tvTitle.text = banner.title
            binding.tvDescription.text = banner.description
            Glide.with(itemView).load(banner.partnerLogo).into(binding.ivLogo)
            binding.claimNow.setDebounceClickListener {
                onCardClick.invoke(banner)
            }
        }
    }
}