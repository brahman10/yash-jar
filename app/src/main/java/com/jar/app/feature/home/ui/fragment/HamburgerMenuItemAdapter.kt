package com.jar.app.feature.home.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.R
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_base.util.orFalse
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.ItemHamburgerMenuBinding
import com.jar.app.feature_homepage.shared.domain.model.hamburger.HamburgerItem

class HamburgerMenuItemAdapter(
    private val onMenuItemClick: (position: Int, menuItem: HamburgerItem) -> Unit
) : ListAdapter<HamburgerItem, HamburgerMenuItemAdapter.HamburgerMenuItemViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<HamburgerItem>() {
            override fun areItemsTheSame(oldItem: HamburgerItem, newItem: HamburgerItem): Boolean {
                return oldItem.text == newItem.text
            }

            override fun areContentsTheSame(
                oldItem: HamburgerItem, newItem: HamburgerItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HamburgerMenuItemViewHolder {
        val binding = ItemHamburgerMenuBinding.inflate(LayoutInflater.from(parent.context))
        return HamburgerMenuItemViewHolder(binding, onMenuItemClick)
    }

    override fun onBindViewHolder(holder: HamburgerMenuItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setMenu(it)
        }
    }

    inner class HamburgerMenuItemViewHolder(
        private val binding: ItemHamburgerMenuBinding,
        private val onMenuItemClick: (position: Int, menuItem: HamburgerItem) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var hamburgerItem: HamburgerItem? = null

        init {
            binding.root.setDebounceClickListener {
                hamburgerItem?.let { item ->
                    onMenuItemClick.invoke(bindingAdapterPosition + 1, item)
                }
            }
        }

        fun setMenu(item: HamburgerItem) {
            item.itemType?.let {
                binding.root.setPlotlineViewTag(tag = it)
            }

            hamburgerItem = item
            binding.root.background = ContextCompat.getDrawable(
                binding.root.context,
                if (item.isHighlighted.orFalse() || item.showNewTag.orFalse()) R.drawable.bg_rounded_highlighted_tile_10dp else R.drawable.bg_rounded_corner_transparent_light_purple
            )
            binding.ivStar.isVisible =
                item.isHighlighted.orFalse() && item.showNewTag.orFalse().not()
            binding.tvNew.isVisible = item.showNewTag.orFalse()
            binding.bgHighlightedView.isVisible = item.showNewTag.orFalse()
            binding.shimmerPlaceholder.isVisible = item.showShimmer.orFalse()
            binding.text.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (item.showNewTag.orFalse()) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_B1A7CE
                )
            )
            when (item.getInfoGraphicType()) {
                InfographicType.IMAGE -> {
                    item.logo?.let {
                        Glide
                            .with(binding.root.context)
                            .load(item.logo)
                            .into(binding.menuIcon)
                    }
                }
                InfographicType.LOTTIE -> {
                    item.logo?.let {
                        binding.menuLottie.playLottieWithUrlAndExceptionHandling(context, it)
                    }
                }
                else -> {}
            }
            binding.text.text = item.text

        }

    }
}