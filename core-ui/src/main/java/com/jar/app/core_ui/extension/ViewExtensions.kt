package com.jar.app.core_ui.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Instrumentation
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.*
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.toSpannable
import androidx.core.text.underline
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.CustomSnackbarViewBinding
import com.jar.app.core_ui.listener.SnapOnScrollListener
import com.jar.app.core_ui.widget.overscroll.NestedScrollViewOverScrollDecorAdapter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import java.util.*
import java.util.regex.Pattern

fun View.setDebounceClickListener(
    ctaDebounceTimeInMillis: Long = 1000L,
    action: (view: View) -> Unit
) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < ctaDebounceTimeInMillis) return
            else action(v)
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun TextView.makeColoredLink(
    message: String,
    words: List<String>,
    color: Int,
    shouldUnderlineWords: Boolean = false,
    vararg onClick: () -> Unit
) {
    val spannable: Spannable = SpannableString(message)
    var linkIndex = 0
    words.forEachIndexed { index, word ->
        var substringStart = 0
        var start: Int
        while (message.indexOf(word, substringStart, true).also { start = it } >= 0) {
            val clickAbleSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    onClick[index].invoke()
                }

                override fun updateDrawState(textPaint: TextPaint) {
                    super.updateDrawState(textPaint)
                    textPaint.color = color
                    textPaint.isUnderlineText = false
                }
            }
            spannable.setSpan(
                clickAbleSpan,
                start,
                start + word.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (shouldUnderlineWords) {
                spannable.setSpan(
                    UnderlineSpan(),
                    start,
                    start + word.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            substringStart = start + word.length
        }
        linkIndex++
    }
    this.movementMethod = LinkMovementMethod.getInstance()
    this.setText(spannable, TextView.BufferType.SPANNABLE)
}

fun ScrollView.scrollToBottom() {
    this.post {
        this.fullScroll(View.FOCUS_DOWN)
    }
}

fun ScrollView.scrollToTop() {
    this.post {
        this.fullScroll(View.FOCUS_UP)
    }
}

//To show complete itemView when RecycleView scrolled
fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onSnapPositionChangeListener: com.jar.app.core_ui.listener.OnSnapPositionChangeListener
) {
    if (this.onFlingListener == null) {
        snapHelper.attachToRecyclerView(this)
        val snapOnScrollListener =
            SnapOnScrollListener(
                snapHelper,
                behavior,
                onSnapPositionChangeListener
            )
        addOnScrollListener(snapOnScrollListener)
    }
}

fun ViewPager2.setCurrentItem(
    position: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width // Default value taken from getWidth() from ViewPager2 view
) {

    val pxToDrag: Int = pagePxWidth * (position - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    try {
        var previousValue = 0
        animator.addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            //Use fakeDragBy(currentPxToDrag) when using RTL.
            fakeDragBy(-currentPxToDrag)
            previousValue = currentValue
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                beginFakeDrag()
            }

            override fun onAnimationEnd(animation: Animator) {
                endFakeDrag()
            }

            override fun onAnimationCancel(animation: Animator) { /* Ignored */
            }

            override fun onAnimationRepeat(animation: Animator) { /* Ignored */
            }
        })
        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    } catch (e: Exception) {
        /*Do nothing*/
        animator.cancel()
    }
}

fun EditText.setTypeAmount(shouldAllowDecimal: Boolean = true) {
    var flags = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
    if (shouldAllowDecimal)
        flags = flags or InputType.TYPE_NUMBER_FLAG_DECIMAL
    this.inputType = flags
}

fun View.hideKeyboard() {
    postDelayed({
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.clearFocus()
        imm.hideSoftInputFromWindow(windowToken, 0)
    }, 200)
}

fun View.showKeyboard() {
    postDelayed({
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.requestFocus()
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }, 0)
}

