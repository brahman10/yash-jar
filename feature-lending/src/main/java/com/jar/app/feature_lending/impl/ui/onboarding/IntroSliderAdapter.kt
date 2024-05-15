package com.jar.app.feature_lending.impl.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.jar.app.feature_lending.databinding.CellLandingReadyCashIntroBinding
import com.jar.app.feature_lending.shared.domain.model.v2.LandingSlider

internal class IntroSliderAdapter :
    ListAdapter<LandingSlider, IntroSliderAdapter.SliderHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LandingSlider>() {
            override fun areItemsTheSame(oldItem: LandingSlider, newItem: LandingSlider): Boolean {
                return oldItem.imageUrl == newItem.imageUrl
            }

            override fun areContentsTheSame(
                oldItem: LandingSlider,
                newItem: LandingSlider
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderHolder {
        return SliderHolder(
            CellLandingReadyCashIntroBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SliderHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class SliderHolder(val binding: CellLandingReadyCashIntroBinding) :
        ViewHolder(binding.root) {

        fun bind(data: LandingSlider) {
            Glide.with(binding.root.context)
                .load(data.imageUrl)
                .into(binding.ivIntroImage)
            binding.tvTitle.text = data.title
            binding.tvDescription.text = data.description
        }
    }
}