package com.jar.app.feature_lending.impl.ui.step_view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep

internal class LendingStepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var mStepViewType: StepViewType = StepViewType.VERTICAL
    private val mRecyclerView: RecyclerView
    private var mStepsAdapter: LendingStepAdapter? = null

    init {
        removeAllViews()
        mRecyclerView = RecyclerView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            isNestedScrollingEnabled = false
            overScrollMode = OVER_SCROLL_NEVER
        }
        setupRecyclerView()
        addView(mRecyclerView)
    }

    private fun setupRecyclerView() {
        mRecyclerView.layoutManager = when (mStepViewType) {
            StepViewType.HORIZONTAL -> LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            StepViewType.VERTICAL -> LinearLayoutManager(context)
        }
        mStepsAdapter = LendingStepAdapter(mStepViewType)
        mRecyclerView.adapter = mStepsAdapter
    }

    fun setSteps(steps: List<LendingProgressStep>,stepViewType: StepViewType = StepViewType.VERTICAL) {
        this.mStepViewType = stepViewType
        mStepsAdapter?.submitList(steps)
        invalidate()
        requestLayout()
    }

    enum class StepViewType {
        HORIZONTAL, VERTICAL
    }
}