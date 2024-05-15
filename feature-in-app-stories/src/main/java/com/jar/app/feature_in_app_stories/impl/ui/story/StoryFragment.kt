package com.jar.app.feature_in_app_stories.impl.ui.story

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.StreamKey
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.ExoDatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.hls.offline.HlsDownloader
import androidx.media3.exoplayer.source.MediaSource
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.downloader.DownloadHelper
import com.jar.app.base.util.shareOnWhatsapp
import com.jar.app.base.util.sound.SoundType
import com.jar.app.base.util.sound.SoundUtil
import com.jar.app.base.util.toLongOrZero
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_utils.data.FileUtils
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.core_utils.data.WhatsAppUtil
import com.jar.app.feature_in_app_stories.R
import com.jar.app.feature_in_app_stories.databinding.FragmentStoryMainBinding
import com.jar.app.feature_in_app_stories.impl.domain.DeepLinkType
import com.jar.app.feature_in_app_stories.impl.domain.event.HandleExternalLinkEvent
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.CTA
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.CTAName
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.Clicked_Storypage
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.ContentId
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.HeadingCopy
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.InteractionType
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.JarStory
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.Liked
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.No
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.PageNumber
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.PublishTime
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.Shared
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.Shown_StoryPage
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.StoryId
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.StoryName
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.SwipeDown
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.UserSegment
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.button_order
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.button_type
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.from
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.homepage
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.link
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.timeSpent
import com.jar.app.feature_in_app_stories.impl.uitl.InAppStoryAnalyticsConstants.timeToLoad
import com.jar.app.feature_in_app_stories.impl.uitl.WatermarkUtil
import com.jar.app.feature_in_app_stories.service.SaveAndAddWaterMarkImageService
import com.jar.app.feature_story.data.model.ActionOrder
import com.jar.app.feature_story.data.model.InAppStoryModel
import com.jar.app.feature_story.data.model.MediaType
import com.jar.app.feature_story.data.model.Page
import com.jar.app.feature_story.ui.UserAction
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.greenrobot.eventbus.EventBus
import so.plotline.insights.Plotline
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs


@UnstableApi
@AndroidEntryPoint
class StoryFragment : BaseFragment<FragmentStoryMainBinding>() {
    companion object {
        const val STORY_DURATION_MILLIS = 4_000L
        const val COUNTDOWN_INTERVAL_MILLIS = 1000L
        private const val CACHE_SIZE = 50 * 1024 * 1024L
        private var cacheInstance: Cache? = null
        private const val PRE_CACHE_SIZE = 5 * 1024 * 1024L
        private const val TAG = "StoryFragment"
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var networkFlow: NetworkFlow

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var deviceUtils: WhatsAppUtil

    private var soundUtil: SoundUtil? = null
    private val watermarkUtil: WatermarkUtil by lazy { WatermarkUtil(requireContext()) }

    private var adapter: StoryAdapter? = null
    private var currentPosition = 0
    private var pausedPlayTime: Long = 0
    private var totalCount: Int? = null
    private var countDownJob: Job? = null
    private var timeLeft = 0L
    private var isStoryPaused = false
    private var currentTime = System.currentTimeMillis()
    private var progressBars = ArrayList<ProgressBar>()
    private var currentAnimation: ObjectAnimator? = null
    private var gestureDetector: GestureDetector? = null
    private val viewModelProvider: StoryViewModelAndroid by viewModels()
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private val args by navArgs<StoryFragmentArgs>()
    private var isInternetConnectedFromStarting = false
    private var isInternetConnected = false
    private var mediaUrl: String? = null
    private var player: SimpleExoPlayer? = null
    private var downloadCache: Cache? = null
    private var isMuted: Boolean = false
    private var videoLoadStartTime: Long = 0
    var currentVolume: Float? = null
    var onPauseTime: Long = 0
    private lateinit var weakReference: WeakReference<Context>
    private val sharingMessage by lazy {
        context?.getString(R.string.feature_in_app_story_sharing_text)
    }

    private val cacheStreamKeys = arrayListOf(
        StreamKey(0, 1),
        StreamKey(1, 1),
        StreamKey(2, 1),
        StreamKey(3, 1),
        StreamKey(4, 1)
    )
    private val uri by lazy {
        viewModel.inAppStoryData?.pages
            ?.firstOrNull { it.mediaType == MediaType.VIDEO.value }
            ?.let { Uri.parse(it.mediaUrl) }
            ?: Uri.EMPTY
    }
    val mediaItem by lazy {
        uri?.let {
            MediaItem.Builder()
                .setUri(uri)
                .setStreamKeys(cacheStreamKeys)
                .build()
        }

    }

    val cache by lazy {
        return@lazy cacheInstance ?: run {
            val exoCacheDir = File("${requireContext().cacheDir.absolutePath}/exo")
            val evictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)
            SimpleCache(exoCacheDir, evictor, ExoDatabaseProvider(requireContext())).also {
                cacheInstance = it
            }
        }
    }

    val upstreamDataSourceFactory by lazy { DefaultDataSourceFactory(requireContext(), "Android") }

