package com.myjar.app.feature_graph_manual_buy.impl.ui.viewHolder

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.myjar.app.feature_graph_manual_buy.R
import com.myjar.app.feature_graph_manual_buy.databinding.ManualBuyGraphLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.model.ManualBuyGraphItem
import com.myjar.app.feature_graph_manual_buy.impl.ui.ImageMarkerView

internal class ManualBuyGraphViewHolder(
    private val binding: ManualBuyGraphLayoutBinding
): BaseViewHolder(binding.root), BaseResources {

    init {
        setupLineChart()
    }

    fun bind(data: ManualBuyGraphItem) {
        with(binding) {
            graphView.text = data.graphItem?.totalManualSavings?.key
            tvTotalAmount.text = data.graphItem?.totalManualSavings?.value
            tvExpectedGrowth.text = data.graphItem?.expectedGrowth?.key
            tvMinAndMaxGrowth.text = data.graphItem?.expectedGrowth?.value
            tvStartDate.text = data.graphItem?.trendsStartText
            tvEndDate.text = data.graphItem?.trendsEndText
            tvMidDate.text = data.graphItem?.trendsMidText
        }
        setGraphData(data.graphItem?.xaxis ?: emptyList(), data.graphItem?.yaxis ?: emptyList(), data.graphItem?.midGraphAmount, data.graphItem?.trendsEndAmount)
    }

    private fun setupLineChart() {
        with(binding) {
            lineChart.setScaleEnabled(false)
            lineChart.isDragEnabled = false
            lineChart.setTouchEnabled(false)
            lineChart.isDoubleTapToZoomEnabled = false
            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.setDrawBorders(false)
            lineChart.setDrawGridBackground(false)
            lineChart.setTouchEnabled(true)
            lineChart.isHighlightPerTapEnabled = true
            lineChart.setPinchZoom(false)

            lineChart.xAxis.apply {
                setDrawLimitLinesBehindData(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawGridLinesBehindData(false)
            }

            lineChart.axisLeft.apply {
                setDrawTopYLabelEntry(false)
                setDrawLimitLinesBehindData(true)
                setDrawZeroLine(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawGridLinesBehindData(false)
                granularity = 0f
            }
            lineChart.axisRight.apply {
                setDrawTopYLabelEntry(false)
                setDrawLimitLinesBehindData(false)
                setDrawZeroLine(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawGridLinesBehindData(false)
                granularity = 0f
            }
        }
    }

    private fun setGraphData(
        xAxis: List<String>,
        yAxis: List<String>,
        midGraphAmount: String?,
        trendsEndAmount: String?, ) {
        val midIndex = yAxis.indexOf("$midGraphAmount")
        if (midIndex == -1) return  // If midGraphAmount is not found in yAxis, simply return

        // Dividing the lists based on the midIndex
        val xAxis1 = xAxis.subList(0, midIndex + 1)
        val yAxis1 = yAxis.subList(0, midIndex + 1)

        val midGraphAmountInFloat =  midGraphAmount.toFloatOrZero()
        val trendsEndAmountInFloat =  trendsEndAmount.toFloatOrZero()

        val totalIncrease = trendsEndAmountInFloat - midGraphAmountInFloat
        val incrementPerX = totalIncrease / (yAxis.size - 1)

        val entries1 = List(xAxis1.size) { index -> Entry(index.toFloat(), yAxis1[index].toFloat()) }
        val entries2 = mutableListOf<Entry>().apply {
            add(Entry(midIndex.toFloat(), midGraphAmountInFloat))
            var currentValue = midGraphAmountInFloat
            for (i in 1 until (yAxis.size)) {
                currentValue += incrementPerX
                add(Entry((i + midIndex).toFloat(), currentValue))
            }
        }

        val dataSet1 = createDataSet(entries1, R.drawable.feature_manual_buy_graph_yellow_dark_yellow_gradient, com.jar.app.core_ui.R.color.color_EEEAFF, "GOLD_DATA_SET")
        val dataSet2 = createDataSet(entries2, R.drawable.feature_manual_buy_graph_blue_gradiant, com.jar.app.core_ui.R.color.color_1EA787, "GOLD_DATA_SET2")

        val lineData = LineData(dataSet1, dataSet2)
        binding.lineChart.data = lineData

        val marker = ImageMarkerView(context, R.layout.layout_graph_hilghter)
        binding.lineChart.marker = marker

        binding.lineChart.highlightValues(arrayOf(
            Highlight(entries1.last().x, entries1.last().y, 0),
            Highlight(entries2.first().x, entries2.first().y, 1)
        ))
        binding.lineChart.setTouchEnabled(false)

        val limitX = entries1.last().x  // or entries2.first().x, they should be the same
        val limitLine = LimitLine(limitX)
        limitLine.lineColor = Color.parseColor("#686474")
        limitLine.lineWidth = 2f
        limitLine.enableDashedLine(10f, 10f, 0f)
        binding.lineChart.xAxis.addLimitLine(limitLine)


        binding.lineChart.invalidate()
        binding.lineChart.animateXY(300, 300, Easing.EaseOutExpo)

    }

    private fun createDataSet(entries: List<Entry>, gradientDrawableRes: Int, colorRes: Int, label: String): LineDataSet {
        val dataSet = LineDataSet(entries, label)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawCircles(false)
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawIcons(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawHorizontalHighlightIndicator(false)
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.isHighlightEnabled = true
        dataSet.lineWidth = 2.5f
        dataSet.setDrawFilled(true)

        val drawable = ContextCompat.getDrawable(binding.graphView.context, gradientDrawableRes)
        dataSet.fillDrawable = drawable
        dataSet.color = ContextCompat.getColor(binding.graphView.context, colorRes)

        return dataSet
    }
}