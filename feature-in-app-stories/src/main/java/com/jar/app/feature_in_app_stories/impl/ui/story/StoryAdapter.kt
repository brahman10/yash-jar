package com.jar.app.feature_in_app_stories.impl.ui.story

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_in_app_stories.R
import com.jar.app.feature_in_app_stories.databinding.CellStoryItemBinding
import com.jar.app.feature_in_app_stories.impl.domain.ActionType
import com.jar.app.feature_in_app_stories.impl.domain.ImageButtonData
import com.jar.app.feature_story.data.model.CTA
import com.jar.app.feature_story.data.model.MediaType
import com.jar.app.feature_story.data.model.Page
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.floor


@UnstableApi
class StoryAdapter(
    private val pauseSlide: () -> Unit,
    private val resumeSlide: () -> Unit,
    private val toggleMuteState: () -> Boolean,
    private val shareThePage: (pageId: String, image: String?, position: Int) -> Unit,
    private val handleDownloadClicked: (mediaUrl: String, mediaType: String, pageId: String, position: Int) -> Unit,
    private val handleLikeClicked: (String, Boolean, Int) -> Unit,
    private val handleCtaClicked: (CTA, String, Int) -> Unit,
    private val handleCloseStory: (String, Int) -> Unit,
    private val isInternetConnect: () -> Boolean,
    private val hideNavigationView: () -> Unit,
    private val showNavigationView: () -> Unit,
) : ListAdapter<Page, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    var storyPosition: Int = -1
    var pageId = ""
    var buttonOrder = ""
    var iscta = false
    var ctaName = ""
    var duration: Int? = 0
    private var player: Player? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    private val playerViewMap: HashMap<Int, SurfaceView> = hashMapOf()
    private var glide: RequestManager? = null

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Page>() {
            override fun areItemsTheSame(
                oldItem: Page,
                newItem: Page
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Page,
                newItem: Page
            ): Boolean {
                return oldItem.pageId == newItem.pageId
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            CellStoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindData(it, position)
        }
    }

    override fun onViewDetachedFromWindow(holder: StoryViewHolder) {
        holder.clearGlide()
        super.onViewDetachedFromWindow(holder)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        for (i in 0 until recyclerView.childCount) {
            val viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i))
            (viewHolder as? StoryViewHolder)?.clearGlide()
        }
        super.onDetachedFromRecyclerView(recyclerView)
    }


    @UnstableApi
    @SuppressLint("ClickableViewAccessibility")
    inner class StoryViewHolder(private val binding: CellStoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isLiked: Boolean = false
        private var pageData: Page? = null
        private var startTime: Long = 0


        private var clParentCustomTarget: CustomTarget<Drawable> = object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    binding.clParent.setBackgroundDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            }

        private var customTarget: CustomTarget<Drawable> = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                pageData?.timeToLoadMedia = System.currentTimeMillis() - startTime
                binding.shimmerLayout.stopShimmer()
                binding.loadingLayout.isVisible = false
                binding.storyContent.isVisible = true
                binding.ivGuide.setImageDrawable(resource)
                resumeSlide()
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                try {
                    pauseSlide()
                    binding.ivGuide.isVisible = false
                    binding.shimmerLayout.startShimmer()
                    binding.ivRefresh.isVisible = true
                    binding.tvRefresh.isVisible = true
//                    binding.clParent.setBackgroundDrawable(
//                        ContextCompat.getDrawable(
//                            binding.root.context,
//                            R.drawable.error_bg
//                        )
//                    )
                    hideNavigationView.invoke()
                } catch (ex: Exception) {
                    Timber.d(ex)
                }

            }
        }


        fun bindData(data: Page, position: Int) {
            storyPosition = position
            pageId = data.pageId
            buttonOrder = ""
            iscta = false
            playerViewMap[position] = binding.storyVideoView
            pageData = data
            startTime = System.currentTimeMillis()

            duration = data.duration

            pauseSlide()
            glide = Glide.with(itemView.context)

            binding.shimmerLayout.startShimmer()
            binding.loadingLayout.isVisible = false
            binding.storyContent.isVisible = true
            binding.ivRefresh.isVisible = false
            binding.tvRefresh.isVisible = false
            binding.clParent.setBackgroundColor(
                ContextCompat.getColor(
                    binding.clParent.context,
                    com.jar.app.core_ui.R.color.color_272239
                )
            )
            showNavigationView.invoke()

            if (data.mediaType == MediaType.IMAGE.value) {
                binding.ivMute.isVisible = false
                binding.videoCardView.isVisible = false
                binding.ivGuide.isVisible = true
                data.mediaUrl?.let {
                    glide?.apply {
                        load(it)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .into(customTarget)
                    }
                }
            } else {
                binding.ivMute.isVisible = true
                binding.storyContent.isVisible = true
                binding.ivGuide.isVisible = false
                binding.videoCardView.isVisible = true

            }
            var ctaName = ""
            val buttonList = data.actionOrders?.sortedBy { it.order }?.map {
                when (ActionType.valueOf(it.actionType.uppercase())) {
                    ActionType.LIKE -> {
                        buttonOrder += "${it.actionType}, "
                        val iconUrl = data.likeCta?.let {
                            isLiked = it.isLiked
                            if (it.isLiked) it.likedIcon else it.unlikedIcon
                        }
                        ImageButtonData(
                            id = it.order,
                            iconUrl = iconUrl,
                            actionType = ActionType.LIKE,
                            count = data.likeCta?.count
                        )
                    }

                    ActionType.SHARE -> {
                        buttonOrder += "${it.actionType}, "
                        val iconUrl = data.shareCta?.icon
                        ImageButtonData(
                            id = it.order,
                            iconUrl = iconUrl,
                            actionType = ActionType.SHARE,
                            count = data.shareCta?.count
                        )
                    }

                    ActionType.DOWNLOAD -> {
                        buttonOrder += "${it.actionType}, "
                        val iconUrl = data.downloadCta?.icon
                        ImageButtonData(
                            id = it.order,
                            iconUrl = iconUrl,
                            actionType = ActionType.DOWNLOAD,
                            count = data.downloadCta?.count
                        )
                    }

                    ActionType.CTA -> {
                        data.cta?.let { cta ->
                            buttonOrder += "${it.actionType}, "
                            iscta = true
                            val text = cta.text
                            ctaName = text.orEmpty()
                            ImageButtonData(
                                id = it.order,
                                iconUrl = data.downloadIcon,
                                text = text,
                                actionType = ActionType.CTA,
                                btnColor = cta.backgroundColor
                            )
                        }
                    }
                }
            }
            buttonList?.let { buttons ->
                binding.llUserAction.removeAllViews()
                for (button in buttons) {
                    button?.let { buttonData ->
                        if (buttonData.actionType == ActionType.CTA) {
                            val cta = AppCompatButton(itemView.context).apply {
                                id = View.generateViewId() // Generate a unique ID for the view
                                setBackgroundDrawable(
                                    ContextCompat.getDrawable(
                                        binding.root.context,
                                        R.drawable.rounded_button
                                    )
                                )
                                (background as? GradientDrawable)?.setColor(
                                    android.graphics.Color.parseColor(
                                        buttonData.btnColor
                                    )
                                )
                                isAllCaps = false
                            }

                            val btnLayoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                5f // Weight of 1, assuming llUserAction's weight sum is 3
                            )
                            buttonData.text?.let { cta.text = it }
                            cta.layoutParams = btnLayoutParams
                            cta.setDebounceClickListener {
                                handleButtonClick(
                                    buttonData.actionType,
                                    data,
                                    it,
                                    ctaName,
                                    position
                                )
                            }
                            binding.llUserAction.addView(cta)
                        } else {
                            // Create a LinearLayout to hold the ImageButton and TextView
                            val container = LinearLayout(itemView.context).apply {
                                orientation = LinearLayout.VERTICAL
                                gravity = Gravity.CENTER
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1f
                                )
                            }

                            val btn = AppCompatImageButton(itemView.context)
                            btn.id = buttonData.id
                            btn.setBackgroundResource(0) // Removes the background
                            glide?.apply {
                                load(buttonData.iconUrl)
                                    .override(itemView.context.resources.getDimensionPixelSize(com.jar.app.core_ui.R.dimen.dimen_22dp))
                                    .into(btn)
                            }

                            btn.setDebounceClickListener {
                                handleButtonClick(
                                    buttonData.actionType,
                                    data,
                                    btn,
                                    ctaName,
                                    position
                                )
                            }
                            container.addView(btn)

                            // Add TextView below the ImageButton
                            buttonData.count?.let {
                                val countTextView = AppCompatTextView(itemView.context).apply {
                                    text = formatNumber(it.toFloat())
                                    gravity = Gravity.CENTER
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    tag = "countText_${buttonData.id}" // Assigning a unique tag
                                }
                                container.addView(countTextView)
                            }

                            binding.llUserAction.addView(container)
                        }
                    }

                }
            }
            data.uploadTime?.let {
                binding.expiryTime.text = it
            }
            binding.ivClose.setDebounceClickListener {
                handleCloseStory(
                    data.pageId,
                    position
                )
            }

            binding.ivMute.setOnClickListener {
                val isMuted = toggleMuteState()
                if (isMuted) {
                    binding.ivMute.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_volume_off
                        )
                    )
                } else {
                    binding.ivMute.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.ic_volume_on
                        )
                    )
                }
            }
        }

        private fun formatNumber(n: Float): String {
            return when {
                n <= 1000 -> String.format("%.0f", n)
                else -> String.format("%.1fk", n / 1000)
            }
        }

        private fun handleButtonClick(
            actionType: ActionType,
            data: Page,
            btn: View,
            ctaName: String,
            position: Int
        ) {
            val countTextView =
                binding.llUserAction.findViewWithTag<AppCompatTextView>("countText_${btn.id}")
            val newCount = getNewCount(actionType, data)
            countTextView?.let {
                countTextView.text = formatNumber(newCount)
            }

            when (actionType) {
                ActionType.LIKE -> {
                    val iconUrl =
                        if (isLiked) data.likeCta?.unlikedIcon else data.likeCta?.likedIcon
                    glide?.apply {
                        load(iconUrl)
                            .override(itemView.context.resources.getDimensionPixelSize(com.jar.app.core_ui.R.dimen.dimen_22dp))
                            .into(btn as AppCompatImageButton)
                    }
                    isLiked = isLiked.not()
                    handleLikeClicked(
                        data.pageId,
                        isLiked,
                        position
                    )
                }

                ActionType.SHARE -> {
                    shareThePage(
                        data.pageId,
                        if(data.mediaType == MediaType.IMAGE.value) data.mediaUrl.orEmpty() else data.videoThumbnailUrl,
                        position
                    )
                }

                ActionType.DOWNLOAD -> {
                    data.mediaUrl?.let {
                        handleDownloadClicked(
                            it,
                            data.mediaType,
                            data.pageId,
                            position
                        )
                    }
                }

                ActionType.CTA -> {
                    data.cta?.let {
                        handleCtaClicked(
                            it,
                            data.pageId,
                            position
                        )
                    }
                }
            }
        }

        private fun getNewCount(
            actionType: ActionType,
            data: Page,
        ): Float {
            val increment = 1f
            return when (actionType) {
                ActionType.LIKE -> {
                    val currentCount = data.likeCta?.count ?: 0
                    if (isLiked.not()) {
                        val newCount = ceil((currentCount) + increment)
                        data.likeCta?.count = newCount.toInt()
                        newCount
                    } else {
                        val newCount = ceil((currentCount) - increment)
                        data.likeCta?.count = newCount.toInt()
                        newCount
                    }
                }

                ActionType.SHARE -> {
                    val currentCount = data.shareCta?.count ?: 0
                    if (data.shareCta?.is_shared == null || data.shareCta?.is_shared == false) {
                        data.shareCta?.is_shared = true
                        return ceil(currentCount + increment)
                    }else{
                        currentCount.toFloat()
                    }
                }
                ActionType.DOWNLOAD -> {
                    val currentCount = data.downloadCta?.count ?: 0
                    if (data.downloadCta?.is_downloaded == null || data.downloadCta?.is_downloaded == false) {
                        data.shareCta?.is_downloaded = true
                        return ceil(currentCount + increment)
                    }else{
                        currentCount.toFloat()
                    }
                }
               else -> {
                   val currentCount = data.likeCta?.count ?: 0
                   ceil(currentCount + increment)
               }
            }
        }

        private fun getCurrentCount(countTextView: AppCompatTextView?): Float {
            val currentText = countTextView?.text?.toString() ?: return 0f

            return when {
                currentText.contains("k") -> parseKFormat(currentText)
                else -> currentText.toFloatOrNull() ?: 0f
            }
        }

        private fun parseKFormat(input: String): Float {
            val regex = """(\d+)(?:\.(\d+))?k""".toRegex()
            val matchResult = regex.matchEntire(input)

            return matchResult?.let { match ->
                val thousandPart = match.groups[1]?.value?.toFloatOrZero().orZero()
                val decimalPart = match.groups[2]?.value?.toFloatOrZero().orZero()
                thousandPart * 1000 + decimalPart * 100
            } ?: 0f
        }


        fun clearGlide() {
            glide?.clear(customTarget)
            glide?.clear(clParentCustomTarget)
        }
    }

    fun getPlayerViewAt(position: Int) = playerViewMap[position]
}