fun View.vibrate(vibrate: Vibrator?, durationInMillis: Long = 1L) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate?.vibrate(
            VibrationEffect.createOneShot(
                durationInMillis,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else
        vibrate?.vibrate(durationInMillis)
}

fun View.getScreenShot(): Bitmap? {
    val bitmap = Bitmap.createBitmap(
        this.width,
        this.height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

@SuppressLint("NotifyDataSetChanged")
fun RecyclerView.runLayoutAnimation(@AnimRes animRes: Int) {
    val controller: LayoutAnimationController =
        AnimationUtils.loadLayoutAnimation(context, animRes)
    layoutAnimation = controller
    adapter?.notifyDataSetChanged()
    scheduleLayoutAnimation()
}

fun NestedScrollView.setUpOverScroll() {
    VerticalOverScrollBounceEffectDecorator(NestedScrollViewOverScrollDecorAdapter(this))
}

fun EditText.setOnImeActionDoneListener(onDone: () -> Unit) {
    setOnEditorActionListener { _, i, _ ->
        if (i == EditorInfo.IME_ACTION_DONE) {
            onDone.invoke()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

fun EditText.setOnImeActionNextListener(onNext: () -> Unit) {
    setOnEditorActionListener { _, i, _ ->
        if (i == EditorInfo.IME_ACTION_NEXT) {
            onNext.invoke()
            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

//Toast
fun String.toast(view: View, toastTime: Int = Toast.LENGTH_SHORT): Toast? {
    val toast = Toast.makeText(view.context, this, toastTime)
    toast.show()
    return toast
}

fun TextView.setHtmlTextValue(htmlText: String?) {
    htmlText?.let {
        val result: Spanned = it.getHtmlTextValue()
        this.text = result
    }
}

fun String?.parseColorStringFromBackend(): Int? {
    this ?: return null
    try {
        return Color.parseColor(this)
    } catch (e: Exception) {

    }
    return null
}

fun String.getHtmlTextValue() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_OPTION_USE_CSS_COLORS)
} else {
    Html.fromHtml(this)
}

//Custom SnackBar
fun String.snackBar(
    view: View,
    @DrawableRes iconRes: Int = -1,
    @RawRes lottieRes: Int = -1,
    @ColorRes progressColor: Int = R.color.color_a841ff,
    duration: Long = 3000,
    translationY: Float = -64.dp.toFloat()
): Snackbar? {
    if (this.isBlank())
        return null
    if (iconRes != -1 && lottieRes != -1)
        throw Exception("Can set either iconRes or lottieRes")
    if (view.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED)
            ?.not().orFalse()
    )
        return null
    val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
    snackBar.view.apply {
        background = ContextCompat.getDrawable(view.context, R.drawable.base_bg_custom_snack_bar)
        this.translationY = translationY
    }
    snackBar.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
    val layout = snackBar.view as Snackbar.SnackbarLayout
    val binding = CustomSnackbarViewBinding.inflate(LayoutInflater.from(view.context))
    binding.progressBar.setIndicatorColor(ContextCompat.getColor(view.context, progressColor))
    binding.tvText.text = this
    if (iconRes != -1) {
        binding.ivIcon.isVisible = true
        Glide.with(binding.root).load(iconRes).into(binding.ivIcon)
    } else {
        binding.ivIcon.isVisible = false
    }
    if (lottieRes != -1) {
        binding.lottie.isVisible = true
        binding.lottie.setAnimation(lottieRes)
        binding.lottie.playAnimation()
    } else {
        binding.lottie.isVisible = false
    }
    layout.setPadding(0, 0, 0, 0)
    layout.addView(binding.root, 0)
    val animation = ObjectAnimator.ofInt(binding.progressBar, "progress", 100, 0)
    animation.duration = duration
    animation.interpolator = LinearInterpolator()

    animation.doOnEnd {
        binding.lottie.cancelAnimation()
        binding.lottie.clearAnimation()
        snackBar.dismiss()
    }

    snackBar.addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            animation.cancel()
        }
    })

    animation.start()
    snackBar.show()
    return snackBar
}

fun String?.snackBarWithGenericFallback(
    view: View,
    @DrawableRes iconRes: Int = -1,
    @RawRes lottieRes: Int = -1,
    @ColorRes progressColor: Int = R.color.color_a841ff,
    duration: Long = 3000,
    translationY: Float = -64.dp.toFloat(),
    genericMessage: String? = null
): Snackbar? {
    return if (this.isNullOrBlank()) {
        val message = genericMessage ?: view.context.getString(R.string.something_went_wrong)
        message.snackBar(view, iconRes, lottieRes, progressColor, duration, translationY)
    } else
        try {
            this.snackBar(view, iconRes, lottieRes, progressColor, duration, translationY)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
}

fun View.keyboardVisibilityChanges(): Flow<Boolean> {
    return onPreDrawFlow()
        .map { isKeyboardVisible() }
        .distinctUntilChanged()
}

fun View.onPreDrawFlow(): Flow<Unit> {
    return callbackFlow {
        val onPreDrawListener = ViewTreeObserver.OnPreDrawListener {
            trySendBlocking(Unit)
            true
        }
        viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
        awaitClose {
            viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
        }
    }
}

fun View.isKeyboardVisible(): Boolean = ViewCompat.getRootWindowInsets(this)
    ?.isVisible(WindowInsetsCompat.Type.ime())
    ?: false

fun TextView.setTextAnimation(text: String, duration: Long = BaseConstants.DEFAULT_DURATION_300_MILLIS, completion: (() -> Unit)? = null) {
    fadOutAnimation(duration) {
        this.text = text
        fadInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun View.fadOutAnimation(duration: Long = BaseConstants.DEFAULT_DURATION_300_MILLIS, visibility: Int = View.INVISIBLE, completion: (() -> Unit)? = null) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            this.visibility = visibility
            completion?.let {
                it()
            }
        }
}

fun View.fadInAnimation(duration: Long = BaseConstants.DEFAULT_DURATION_300_MILLIS, completion: (() -> Unit)? = null) {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(duration)
        .withEndAction {
            completion?.let {
                it()
            }
        }
}

fun View.slideToRevealNew(viewToReveal: View, onAnimationEnd: () -> Unit = {}) {
    val fromView = this
    viewToReveal.visibility = View.INVISIBLE
    viewToReveal.translationX = viewToReveal.width.toFloat()

    val duration = 300L

    val fromAnim =
        ObjectAnimator.ofFloat(fromView, View.TRANSLATION_X, 0f, -fromView.width.toFloat())
    fromAnim.interpolator = LinearInterpolator()
    fromAnim.duration = duration

    val toAnim = ObjectAnimator.ofFloat(
        viewToReveal,
        View.TRANSLATION_X,
        viewToReveal.width.toFloat(),
        0f
    )
    toAnim.interpolator = LinearInterpolator()
    toAnim.duration = duration

    toAnim.doOnStart {
        viewToReveal.isVisible = true
    }

    toAnim.doOnEnd {
        if (this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }

    fromAnim.start()
    toAnim.start()
}

fun View.slideRightToReveal(viewToReveal: View, onAnimationEnd: () -> Unit = {}, duration: Long = 300L) {
    val fromView = this
    viewToReveal.visibility = View.INVISIBLE
    viewToReveal.translationX = viewToReveal.width.toFloat()

    val fromAnim =
        ObjectAnimator.ofFloat(fromView, View.TRANSLATION_X, 0f, fromView.width.toFloat())
    fromAnim.interpolator = LinearInterpolator()
    fromAnim.duration = duration

    val toAnim = ObjectAnimator.ofFloat(
        viewToReveal,
        View.TRANSLATION_X,
        0f,
        viewToReveal.width.toFloat()
    )
    toAnim.interpolator = LinearInterpolator()
    toAnim.duration = duration

    toAnim.doOnStart {
        viewToReveal.isVisible = true
    }

    toAnim.doOnEnd {
        if (this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }

    fromAnim.start()
    toAnim.start()
}

fun View.slideTopToReveal(
    viewToReveal: View,
    onAnimationEnd: () -> Unit = {},
    duration: Long = 1000L
) {
    val fromView = this
    viewToReveal.visibility = View.INVISIBLE
    viewToReveal.translationY = viewToReveal.height.toFloat()

    val fromAnim =
        ObjectAnimator.ofFloat(fromView, View.TRANSLATION_Y, 0f, -fromView.height.toFloat())
    fromAnim.interpolator = LinearInterpolator()
    fromAnim.duration = duration

    val toAnim = ObjectAnimator.ofFloat(
        viewToReveal,
        View.TRANSLATION_Y,
        viewToReveal.height.toFloat(),
        0f
    )
    toAnim.interpolator = LinearInterpolator()
    toAnim.duration = duration

    toAnim.doOnStart {
        viewToReveal.isVisible = true
    }

    toAnim.doOnEnd {
        if (this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }

    fromAnim.start()
    toAnim.start()
}

/**
 * Simulate touching a specific location and dragging to a new location.
 *
 * @param fromX X coordinate of the initial touch, in screen coordinates
 * @param toX Xcoordinate of the drag destination, in screen coordinates
 * @param fromY X coordinate of the initial touch, in screen coordinates
 * @param toY Y coordinate of the drag destination, in screen coordinates
 * @param stepCount How many move steps to include in the drag
 */
fun fling(
    fromX: Float, toX: Float, fromY: Float,
    toY: Float, stepCount: Int
) {

    val inst = Instrumentation()

    val downTime = SystemClock.uptimeMillis()
    var eventTime = SystemClock.uptimeMillis()

    var y = fromY
    var x = fromX

    val yStep = (toY - fromY) / stepCount
    val xStep = (toX - fromX) / stepCount

    var event = MotionEvent.obtain(
        downTime, eventTime,
        MotionEvent.ACTION_DOWN, fromX, fromY, 0
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        event.source = InputDevice.SOURCE_TOUCHSCREEN
    }
    inst.sendPointerSync(event)

    for (i in 0 until stepCount) {
        y += yStep
        x += xStep
        eventTime = SystemClock.uptimeMillis()
        event = MotionEvent.obtain(
            downTime, eventTime + stepCount,
            MotionEvent.ACTION_MOVE, x, y, 0
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            event.source = InputDevice.SOURCE_TOUCHSCREEN
        }
        inst.sendPointerSync(event)
    }

    eventTime = SystemClock.uptimeMillis() + stepCount.toLong() + 2
    event = MotionEvent.obtain(
        downTime, eventTime,
        MotionEvent.ACTION_UP, toX, toY, 0
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        event.source = InputDevice.SOURCE_TOUCHSCREEN
    }
    inst.sendPointerSync(event)
}

fun LottieAnimationView.playLottieWithUrlAndExceptionHandling(
    context: Context, url: String, safeMode: Boolean = true
): LottieAnimationView {
    val compositionTask = LottieCompositionFactory.fromUrl(context, url)
    compositionTask.addListener { result ->
        result?.let {
            this.setComposition(it)
            this.setSafeMode(safeMode)
            // If setSafeMode to true, draw will be wrapped with a try/catch which will cause Lottie to
            // render an empty frame rather than crash your app.
            this.playAnimation()
        }
        compositionTask.addFailureListener { throwable ->
            //Handle Exception
            throwable.printStackTrace()
        }
    }
    return this
}

fun LottieAnimationView.playLottieUrlSequentially(url: String) {
    try {
        this.cancelAnimation()
        this.setAnimationFromUrl(url)
        this.playAnimation()
    } catch (e: Exception) {
        /** Do Nothing **/
    }
}

fun String.isLetters(): Boolean {
    val letter = Pattern.compile("[a-zA-z]")
    return letter.matcher(this).find()
}

fun String.isNumber(): Boolean {
    val digit = Pattern.compile("[0-9]")
    return digit.matcher(this).find()
}

fun String.isPanFormat(): Boolean {
    val pan = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
    return pan.matcher(this).find()
}

fun String.isAadhaarFormat(): Boolean {
    val pan = Pattern.compile("[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$")
    return pan.matcher(this).find()
}

fun View.animateViewRightToLeftFadeOut(duration: Long, onAnimationEnd: () -> Unit = {}) {
    val animator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, 0f, -this.width.toFloat())
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()

    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            this@animateViewRightToLeftFadeOut.visibility = View.INVISIBLE
        }
    })

    val fadeOut = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)
    fadeOut.duration = duration

    animator.start()
    fadeOut.start()

    fadeOut.doOnEnd {
        if (
            this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }
}


fun View.animateViewLeftToRightFadeOut(duration: Long, onAnimationEnd: () -> Unit = {}) {
    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, 0f, this.width.toFloat())
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()

    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            this@animateViewLeftToRightFadeOut.visibility = View.INVISIBLE
        }
    })

    val fadeOut = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)
    fadeOut.duration = duration

    animator.start()
    fadeOut.start()

    fadeOut.doOnEnd {
        if (
            this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }
}

