package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.jarDuoV2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_homepage.databinding.FeatureHomePageDuoViewpagerItemBinding

class DuoHomeViewPagerAdapter :
    ListAdapter<String, DuoHomeViewPagerAdapter.JarHomeViewPagerViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = JarHomeViewPagerViewHolder(
        FeatureHomePageDuoViewpagerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: JarHomeViewPagerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setText(it)
        }
    }


    inner class JarHomeViewPagerViewHolder(
        private val binding: FeatureHomePageDuoViewpagerItemBinding,

        ) : BaseViewHolder(binding.root) {
        fun setText(it: String) {
            binding.viewPagerTxt.text = it
        }
    }


}