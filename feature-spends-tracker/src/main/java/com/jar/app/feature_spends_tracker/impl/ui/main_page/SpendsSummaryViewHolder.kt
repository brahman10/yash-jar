package com.jar.app.feature_spends_tracker.impl.ui.main_page

import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_spends_tracker.R
import com.jar.app.feature_spends_tracker.databinding.FeatureSpendsTrackerCellSpendsSummaryBinding
import com.jar.app.feature_spends_tracker.impl.ui.components.GradientBarChart
import com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData.SpendsData

class SpendsSummaryViewHolder(
    private val binding: FeatureSpendsTrackerCellSpendsSummaryBinding,
    private val listener: RvSummaryClickListener
) : BaseViewHolder(binding.root), View.OnClickListener {

    private var data: SpendsData? = null

    init {
        binding.apply {
            balanceView.setDebounceClickListener { view -> onClick(view) }
            spendsView.setDebounceClickListener { view -> onClick(view) }
            tvPromptText.setDebounceClickListener { view -> onClick(view) }
            graphOverLay.setDebounceClickListener { view -> onClick(view) }
            btnSaveGold.setOnClickListener { view -> onClick(view) }
        }


    }

    fun setSpendsSummary(spendsData: SpendsData) {
        data = spendsData

        binding.apply {
            tvMonthSummaryLabel.text = HtmlCompat.fromHtml(
                spendsData.spendsTrackerResponseSummary.summaryTitle,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvSpendsLabel.text = spendsData.spendsTrackerResponseSummary.spendsText
            tvBalanceLabel.text = spendsData.spendsTrackerResponseSummary.balanceText
            tvSpendsAmount.text = spendsData.spendsTrackerResponseSummary.spends
            tvBalanceAmount.text = spendsData.spendsTrackerResponseSummary.balance
            tvPromptText.text = HtmlCompat.fromHtml(
                spendsData.spendsTrackerResponseSummary.spendsPrompt,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvSpendsComparisonLabel.text = spendsData.spendsGraphDetails.spendsComparisonText

            Glide.with(context).load(spendsData.spendsFlowRedirectionDetails.buyGoldIcon)
                .override(50.dp).into(icSaveGold)

            Glide.with(context)
                .load(spendsData.spendsTrackerResponseSummary.spendsPromptIcon).override(40.dp)
                .into(ivPromptImage)

            Glide.with(context)
                .load(spendsData.spendsTrackerResponseSummary.spendsIcon)
                .into(ivSpends)

            Glide.with(context)
                .load(spendsData.spendsTrackerResponseSummary.balanceIcon)
                .into(ivBalance)

            tvSaveGoldTitle.text = spendsData.spendsFlowRedirectionDetails.headers
            btnSaveGold.setText(spendsData.spendsFlowRedirectionDetails.buttonText)

            tvSaveGoldFooter.text =
                spendsData.spendsFlowRedirectionDetails.spendsTrackerEducationinfo

            tvSpendsTransactionLabel.text =
                spendsData.spendsFlowRedirectionDetails.spendsTransactionText
            tvTransactionMonth.text = spendsData.spendsFlowRedirectionDetails.monthText

            previousMonthLabel.text = spendsData.spendsGraphDetails.subHeader
        }


        val maxValue = spendsData.spendsGraphDetails.yaxis.max()
        val formattedYAxis = spendsData.spendsGraphDetails.yaxis.sortedDescending().map { value ->
            "₹${(value.toFloat() / 1000).roundDown(1)}k"
        }
        binding.graphComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GradientBarChart(
                    data = spendsData.spendsGraphDetails.xaxisValues.reversed(),
                    yAxis = formattedYAxis,
                    xAxis = spendsData.spendsGraphDetails.xaxis.reversed(),
                    maxValue = maxValue
                )
            }
        }
    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.balanceView -> {
                listener.balanceViewClickListener()
            }

            R.id.spendsView -> {
                listener.spendsViewClickListener()
            }

            R.id.tvPromptText -> {
                listener.promptViewClickListener()
            }

            R.id.graphOverLay -> {
                listener.graphViewClickListener()
            }

            R.id.btnSaveGold -> {
                data?.let { spendsData ->
                    listener.btnSaveGoldClickListener(spendsData = spendsData)
                }
            }


        }

    }
}