fun View.animateViewLeftToRightFadeIn(duration: Long, onAnimationEnd: () -> Unit = {}) {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.translationX = -this.width.toFloat()

    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, -this.width.toFloat(), 0f)
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()

    val fadeIn = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)
    fadeIn.duration = duration

    animator.start()
    fadeIn.start()

    fadeIn.doOnEnd {
        if (
            this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }
}

fun View.animateViewRightToLeftFadeIn(duration: Long, onAnimationEnd: () -> Unit = {}) {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.translationX = this.width.toFloat()

    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, this.width.toFloat(), 0f)
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()

    val fadeIn = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)
    fadeIn.duration = duration

    animator.start()
    fadeIn.start()

    fadeIn.doOnEnd {
        if (
            this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }
}


fun View.animateViewTopToBottomFadeOut(duration: Long, onAnimationEnd: () -> Unit = {}) {
    this.alpha = 1f
    val animator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, this.translationY, this.height.toFloat())
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()

    val fadeOut = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)
    fadeOut.duration = duration

    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            this@animateViewTopToBottomFadeOut.visibility = View.INVISIBLE
        }
    })

    animator.start()
    fadeOut.start()

    fadeOut.doOnEnd {
        if (
            this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }
}

fun View.animateViewBottomToTopFadeOut(duration: Long, onAnimationEnd: () -> Unit = {}) {
    this.alpha = 1f

    val animator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, this.translationY, -this.height.toFloat())
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()

    val fadeOut = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)
    fadeOut.duration = duration

    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            this@animateViewBottomToTopFadeOut.visibility = View.INVISIBLE
        }
    })

    animator.start()
    fadeOut.start()

    fadeOut.doOnEnd {
        if (
            this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }
}

