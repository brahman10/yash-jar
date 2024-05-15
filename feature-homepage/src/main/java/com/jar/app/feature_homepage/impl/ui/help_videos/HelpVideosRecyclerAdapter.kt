package com.jar.app.feature_homepage.impl.ui.help_videos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellGridHelpVideoBinding
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellLinearHelpVideoBinding
import com.jar.app.feature_homepage.shared.domain.model.HelpVideo

internal class HelpVideosRecyclerAdapter(
    private val isGridView: Boolean = false,
    private val onItemClick: (HelpVideo) -> Unit
) : ListAdapter<HelpVideo, RecyclerView.ViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<HelpVideo>() {
            override fun areItemsTheSame(oldItem: HelpVideo, newItem: HelpVideo): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: HelpVideo, newItem: HelpVideo): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isGridView) HelpVideosGridHolder(
            FeatureHomepageCellGridHelpVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        else HelpVideosLinearHolder(
            FeatureHomepageCellLinearHelpVideoBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HelpVideosGridHolder -> {
                holder.bind(getItem(position))
            }
            is HelpVideosLinearHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    private fun convertToMinSec(seconds: String): CharSequence {
        return try {
            val s = seconds.toInt()
            val min = s / 60
            val sec = s % 60
            "$min min $sec sec"
        } catch (ex: Exception) {
            ex.printStackTrace()
            "0 min"
        }
    }

    inner class HelpVideosGridHolder(
        private val binding: FeatureHomepageCellGridHelpVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(helpVideo: HelpVideo) {
            binding.tvVideoTitle.text = helpVideo.title
            binding.tvVideoTime.text = convertToMinSec(helpVideo.lengthInSeconds)
            if (helpVideo.thumbnail.isNullOrEmpty().not()) {
                Glide.with(binding.root).load(helpVideo.thumbnail).into(binding.ivThumbnail)
            }
            binding.flThumbnailHolder.setDebounceClickListener {
                onItemClick.invoke(helpVideo)
            }
        }
    }

    inner class HelpVideosLinearHolder(
        private val binding: FeatureHomepageCellLinearHelpVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(helpVideo: HelpVideo) {
            binding.tvVideoTitle.text = helpVideo.title
            binding.tvVideoTime.text = convertToMinSec(helpVideo.lengthInSeconds)
            if (helpVideo.thumbnail.isNullOrEmpty().not()) {
                Glide.with(binding.root).load(helpVideo.thumbnail).into(binding.ivThumbnail)
            }
            binding.flThumbnailHolder.setDebounceClickListener {
                onItemClick.invoke(helpVideo)
            }
        }
    }
}