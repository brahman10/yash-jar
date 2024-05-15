package com.jar.app.core_ui.jarProgressBarWithDrawable

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.text.TextPaint
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toBitmap
import com.jar.app.core_ui.R


class JarProgressBarWithDrawable @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ProgressBar(context, attrs) {


    var drawable: Drawable? = null
    private var mImage: Bitmap? = null
    private var textBitmap: Bitmap? = null
    private var drawableRadius: Int? = null
    private var drawableBorder: Int? = null
    private var userInitials: String? = null
    private var animate: Boolean = false
    private var shapeDrawable: ShapeDrawable? = null

    private lateinit var barAnimator: ValueAnimator
    private val outerCirclePaint by lazy {
        Paint().apply {
            color = Color.WHITE
            strokeWidth = 30F
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            isDither = true
        }
    }
    private val innerCirclePaint by lazy {
        Paint().apply {
            color = ContextCompat.getColor(context, R.color.lightBgColor)
            strokeWidth = 30F
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            isDither = true
        }
    }
    private val textPaint by lazy {
        TextPaint().apply {
            color = Color.WHITE
            textSize = 10 * resources.displayMetrics.density
            isAntiAlias = true
            textAlignment = TEXT_ALIGNMENT_CENTER

        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.JarProgressBarWithDrawable) {
            userInitials = getString(R.styleable.JarProgressBarWithDrawable_user_initial)
            drawable = getDrawable(R.styleable.JarProgressBarWithDrawable_progress_image)
            drawableRadius = getInt(R.styleable.JarProgressBarWithDrawable_drawable_radius, 0)
            drawableBorder = getInt(R.styleable.JarProgressBarWithDrawable_drawable_border, 0)
            animate = getBoolean(R.styleable.JarProgressBarWithDrawable_animate, false)

        }

        setupDrawable()

    }

    fun getCircledBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(),
            (bitmap.height / 2).toFloat(), (bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    private fun setupDrawable() {

        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)

                if (drawable != null) {
                    mImage = drawable?.toBitmap()
                   /* mImage = mImage?.let {
                        getCircledBitmap(it)
                    }*/
                    mImage = mImage?.let { getResizedBitmap(it, width, height-10) }
                } else {
                    textBitmap = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888)
                    val customCanvas = Canvas(textBitmap!!)
                    shapeDrawable = ShapeDrawable(OvalShape()).apply {
                        setBounds(5, 5, height - 5, height - 5)
                        paint.color = innerCirclePaint.color
                        draw(customCanvas)
                    }
                }


                return true
            }

        })
    }


    private fun getResizedBitmap(mImage: Bitmap, width: Int, height: Int): Bitmap {
        val matrix = Matrix()
        val src = RectF(0f, 0f, mImage.width.toFloat(), mImage.height.toFloat())
        val dest = RectF(0f, 0f, width.toFloat(), height.toFloat())
        matrix.setRectToRect(src, dest, Matrix.ScaleToFit.CENTER)
        return Bitmap.createBitmap(mImage, 0, 0, mImage.width, mImage.height, matrix, true)
    }

    fun setUserInitials(initals: String) {
        this.userInitials = initals
        postInvalidate()
    }

    fun setDrawableRadius(radius: Int) {
        this.drawableRadius = radius
        postInvalidate()
    }

    fun setProgressImage(drawable: Drawable) {
        this.drawable = drawable
        setupDrawable()
        invalidate()
    }

    fun setDrawableBorder(border: Int) {
        drawableBorder = border
        postInvalidate()
    }

    fun setAnimate(animate: Boolean) {
        this.animate = animate
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val outerCirclePosX =
            (width - paddingStart - paddingEnd) * (progress.toFloat() / 100) - height / 2f + 5
        canvas.drawCircle(
            if (progress == 0) height / 2f else outerCirclePosX,
            height / 2f,
            height / 2f - 15,
            outerCirclePaint
        )
        if (mImage != null) {
            mImage?.let { bitmap ->
                canvas.drawBitmap(
                    bitmap,
                    if (progress == 0) {
                        height / 2f - bitmap.width / 2
                    } else {
                        (width - paddingStart - paddingEnd) * (progress.toFloat() / 100) - height / 2f - bitmap.width / 2 + 5
                    },
                    5f,
                    null
                )
            }
        } else {
            userInitials?.let { initials ->
                val textHeight = textPaint.descent() - textPaint.ascent();
                val textOffset = (textHeight / 2) - textPaint.descent()
                textBitmap?.let { bitmap ->
                    canvas.drawBitmap(
                        bitmap,
                        if (progress == 0) 0f else {
                            outerCirclePosX - bitmap.width / 2
                        },
                        0f,
                        null
                    )
                    val textPositionX =
                        ((width - paddingStart - paddingEnd) * (progress.toFloat() / 100))
                    val textWidth = textPaint.measureText(initials)
                    canvas.drawText(
                        initials,
                        if (progress == 0) {
                            bitmap.width / 2f - textWidth / 2f
                        } else {
                            if (initials.length <= 1) {
                                textPositionX - bitmap.width / 2f - 3
                            } else {
                                textPositionX - bitmap.width / 2f - textWidth / 2f + 5
                            }
                        },
                        shapeDrawable!!.bounds.centerY().toFloat() + textOffset,
                        textPaint
                    )

                }

            }
        }
    }


    override fun setProgress(progress: Int) {
        if (animate) {
            setAnimatedProgress(progress)
        } else {
            super.setProgress(progress)
        }
    }

    private fun setAnimatedProgress(progress: Int) {
        barAnimator = ValueAnimator.ofFloat(0f, 1f)
        barAnimator.duration = 1000
        super.setProgress(0)
        barAnimator.interpolator = LinearInterpolator()
        barAnimator.addUpdateListener { animation ->
            val interpolation = animation.animatedValue as Float
            super.setProgress((interpolation * progress).toInt())
        }

        if (!barAnimator.isStarted) {
            barAnimator.start()
        }

    }

}