fun View.animateViewBottomToTopFadeIn(duration: Long, onAnimationEnd: () -> Unit = {}) {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.translationY = this.height.toFloat()

    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, this.height.toFloat(), 0f)
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()

    val fadeIn = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)
    fadeIn.duration = duration

    animator.start()
    fadeIn.start()


    fadeIn.doOnEnd {
        if (
            this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }
}


fun View.animateViewTopToBottomFadeIn(
    duration: Long,
    onAnimationEnd: () -> Unit = {},
    onAnimationUpdate: (Float) -> Unit = {}
) {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.translationY = -this.height.toFloat()

    val animator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f)
    animator.duration = duration
    animator.interpolator = AccelerateInterpolator()

    val fadeIn = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)
    fadeIn.duration = duration

    animator.start()
    fadeIn.start()

    animator.addUpdateListener {
        onAnimationUpdate.invoke(it.animatedValue.toString().toFloat())
    }
    fadeIn.doOnEnd {
        if (
            this.findViewTreeLifecycleOwner()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.CREATED)
                .orFalse()
        )
            onAnimationEnd.invoke()
    }
}

fun View.animateViewWithFadeOutAnimation(duration: Long, onAnimationEnd: () -> Unit = {}) {
    this.animate()
        .alpha(0f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@animateViewWithFadeOutAnimation.visibility = View.INVISIBLE
                onAnimationEnd.invoke()
            }
        })
}