    val cacheDataSourceFactory by lazy {
        val cacheSink = CacheDataSink.Factory().setFragmentSize(CacheDataSink.DEFAULT_FRAGMENT_SIZE)
            .setCache(cache)
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
            .setCacheWriteDataSinkFactory(cacheSink)
            .setCacheReadDataSourceFactory(FileDataSource.Factory())
            .setEventListener(object : CacheDataSource.EventListener {
                override fun onCachedBytesRead(cacheSizeBytes: Long, cachedBytesRead: Long) {}

                override fun onCacheIgnored(reason: Int) {}
            })
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
            if (isGranted) {
                startDownload(mediaUrl!!)
            }
        }
    private val thumbnailUrl = "https://cdn.myjar.app/Jar_Stories/Inapp_bottom_watermark.png"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.storyId == null) {
            getStoryFromLocalData()
        }
    }

    private fun getStoryFromLocalData() {


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                //TODO @Prasenjit to be changed to local db implementation later on when the db is finalised for shared data
                val content = fileUtils.restoreContentFromFile("story_data.json")
                if (content.isNullOrBlank().not()) {
                    val data: InAppStoryModel? =
                        content?.let { Json.decodeFromString<InAppStoryModel>(it) }
                    data?.let {
                        dismissLoadingView()
                        setupStoryView(it)
                    }
                } else {
                    dismissLoadingView()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausedPlayTime = currentAnimation?.currentPlayTime ?: 0L
        viewModel.savedCurrentPosition = currentPosition
        onPauseTime = System.currentTimeMillis()

        if (currentPosition <= viewModel.inAppStoryData?.pages?.size.orZero() - 1) {
            if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.VIDEO.value) {
                pausePlayer()
            }
            pauseSlide()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weakReference = WeakReference<Context>(requireContext())
        binding.ivClose.setOnClickListener {
            popBackStack()
        }
        gestureDetector = GestureDetector(requireActivity(), object : SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                isLongPressed = true
                pausePlayer()
                pauseSlide()
            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val deltaY = e2.y - e1.y
                val deltaX = e2.x - e1.x
                if (abs(deltaY) > abs(deltaX) && deltaY > 0) {
                    val currentPage = viewModel.inAppStoryData?.pages?.get(currentPosition)
                    analyticsHandler.postEvent(
                        Clicked_Storypage,
                        mapOf(
                            InteractionType to SwipeDown,
                            button_order to "${adapter?.buttonOrder}",
                            timeSpent to getTimeSpent(),
                            Shared to No,
                            PageNumber to "${adapter?.ctaName}",
                            UserSegment to "${viewModel.inAppStoryData?.userSegmentIds?.isNotBlank()}",
                            CTA to "${adapter?.iscta}",
                            CTAName to "${adapter?.ctaName}",
                            PublishTime to "${adapter?.duration}",
                            InAppStoryAnalyticsConstants.MediaType to "${currentPage?.mediaType}",
                            StoryId to "${viewModel.inAppStoryData?.storyId}",
                            StoryName to "${viewModel.inAppStoryData?.storyName}",
                            ContentId to "${currentPage?.contentId}",
                            InAppStoryAnalyticsConstants.CategoryType to currentPage?.categories?.joinToString(
                                ","
                            ).orEmpty(),
                            HeadingCopy to JarStory,
                        )
                    )
                    popBackStack()
                    return true
                }
                return false
            }
        })
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkFlow.networkStatus.collectLatest {
                    toggleMessageLayout(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.inAppStoryFlow.collect(
                    onLoading = {},
                    onSuccess = { appStoryModel ->
                        viewModel.inAppStoryData = appStoryModel
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            appStoryModel.pages?.firstOrNull() { it.mediaType == MediaType.VIDEO.value }?.mediaUrl?.let { link ->
                                preCacheVideo(
                                    link
                                )
                            }
                        }
                        setupStoryView(appStoryModel)
                    },
                    onError = { error, errorCode ->
                        binding.groupStories.isVisible = true
                    }
                )
            }
        }

    }

    private fun dismissLoadingView() {
        binding.messageLoading.isVisible = false
        binding.groupStoryExpired.isVisible = false
        binding.ivJarErrorLogo.isVisible = false
        binding.shimmerLayout.isVisible = false
        binding.shimmerLayout.stopShimmer()
    }

    private fun setupStoryView(appStoryModel: InAppStoryModel) {
        binding.messageLoading.isVisible = false
        binding.groupStoryExpired.isVisible = false
        binding.ivJarErrorLogo.isVisible = false
        binding.shimmerLayout.isVisible = false
        binding.shimmerLayout.stopShimmer()
        if (appStoryModel.pages?.size.orZero() > 0 || (args.storyId != null && appStoryModel.pages?.size.orZero() == 1)) {
            binding.groupStories.isVisible = true
            binding.llErrorLayout.isVisible = false
            viewModel.inAppStoryData = appStoryModel
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    appStoryModel.pages?.firstOrNull { it.mediaType == MediaType.VIDEO.value }?.mediaUrl?.let { link ->
                        preCacheVideo(
                            link
                        )
                    }
                }
            }
            appStoryModel.pages?.size?.let {
                setupProgressLayout(it)
                setUpViewPager(it)
                totalCount = it
            }
            adapter?.submitList(appStoryModel.pages)
            startStoryView()
        } else if (args.storyId != null && (appStoryModel.pages?.size.orZero() == 1 || appStoryModel.pages?.size.orZero() == 0)) {
            binding.groupStories.isVisible = false
            binding.llErrorLayout.isVisible = true
            binding.errorImage.isVisible = false
            binding.errorMessage.text =
                "The story you were looking for has expired.."
            binding.groupStoryExpired.isVisible = true
        } else {
            binding.groupStories.isVisible = false
            binding.llErrorLayout.isVisible = true
            binding.errorImage.setImageResource(R.drawable.be_back_soon)
            binding.errorMessage.text =
                requireContext().resources.getString(R.string.no_story_available)
            binding.groupStoryExpired.isVisible = true

        }
    }


    private var isAlreadyLoaded = false
    private fun toggleMessageLayout(isInternetConnected: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                binding.message.isVisible = !isInternetConnected
                isInternetConnectedFromStarting = true
                if (viewModel.inAppStoryData == null && isAlreadyLoaded.not()) {
                    viewModel.fetchStories(args.storyId)
                    isAlreadyLoaded = true
                }
            }
        }
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStoryMainBinding
        get() = FragmentStoryMainBinding::inflate


    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeData()
        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        soundUtil = context?.let { SoundUtil(WeakReference(it), false) }
    }

    private fun startStoryView() {
        currentPosition = viewModel.savedCurrentPosition

        setStoryData()
    }

    private fun showStoryScreen() {
        binding.groupStories.visibility = View.VISIBLE
    }

    private fun releasePlayer() {
        player?.let { player ->
            /*playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady*/
            //  player.removeListener(playbackStateListener)
            player.stop()
            player.release()
        }
        player = null
    }

    private fun setUpViewPager(totalCount: Int) {
        //disable swiping
        binding.vpGuide.isUserInputEnabled = false
        player = SimpleExoPlayer.Builder(requireContext()).build()
        currentVolume = if (player!!.volume > 0) player!!.volume else 1f
        adapter = StoryAdapter(
            this::pauseSlide,
            this::resumeSlide,
            handleLikeClicked = { pageId,
                                  isLiked,
                                  position ->
                viewModel.updateUserAction(UserAction.LIKE.value, isLiked, pageId, null)

                val currentPage = viewModel.inAppStoryData?.pages?.get(position)
                val buttonOrder = actionOrderToString(currentPage?.actionOrders, currentPage)
                val cta = containsStringInActionType(currentPage, "CTA")
                val ctaName = if (cta) {
                    currentPage?.cta?.text
                } else ""
                val publishTime = currentPage?.uploadTime
                val mediaType = currentPage?.mediaType
                val headingCopy = "Jar-story"
                val storyId = viewModel.inAppStoryData?.storyId
                val storyName = viewModel.inAppStoryData?.storyName
                val contentId = currentPage?.contentId
                val userSegment = viewModel.inAppStoryData?.userSegmentIds?.isNotBlank()


                analyticsHandler.postEvent(
                    Clicked_Storypage,
                    mapOf(
                        button_type to if (isLiked) "like" else "unlike",
                        button_order to buttonOrder,
                        timeSpent to getTimeSpent(),
                        Liked to "$isLiked",
                        Shared to "No",
                        PageNumber to "$position",
                        UserSegment to "$userSegment",
                        CTA to "$cta",
                        CTAName to "$ctaName",
                        PublishTime to "$publishTime",
                        InAppStoryAnalyticsConstants.MediaType to "$mediaType",
                        StoryId to "$storyId",
                        StoryName to "$storyName",
                        ContentId to "$contentId",
                        HeadingCopy to headingCopy,
                        InAppStoryAnalyticsConstants.CategoryType to currentPage?.categories?.joinToString(
                            ","
                        ).orEmpty()
                    )
                )
            },
            handleDownloadClicked = { mediaUrl, mediaType, pageId,
                                      position ->

                viewModel.updateUserAction(UserAction.DOWNLOAD.value, true, pageId, null)
                val currentPage = viewModel.inAppStoryData?.pages?.get(position)
                val buttonOrder = actionOrderToString(currentPage?.actionOrders, currentPage)
                val cta = containsStringInActionType(currentPage, "CTA")
                val ctaName = if (cta) {
                    currentPage?.cta?.text
                } else ""
                val isLiked = currentPage?.likeCta?.isLiked
                val publishTime = currentPage?.uploadTime
                val headingCopy = "Jar-story"
                val storyId = viewModel.inAppStoryData?.storyId
                val storyName = viewModel.inAppStoryData?.storyName
                val contentId = currentPage?.contentId
                val userSegment = viewModel.inAppStoryData?.userSegmentIds?.isNotBlank()
                val downloadVideoUrl: String? =
                    viewModel.inAppStoryData?.pages?.get(position)?.downloadVideoUrl


                analyticsHandler.postEvent(
                    Clicked_Storypage,
                    mapOf(
                        button_type to "download",
                        button_order to buttonOrder,
                        timeSpent to getTimeSpent(),
                        Liked to "$isLiked",
                        Shared to "No",
                        PageNumber to "$position",
                        UserSegment to "$userSegment",
                        CTA to "$cta",
                        CTAName to "$ctaName",
                        PublishTime to "$publishTime",
                        InAppStoryAnalyticsConstants.MediaType to mediaType,
                        StoryId to "$storyId",
                        StoryName to "$storyName",
                        ContentId to "$contentId",
                        HeadingCopy to headingCopy,
                        InAppStoryAnalyticsConstants.CategoryType to currentPage?.categories?.joinToString(
                            ","
                        ).orEmpty()
                    )
                )
                this.mediaUrl = mediaUrl
                if (mediaType == MediaType.IMAGE.value) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Scoped Storage is enforced
                        startDownload(mediaUrl)
                    } else {
                        // Scoped Storage is not enforced
                        requestPermissionsLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    }
                } else {
                    downloadVideoUrl?.let {
                        startVideoDownload(it)
                    }

                }
            },
            shareThePage = { pageId, image, position ->
                viewModel.updateUserAction(UserAction.SHARE.value, true, pageId, null)
                val currentPage =
                    viewModel.inAppStoryData?.pages?.get(position)//findPageById(pageId)
                val buttonOrder = actionOrderToString(currentPage?.actionOrders, currentPage)
                val cta = containsStringInActionType(currentPage, "CTA")
                val ctaName = if (cta) {
                    currentPage?.cta?.text
                } else ""
                val isLiked = currentPage?.likeCta?.isLiked
                val publishTime = currentPage?.uploadTime
                val mediaType = currentPage?.mediaType
                val headingCopy = "Jar-story"
                val storyId = viewModel.inAppStoryData?.storyId
                val storyName = viewModel.inAppStoryData?.storyName
                val contentId = currentPage?.contentId
                val userSegment = viewModel.inAppStoryData?.userSegmentIds?.isNotBlank()
                val shareLink = currentPage?.shareCta?.link


                analyticsHandler.postEvent(
                    Clicked_Storypage,
                    mapOf(
                        button_type to "share",
                        button_order to buttonOrder,
                        timeSpent to getTimeSpent(),
                        Liked to "$isLiked",
                        Shared to "Yes",
                        PageNumber to "$position",
                        UserSegment to "$userSegment",
                        CTA to "$cta",
                        CTAName to "$ctaName",
                        PublishTime to "$publishTime",
                        InAppStoryAnalyticsConstants.MediaType to "$mediaType",
                        StoryId to "$storyId",
                        StoryName to "$storyName",
                        ContentId to "$contentId",
                        HeadingCopy to headingCopy,
                        InAppStoryAnalyticsConstants.CategoryType to currentPage?.categories?.joinToString(
                            ","
                        ).orEmpty()
                    )
                )
                shareLink?.let { link ->
                    image?.let {
                        shareImage(image = image, shareLink = link)
                    } ?: kotlin.run {
                        shareImage(false,
                            weakReference = weakReference,
                            "$sharingMessage $shareLink"
                        )
                    }
                }
            },
            handleCtaClicked = { cta, pageId, position ->
                viewModel.updateUserAction(UserAction.CTA.value, true, pageId, null)
                val currentPage = viewModel.inAppStoryData?.pages?.get(position)
                val buttonOrder = actionOrderToString(currentPage?.actionOrders, currentPage)
                val iscta = containsStringInActionType(currentPage, "CTA")
                val ctaName = if (iscta) {
                    currentPage?.cta?.text
                } else ""
                val isLiked = currentPage?.likeCta?.isLiked
                val publishTime = currentPage?.uploadTime
                val mediaType = currentPage?.mediaType
                val headingCopy = "Jar-story"
                val storyId = viewModel.inAppStoryData?.storyId
                val storyName = viewModel.inAppStoryData?.storyName
                val contentId = currentPage?.contentId
                val userSegment = viewModel.inAppStoryData?.userSegmentIds?.isNotBlank()


                analyticsHandler.postEvent(
                    Clicked_Storypage,
                    mapOf(
                        button_type to "cta",
                        button_order to buttonOrder,
                        timeSpent to getTimeSpent(),
                        Liked to "$isLiked",
                        Shared to "No",
                        PageNumber to "$position",
                        UserSegment to "$userSegment",
                        CTA to "$iscta",
                        CTAName to "$ctaName",
                        PublishTime to "$publishTime",
                        InAppStoryAnalyticsConstants.MediaType to "$mediaType",
                        StoryId to "$storyId",
                        StoryName to "$storyName",
                        ContentId to "$contentId",
                        HeadingCopy to headingCopy,
                        InAppStoryAnalyticsConstants.CategoryType to currentPage?.categories?.joinToString(
                            ","
                        ).orEmpty()
                    )
                )
                if (cta.type.orEmpty() == DeepLinkType.INTERNAL.value) {
                    cta.link?.let { it1 ->
                        EventBus.getDefault().post(HandleDeepLinkEvent(it1))
                    }
                } else {
                    EventBus.getDefault()
                        .post(cta.link?.let { it1 -> HandleExternalLinkEvent(it1) })
                }
            },
            handleCloseStory = { pageId, position ->
                val currentPage = viewModel.inAppStoryData?.pages?.get(position)
                val buttonOrder = actionOrderToString(currentPage?.actionOrders, currentPage)
                val cta = containsStringInActionType(currentPage, "CTA")
                val ctaName = if (cta) {
                    currentPage?.cta?.text
                } else ""
                val isLiked = currentPage?.likeCta?.isLiked
                val publishTime = currentPage?.uploadTime
                val mediaType = currentPage?.mediaType
                val headingCopy = "Jar-story"
                val storyId = viewModel.inAppStoryData?.storyId
                val storyName = viewModel.inAppStoryData?.storyName
                val contentId = currentPage?.contentId
                val userSegment = viewModel.inAppStoryData?.userSegmentIds?.isNotBlank()


                analyticsHandler.postEvent(
                    Clicked_Storypage,
                    mapOf(
                        button_type to "close",
                        button_order to buttonOrder,
                        timeSpent to getTimeSpent(),
                        Liked to "$isLiked",
                        Shared to "No",
                        PageNumber to "$position",
                        UserSegment to "$userSegment",
                        CTA to "$cta",
                        CTAName to "$ctaName",
                        PublishTime to "$publishTime",
                        InAppStoryAnalyticsConstants.MediaType to "$mediaType",
                        StoryId to "$storyId",
                        StoryName to "$storyName",
                        ContentId to "$contentId",
                        HeadingCopy to headingCopy,
                        InAppStoryAnalyticsConstants.CategoryType to currentPage?.categories?.joinToString(
                            ","
                        ).orEmpty()
                    )
                )
                popBackStack()
            },
            isInternetConnect = {
                isInternetConnected
            },
            hideNavigationView = {
                binding.btnNext.isVisible = false
                binding.btnPrev.isVisible = false
            },
            toggleMuteState = {
                if (isMuted) {
                    isMuted = false
                    unMute()
                } else {
                    isMuted = true
                    mute()
                }
                isMuted
            }
        ) {
            binding.btnNext.isVisible = true
            binding.btnPrev.isVisible = true
        }
        binding.vpGuide.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val playerView = adapter?.getPlayerViewAt(position)
                if (viewModel.inAppStoryData?.pages?.get(position)?.mediaType == MediaType.VIDEO.value) {
                    soundUtil?.stopCurrentTrack()
                    playerView?.let {
                        videoLoadStartTime = System.currentTimeMillis()
                        initializePlayer(
                            playerView,
                            viewModel.inAppStoryData?.pages?.get(position)?.mediaUrl.orEmpty()
                        )
                    }
                } else {
                    dismissProgressBar()
                    viewModel.inAppStoryData?.pages?.get(position)?.audioUrl?.let {
                        playSound(soundUrl = it)
                    } ?: run {
                        soundUtil?.stopCurrentTrack()
                    }

                }
            }
        })
        binding.vpGuide.adapter = adapter
        if (totalCount > 0) {
            binding.vpGuide.offscreenPageLimit = totalCount
        }


        try {
            (binding.vpGuide.getChildAt(0) as? RecyclerView)?.let {
                it.layoutManager?.isItemPrefetchEnabled = false
                it.isNestedScrollingEnabled = false
                it.overScrollMode = View.OVER_SCROLL_NEVER
            }
        } catch (_: Exception) {
        }
    }

    private fun unMute() {
        player?.volume = currentVolume ?: 1f
    }

    private fun mute() {
        player?.volume = 0f
    }

    private fun startVideoDownload(mediaUrl: String) {
        DownloadHelper(requireContext()).downloadVideoFile(mediaUrl)
    }

    private fun initializePlayer(playerView: SurfaceView, url: String) {

        val uri = Uri.parse(url)
        val mediaItem =
            MediaItem.Builder()
                .setUri(uri)
                .setStreamKeys(cacheStreamKeys)
                .build()
        player?.also { exoPlayer ->
            exoPlayer.setVideoSurfaceView(playerView)
            val hlsMediaSource: MediaSource = HlsMediaSource.Factory(cacheDataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(mediaItem)
            exoPlayer.playWhenReady = true

            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        binding.ivReload.isVisible = false
                        dismissProgressBar()
                        if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.VIDEO.value) {
                            if (player?.duration.orZero() > 0) {
                                if(viewModel.inAppStoryData?.pages?.get(currentPosition)?.timeToLoadMedia == null) {
                                    viewModel.inAppStoryData?.pages?.get(currentPosition)?.timeToLoadMedia =
                                        System.currentTimeMillis() - videoLoadStartTime
                                }
                                startAnimation()
                                resumeSlide()
                            }
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    binding.ivReload.isVisible = true
                    binding.ivReload.setDebounceClickListener {
                        initializePlayer(
                            playerView,
                            viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaUrl.orEmpty()
                        )
                    }

                }
            })
            exoPlayer.prepare(hlsMediaSource)
        }
    }

    private fun getDownloadCache(): Cache {
        if (downloadCache == null) {
            downloadCache =
                SimpleCache(
                    requireContext().cacheDir,
                    LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024),
                    StandaloneDatabaseProvider(requireContext())
                )
        }
        return downloadCache!!
    }

    private fun findPageById(pageId: String): Page? {
        return viewModel.inAppStoryData?.pages?.find { it.pageId == pageId }
    }

    private fun actionOrderToString(actionOrders: List<ActionOrder>?, page: Page?): String {
        return actionOrders?.sortedBy { it.order }?.joinToString(separator = "-") {
            if (it.actionType == "Cta" && page?.cta == null) {
                ""
            } else {
                it.actionType
            }
        }
            ?: ""
    }

    private fun containsStringInActionType(page: Page?, targetString: String): Boolean {
        return page?.cta != null
    }

    private fun startDownload(mediaUrl: String) {
        val intent = Intent(requireContext(), SaveAndAddWaterMarkImageService::class.java)
        intent.putExtra("originalImageUrl", mediaUrl)
        intent.putExtra(
            "watermarkImageUrl",
            "https://cdn.myjar.app/Jar_Stories/Inapp_bottom_watermark.png"
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        //Don't make it debounce click listener
        binding.btnPrev.setOnClickListener {
            moveToPreviousSlide()
        }


        //Don't make it debounce click listener
        binding.btnNext.setOnClickListener {
            moveToNextSlide()
        }
        binding.btnPrev.setOnTouchListener(onTouchListener)
        binding.btnNext.setOnTouchListener(onTouchListener)
        binding.draggableContainer.setOnTouchListener(onTouchListener)

        // binding.btnPause.setOnTouchListener(onTouchListener)
    }

    private var isLongPressed = false

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { v, event ->
        gestureDetector?.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isLongPressed = false
                return@OnTouchListener true
            }

            MotionEvent.ACTION_UP -> {
                resumeSlide()

                if (!isLongPressed) {
                    when (v.id) {
                        R.id.btnPrev -> moveToPreviousSlide()
                        R.id.btnNext -> moveToNextSlide()
                    }
                }
                return@OnTouchListener true
            }
        }
        false
    }


    private fun setStoryData() {
        showStoryScreen()
        timeLeft =
            if (viewModel.inAppStoryData?.pages?.getOrNull(currentPosition)?.mediaType == MediaType.IMAGE.value) getCurrentStoryPlayDuration() else player?.currentPosition.orZero()
        binding.vpGuide.setCurrentItem(currentPosition, false)
        if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.IMAGE.value) {
            startTimer()
            startAnimation()
        }
    }

    private fun setupProgressLayout(totalCount: Int) {
        progressBars.clear()
        binding.llProgressBar.removeAllViews()
        val progressBarLayoutParam =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        val spaceLayoutParam = LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.WRAP_CONTENT)
        val progressColor =
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)
        repeat(totalCount) {
            val progressBar =
                ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal)
            progressBar.layoutParams = progressBarLayoutParam
            progressBar.progressDrawable.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    progressColor,
                    BlendModeCompat.SRC_ATOP
                )
            progressBar.max = 100
            progressBars.add(progressBar)
            binding.llProgressBar.addView(progressBar)
            if (it + 1 < totalCount)
                binding.llProgressBar.addView(getSpacer(spaceLayoutParam))
        }
    }

    private fun getSpacer(layoutParams: LinearLayout.LayoutParams): View {
        val space = View(context)
        space.layoutParams = layoutParams
        return space
    }

    private var pausedPercentage: Float = 0f

    private fun pauseSlide() {
        binding.llProgressBar.isVisible = false
        isStoryPaused = true
        currentAnimation?.let {
            pausedPercentage =
                if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.IMAGE.value) {

                    it.currentPlayTime.toFloat() / it.duration
                } else {
                    player?.currentPosition?.toFloat().orZero() / player?.contentDuration.orZero()
                }
        }
        countDownJob?.cancel()
        if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.IMAGE.value) {

            currentAnimation?.pause()
        }

        soundUtil?.pauseTrack()
    }

    fun pausePlayer() {
        player?.pause()
        //playbackPosition = player?.currentPosition ?: player?.duration.orZero()
    }

    private fun resumeSlide() {
        if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && binding != null) {
            binding.llProgressBar.isVisible = true
            isStoryPaused = false
            val currentMediaType = viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType
            if (currentMediaType == MediaType.VIDEO.value) {
                if ((player?.duration.orZero()) > 0) {
                    timeLeft = (getCurrentStoryPlayDuration() * (1 - pausedPercentage)).toLong()
                    startTimer()
                    player?.play()
                    currentAnimation?.resume()
                }
            } else {
                timeLeft = (getCurrentStoryPlayDuration() * (1 - pausedPercentage)).toLong()
                startTimer()
                soundUtil?.resumeTrack()
                currentAnimation?.resume()
            }
        }
    }

    private fun getCurrentStoryPlayDuration(): Long {
        return if (viewModel.inAppStoryData?.pages?.getOrNull(currentPosition)?.mediaType == MediaType.IMAGE.value) {
            (viewModel.inAppStoryData?.pages?.getOrNull(currentPosition)?.duration?.toLong()
                ?: STORY_DURATION_MILLIS)
        } else {
            player?.duration!!
        }
    }

    private fun moveToNextSlide(isAutomatic: Boolean = false) {
        if (isMovingForwardAllowed().not())
            return
        val currentPage = viewModel.inAppStoryData?.pages?.get(currentPosition)
        val isUserSegmentAvailable = viewModel.inAppStoryData?.userSegmentIds?.isNotEmpty()
        analyticsHandler.postEvent(
            Clicked_Storypage,
            mapOf(
                "button_type" to if (!isAutomatic) "next_slide" else "next_slide_auto",
                InteractionType to "right tap",
                button_order to "${adapter?.buttonOrder}",
                timeSpent to getTimeSpent(),
                Shared to "No",
                "page_number" to "${adapter?.storyPosition}",
                UserSegment to "$isUserSegmentAvailable",
                CTA to "${adapter?.iscta}",
                CTAName to "${adapter?.ctaName}",
                PublishTime to "${adapter?.duration}",
                InAppStoryAnalyticsConstants.MediaType to "${currentPage?.mediaType}",
                StoryId to "${viewModel.inAppStoryData?.storyId}",
                StoryName to "${viewModel.inAppStoryData?.storyName}",
                ContentId to currentPage?.contentId.orEmpty(),
                InAppStoryAnalyticsConstants.CategoryType to currentPage?.categories?.joinToString(",")
                    .orEmpty(),
                HeadingCopy to JarStory,
            )
        )
        viewModel.inAppStoryData?.let {
            viewModel.updateUserAction(
                UserAction.TIMESPENT.value,
                true,
                viewModel.inAppStoryData?.pages?.getOrNull(currentPosition)?.pageId.orEmpty(),
                getTimeSpent()
            )
        }


        // End the current animation and set its progress bar to complete.
        currentAnimation?.end()
        if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.VIDEO.value) {
            player?.seekTo(player?.contentDuration!!)
            player?.stop()
            player?.pauseAtEndOfMediaItems = true
            player?.clearMediaItems()
            player?.playWhenReady = false
        }
        progressBars.getOrNull(currentPosition)?.progress = 100
        progressBars[currentPosition].progress = 100
        currentPosition++
        currentAnimation?.end()

        if (currentPosition == totalCount) {
            // Handle the end of all stories, if needed.
            popBackStack()
        } else {
            // Reset pausedPlayTime and pausedPercentage before setting the story data.
            pausedPlayTime = 0
            pausedPercentage = 0f
            setStoryData()
        }
    }

    private fun moveToPreviousSlide() {
        if (isMovingBackwardAllowed().not())
            return
        val currentPage = viewModel.inAppStoryData?.pages?.get(currentPosition)
        analyticsHandler.postEvent(
            Clicked_Storypage,
            mapOf(
                button_type to "previous_slide",
                InteractionType to "left tap",
                button_order to "${adapter?.buttonOrder}",
                timeSpent to getTimeSpent(),
                Liked to "",
                Shared to "No",
                PageNumber to "${adapter?.storyPosition}",
                UserSegment to "${viewModel.inAppStoryData?.userSegmentIds?.isNotBlank()}",
                CTA to "${adapter?.iscta}",
                CTAName to "${adapter?.ctaName}",
                PublishTime to "${adapter?.duration}",
                InAppStoryAnalyticsConstants.MediaType to "${currentPage?.mediaType}",
                StoryId to "${viewModel.inAppStoryData?.storyId}",
                StoryName to "${viewModel.inAppStoryData?.storyName}",
                ContentId to "${currentPage?.contentId}",
                InAppStoryAnalyticsConstants.CategoryType to currentPage?.categories?.joinToString(",")
                    .orEmpty(),
                HeadingCopy to JarStory,
            )
        )

        // End the current animation and reset its progress bar to 0.
        if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.VIDEO.value) {
            player?.seekTo(0)
            player?.stop()
        }
        currentAnimation?.end()
        progressBars.getOrNull(currentPosition)?.progress = 0

        currentPosition--

        // Reset pausedPlayTime and pausedPercentage before setting the story data.
        pausedPlayTime = 0
        pausedPercentage = 0f
        setStoryData()

    }

    private fun getTimeSpent(): Long {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - currentTime - onPauseTime)
    }


    private fun isMovingBackwardAllowed() = currentPosition > 0

    private fun isMovingForwardAllowed() =
        isStoryPaused.not() && currentPosition < totalCount.orZero()

    private fun startTimer() {
        countDownJob?.cancel()
        countDownJob = uiScope.countDownTimer(
            totalMillis = timeLeft,//if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.IMAGE.value) timeLeft else player?.contentDuration.orZero(),
            intervalInMillis = COUNTDOWN_INTERVAL_MILLIS,
            onInterval = {
                if (currentPosition < viewModel.inAppStoryData?.pages?.size.orZero()) {
                    if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.IMAGE.value) {
                        timeLeft -= COUNTDOWN_INTERVAL_MILLIS
                    } else {
                        timeLeft = player?.currentPosition.orZero()
                    }
                }
            },
            onFinished = {
                player?.stop()
                moveToNextSlide(true)
                currentTime = System.currentTimeMillis()
                onPauseTime = 0L
            }
        )
    }

    override fun onResume() {
        Plotline.setShouldDisablePlotline(true)
        if (onPauseTime != 0L)
            onPauseTime = System.currentTimeMillis() - onPauseTime
        super.onResume()
    }

    private fun playSound(soundUrl: String) {

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            // General error handling here
            Timber.e(throwable)
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO + exceptionHandler) {
            delay(500)
            if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                soundUtil?.playSound(SoundType.CustomSound(soundUrl), true)
            }
        }

    }

    private fun startAnimation() {
        currentAnimation?.end()
        val startValue = (100 * pausedPercentage).toInt()
        val endValue = 100
        for (i in 0..currentPosition - 1) {
            progressBars.getOrNull(i)?.progress = 100
        }
        for (i in currentPosition until progressBars.size) {
            progressBars.getOrNull(i)?.progress = 0
        }

        viewModel.inAppStoryData?.pages?.getOrNull(currentPosition)?.let { page ->
            val buttonOrder = actionOrderToString(page?.actionOrders, page)
            val cta = containsStringInActionType(page, "CTA")
            val ctaName = if (cta) {
                page?.cta?.text
            } else ""
            val userSegment = viewModel.inAppStoryData?.userSegmentIds?.isNotBlank()
            val isLiked = page.likeCta?.isLiked
            val publishTime = page.uploadTime
            val timeToLoadImage = page.timeToLoadMedia ?: 0

            analyticsHandler.postEvent(
                Shown_StoryPage,
                mapOf(
                    button_order to buttonOrder,
                    timeSpent to getTimeSpent(),
                    PageNumber to currentPosition,
                    Liked to "$isLiked",
                    Shared to No,
                    UserSegment to "$userSegment",
                    CTA to "$cta",
                    CTAName to "$ctaName",
                    PublishTime to "$publishTime",
                    InAppStoryAnalyticsConstants.MediaType to page.mediaType.orEmpty(),
                    StoryId to "${viewModel.inAppStoryData?.storyId}",
                    StoryName to "${viewModel.inAppStoryData?.storyName}",
                    ContentId to "${page.contentId}",
                    from to if (args.storyId == null) homepage else link,
                    HeadingCopy to JarStory,
                    timeToLoad to "$timeToLoadImage".toLongOrZero(),
                    InAppStoryAnalyticsConstants.CategoryType to page.categories?.joinToString(",").orEmpty()
                )
            )
            if (page.isViewed.not()) {
                viewModel.updateUserAction(UserAction.VIEW.value, true, page.pageId, null)
            }
        }
        currentAnimation =
            ObjectAnimator.ofInt(progressBars[currentPosition], "progress", startValue, endValue)
        currentAnimation?.duration =
            if (viewModel.inAppStoryData?.pages?.get(currentPosition)?.mediaType == MediaType.IMAGE.value) timeLeft else player?.contentDuration.orZero()
        currentAnimation?.interpolator = AccelerateInterpolator()
        currentAnimation?.start()
    }

    private fun shareImage(image: String, shareLink: String) {
        watermarkUtil.applyWatermarkToImages(
            image,
            thumbnailUrl
        ) {
            shareVia(
                it,
                false,
                "Check out Jar Stories. Click on the link below: $shareLink"
            )
        }
    }

    private fun shareVia(resource: Bitmap?, shouldShareOnlyOnWA: Boolean, shareMessage: String) {
        val weakReference = weakReference
        if (resource != null) {
            uiScope.launch {
                fileUtils.copyBitmap(
                    resource,
                    "story_share_image_${
                        Calendar.getInstance().timeInMillis.toString().takeLast(4)
                    }"
                )?.let {
                    withContext(Dispatchers.Main.immediate) {
                        shareImage(shouldShareOnlyOnWA, weakReference, shareMessage, it)
                        dismissProgressBar()
                    }
                }
            }
        } else {
            dismissProgressBar()
            shareImage(shouldShareOnlyOnWA, weakReference, shareMessage, null)
        }
    }

    private fun shareImage(
        shouldShareOnlyOnWA: Boolean,
        weakReference: WeakReference<Context>,
        shareMessage: String,
        it: File? = null
    ) {
        val context = weakReference.get() ?: return
        if (shouldShareOnlyOnWA) {
            context.shareOnWhatsapp(deviceUtils.getWhatsappPackageName(), shareMessage, image = it)
        } else {
            it?.let {
                fileUtils.shareImage(context, it, shareMessage)
            } ?: run {
                fileUtils.shareText(shareMessage, "Jar in-app story")
            }
        }
    }

    override fun onDestroyView() {
        countDownJob?.cancel()
        currentAnimation?.cancel()
        cancelPreCache()
        Plotline.setShouldDisablePlotline(false);
        binding.vpGuide.adapter = null
        soundUtil = null
        currentPosition = 0
        releasePlayer()
        super.onDestroyView()
    }

    private val downloader by lazy {
        mediaItem?.let { HlsDownloader(it, cacheDataSourceFactory) }
    }

    private fun cancelPreCache() {
        downloader?.cancel()
    }

    private suspend fun preCacheVideo(uri: String) = withContext(Dispatchers.IO) {
        runCatching {
            // do nothing if already cache enough
            if (cache.isCached(uri.toString(), 0, PRE_CACHE_SIZE)) {
                return@runCatching
            }
            downloader?.download { contentLength, bytesDownloaded, percentDownloaded ->
                if (bytesDownloaded >= PRE_CACHE_SIZE) downloader?.cancel()
            }
        }.onFailure {
            if (it is InterruptedException) return@onFailure
            it.printStackTrace()
        }.onSuccess {}
        Unit
    }
}