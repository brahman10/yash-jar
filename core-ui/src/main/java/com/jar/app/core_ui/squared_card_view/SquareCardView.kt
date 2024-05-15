package com.jar.app.core_ui.squared_card_view

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class SquareCardView : CardView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = (parent as? RecyclerView)?.measuredWidth ?: 0
        val width = if (parentWidth > 0) parentWidth / 2 else MeasureSpec.getSize(widthMeasureSpec)
        val adjustedMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        super.onMeasure(adjustedMeasureSpec, adjustedMeasureSpec)
    }
}