fun View.animateViewWithFadeInAnimation(duration: Long, onAnimationEnd: () -> Unit = {}) {
    this.animate()
        .alpha(1f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@animateViewWithFadeInAnimation.visibility = View.VISIBLE
                onAnimationEnd.invoke()
            }
        })
}

fun View.animateViewTransform(
    toView: View,
    duration: Long,
    onAnimationEnd: () -> Unit = {},
    onAnimationUpdate: (Float) -> Unit = {}
) {
    // Get the original position and size of the fromView
    //First Transaction Example = fromView clFirstTransactionSuccessContainer toView dummyLockerView
    val fromViewOriginalX = this.left.toFloat()
    val fromViewOriginalY = this.top.toFloat()
    val fromViewOriginalWidth = this.width.toFloat()
    val fromViewOriginalHeight = this.height.toFloat()

    // Get the final position and size of the toView
    val toViewFinalX = toView.left.toFloat()
    val toViewFinalY = toView.top.toFloat()
    val toViewFinalWidth = toView.width.toFloat()
    val toViewFinalHeight = toView.height.toFloat()

    // Create and configure the translationX animator
    val translationXAnimator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, toViewFinalX - fromViewOriginalX)
    translationXAnimator.duration = duration
    translationXAnimator.interpolator = AccelerateInterpolator()


    this.pivotX = 0f
    this.pivotY = 0f

    // Create and configure the translationY animator
    val translationYAnimator =
        ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, toViewFinalY - fromViewOriginalY)
    translationYAnimator.duration = duration
    translationYAnimator.interpolator = AccelerateInterpolator()

    // Create and configure the scaleX animator
    val scaleXAnimator =
        ObjectAnimator.ofFloat(this, View.SCALE_X, toViewFinalWidth / fromViewOriginalWidth)
    scaleXAnimator.duration = duration
    scaleXAnimator.interpolator = AccelerateInterpolator()

    // Create and configure the scaleY animator
    val scaleYAnimator =
        ObjectAnimator.ofFloat(this, View.SCALE_Y, toViewFinalHeight / fromViewOriginalHeight)
    scaleYAnimator.duration = duration
    scaleYAnimator.interpolator = AccelerateInterpolator()

    // Create a listener to clean up the fromView after the animation ends
    val animatorListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            // Remove the fromView from its parent
            this@animateViewTransform.visibility = View.INVISIBLE
            onAnimationEnd.invoke()
        }
    }

    translationXAnimator.addUpdateListener {
        onAnimationUpdate.invoke(it.animatedValue.toString().toFloat())
    }

    // Start all the animators together and set the listener
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(
        translationXAnimator,
        translationYAnimator,
        scaleXAnimator,
        scaleYAnimator
    )
    animatorSet.addListener(animatorListener)
    animatorSet.start()
}

