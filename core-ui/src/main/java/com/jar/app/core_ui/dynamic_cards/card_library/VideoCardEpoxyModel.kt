package com.jar.app.core_ui.dynamic_cards.card_library

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.dp
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.Infographic
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.DynamicCardsCellVideoCardBinding
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference

class VideoCardEpoxyModel(
    private val exoPlayer: ExoPlayer,
    private val datasourceFactory: CacheDataSource.Factory,
    private val scope: CoroutineScope?,
    private val libraryCardViewData: LibraryCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
) : CustomViewBindingEpoxyModel<DynamicCardsCellVideoCardBinding>(R.layout.dynamic_cards_cell_video_card) {

    private var timerJob: Job? = null

    private var binding: DynamicCardsCellVideoCardBinding? = null
    private var infographic: Infographic? = null
    private var hasStartedVideo = false

    private val eventData by lazy {
        CardEventData(
            map = mutableMapOf(
                DynamicCardEventKey.CardType to libraryCardViewData.cardType,
                DynamicCardEventKey.FeatureType to libraryCardViewData.featureType,
                DynamicCardEventKey.Data to libraryCardViewData.state.toString(),
                DynamicCardEventKey.CardVerticalPosition to libraryCardViewData.verticalPosition.toString(),
                DynamicCardEventKey.CardHorizontalPosition to libraryCardViewData.horizontalPosition.toString(),
                DynamicCardEventKey.CardTitle to libraryCardViewData.cardMeta?.title?.textList?.getOrNull(0)?.toString().orEmpty(),
                DynamicCardEventKey.CardDescription to libraryCardViewData.cardMeta?.description?.textList?.getOrNull(0)?.toString().orEmpty(),
                DynamicCardEventKey.TopLabelTitle to libraryCardViewData.cardMeta?.labelTop?.text?.convertToRawString().toString(),
                DynamicCardEventKey.BottomLabelTitle to libraryCardViewData.cardMeta?.labelBottom?.text?.convertToRawString().toString(),
            ),
            fromSection = libraryCardViewData.header?.convertToRawString(),
            fromCard = libraryCardViewData.featureType
        )
    }

    override fun bindItem(binding: DynamicCardsCellVideoCardBinding) {
        this.binding = binding
        binding.root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
        binding.btnAction.setDebounceClickListener {
            val data = PrimaryActionData(
                type = PrimaryActionType.DEEPLINK,
                value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
                order = libraryCardViewData.getSortKey(),
                cardType = libraryCardViewData.getCardType(),
                featureType = libraryCardViewData.featureType
            )
            onActionClick.invoke(data, eventData)
        }

        binding.tvReplay.setDebounceClickListener {
            exoPlayer.seekTo(0)
            exoPlayer.play()
        }

        val background = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor(libraryCardViewData.cardMeta?.cardBackground?.startColor),
                Color.parseColor(libraryCardViewData.cardMeta?.cardBackground?.endColor),
            )
        )
        background.cornerRadius = libraryCardViewData.cardMeta?.cardBackground?.getCornerRadius()?.dp.orZero()
        binding.clPlayer.background = background

        Glide.with(binding.root)
            .load(libraryCardViewData.cardMeta?.infographic?.thumbnail)
            .into(binding.ivThumbnail)

        val contextRef = WeakReference(binding.root.context)
        libraryCardViewData.cardMeta?.cta?.text?.convertToString(contextRef)
            ?.let { binding.btnAction.setText(it) }

        when (libraryCardViewData.cardMeta?.infographic?.getInfographicType()) {
            InfographicType.VIDEO -> infographic = libraryCardViewData.cardMeta?.infographic
            else -> {}
        }
    }

    private fun startVideo() {
        onCardShown.invoke(eventData)
        starVideoPlayBack()
        timerJob?.cancel()
        timerJob = scope?.doRepeatingTask(1000) {
            val timLeft = exoPlayer.duration - exoPlayer.currentPosition
            if (timLeft <= 0) {
                binding?.tvTimeLeft?.visibility = View.INVISIBLE
                binding?.tvReplay?.visibility = View.VISIBLE
            } else {
                binding?.tvTimeLeft?.text = timLeft.milliSecondsToCountDown()
                if (binding?.tvTimeLeft?.isVisible?.not().orFalse()) {
                    binding?.tvTimeLeft?.visibility = View.VISIBLE
                    binding?.tvReplay?.visibility = View.INVISIBLE
                }
            }
        }
        if (binding?.playerView?.isVisible?.not().orFalse()) {
            binding?.playerView?.isVisible = true
            binding?.ivThumbnail?.isVisible = false
        }
    }

    private fun starVideoPlayBack() {
        if (binding == null || infographic == null) return
        exoPlayer.clearMediaItems()
        binding?.playerView?.player = exoPlayer
        binding?.playerView?.setShutterBackgroundColor(Color.TRANSPARENT)
        val mediaItem = MediaItem.fromUri(infographic!!.url)
        val mediaSource =
            ProgressiveMediaSource.Factory(datasourceFactory).createMediaSource(mediaItem)
        exoPlayer.setMediaSource(mediaSource, true)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun stopVideo() {
        timerJob?.cancel()
        binding?.playerView?.isVisible = false
        binding?.tvTimeLeft?.isVisible = false
        binding?.tvReplay?.isVisible = false
        binding?.ivThumbnail?.isVisible = true
        binding?.playerView?.player = null
        hasStartedVideo = false
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            if (hasStartedVideo.not()) {
                hasStartedVideo = true
                startVideo()
            }
        }
    }

    override fun onViewDetachedFromWindow(view: View) {
        super.onViewDetachedFromWindow(view)
        stopVideo()
    }

    override fun getBinding(view: View): DynamicCardsCellVideoCardBinding {
        return DynamicCardsCellVideoCardBinding.bind(view)
    }
}