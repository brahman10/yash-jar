package com.jar.app.feature_lending.impl.ui.repeat_withdrawal_landing

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.jar.app.base.util.dp
import com.jar.app.feature_lending.R
import java.lang.ref.WeakReference

class VariableWidthIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val DEFAULT_COUNT = 3
    private val spacing = 2.dp
    private var selectedDrawable: Int = R.drawable.feature_lending_bg_selected_tab_indicator
    private var unSelectedDrawable: Int = R.drawable.feature_lending_bg_unselected_tab_indicator
    private var indicatorCount = DEFAULT_COUNT
    private var selectedPosition = 0

    private val indicators = ArrayList<WeakReference<AppCompatImageView>>()

    init {
        orientation = HORIZONTAL
        createIndicator()
    }

    fun setSelectedDrawable(@DrawableRes selectedDrawable: Int) {
        this.selectedDrawable = selectedDrawable
        invalidate()
    }

    fun setUnSelectedDrawable(@DrawableRes unSelectedDrawable: Int) {
        this.unSelectedDrawable = unSelectedDrawable
        invalidate()
    }

    fun setIndicatorCount(count: Int) {
        this.indicatorCount = count
        createIndicator()
    }

    private fun createIndicator() {
        indicators.clear()
        removeAllViews()
        for (count in 0 until indicatorCount) {
            val indicator = AppCompatImageView(context)
            indicator.id = View.generateViewId()
            addView(indicator)
            val layoutParams = indicator.layoutParams as MarginLayoutParams
            layoutParams.marginEnd = spacing
            indicator.layoutParams = layoutParams
            indicators.add(WeakReference(indicator))
        }
        setIndicatorDrawables()
    }

    private fun setIndicatorDrawables() {
        indicators.forEachIndexed { index, reference ->
            val drawableRes =
                if (index == selectedPosition) selectedDrawable else unSelectedDrawable
            reference.get()?.setImageResource(drawableRes)
        }
        postInvalidate()
    }

    fun selectIndicator(position: Int) {
        this.selectedPosition = position
        setIndicatorDrawables()
        invalidate()
    }
}