fun View.animateBackgroundColor(
    startColor: Int,
    endColor: Int,
    duration: Long,
    onAnimationEnd: () -> Unit = {}
) {
    val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
    colorAnimator.duration = duration

    colorAnimator.addUpdateListener { animator ->
        val animatedValue = animator.animatedValue as Int
        if (animatedValue == 100) {
            onAnimationEnd.invoke()
        }
        this.setBackgroundColor(animatedValue)
    }
    colorAnimator.start()
}

fun View.fadeOutInColors(
    fadeOutColor: Int,
    fadeInColor: Int,
    duration: Long,
    onAnimationEnd: () -> Unit = {}
) {
    val colorAnimator =
        ObjectAnimator.ofObject(this, "backgroundColor", ArgbEvaluator(), fadeOutColor, fadeInColor)
    colorAnimator.duration = duration
    colorAnimator.addUpdateListener { animator ->
        val animatedValue = animator.animatedValue as Int
        if (animatedValue == 100) {
            onAnimationEnd.invoke()
        }
    }
    colorAnimator.start()
}

fun View.getBackgroundColor(): Int {
    val background = this.background
    if (background is ColorDrawable) {
        return background.color
    }
    return 0 // Default color if background is not a ColorDrawable
}

fun Drawable.updateDrawableTint(color: Int) {
    val wrappedDrawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(wrappedDrawable, color)
    DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN)
}

