package com.myjar.app.feature_graph_manual_buy.impl.ui

import android.content.Context
import android.widget.ImageView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class ImageMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        // You can modify the imageView here if needed, based on the entry's data
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        val offsetBelow = -10f  // Adjust this value to position the marker further down
        return MPPointF(-width / 2f, -height.toFloat() - offsetBelow)
    }
}