package com.jar.app.core_image_picker.impl.ui.crop.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.jar.app.base.util.dp
import com.jar.app.core_image_picker.R

class CropAreaView(context: Context) : View(context) {

    private val topLeftCorner = RectF()
    private val topRightCorner = RectF()
    private val bottomLeftCorner = RectF()
    private val bottomRightCorner = RectF()
    private val topEdge = RectF()
    private val leftEdge = RectF()
    private val bottomEdge = RectF()
    private val rightEdge = RectF()

    var lockAspectRatio: Float = 0.toFloat()
        private set

    private var activeControl: Control? = null
    private val actualRect = RectF()
    private val tempRect = RectF()
    private var previousX: Int = 0
    private var previousY: Int = 0

    private var bottomPadding: Float = 0.toFloat()
    private var dimVisibile: Boolean = false
    private var frameVisible: Boolean = false

    private var dimPaint: Paint
    private var shadowPaint: Paint
    private var linePaint: Paint
    private var handlePaint: Paint
    private var handleAcentPaint: Paint
    private var framePaint: Paint

    private var interpolator = AccelerateDecelerateInterpolator()

    private val sidePadding: Float
    private val minWidth: Float
    private val handleRadius: Float
    private val smallHandleRadius: Float

    private var previousGridType: GridType? = null
    private var gridType: GridType? = null
    private var gridProgress: Float = 0.toFloat()
    private var gridAnimator: Animator? = null
    private val statusBarHeight = 48.dp
    private val middleBarHalfLength = 24.dp
    private val middleBarHeight = 6.dp

    private var listener: AreaViewListener? = null

    var isDragging: Boolean = false
        private set

    private var freeform = true
    private var circleBitmap: Bitmap? = null
    private val eraserPaint: Paint
    private var animator: Animator? = null
    var handX: Float = 0.0f
    var handY: Float = 0.0f

    val aspectRatio: Float
        get() = (actualRect.right - actualRect.left) / (actualRect.bottom - actualRect.top)

    var cropLeft: Float
        get() = actualRect.left
        @Keep
        private set(value) {
            actualRect.left = value
            invalidate()
        }

    var cropTop: Float
        get() = actualRect.top
        @Keep
        private set(value) {
            actualRect.top = value
            invalidate()
        }

    var cropRight: Float
        get() = actualRect.right
        @Keep
        private set(value) {
            actualRect.right = value
            invalidate()
        }

    var cropBottom: Float
        get() = actualRect.bottom
        @Keep
        private set(value) {
            actualRect.bottom = value
            invalidate()
        }

    val cropCenterX: Float
        get() = actualRect.left + (actualRect.right - actualRect.left) / 2.0f

    val cropCenterY: Float
        get() = actualRect.top + (actualRect.bottom - actualRect.top) / 2.0f

    val cropWidth: Float
        get() = actualRect.right - actualRect.left

    val cropHeight: Float
        get() = actualRect.bottom - actualRect.top

    val targetRectToFill: RectF
        get() {
            val rect = RectF()
            calculateRect(rect, aspectRatio)
            return rect
        }

    fun animateAreaView(x: Float, y: Float) {
        actualRect.top = y
        actualRect.right = x
        setActualRect(actualRect)
    }

    private enum class Control {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, LEFT, BOTTOM, RIGHT
    }

    interface AreaViewListener {
        fun onAreaChangeBegan()

        fun onAreaChange()

        fun onAreaChangeEnded()
    }

    enum class GridType {
        NONE, MINOR, MAJOR
    }

    private val lineThickness: Int
    private val handleSize: Int
    private val handleThickness: Int

    init {

        frameVisible = true
        dimVisibile = true
        lineThickness = 3.dp
        handleSize = 16.dp
        handleThickness = 3.dp

        sidePadding = 12.dp.toFloat()
        minWidth = 32.dp.toFloat()
        handleRadius = 9.dp.toFloat()
        smallHandleRadius = 7.dp.toFloat()
        gridType = GridType.NONE

        dimPaint = Paint()
        dimPaint.color = 0x60272239

        shadowPaint = Paint()
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.color = 0x1a000000
        shadowPaint.strokeWidth = 2.dp.toFloat()
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = -0x1
            strokeWidth = 1.dp.toFloat()
        }

        handlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, com.jar.app.core_ui.R.color.white)
        }
        handleAcentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, com.jar.app.core_ui.R.color.white)
        }
        eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0
            style = Paint.Style.FILL
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    fun setDimVisibility(visible: Boolean) {
        dimVisibile = visible
    }

    fun setFrameVisibility(visible: Boolean) {
        frameVisible = visible
    }

    fun setBottomPadding(value: Float) {
        bottomPadding = value
    }

    fun getInterpolator(): Interpolator {
        return interpolator
    }

    fun setListener(l: AreaViewListener) {
        listener = l
    }

    fun setBitmap(bitmap: Bitmap?, sideward: Boolean, fform: Boolean) {
        if (bitmap == null || bitmap.isRecycled) {
            return
        }
        freeform = fform
        var aspectRatio: Float
        aspectRatio = if (sideward) {
            bitmap.height.toFloat() / bitmap.width.toFloat()
        } else {
            bitmap.width.toFloat() / bitmap.height.toFloat()
        }

        if (!freeform) {
            aspectRatio = 1.0f
            lockAspectRatio = 1.0f
        }

        setActualRect(aspectRatio)
    }

    fun setFreeform(fform: Boolean) {
        freeform = fform
    }

    fun setActualRect(aspectRatio: Float) {
        calculateRect(actualRect, aspectRatio)
        updateTouchAreas()
        invalidate()
    }

    fun setActualRect(rect: RectF) {
        actualRect.set(rect)
        updateTouchAreas()
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        if (freeform) {

            val originX = actualRect.left.toInt() - lineThickness
            val originY = actualRect.top.toInt() - lineThickness
            val width = (actualRect.right - actualRect.left).toInt() + lineThickness * 2
            val height = (actualRect.bottom - actualRect.top).toInt() + lineThickness * 2
            handX = (originX + width).toFloat()
            handY = originY.toFloat()
            if (dimVisibile) {
                canvas.drawRect(
                    0f,
                    0f,
                    getWidth().toFloat(),
                    (originY + lineThickness).toFloat(),
                    dimPaint
                )
                canvas.drawRect(
                    0f,
                    (originY + lineThickness).toFloat(),
                    (originX + lineThickness).toFloat(),
                    (originY + height - lineThickness).toFloat(),
                    dimPaint
                )
                canvas.drawRect(
                    (originX + width - lineThickness).toFloat(),
                    (originY + lineThickness).toFloat(),
                    getWidth().toFloat(),
                    (originY + height - lineThickness).toFloat(),
                    dimPaint
                )
                canvas.drawRect(
                    0f,
                    (originY + height - lineThickness).toFloat(),
                    getWidth().toFloat(),
                    getHeight().toFloat(),
                    dimPaint
                )
            }

            if (!frameVisible) {
                return
            }

            val inset = handleThickness - lineThickness
            val gridWidth = width - handleThickness * 2
            val gridHeight = height - handleThickness * 2

            var type = gridType
            if (type == GridType.NONE && gridProgress > 0)
                type = previousGridType

            shadowPaint.alpha = (gridProgress * 26).toInt()
            linePaint.alpha = (gridProgress * 178).toInt()

            for (i in 0..2) {
                if (type == GridType.MINOR) {
                    for (j in 1..3) {
                        if (i == 2 && j == 3)
                            continue

                        canvas.drawLine(
                            (originX + handleThickness + gridWidth / 3 / 3 * j + gridWidth / 3 * i).toFloat(),
                            (originY + handleThickness).toFloat(),
                            (originX + handleThickness + gridWidth / 3 / 3 * j + gridWidth / 3 * i).toFloat(),
                            (originY + handleThickness + gridHeight).toFloat(),
                            shadowPaint
                        )
                        canvas.drawLine(
                            (originX + handleThickness + gridWidth / 3 / 3 * j + gridWidth / 3 * i).toFloat(),
                            (originY + handleThickness).toFloat(),
                            (originX + handleThickness + gridWidth / 3 / 3 * j + gridWidth / 3 * i).toFloat(),
                            (originY + handleThickness + gridHeight).toFloat(),
                            linePaint
                        )

                        canvas.drawLine(
                            (originX + handleThickness).toFloat(),
                            (originY + handleThickness + gridHeight / 3 / 3 * j + gridHeight / 3 * i).toFloat(),
                            (originX + handleThickness + gridWidth).toFloat(),
                            (originY + handleThickness + gridHeight / 3 / 3 * j + gridHeight / 3 * i).toFloat(),
                            shadowPaint
                        )
                        canvas.drawLine(
                            (originX + handleThickness).toFloat(),
                            (originY + handleThickness + gridHeight / 3 / 3 * j + gridHeight / 3 * i).toFloat(),
                            (originX + handleThickness + gridWidth).toFloat(),
                            (originY + handleThickness + gridHeight / 3 / 3 * j + gridHeight / 3 * i).toFloat(),
                            linePaint
                        )
                    }
                } else if (type == GridType.MAJOR) {
                    if (i > 0) {
                        canvas.drawLine(
                            (originX + handleThickness + gridWidth / 3 * i).toFloat(),
                            (originY + handleThickness).toFloat(),
                            (originX + handleThickness + gridWidth / 3 * i).toFloat(),
                            (originY + handleThickness + gridHeight).toFloat(),
                            shadowPaint
                        )
                        canvas.drawLine(
                            (originX + handleThickness + gridWidth / 3 * i).toFloat(),
                            (originY + handleThickness).toFloat(),
                            (originX + handleThickness + gridWidth / 3 * i).toFloat(),
                            (originY + handleThickness + gridHeight).toFloat(),
                            linePaint
                        )

                        canvas.drawLine(
                            (originX + handleThickness).toFloat(),
                            (originY + handleThickness + gridHeight / 3 * i).toFloat(),
                            (originX + handleThickness + gridWidth).toFloat(),
                            (originY + handleThickness + gridHeight / 3 * i).toFloat(),
                            shadowPaint
                        )
                        canvas.drawLine(
                            (originX + handleThickness).toFloat(),
                            (originY + handleThickness + gridHeight / 3 * i).toFloat(),
                            (originX + handleThickness + gridWidth).toFloat(),
                            (originY + handleThickness + gridHeight / 3 * i).toFloat(),
                            linePaint
                        )
                    }
                }
            }

            canvas.drawRect(
                (originX + inset).toFloat(),
                (originY + inset).toFloat(),
                (originX + width - inset).toFloat(),
                (originY + inset + lineThickness).toFloat(),
                framePaint
            )
            canvas.drawRect(
                (originX + inset).toFloat(),
                (originY + inset).toFloat(),
                (originX + inset + lineThickness).toFloat(),
                (originY + height - inset).toFloat(),
                framePaint
            )
            canvas.drawRect(
                (originX + inset).toFloat(),
                (originY + height - inset - lineThickness).toFloat(),
                (originX + width - inset).toFloat(),
                (originY + height - inset).toFloat(),
                framePaint
            )
            canvas.drawRect(
                (originX + width - inset - lineThickness).toFloat(),
                (originY + inset).toFloat(),
                (originX + width - inset).toFloat(),
                (originY + height - inset).toFloat(),
                framePaint
            )

            canvas.drawCircle(originX.toFloat(), originY.toFloat(), handleRadius, handlePaint)
            canvas.drawCircle(
                originX.toFloat(),
                originY.toFloat(),
                smallHandleRadius,
                handleAcentPaint
            )

            canvas.drawCircle(
                (originX + width).toFloat(),
                originY.toFloat(),
                handleRadius,
                handlePaint
            )
            canvas.drawCircle(
                (originX + width).toFloat(),
                originY.toFloat(),
                smallHandleRadius,
                handleAcentPaint
            )

            canvas.drawCircle(
                originX.toFloat(),
                (originY + height - handleThickness).toFloat(),
                handleRadius,
                handlePaint
            )
            canvas.drawCircle(
                originX.toFloat(),
                (originY + height - handleThickness).toFloat(),
                smallHandleRadius,
                handleAcentPaint
            )

            canvas.drawCircle(
                (originX + width).toFloat(),
                (originY + height - handleThickness).toFloat(),
                handleRadius,
                handlePaint
            )
            canvas.drawCircle(
                (originX + width).toFloat(),
                (originY + height - handleThickness).toFloat(),
                smallHandleRadius,
                handleAcentPaint
            )
            canvas.drawRoundRect(
                (originX + width / 2 - middleBarHalfLength).toFloat(),
                (originY - middleBarHeight + handleThickness).toFloat(),
                (originX + width / 2 + middleBarHalfLength).toFloat(),
                (originY + middleBarHeight).toFloat(),
                middleBarHeight.toFloat(),
                middleBarHeight.toFloat(),
                handlePaint
            )
            canvas.drawRoundRect(
                (originX - middleBarHeight + handleThickness).toFloat(),
                (originY + height / 2 - middleBarHalfLength).toFloat(),
                (originX + middleBarHeight).toFloat(),
                (originY + height / 2 + middleBarHalfLength).toFloat(),
                middleBarHeight.toFloat(),
                middleBarHeight.toFloat(),
                handlePaint
            )
            canvas.drawRoundRect(
                (originX + width / 2 - middleBarHalfLength).toFloat(),
                ((originY + height) - middleBarHeight).toFloat(),
                (originX + width / 2 + middleBarHalfLength).toFloat(),
                ((originY + height) + middleBarHeight - handleThickness).toFloat(),
                middleBarHeight.toFloat(),
                middleBarHeight.toFloat(),
                handlePaint
            )
            canvas.drawRoundRect(
                ((originX + width) - middleBarHeight).toFloat(),
                (originY + height / 2 - middleBarHalfLength).toFloat(),
                ((originX + width) + middleBarHeight - handleThickness).toFloat(),
                (originY + height / 2 + middleBarHalfLength).toFloat(),
                middleBarHeight.toFloat(),
                middleBarHeight.toFloat(),
                handlePaint
            )
        } else {
            if (circleBitmap == null || circleBitmap!!.width.toFloat() != actualRect.width()) {
                if (circleBitmap != null) {
                    circleBitmap!!.recycle()
                    circleBitmap = null
                }
                try {
                    circleBitmap = Bitmap.createBitmap(
                        actualRect.width().toInt(),
                        actualRect.height().toInt(),
                        Bitmap.Config.ARGB_8888
                    )
                    val circleCanvas = Canvas(circleBitmap!!)
                    circleCanvas.drawRect(0f, 0f, actualRect.width(), actualRect.height(), dimPaint)
                    circleCanvas.drawCircle(
                        actualRect.width() / 2,
                        actualRect.height() / 2,
                        actualRect.width() / 2,
                        eraserPaint
                    )
                    circleCanvas.setBitmap(null)
                } catch (ignore: Throwable) {

                }

            }
            canvas.drawRect(0f, 0f, width.toFloat(), actualRect.top.toInt().toFloat(), dimPaint)
            canvas.drawRect(
                0f,
                actualRect.top.toInt().toFloat(),
                actualRect.left.toInt().toFloat(),
                actualRect.bottom.toInt().toFloat(),
                dimPaint
            )
            canvas.drawRect(
                actualRect.right.toInt().toFloat(),
                actualRect.top.toInt().toFloat(),
                width.toFloat(),
                actualRect.bottom.toInt().toFloat(),
                dimPaint
            )
            canvas.drawRect(
                0f,
                actualRect.bottom.toInt().toFloat(),
                width.toFloat(),
                height.toFloat(),
                dimPaint
            )
            canvas.drawBitmap(
                circleBitmap!!,
                actualRect.left.toInt().toFloat(),
                actualRect.top.toInt().toFloat(),
                null
            )
        }
    }

    private fun updateTouchAreas() {
        val touchPadding = 32.dp

        topLeftCorner.set(
            actualRect.left - touchPadding,
            actualRect.top - touchPadding,
            actualRect.left + touchPadding,
            actualRect.top + touchPadding
        )
        topRightCorner.set(
            actualRect.right - touchPadding,
            actualRect.top - touchPadding,
            actualRect.right + touchPadding,
            actualRect.top + touchPadding
        )
        bottomLeftCorner.set(
            actualRect.left - touchPadding,
            actualRect.bottom - touchPadding,
            actualRect.left + touchPadding,
            actualRect.bottom + touchPadding
        )
        bottomRightCorner.set(
            actualRect.right - touchPadding,
            actualRect.bottom - touchPadding,
            actualRect.right + touchPadding,
            actualRect.bottom + touchPadding
        )

        topEdge.set(
            actualRect.left + touchPadding,
            actualRect.top - touchPadding,
            actualRect.right - touchPadding,
            actualRect.top + touchPadding
        )
        leftEdge.set(
            actualRect.left - touchPadding,
            actualRect.top + touchPadding,
            actualRect.left + touchPadding,
            actualRect.bottom - touchPadding
        )
        rightEdge.set(
            actualRect.right - touchPadding,
            actualRect.top + touchPadding,
            actualRect.right + touchPadding,
            actualRect.bottom - touchPadding
        )
        bottomEdge.set(
            actualRect.left + touchPadding,
            actualRect.bottom - touchPadding,
            actualRect.right - touchPadding,
            actualRect.bottom + touchPadding
        )
    }

    fun setLockedAspectRatio(aspectRatio: Float) {
        lockAspectRatio = aspectRatio
    }

    fun setGridType(type: GridType, animated: Boolean) {
        if (gridAnimator != null) {
            if (!animated || gridType != type) {
                gridAnimator!!.cancel()
                gridAnimator = null
            }
        }

        if (gridType == type)
            return

        previousGridType = gridType
        gridType = type

        val targetProgress = if (type == GridType.NONE) 0.0f else 1.0f
        if (!animated) {
            gridProgress = targetProgress
            invalidate()
        } else {
            gridAnimator =
                ObjectAnimator.ofFloat(this, "gridProgress", gridProgress, targetProgress)
            gridAnimator!!.duration = 200
            gridAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    gridAnimator = null
                }
            })
            if (type == GridType.NONE)
                gridAnimator!!.startDelay = 200
            gridAnimator!!.start()
        }
    }

    @Keep
    private fun setGridProgress(value: Float) {
        gridProgress = value
        invalidate()
    }

    private fun getGridProgress(): Float {
        return gridProgress
    }

    fun fill(targetRect: RectF, scaleAnimator: Animator, animated: Boolean) {
        if (animated) {
            if (animator != null) {
                animator!!.cancel()
                animator = null
            }

            val set = AnimatorSet()
            animator = set
            set.duration = 300

            val animators = arrayOfNulls<Animator>(5)
            animators[0] = ObjectAnimator.ofFloat(this, "cropLeft", targetRect.left)
            animators[0]?.interpolator = interpolator
            animators[1] = ObjectAnimator.ofFloat(this, "cropTop", targetRect.top)
            animators[1]?.interpolator = interpolator
            animators[2] = ObjectAnimator.ofFloat(this, "cropRight", targetRect.right)
            animators[2]?.interpolator = interpolator
            animators[3] = ObjectAnimator.ofFloat(this, "cropBottom", targetRect.bottom)
            animators[3]?.interpolator = interpolator
            animators[4] = scaleAnimator
            animators[4]?.interpolator = interpolator

            set.playTogether(*animators)
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    setActualRect(targetRect)
                    animator = null
                }
            })
            set.start()
        } else {
            setActualRect(targetRect)
        }
    }

    fun resetAnimator() {
        if (animator != null) {
            animator!!.cancel()
            animator = null
        }
    }

    fun calculateRect(rect: RectF, cropAspectRatio: Float) {
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val measuredHeight = measuredHeight.toFloat() - bottomPadding - statusBarHeight
        val aspectRatio = measuredWidth.toFloat() / measuredHeight
        val minSide = Math.min(measuredWidth.toFloat(), measuredHeight) - 2 * sidePadding
        val minSideNew = Math.min(measuredWidth.toFloat(), measuredHeight) - sidePadding
        val width = measuredWidth - 2 * sidePadding
        val height = measuredHeight - 2 * sidePadding
        val heightNew = measuredHeight - sidePadding
        val centerX = measuredWidth / 2.0f
        val centerY = statusBarHeight + measuredHeight / 2.0f

        when {
            Math.abs(1.0f - cropAspectRatio) < 0.0001 -> {
                left = centerX - minSideNew / 2.0f
                top = centerY - minSideNew / 2.0f
                right = centerX + minSideNew / 2.0f
                bottom = centerY + minSide / 2.0f
            }
            cropAspectRatio > aspectRatio -> {
                left = centerX - width / 2.0f
                top = centerY - width / cropAspectRatio / 2.0f
                right = centerX + width / 2.0f
                bottom = centerY + width / cropAspectRatio / 2.0f
            }
            else -> {
                left = centerX - heightNew * cropAspectRatio / 2.0f
                top = centerY - heightNew / 2.0f
                right = centerX + heightNew * cropAspectRatio / 2.0f
                bottom = centerY + height / 2.0f
            }
        }
        rect.set(left, top, right, bottom)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x - (parent as ViewGroup).x).toInt()
        val y = (event.y - (parent as ViewGroup).y).toInt()

        val action = event.actionMasked

        if (action == MotionEvent.ACTION_DOWN) {
            if (freeform) {
                when {
                    this.topLeftCorner.contains(x.toFloat(), y.toFloat()) -> activeControl =
                        Control.TOP_LEFT
                    this.topRightCorner.contains(x.toFloat(), y.toFloat()) -> activeControl =
                        Control.TOP_RIGHT
                    this.bottomLeftCorner.contains(x.toFloat(), y.toFloat()) -> activeControl =
                        Control.BOTTOM_LEFT
                    this.bottomRightCorner.contains(x.toFloat(), y.toFloat()) -> activeControl =
                        Control.BOTTOM_RIGHT
                    this.leftEdge.contains(x.toFloat(), y.toFloat()) -> activeControl = Control.LEFT
                    this.topEdge.contains(x.toFloat(), y.toFloat()) -> activeControl = Control.TOP
                    this.rightEdge.contains(x.toFloat(), y.toFloat()) -> activeControl =
                        Control.RIGHT
                    this.bottomEdge.contains(x.toFloat(), y.toFloat()) -> activeControl =
                        Control.BOTTOM
                    else -> {
                        activeControl = Control.NONE
                        return false
                    }
                }
            } else {
                activeControl = Control.NONE
                return false
            }
            previousX = x
            previousY = y
            setGridType(GridType.MAJOR, false)

            isDragging = true

            if (listener != null)
                listener!!.onAreaChangeBegan()

            return true
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            isDragging = false

            if (activeControl == Control.NONE)
                return false

            activeControl = Control.NONE

            if (listener != null)
                listener!!.onAreaChangeEnded()

            return true
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (activeControl == Control.NONE)
                return false

            tempRect.set(actualRect)

            val translationX = (x - previousX).toFloat()
            val translationY = (y - previousY).toFloat()
            previousX = x
            previousY = y

            when (activeControl) {
                Control.TOP_LEFT -> {
                    tempRect.left += translationX
                    tempRect.top += translationY

                    if (lockAspectRatio > 0) {
                        val w = tempRect.width()
                        val h = tempRect.height()

                        if (Math.abs(translationX) > Math.abs(translationY)) {
                            constrainRectByWidth(tempRect, lockAspectRatio)
                        } else {
                            constrainRectByHeight(tempRect, lockAspectRatio)
                        }

                        tempRect.left -= tempRect.width() - w
                        tempRect.top -= tempRect.width() - h
                    }
                }

                Control.TOP_RIGHT -> {
                    tempRect.right += translationX
                    tempRect.top += translationY

                    if (lockAspectRatio > 0) {
                        val h = tempRect.height()

                        if (Math.abs(translationX) > Math.abs(translationY)) {
                            constrainRectByWidth(tempRect, lockAspectRatio)
                        } else {
                            constrainRectByHeight(tempRect, lockAspectRatio)
                        }

                        tempRect.top -= tempRect.width() - h
                    }
                }

                Control.BOTTOM_LEFT -> {
                    tempRect.left += translationX
                    tempRect.bottom += translationY

                    if (lockAspectRatio > 0) {
                        val w = tempRect.width()

                        if (Math.abs(translationX) > Math.abs(translationY)) {
                            constrainRectByWidth(tempRect, lockAspectRatio)
                        } else {
                            constrainRectByHeight(tempRect, lockAspectRatio)
                        }

                        tempRect.left -= tempRect.width() - w
                    }
                }

                Control.BOTTOM_RIGHT -> {
                    tempRect.right += translationX
                    tempRect.bottom += translationY

                    if (lockAspectRatio > 0) {
                        if (Math.abs(translationX) > Math.abs(translationY)) {
                            constrainRectByWidth(tempRect, lockAspectRatio)
                        } else {
                            constrainRectByHeight(tempRect, lockAspectRatio)
                        }
                    }
                }

                Control.TOP -> {
                    tempRect.top += translationY

                    if (lockAspectRatio > 0) {
                        constrainRectByHeight(tempRect, lockAspectRatio)
                    }
                }

                Control.LEFT -> {
                    tempRect.left += translationX

                    if (lockAspectRatio > 0) {
                        constrainRectByWidth(tempRect, lockAspectRatio)
                    }
                }

                Control.RIGHT -> {
                    tempRect.right += translationX

                    if (lockAspectRatio > 0) {
                        constrainRectByWidth(tempRect, lockAspectRatio)
                    }
                }

                Control.BOTTOM -> {
                    tempRect.bottom += translationY

                    if (lockAspectRatio > 0) {
                        constrainRectByHeight(tempRect, lockAspectRatio)
                    }
                }

                else -> {
                }
            }

            if (tempRect.left < sidePadding) {
                if (lockAspectRatio > 0) {
                    tempRect.bottom =
                        tempRect.top + (tempRect.right - sidePadding) / lockAspectRatio
                }
                tempRect.left = sidePadding
            } else if (tempRect.right > width - sidePadding) {
                tempRect.right = width - sidePadding
                if (lockAspectRatio > 0) {
                    tempRect.bottom = tempRect.top + tempRect.width() / lockAspectRatio
                }
            }

            val topPadding = statusBarHeight + sidePadding
            val finalBottomPadidng = bottomPadding + sidePadding
            if (tempRect.top < topPadding) {
                if (lockAspectRatio > 0) {
                    tempRect.right =
                        tempRect.left + (tempRect.bottom - topPadding) * lockAspectRatio
                }
                tempRect.top = topPadding
            } else if (tempRect.bottom > height - finalBottomPadidng) {
                tempRect.bottom = height - finalBottomPadidng
                if (lockAspectRatio > 0) {
                    tempRect.right = tempRect.left + tempRect.height() * lockAspectRatio
                }
            }

            if (tempRect.width() < minWidth) {
                tempRect.right = tempRect.left + minWidth
            }
            if (tempRect.height() < minWidth) {
                tempRect.bottom = tempRect.top + minWidth
            }

            if (lockAspectRatio > 0) {
                if (lockAspectRatio < 1) {
                    if (tempRect.width() <= minWidth) {
                        tempRect.right = tempRect.left + minWidth
                        tempRect.bottom = tempRect.top + tempRect.width() / lockAspectRatio
                    }
                } else {
                    if (tempRect.height() <= minWidth) {
                        tempRect.bottom = tempRect.top + minWidth
                        tempRect.right = tempRect.left + tempRect.height() * lockAspectRatio
                    }
                }
            }

            setActualRect(tempRect)

            if (listener != null) {
                listener!!.onAreaChange()
            }

            return true
        }

        return false
    }

    private fun constrainRectByWidth(rect: RectF, aspectRatio: Float) {
        val w = rect.width()
        val h = w / aspectRatio

        rect.right = rect.left + w
        rect.bottom = rect.top + h
    }

    private fun constrainRectByHeight(rect: RectF, aspectRatio: Float) {
        val h = rect.height()
        val w = h * aspectRatio

        rect.right = rect.left + w
        rect.bottom = rect.top + h
    }

    fun getCropRect(rect: RectF) {
        rect.set(actualRect)
    }
}