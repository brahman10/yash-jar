package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.jar.app.base.util.dp
import com.jar.app.feature_daily_investment.shared.domain.model.Steps
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeFiveFooterViewBinding

class TypeFiveEpoxyModelFooterView @JvmOverloads constructor(
    context: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr)
{

    private var binding: FeatureHomepageCellTypeFiveFooterViewBinding? = null

    init {
        binding = FeatureHomepageCellTypeFiveFooterViewBinding.inflate(LayoutInflater.from(context))
        binding?.let {
            addView(it.root)
        }
    }

    fun changeLayoutParamsFooterImage(){
        binding?.apply {

            ivCellFooter.layoutParams.height = 16.dp
            ivCellFooter.layoutParams.width = 36.dp

            ivCellFooter.requestLayout()
        }
    }

    fun setData(data: Steps) {
        binding?.apply {
            tvCellFooter.text = data.title
            Glide.with(root).load(data.imageUrl)
                .into(ivCellFooter)
        }
    }

}