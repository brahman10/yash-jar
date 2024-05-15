package com.jar.app.core_image_picker.impl.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.jar.app.base.util.dp
import com.jar.app.core_image_picker.R

class PreviewSelfieOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnTouchListener {

    private var ovalRect: RectF? = null
    private val padding = 30.dp
    private var halfOvalHeight = 200.dp

    init {
        setBackgroundColor(ContextCompat.getColor(context, com.jar.app.core_ui.R.color.transparent))
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private val ovalPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(
            context,
            com.jar.app.core_ui.R.color.bgBackgroundColor
        )
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 1.dp.toFloat()
    }

    fun setHalfOvalHeight(halfOvalHeight:Int){
        this.halfOvalHeight = halfOvalHeight
        invalidate()
    }
    fun getCropRect() = ovalRect

    fun cropImageToOverlay(bitmap: Bitmap): Bitmap {
        val width = ovalRect?.width()?.toInt() ?: 0
        val height = ovalRect?.height()?.toInt() ?: 0
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val matrix = Matrix()
        matrix.postTranslate((width / 2).toFloat(), (height / 2).toFloat())
        Canvas(resultBitmap).drawBitmap(bitmap, matrix, Paint(FILTER_BITMAP_FLAG))
        return resultBitmap
    }

    private val scrimPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(
            context,
            com.jar.app.core_ui.R.color.bgColor
        )
        style = Paint.Style.FILL
    }

    private val eraserPaint: Paint = Paint().apply {
        color = 0
        strokeWidth = ovalPaint.strokeWidth
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
        val top = ((height / 2) - halfOvalHeight).toFloat()
        val bottom = ((height / 2) + halfOvalHeight).toFloat()
        if (ovalRect == null)
            ovalRect = RectF(
                padding.toFloat(),
                top,
                (width - padding).toFloat(),
                bottom
            )
        ovalRect?.let {
            eraserPaint.style = Paint.Style.FILL
            canvas.drawOval(it, eraserPaint)
            eraserPaint.style = Paint.Style.STROKE
            canvas.drawOval(it, eraserPaint)
            canvas.drawOval(it, ovalPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

}