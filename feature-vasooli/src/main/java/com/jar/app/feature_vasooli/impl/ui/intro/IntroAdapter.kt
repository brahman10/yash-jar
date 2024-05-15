package com.jar.app.feature_vasooli.impl.ui.intro

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_vasooli.databinding.CellIntroBinding
import com.jar.app.feature_vasooli.impl.domain.model.Intro

internal class IntroAdapter : ListAdapter<Intro, IntroAdapter.IntroViewHolder>(DIFF_CALLBACK){

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Intro>() {
            override fun areItemsTheSame(oldItem: Intro, newItem: Intro): Boolean {
                return  oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Intro, newItem: Intro): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val binding = CellIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class IntroViewHolder(
        private val binding: CellIntroBinding
    ) : BaseViewHolder(binding.root) {

        fun bind(data: Intro) {
            data.title?.let { binding.tvIntroTitle.setText(it) }
            data.description?.let {
                binding.tvIntroDescription.setText(data.description)
            }

            //If using match_parent for images we need to specify the width and height of the screen explicitly else Glide blurs the image
            Glide.with(context)
                .load(data.imageLink)
                .override(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)
                .into(binding.ivIntro)
        }
    }
}