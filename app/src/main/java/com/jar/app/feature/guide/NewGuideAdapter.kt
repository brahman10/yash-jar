package com.jar.app.feature.guide

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jar.app.R
import com.jar.app.databinding.CellGuideItemBinding

class NewGuideAdapter(
    private val pauseSlide: () -> Unit,
    private val resumeSlide: () -> Unit,
) : ListAdapter<String, NewGuideAdapter.NewGuideViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGuideViewHolder {
        val binding =
            CellGuideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewGuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewGuideViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class NewGuideViewHolder(private val binding: CellGuideItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isImageLoaded: Boolean = false

        private var circularProgressDrawable: CircularProgressDrawable? = null

        private val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                isImageLoaded = false
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                isImageLoaded = true
                resumeSlide()
                return false
            }
        }

        init {
            if (circularProgressDrawable == null) {
                circularProgressDrawable = CircularProgressDrawable(itemView.context)
                circularProgressDrawable?.strokeWidth = 5f
                circularProgressDrawable?.centerRadius = 30f
                circularProgressDrawable?.setColorSchemeColors(com.jar.app.core_ui.R.color.white)
                circularProgressDrawable?.start()
            }

            binding.root.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> pauseSlide()
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (isImageLoaded) resumeSlide()
                }
                false
            }
        }

        fun bindData(data: String) {
            pauseSlide()
            Glide.with(itemView.context)
                .load(data)
                .listener(listener)
                .placeholder(circularProgressDrawable)
                .dontAnimate()
                .into((binding.ivGuide))
        }
    }
}