fun View.globalLayoutListener(onLayout: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver?.removeOnGlobalLayoutListener(this)
            onLayout()
        }
    })
}

fun Canvas.drawTextMultiline(text: String, x: Float, y: Float, paint: Paint) {
    var yPos = y
    val lines = text.split("\n")
    for (line in lines) {
        drawText(line, x, yPos, paint)
        yPos += -paint.ascent() + paint.descent() // Move down to the next line
    }
}


/**
 * Searches for all URLSpans in current text replaces them with ClickableSpans
 * forwards clicks to provided function.
 */
fun TextView.handleUrlClicks(onClicked: ((String, String) -> Unit)? = null) {
    //create span builder and replaces current text with it
    text = SpannableStringBuilder.valueOf(text).apply {
        //search for all URL spans and replace all spans with our own clickable spans
        getSpans(0, length, URLSpan::class.java).forEach {
            val start = getSpanStart(it)
            val end = getSpanEnd(it)
            val urlText = if (start != -1 && end != -1) substring(start, end) else ""
            //add new clickable span at the same position
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onClicked?.invoke(it.url, urlText)
                        it.onClick(widget) //open url as action view
                    }
                },
                start,
                end,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            //remove old URLSpan
            removeSpan(it)
        }
    }
    //make sure movement method is set
    movementMethod = LinkMovementMethod.getInstance()
}

/**\
 * This add Read more clickable text within {maxLine} of the text
 * Expandable  text with Read more Or Read Less option.
 */
fun TextView.setSeeMoreOrLessView(
    fullText: String,
    maxLines: Int = 3,
    seeMoreText: String = "Read More",
    seeLessText: String = "Read Less",
    onRestTextClickListener:(View) -> Unit
) {
    text = fullText
    doOnLayout {
        var spannableTextSeeMore: Spannable? = null
        var spannableTextSeeLess: Spannable? = null
        val restTextClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                onRestTextClickListener.invoke(widget)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = currentTextColor
                ds.isUnderlineText = false
            }
        }
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //change spannable string
                val currentTag = tag as? String?
                if (currentTag?.equals(seeMoreText) == true) {
                    text = spannableTextSeeLess
                    tag = seeLessText
                } else {
                    text = spannableTextSeeMore
                    tag = seeMoreText
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = currentTextColor
                ds.isUnderlineText = true
            }
        }

        val textToShow = if (maxLines > lineCount) {
            buildSpannedString {
                append(fullText)
                setSpan(
                    restTextClickableSpan,
                    0,
                    fullText.length,
                    0
                )
            }.toSpannable()
        } else {
            val separator = "..."
            val truncatedText = text.subSequence(0, layout.getLineEnd(maxLines - 1) - seeMoreText.length + 1)
            spannableTextSeeMore = buildSpannedString {
                append(truncatedText)
                append(separator)
                underline {
                    append(seeMoreText)
                }
                setSpan(
                    restTextClickableSpan,
                    0,
                    truncatedText.length,
                    0
                )
                setSpan(
                    clickableSpan,
                    truncatedText.length+separator.length,
                    truncatedText.length+separator.length + seeMoreText.length,
                    0
                )
            }.toSpannable()
            val fullTextWithSeeLess = text.subSequence(0, layout.getLineEnd(lineCount - 1))
            spannableTextSeeLess = buildSpannedString {
                append(fullTextWithSeeLess)
                append(" ")
                underline {
                    append(seeLessText)
                }
                setSpan(
                    restTextClickableSpan,
                    0,
                    fullTextWithSeeLess.length,
                    0
                )
                setSpan(
                    clickableSpan,
                    fullTextWithSeeLess.length+1,
                    fullTextWithSeeLess.length+1 + seeLessText.length,
                    0
                )
            }.toSpannable()
            spannableTextSeeMore  // default text to first
        }
        text = textToShow
        tag = seeMoreText
        movementMethod = LinkMovementMethod.getInstance()
    }
}