package com.jar.app.feature_homepage.impl.util.showcase

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.jar.app.base.util.getStatusBarHeight
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_homepage.R


class ShowCaseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val BLACK_80_OPACITY = "#CC000000"
        private const val ANIMATION_DURATION_FOR_INTERNAL_VIEWS = 1100L
        private const val HOLLOW_VIEW_ANIMATION_DURATION = 600L
        private const val OVERLAY_VIEW_ANIMATION_DURATION = 300L
    }

    private var header: String? = null
    private var title: String? = null
    private var firstView: View? = null
    private var lastView: View? = null
    private var targetViewList: MutableList<View> = mutableListOf()
    private val overlayPath = Path()
    private val overlayRect = RectF()
    private val overlayPaint = Paint()
    private val viewPaint = Paint()
    private val overlayBackgroundRect = RectF()
    private val overlayBackgroundPaint by lazy {
        Paint().apply {
            color = Color.parseColor(BLACK_80_OPACITY) // Semi-transparent black color
        }
    }
    private val textPaint: Paint = Paint()

    private var headerAlpha = 0f // Initial alpha value
    private var arrowAlpha = 0f // Initial alpha value
    private var titleAlpha = 0f // Initial alpha value
    private var hollowViewAlpha = 0f // Initial alpha value
    private var overlayAlpha = 0f // Initial alpha value
    private fun getUpdatedBitmap(viewBottom: Int, viewRight: Int) =
        BitmapFactory.decodeResource(
            resources,
            getCurveDrawableAccordingToLocationOfTargetView(viewBottom, viewRight)
        )

    private val statusBarHeight: Int by lazy {
        resources.getDimensionPixelSize(
            resources.getIdentifier("status_bar_height", "dimen", "android")
        )
    }

    private val navigationBarHeight: Int by lazy {
        resources.getDimensionPixelSize(
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        )
    }

    private var isTopAligned = false
    private var isLeftAligned = false
    private val screenHeight by lazy { resources.displayMetrics.heightPixels }
    private val screenWidth by lazy { resources.displayMetrics.widthPixels }
    private val viewLocation = IntArray(2)
    private val firstViewLocation = IntArray(2)
    private val textBounds = Rect()
    private var focusShape = FocusShape.ROUNDED_RECTANGLE
    private val interFont by lazy {
        try {
            ResourcesCompat.getFont(context, com.jar.app.core_ui.R.font.inter)
        } catch (exception: NotFoundException) {
            exception.printStackTrace()
            null
        }
    }
    private val interBoldFont by lazy {
        try {
            ResourcesCompat.getFont(context, com.jar.app.core_ui.R.font.inter_bold)
        } catch (exception: NotFoundException) {
            exception.printStackTrace()
            null
        }
    }

    init {
        overlayPaint.apply {
            color = Color.TRANSPARENT
            style = Paint.Style.FILL
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private fun getCurveDrawableAccordingToLocationOfTargetView(
        viewBottom: Int,
        viewRight: Int
    ): Int {
        // To identify whether the targeted view is left aligned or right
        isLeftAligned = viewRight < (screenWidth / 2)

        // To identify whether the targeted view is top aligned or bottom aligned
        isTopAligned = viewBottom < (screenHeight / 2)

        return if (isLeftAligned && isTopAligned)
            R.drawable.feature_homepage_ic_right_curve_to_top
        else if (isLeftAligned.not() && isTopAligned)
            R.drawable.feature_homepage_ic_left_curve_to_top
        else if (isLeftAligned && isTopAligned.not())
            R.drawable.feature_homepage_ic_left_curve_to_bottom
        else
            R.drawable.feature_homepage_ic_right_curve_to_bottom
    }

    fun setOverlayContent(
        header: String?,
        title: String,
        targetView: List<View>,
        focusShape: FocusShape = FocusShape.ROUNDED_RECTANGLE
    ) {
        this.title = title
        this.header = header
        if (targetView.isNotEmpty()) {
            this.firstView = targetView[0]
            this.lastView = targetView[targetView.size - 1]
        }
        this.targetViewList = targetView.toMutableList()
        this.focusShape = focusShape
        animateViewsLogic()
        invalidate() // Redraw the view when the content changes
    }

    fun clearTargetList() {
        this.firstView = null
        this.lastView = null
        targetViewList.clear()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        firstView?.let { firstView ->
            firstView.getLocationOnScreen(firstViewLocation)
            lastView?.let {
                // Draw background overlay
                overlayBackgroundPaint.alpha = (255 * overlayAlpha).toInt()
                canvas.drawRect(
                    0f,
                    0f,
                    screenWidth.toFloat(),
                    screenHeight.toFloat(),
                    overlayBackgroundPaint
                )

                // Draw highlighting sections
                drawHiglhtingSections(canvas)


                // Draw the header
                var padding =
                    resources.getDimensionPixelSize(com.jar.app.core_ui.R.dimen.dimen_22dp)
                        .toFloat()
                it.getLocationOnScreen(viewLocation)
                val viewLeft = viewLocation[0]
                val viewTop = firstViewLocation[1] - context.getStatusBarHeight()
                val viewRight = viewLeft + it.width
                val viewBottom = viewLocation[1] - context.getStatusBarHeight() + it.height
                var textX: Float
                var textY: Float
                header?.let { text ->
                    textPaint.apply {
                        textSize =
                            resources.getDimensionPixelSize(com.jar.app.core_ui.R.dimen.dimen_18sp)
                                .toFloat()
                        style = Paint.Style.FILL
                        interBoldFont?.let { typeface = it }
                        color = Color.WHITE
                        translationZ = 6f
                        alpha = (255 * headerAlpha).toInt()
                    }
                    textX = it.rootView.left.toFloat() + padding
                    textY = viewTop - textPaint.textSize
                    canvas.drawText(text, textX, textY, textPaint)
                }


                // Draw Arrow Bitmap
                padding =
                    resources.getDimensionPixelSize(com.jar.app.core_ui.R.dimen.dimen_16dp)
                        .toFloat()
                textX =
                    if (viewRight <= (screenWidth / 2)) (it.rootView.left.toFloat() + padding) else (screenWidth / 2).toFloat()

                val bitmap = getUpdatedBitmap(viewBottom, viewRight)

                textPaint.alpha = (255 * headerAlpha).toInt()
                textY = if (viewTop <= (screenHeight / 2))
                    viewBottom + padding
                else
                    viewTop - bitmap.height - padding

                textPaint.alpha = (255 * arrowAlpha).toInt()
                canvas.drawBitmap(bitmap, textX, textY, textPaint)


                // Draw Title
                textPaint.apply {
                    textSize =
                        resources.getDimensionPixelSize(com.jar.app.core_ui.R.dimen.dimen_20sp)
                            .toFloat()
                    interFont?.let { typeface = it }
                }
                textX = (it.rootView.left.toFloat() + padding)
                textY =
                    if (viewTop <= (screenHeight / 2))
                        viewBottom + bitmap.height + padding * 2
                    else
                        viewTop - bitmap.height - padding * 2
                title?.let {
                    textPaint.apply {
                        getTextBounds(it, 0, it.length, textBounds)
                        alpha = (255 * titleAlpha).toInt()
                    }
                    val lines = it.split("\n")
                    for (line in lines) {
                        canvas.drawText(line, textX, textY, textPaint)
                        textY += -textPaint.ascent() + textPaint.descent() // Move down to the next line
                    }
                }
            }
        }
    }

    private fun drawHiglhtingSections(canvas: Canvas) {
        overlayRect.setEmpty()
        // Get the position of the target views in the screen
        overlayPaint.alpha = (255 * hollowViewAlpha).toInt()
        if (targetViewList.isNotEmpty())
            targetViewList.map {
                val viewLocation = IntArray(2)
                it.getLocationOnScreen(viewLocation)

                // Draw the overlay rectangle
                val viewLeft = viewLocation[0]
                val viewTop = viewLocation[1] - context.getStatusBarHeight()
                val viewRight = viewLeft + it.width
                val viewBottom = viewTop + it.height
                if (focusShape == FocusShape.ROUNDED_RECTANGLE) {
                    // Set the bounds for the overlay rectangle
                    overlayRect.set(
                        viewLeft.toFloat(),
                        viewTop.toFloat(),
                        viewRight.toFloat(),
                        viewBottom.toFloat()
                    )
                    overlayPath.reset()
                    val corners = floatArrayOf(
                        20f, 20f,   // Top left radius in px
                        20f, 20f,   // Top right radius in px
                        20f, 20f,     // Bottom right radius in px
                        20f, 20f      // Bottom left radius in px
                    )
                    overlayPath.addRoundRect(overlayRect, corners, Path.Direction.CW)
                    canvas.drawPath(overlayPath, overlayPaint)
                } else {
                    // Set the bounds for the overlay rectangle
                    overlayRect.set(
                        viewLeft.toFloat() + 10,
                        viewTop.toFloat() + 10,
                        viewRight.toFloat() - 10,
                        viewBottom.toFloat() - 10
                    )
                    canvas.drawRect(overlayRect, overlayPaint)
                }
            }
    }

    private fun animateViewsLogic() {

        val headerAnimator = ValueAnimator.ofFloat(0f, 1f)
        headerAnimator.duration =
            ANIMATION_DURATION_FOR_INTERNAL_VIEWS // Animation header text view duration in milliseconds
        headerAnimator.addUpdateListener { valueAnimator ->
            headerAlpha = valueAnimator.animatedValue as Float
            invalidate() // Redraw the view with the updated alpha value
        }
        headerAnimator.start()

        val arrowAnimator = ValueAnimator.ofFloat(0f, 1f)
        arrowAnimator.duration =
            ANIMATION_DURATION_FOR_INTERNAL_VIEWS // Animation duration arrow image view in milliseconds
        arrowAnimator.addUpdateListener { valueAnimator ->
            arrowAlpha = valueAnimator.animatedValue as Float
            invalidate() // Redraw the view with the updated alpha value
        }
        arrowAnimator.start()

        val titleAnimator = ValueAnimator.ofFloat(0f, 1f)
        titleAnimator.duration =
            ANIMATION_DURATION_FOR_INTERNAL_VIEWS // Animation duration for title text view in milliseconds
        titleAnimator.addUpdateListener { valueAnimator ->
            titleAlpha = valueAnimator.animatedValue as Float
            invalidate() // Redraw the view with the updated alpha value
        }
        titleAnimator.start()

        val hollowViewAnimator = ValueAnimator.ofFloat(0f, 1f)
        hollowViewAnimator.duration =
            HOLLOW_VIEW_ANIMATION_DURATION // Animation duration for Hollow View creation in milliseconds
        hollowViewAnimator.addUpdateListener { valueAnimator ->
            hollowViewAlpha = valueAnimator.animatedValue as Float
            invalidate() // Redraw the view with the updated alpha value
        }
        hollowViewAnimator.start()


        val overlayAnimator = ValueAnimator.ofFloat(0f, 1f)
        overlayAnimator.duration =
            OVERLAY_VIEW_ANIMATION_DURATION // Animation duration for Background Overlay View in milliseconds
        overlayAnimator.addUpdateListener { valueAnimator ->
            overlayAlpha = valueAnimator.animatedValue as Float
            invalidate() // Redraw the view with the updated alpha value
        }
        overlayAnimator.start()
    }
}
