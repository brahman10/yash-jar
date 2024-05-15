package com.jar.app.feature_spin.impl.custom

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.contains
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_spin.R
import com.jar.app.feature_spin.databinding.FeatureSpinsV2Binding
import com.jar.app.feature_spin.impl.custom.component.models.*
import com.jar.app.feature_spin.impl.custom.interpolators.WinningOpeningCustomInterpolator
import com.jar.app.feature_spin.impl.custom.listeners.DragDropTouchListener
import com.jar.app.feature_spin.impl.custom.listeners.SpinWheelListener
import com.jar.app.feature_spin.impl.custom.util.*
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.base.util.sound.SoundType
import com.jar.app.base.util.sound.SoundUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.Float
import java.lang.ref.WeakReference
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.apply
import kotlin.getValue
import kotlin.lazy
import kotlin.let
import kotlin.random.Random

internal class SpinWheelV2(
    context: Context,
    attr: AttributeSet? = null,
    private val parentLifecycle: Lifecycle? = null,
    uiScope: CoroutineScope
) : FrameLayout(
    context, attr
), LifecycleOwner {

    private var flowTypeContext: SpinsContextFlowType = SpinsContextFlowType.SPINS
    private var spinWheelListenerParent: SpinWheelListener? = null
    private var nOptions: List<com.jar.app.feature_spin.shared.domain.model.Option>? = null
    private var binding: FeatureSpinsV2Binding? = null
    private val state: MutableStateFlow<SpinModel?> by lazy {
        MutableStateFlow(null)
    }
    private val soundUtil = SoundUtil(WeakReference(context), false)

    private val animationSet by lazy {
        AnimatorSet()
    }

    private val resetNudgeAnimation by lazy {
        AnimatorSet()
    }

    private val openWinningAnimation by lazy {
        AnimatorSet()
    }

    private val closeWinningAnimation by lazy {
        AnimatorSet()
    }

    private var isAlertManuallyShown = false
    private var isAlertManuallyCanceled = false
    private var backgroundRotationAnimation: ObjectAnimator? = null
    private val winningAnimationInterpolator by lazy {
        WinningOpeningCustomInterpolator()
    }
    private var previousRotation = 0f

    // these values are used to set the starting values
    private var parentViewGroup: ViewGroup? = null
    private var remainingSpins = 0
    private var totalSpins = 0
    private var totalWinning = 0
    private var isShowIntroScreen = true
    private var introScreenDelay = 2000L
    private var activity: FragmentActivity? = null
    private var spinOutCome: Int = 0
    private var gameResult: com.jar.app.feature_spin.shared.domain.model.GameResult? = null
    private var spinToWinResponse: com.jar.app.feature_spin.shared.domain.model.SpinToWinResponse? = null
    private var showAlertNudge = false

    private val dragListener by lazy {
        DragDropTouchListener(
            onYChange = {
                state.value = state.value?.copy(
                    onYChange = Event(it)
                )
            },
            onStartSpin = {
                state.value = state.value?.copy(
                    onStartRotation = Event(System.currentTimeMillis())
                )
            },
            onCancel = {
                uiScopeWithExceptionHandler.launch(Dispatchers.IO) {
                    soundUtil.stopCurrentTrack()
                    spinWheelListenerParent?.onDragCancel()
                }
            },
            onPause = {
                uiScopeWithExceptionHandler.launch(Dispatchers.IO) {
                    soundUtil.stopCurrentTrack()
                }
            },
            onPlayLiverUpSound = {
                uiScopeWithExceptionHandler.launch(Dispatchers.IO) {
                    soundUtil.playSound(SoundType.BUTTON_RESET)
                }
            },
            onPlayVibration = {
                spinWheelListenerParent?.onLiverReachedToMax()
            }
        )
    }

    private val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
    }

    private val uiScopeWithExceptionHandler = uiScope + handler

    companion object {
        const val TAG = "SpinWheelV2"
        const val TOTAL_WINNING_OPEN_ANIMATION_DURATION = 1000L
        const val SPIN_ROTATION_DURATION = 2360L
        const val WINNING_OPENING_ANIMATION_DURATION = 500L
        const val SHOW_ALERT_NUDGE = 2000L
    }

    override val lifecycle: Lifecycle
        get() = parentLifecycle ?: LifecycleRegistry(this)

    init {
        val newLayoutParam = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        this.layoutParams = newLayoutParam
        binding = FeatureSpinsV2Binding.inflate(LayoutInflater.from(context))
        binding?.apply {
            addView(this.root)
            setMarginAsPercentage(
                context,
                this.activeDropContainer,
                0,
                0,
                0,
                3
            )
            activeDropContainer.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    activeDropContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    dragListener.maxDepth = activeDropContainer.measuredHeight.toFloat()
                }
            })

            activeCoinBtn.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    activeCoinBtn.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    dragListener.activeCoinHeight = activeCoinBtn.measuredHeight.toFloat()
                }
            })
        }
        observeState()
        setListener()
        parentLifecycle?.addObserver(
            soundUtil
        )
    }


    private fun setUpView() {
        uiScopeWithExceptionHandler.launch {
            if (isAlertManuallyCanceled.not() && showAlertNudge) {
                alertUser()
            }
            binding?.apply {
                rotateBackground()
                linearLayout.viewTreeObserver?.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
                    override fun onDraw() {
                        ConstraintSet().apply {
                            val height = linearLayout.height
                            clone(binding?.root)
                            val marginTop = context.getDimension(height / 2.5f)
                            setMargin(linearLayout.id, ConstraintSet.TOP, marginTop)
                        }.applyTo(binding?.root)
                    }
                })

                tvTotalSpins.text = "${spinToWinResponse?.totalSpinsCta?.text}"
                Glide.with(ivTotalSpin).load(spinToWinResponse?.totalSpinsCta?.iconLink)
                    .into(ivTotalSpin)
                totalSpins.text = "${spinToWinResponse?.totalSpinsCta?.value}"

                // setting the header image
                Glide.with(tvSpinToWin).load(spinToWinResponse?.header).into(tvSpinToWin)

                //setting the bottom daily spin left text
                tvSpinsLeft.text = "${spinToWinResponse?.dailySpinsLeftText}"

                //setting the total winnings
                tvTodaysWinning.text = "${spinToWinResponse?.todayWinnings?.text}"
                Glide.with(ivTodaysWining)
                    .load(spinToWinResponse?.todayWinnings?.iconLink)
                    .into(ivTodaysWining)
                tvTodaysWinningValue.text = "${spinToWinResponse?.todayWinnings?.value}"

                if (spinToWinResponse?.showSpinsOverMessage == true) {
                    hideNudgeAndCoin()
                    binding?.spinWheelBg?.turnOffAllBulb()
                    binding?.alertActiveCoinBtn?.visibility = View.GONE
                    binding?.root?.setOnClickListener(null)
                    binding?.spinBottom?.setImageResource(R.drawable.spin_bottom_part)
                    binding?.parentLayout?.background = ContextCompat.getDrawable(context, R.drawable.spin_bg_disable)
                    soundUtil.pauseAlwaysOnSound()
                    endBgRotatingAnimation()

                    Glide.with(spinOverLeftDot)
                        .load(spinToWinResponse?.spinsOverMessageObject?.iconLink)
                        .into(spinOverLeftDot)
                    txSpinOverText.text = spinToWinResponse?.spinsOverMessageObject?.message
                    Glide.with(spinOverRightDot)
                        .load(spinToWinResponse?.spinsOverMessageObject?.iconLink)
                        .into(spinOverRightDot)
                    showDailySpinOver()
                } else {
                    binding?.llSpinOver?.visibility = View.GONE
                }

                if (spinToWinResponse?.showUseWinningsCta == true) {
                    binding?.spinWheelBg?.stopRotationAnimation()

                    if (spinToWinResponse?.useWinningsCta?.deeplink == null) {
                        binding?.tvComeTomorrow?.visibility = View.VISIBLE
                        binding?.tvComeTomorrow?.text = "${spinToWinResponse?.useWinningsCta?.text}"
                    } else {
                        val shimmer = Shimmer.AlphaHighlightBuilder().setBaseAlpha(1f).build()
                        binding?.btnUseWinning?.setShimmer(shimmer)
                        binding?.btnUseWinning?.visibility = View.VISIBLE
                        binding?.btnUseWinningTxt?.text =
                            "${spinToWinResponse?.useWinningsCta?.text}"
                        binding?.btnUseWinningTxt?.setDebounceClickListener {
                            spinToWinResponse?.useWinningsCta?.deeplink?.let {
                                spinWheelListenerParent?.onUseWinningClicked(
                                    it
                                )
                            }
                        }
                    }
                }

                if (spinToWinResponse?.showTodayWinnings == true) {
                    binding?.spinWheelBg?.turnOffAllBulb()
                    hideNudgeAndCoin()
                    binding?.alertActiveCoinBtn?.visibility = View.GONE
                    binding?.root?.setOnClickListener(null)
                    soundUtil.pauseAlwaysOnSound()
                    endBgRotatingAnimation()

                    openTotalWinning()
                }
                //playing nudge animation
                binding?.activeDropDown?.visibility = View.VISIBLE
                binding?.activeDropDown?.playAnimation()

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener() {
        binding?.root?.setOnClickListener {
            endAlertUserAnimation()
        }

        binding?.backArrow?.setDebounceClickListener {
            spinWheelListenerParent?.onBackIconClicked()
        }

        binding?.spinWheelDisk?.addSpinListener(object : SpinWheelListener {
            override fun onSpinComplete(outcome: Int?, spinId: String?) {
                uiScopeWithExceptionHandler.launch {
                    previousRotation = binding?.spinWheelDisk?.rotation ?: 0f
                    state.value = state.value?.copy(
                        onSpinComplete = Event(
                            SpinCompleteModel(
                                spinOutCome,
                                RemainingSpinModel(
                                    daySpin = totalSpins,
                                    remainingSpins = --remainingSpins
                                )
                            )
                        ),
                        totalWinnings = Event(TotalWinning(totalWinning, spinOutCome))
                    )
                }
            }
        })
    }

    // custom/util function
    private fun observeState() {
        uiScopeWithExceptionHandler.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                state.collect {
                    it?.let {
                        it.totalWinnings.getIfNotHandled()?.let { totalWinnings ->
                            val currentWinning =
                                totalWinnings.currentTotal
                            val spinOutcome = totalWinnings.outCome
                            this@SpinWheelV2.totalWinning = currentWinning + (spinOutcome ?: 0)
                            binding?.dialer?.updateValue("${this@SpinWheelV2.totalWinning}")
                            if (spinOutcome.isNotJackpotAndIsNull()) {
                                if (spinOutcome.isNotOhNoNotJackpotAndIsNotNull()) {
                                    binding?.dialer?.zoomInWinning()
                                }
                            }
                        }

                        it.onYChange.getIfNotHandled()?.let {
                            uiScopeWithExceptionHandler.launch {
                                if (it != -1f) {
                                    soundUtil.playSound(SoundType.PULL_DOWN, true)
                                }
                            }
                            binding?.spinWheelDisk?.rotation =
                                (previousRotation - it) // for anti clockwise rotation when user drags the livers down
                        }

                        it.onStartRotation.getIfNotHandled()?.let {
                            uiScopeWithExceptionHandler.launch {
                                binding?.spinWheelBg?.turnOnAllBulb()
                                binding?.activeCoinBtn?.setImageResource(R.drawable.gold_coin_without_shadow)
                                gameResult?.outcome?.let { outcome ->
                                    spinWheelListenerParent?.onDragComplete(
                                        gameResult?.outcomeType, outcome, gameResult?.id
                                    )
                                }
                                binding?.activeDropDown?.visibility = View.GONE
                                val index = if (flowTypeContext == SpinsContextFlowType.SPINS) nOptions?.indexOfFirst {
                                    it.value == spinOutCome
                                } ?: 0
                                else
                                    Random.nextInt(nOptions?.size.orZero())

                                binding?.spinWheelDisk?.rotateAndStop(index, SPIN_ROTATION_DURATION)
                                soundUtil.playSound(SoundType.SPIN_WHEEL, false)
                                binding?.activeCoinBtn?.setOnTouchListener(null)
                            }
                        }

                        it.onSpinComplete.getIfNotHandled()?.let { spinCompleteModel ->
                            spinCompleteModel.winningAmount?.let {
                                //pausing spin animation
                                soundUtil.stopCurrentTrack()

                                // stopping bulb rotating animation
                                binding?.spinWheelBg?.stopRotationAnimation()

                                binding?.tvSpinsLeftValue?.text =
                                    binding?.root?.context?.getString(
                                        R.string.remaining_spins,
                                        spinCompleteModel.RemainingSpinModel.remainingSpins,
                                        spinCompleteModel.RemainingSpinModel.daySpin
                                    )

                                gameResult?.outcome?.let { outcome ->
                                    playAppropriateSound(outcome, gameResult?.outcomeType.orEmpty())
                                    if (it.isOhNo()) {
                                        openOhNo()
                                        delay(3000)
                                    }
                                    spinWheelListenerParent?.onSpinComplete(outcome, gameResult?.id)
                                }
                            }

                            binding?.tvSpinsLeftValue?.text =
                                binding?.root?.context?.getString(
                                    R.string.remaining_spins,
                                    spinCompleteModel.RemainingSpinModel.remainingSpins,
                                    spinCompleteModel.RemainingSpinModel.daySpin
                                )
                        }
                    }
                }
            }
        }
    }

    fun setParentViewGroup(view: ViewGroup): SpinWheelV2 {
        this.parentViewGroup?.removeAllViews()
        this.parentViewGroup = view
        return this
    }

    fun setRemainingSpins(remainingSpins: Int): SpinWheelV2 {
        this.remainingSpins = remainingSpins
        return this
    }

    fun setTotalSpins(totalSpins: Int): SpinWheelV2 {
        this.totalSpins = totalSpins
        return this
    }

    fun setTotalWinnings(totalWinnings: Int): SpinWheelV2 {
        this.totalWinning = totalWinnings
        return this
    }

    fun setIsShowIntroScreen(isShowIntroScreen: Boolean): SpinWheelV2 {
        this.isShowIntroScreen = isShowIntroScreen
        return this
    }

    fun setIntroScreenDelay(introScreenDelay: Long): SpinWheelV2 {
        this.introScreenDelay = introScreenDelay
        return this
    }

    fun setActivity(activity: FragmentActivity): SpinWheelV2 {
        this.activity = activity
        return this
    }

    fun setGameResult(gameResult: com.jar.app.feature_spin.shared.domain.model.GameResult): SpinWheelV2 {
        this.gameResult = gameResult
        return this
    }

    fun setSpinToWinResponse(spinToWinResponse: com.jar.app.feature_spin.shared.domain.model.SpinToWinResponse): SpinWheelV2 {
        this.spinToWinResponse = spinToWinResponse
        return this
    }

    fun setSpinCallbackFromActivity(spinWheelListener: SpinWheelListener): SpinWheelV2 {
        this.spinWheelListenerParent = spinWheelListener
        return this
    }

    fun setSpinWheel(options: List<com.jar.app.feature_spin.shared.domain.model.Option>, context: SpinsContextFlowType) {
        animationSet.cancel()
        resetNudgeAnimation.cancel()

        this.nOptions = options
        this.flowTypeContext = context
        if (spinToWinResponse?.showTodayWinnings == true) {
            options.forEach() {
                it.value = null
            }
            binding?.spinWheelDisk?.setNSegments(
                options, flowTypeContext
            )
        } else {
            binding?.spinWheelDisk?.setNSegments(
                options, flowTypeContext
            )
            binding?.activeCoinBtn?.alpha = 1f
            binding?.activeCoinBtn?.scaleX = 1f
            binding?.activeCoinBtn?.scaleY = 1f
            binding?.activeCoinBtn?.setOnTouchListener(dragListener)
        }
        if (flowTypeContext == SpinsContextFlowType.QUESTS) {
            binding?.linearLayout?.isVisible = false
            binding?.actionBarEnd?.isVisible = false
        }
    }

    fun setWinner(outcome: Int): SpinWheelV2 {
        this.spinOutCome = outcome
        return this
    }

    fun setShowAlertNudge(showAlertNudge: Boolean): SpinWheelV2 {
        this.showAlertNudge = showAlertNudge
        return this
    }

    fun build(): SpinWheelV2 {
        val spinModel = SpinModel(
            totalSpins = Event(totalSpins),
            totalWinnings = Event(TotalWinning(totalWinning, null)),
            onSpinComplete = Event(
                SpinCompleteModel(
                    null,
                    RemainingSpinModel(totalSpins, remainingSpins)
                )
            )
        )
        state.value = spinModel
        if (parentViewGroup?.contains(this) == false) {
            parentViewGroup?.addView(this)
        }
        soundUtil.isInDisableMode = spinToWinResponse?.showSpinsOverMessage.orFalse()
        if (spinToWinResponse?.showSpinsOverMessage.orFalse().not()) {
            resetSetBulbs()
        } else {
            binding?.spinWheelBg?.turnOffAllBulb()
        }
        resetNudgeAndGoldCoin()
        setUpView()
        return this
    }

    private fun resetNudgeAndGoldCoin() {
        binding?.activeCoinBtn?.apply {
            setImageResource(R.drawable.active_coin_button)

            elevation = 200f

            val scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX",  1f)
            val scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY",  1f)

            // Set the duration and interpolator for the animations
            val animationDuration = 1000 // 1000ms = 1s
            scaleXAnimator.duration = animationDuration.toLong()
            scaleYAnimator.duration = animationDuration.toLong()

            // Play the animations together using AnimatorSet
            resetNudgeAnimation.apply {
                playTogether(scaleXAnimator, scaleYAnimator)
            }.start()

            colorFilter = null
        }
    }

    private fun playAppropriateSound(output: Int, outcomeType: String) {
        if (flowTypeContext == SpinsContextFlowType.QUESTS) {
            uiScopeWithExceptionHandler.launch {
                when {
                    output.isJackpot() -> {
                        soundUtil.playSound(SoundType.JACKPOT_CELEBRATION, false)
                    }

                    output.isOhNo() -> {
                        soundUtil.playSound(SoundType.OH_NO, false)
                    }
                }
            }
        } else {
            uiScopeWithExceptionHandler.launch {
                if (outcomeType == "FLAT") {
                    soundUtil.playSound(SoundType.OH_NO, false)
                } else if (outcomeType.isNotBlank()) {
                    soundUtil.playSound(SoundType.JACKPOT_CELEBRATION, false)
                } else {
                    // play no sound if its blank
                }
            }
        }
    }

    fun resetNudge() {
        animationSet.cancel()
        resetNudgeAnimation.cancel()
        binding?.activeCoinBtn?.setOnTouchListener(dragListener)
        binding?.activeCoinBtn?.elevation = 200f
        binding?.activeCoinBtn?.colorFilter = null
        binding?.activeCoinBtn?.apply {
            scaleY = 1f
            scaleX = 1f
        }
    }

    fun openWinning(flatOutcome: com.jar.app.feature_spin.shared.domain.model.FlatOutcome) {
        binding?.openUpWinning?.apply {
            val padding = (Float.min(width / 2f, height / 2f) * (10 / 100f)).toInt()
            setPadding(padding, padding, padding, padding)
            binding?.tvYouGot?.text = "${flatOutcome.preText}"
            binding?.ivWinningIcon?.let {
                Glide
                    .with(it)
                    .load(flatOutcome.winningsIconLink)
                    .into(it)
            }
            binding?.tvWinningAmount?.text = "${flatOutcome.outcome}"
            binding?.tvWinnings?.text = "${flatOutcome.postText}"
            uiScopeWithExceptionHandler.launch {
                soundUtil.playSound(SoundType.CELEBRATION, false)
            }
            binding?.tvWinningAmount?.text = "${flatOutcome.outcome}"
            visibility = View.VISIBLE

            val view = this
            startRotatingAnimation(binding?.openUpWinningDrawable)
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f).apply {
                duration = WINNING_OPENING_ANIMATION_DURATION
                startDelay = 80
                interpolator = WinningOpeningCustomInterpolator()
            }

            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f).apply {
                duration = WINNING_OPENING_ANIMATION_DURATION
                startDelay = 80
                interpolator = WinningOpeningCustomInterpolator()
            }

            openWinningAnimation.apply {
                playTogether(scaleX, scaleY)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        spinWheelListenerParent?.onShownWinnings(spinOutCome)
                        closeWinning()
                    }
                })
                binding?.spinWheelBg?.toggleImageSource()
                start()
            }
        }
    }

    private fun closeWinning() {
        binding?.openUpWinning?.apply {
            val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f).apply {
                duration = 300
                startDelay = 2000L
                interpolator = winningAnimationInterpolator
            }

            val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0f).apply {
                duration = 300
                startDelay = 2000L
                interpolator = winningAnimationInterpolator
            }

            closeWinningAnimation.apply {
                playTogether(scaleX, scaleY)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        uiScopeWithExceptionHandler.launch {
                            soundUtil.stopCurrentTrack()
                        }
                        spinWheelListenerParent?.onCloseWinnings()
                    }
                })
                start()
            }
        }
    }

    private fun openTotalWinning() {
        binding?.rlTotalWinning?.apply {
            visibility = View.VISIBLE
            val animZoomin: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.zoom_in
            ).apply {
                duration = TOTAL_WINNING_OPEN_ANIMATION_DURATION
            }
            animZoomin.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    spinWheelListenerParent?.onShownTotalWinnings()
                }

                override fun onAnimationRepeat(p0: Animation?) {}

            })
            startAnimation(animZoomin)
        }
    }

    private suspend fun showDailySpinOver() {
        withContext(Dispatchers.Main) {
            binding?.llSpinOver?.visibility = View.VISIBLE
            val myView = binding?.llSpinOver
            val startWidth = 0 // Starting width of the view
            val endWidth = 30f.dpToPx(context) // Desired end width
            val valueAnimator = startWidth.let {
                ValueAnimator.ofInt(it, endWidth).apply {
                    duration = 500 // Duration of the animation in milliseconds
                    addUpdateListener { animation ->
                        val params = myView?.layoutParams
                        params?.height = animation.animatedValue as Int
                        myView?.layoutParams = params
                    }
                }
            }
            valueAnimator?.start()
        }
    }

    private var alertUserJob: Job? = null
    private fun alertUser() {
        alertUserJob = uiScopeWithExceptionHandler.launch {
            delay(SHOW_ALERT_NUDGE)
            isAlertManuallyShown = true
            ensureActive()
            binding?.apply {
                activeCoinBtn.visibility = View.GONE
                flalertActiveCoinBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun endAlertUserAnimation() {
        if (isAlertManuallyShown && isAlertManuallyCanceled.not()) {
            binding?.activeCoinBtn?.visibility = View.VISIBLE
            binding?.alertActiveCoinBtn?.visibility = View.GONE
            animationSet.end()
            this.isAlertManuallyCanceled = true
        }
    }

    private fun startRotatingAnimation(view: View?) {
        val rotationAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)

        rotationAnimator.duration = 10000

        // Set the interpolator (LinearInterpolator for smooth and continuous rotation)
        rotationAnimator.interpolator = LinearInterpolator()

        // Set the repeat count (INFINITE for an indefinite loop)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE

        rotationAnimator.start()
    }

    private fun rotateBackground() {
        backgroundRotationAnimation = ObjectAnimator.ofFloat(
            binding?.spiralBg,
            "rotation",
            binding?.spiralBg?.rotation.orZero(),
            360f
        )
        backgroundRotationAnimation?.duration = 60000 // Duration in milliseconds
        backgroundRotationAnimation?.interpolator = LinearInterpolator()
        backgroundRotationAnimation?.repeatCount = ValueAnimator.INFINITE
        backgroundRotationAnimation?.repeatMode = ValueAnimator.RESTART
        backgroundRotationAnimation?.start()
    }

    private fun endBgRotatingAnimation() {
        backgroundRotationAnimation?.end()
        binding?.spiralBg?.visibility = View.INVISIBLE
    }

    private fun openOhNo() {
        uiScopeWithExceptionHandler.launch {
            binding?.spinWheelBg?.clearJobs()
            binding?.spinWheelBg?.turnOffAllBulb()
            delay(3000)
        }
    }

    private fun resetSetBulbs() {
        binding?.spinWheelBg?.bulbRotatingAnimation()
    }

    private fun hideNudgeAndCoin() {
        binding?.apply {
            activeDropContainer.visibility = View.GONE
            activeCoinBtn.visibility = View.GONE
        }
    }

    override fun onDetachedFromWindow() {
        animationSet.cancel()
        resetNudgeAnimation.cancel()
        openWinningAnimation.cancel()
        closeWinningAnimation.cancel()
        super.onDetachedFromWindow()
    }
}
