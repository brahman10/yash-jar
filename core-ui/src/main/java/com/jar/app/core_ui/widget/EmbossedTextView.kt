package com.jar.app.core_ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.EmbossMaskFilter
import android.graphics.LinearGradient
import android.graphics.MaskFilter
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class EmbossedTextView : AppCompatTextView {
    companion object {
        private const val COLOR_AA8C4B = "#AA8C4B"
        private const val COLOR_576265 = "#576265"
        private const val COLOR_9EA1A1 = "#9EA1A1"
        private const val COLOR_848B8A = "#848B8A"
        private const val COLOR_757A7B = "#757A7B"
    }


    var GOLD_GRADIENT = intArrayOf(
        Color.parseColor(COLOR_AA8C4B),
        Color.parseColor(COLOR_AA8C4B)
    )
    var DIAMOND_GRADIENT = intArrayOf(
        Color.parseColor(COLOR_576265),
        Color.parseColor(COLOR_9EA1A1),
        Color.parseColor(COLOR_848B8A),
        Color.parseColor(COLOR_576265),
        Color.parseColor(COLOR_576265),
        Color.parseColor(COLOR_757A7B),
        Color.parseColor(COLOR_576265)
    )

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    private fun init() {
        val direction = floatArrayOf(0f, -1.0f, 0.5f)
        val filter: MaskFilter = EmbossMaskFilter(direction, 0.8f, 15f, 1f)
        val paint = paint
        paint.setMaskFilter(filter)
        setColor(GOLD_GRADIENT)
        setShadowLayer(0.6f, 1f, 1f, Color.BLACK)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
    }

    fun setColor(color: IntArray?) {
        val paint = paint
        val width = paint.measureText("₹5000")
        val textShader: Shader =
            LinearGradient(0f, 0f, width, this.textSize, color!!, null, Shader.TileMode.CLAMP)
        paint.setShader(textShader)
    }
}
