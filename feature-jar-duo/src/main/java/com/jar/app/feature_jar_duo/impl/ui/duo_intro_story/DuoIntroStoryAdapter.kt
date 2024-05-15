package com.jar.app.feature_jar_duo.impl.ui.duo_intro_story

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jar.app.feature_jar_duo.databinding.CellOnboardingItemBinding
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject

class DuoIntroStoryAdapter(
    private val pauseSlide: () -> Unit,
    private val resumeSlide: () -> Unit,
) : ListAdapter<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject, DuoIntroStoryAdapter.OnBoardingViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject>() {
            override fun areItemsTheSame(oldItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject, newItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject, newItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding =
            CellOnboardingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class OnBoardingViewHolder(private val binding: CellOnboardingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isImageLoaded: Boolean = false

      //  private var circularProgressDrawable: CircularProgressDrawable? = null

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
           /* if (circularProgressDrawable == null) {
                circularProgressDrawable = CircularProgressDrawable(itemView.context)
                circularProgressDrawable?.strokeWidth = 5f
                circularProgressDrawable?.centerRadius = 30f
                circularProgressDrawable?.setColorSchemeColors(com.jar.app.core_ui.R.color.white)
                circularProgressDrawable?.start()
            }*/

            binding.root.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> pauseSlide()
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (isImageLoaded) resumeSlide()
                }
                false
            }
        }

        fun bindData(data: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroPageObject) {
            pauseSlide()
            Glide.with(itemView.context)
                .load(data.imageUrl)
                .listener(listener)
              //  .placeholder(circularProgressDrawable)
                .dontAnimate()
                .into((binding.ivGuide))
            binding.duoIntroStoryText.text = data.pageText
        }
    }
}