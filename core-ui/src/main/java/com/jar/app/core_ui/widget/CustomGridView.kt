package com.jar.app.core_ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jar.app.core_ui.R

/*
* This view will paint a grid with a custom height, custom color and
* options should x - axis and y - axis should be rendered.
* */
class CustomGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint()
    private var gridColor: Int
    private var lineHeight: Int
    private var drawVertical: Boolean
    private var drawHorizontal: Boolean

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomGridView,
            0, 0
        )

        gridColor = a.getColor(R.styleable.CustomGridView_gridColor, Color.BLACK)
        lineHeight = a.getInteger(R.styleable.CustomGridView_gridHeight, 10)
        drawVertical = a.getBoolean(R.styleable.CustomGridView_drawVertical, true)
        drawHorizontal = a.getBoolean(R.styleable.CustomGridView_drawHorizontal, true)

        a.recycle()

        paint.color = gridColor
        paint.strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height

        if (drawVertical) {
            for (i in 0 until width step lineHeight) {
                canvas.drawLine(i.toFloat(), 0f, i.toFloat(), height.toFloat(), paint)
            }
        }

        if (drawHorizontal) {
            for (i in 0 until height step lineHeight) {
                canvas.drawLine(0f, i.toFloat(), width.toFloat(), i.toFloat(), paint)
            }
        }
    }
}
