package com.jar.app.feature_lending_kyc.impl.ui.step_view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.util.dp
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_lending_kyc.impl.data.KycStep

class KycStepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mStepViewType: StepViewType = StepViewType.VERTICAL
    private val mRecyclerView: RecyclerView
    private var mStepsAdapter: KycStepsAdapter? = null
    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()
    private val spaceItemDecoration = SpaceItemDecoration(20.dp, 6.dp)

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

    fun setStepViewType(viewType: StepViewType) {
        this.mStepViewType = viewType
        setupRecyclerView()
        invalidate()
    }

    private fun setupRecyclerView() {
        mRecyclerView.layoutManager = when (mStepViewType) {
            StepViewType.HORIZONTAL -> LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            StepViewType.VERTICAL -> LinearLayoutManager(context)
        }
        mStepsAdapter = KycStepsAdapter(mStepViewType)
        mRecyclerView.adapter = mStepsAdapter
    }

    fun setSteps(steps: List<KycStep>) {
        mStepsAdapter?.submitList(steps)
        invalidate()
        requestLayout()
    }

    enum class StepViewType {
        HORIZONTAL, VERTICAL
    }

}