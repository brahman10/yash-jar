package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_breakdown

import android.content.Context
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingsBreakdownData
import com.jar.app.feature_daily_investment.shared.domain.model.Details
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.DAYS_IN_MONTH
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DailySavingsV2BreakdownGenerator @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) {

    suspend fun generateBottomSheetData(
        contextRef: WeakReference<Context>?,
        totalInvestedAmount: Float,
        totalInvestedAmountAfterTax: Float,
        totalAmountSavedAfterAppreciation: Float,
        totalNumberOfDays: Int,
        dailyInvestment: Float,
        totalGramsOfGold: Float,
        gstOnTotalAmountSaved: Float
    ): DailySavingsBreakdownData =
        withContext(dispatcherProvider.default) {

            val context = contextRef?.get()!!

            return@withContext DailySavingsBreakdownData(
                heading = context.getString(
                    R.string.daily_investment_Breakdown_heading,
                    dailyInvestment.toInt()
                ),
                chips = listOf(
                    context.getString(R.string.daily_investment_amount_selection_jar1_label),
                    context.getString(R.string.daily_investment_amount_selection_jar2_label),
                    context.getString(R.string.daily_investment_amount_selection_jar3_label)
                ),
                subHeading1 = context.getString(R.string.daily_investment_Breakdown_subHeading1),
                breakDownSummary = listOf(
                    Details(
                        label = context.getString(R.string.daily_investment_breakdown_worth_label),
                        value = context.getString(
                            R.string.feature_daily_investment_add_rupee_symbol,
                            (totalAmountSavedAfterAppreciation.toInt()).getFormattedAmount(0)
                        ),
                        color = com.jar.app.core_ui.R.color.color_EEEAFF
                    ),
                    Details(
                        label = context.getString(R.string.daily_investment_breakdown_gold_saved_label),
                        value = context.getString(
                            R.string.daily_investment_breakdown_gold_saved_value,
                            totalGramsOfGold
                        ),
                        color = com.jar.app.core_ui.R.color.color_FFCD5A
                    )
                ),
                subHeading2 = context.getString(R.string.daily_investment_Breakdown_subHeading2),
                breakDownDetails = listOf(
                    Details(
                        label = context.resources.getQuantityString(
                            R.plurals.daily_investment_breakdown_total_investment_label,
                            totalNumberOfDays / DAYS_IN_MONTH,
                            dailyInvestment.toInt(),
                            totalNumberOfDays / DAYS_IN_MONTH,
                        ),
                        value = context.getString(
                            R.string.feature_daily_investment_add_rupee_symbol,
                            totalInvestedAmount.toInt().getFormattedAmount(0)
                        ),
                        color = com.jar.app.core_ui.R.color.color_D5CDF2
                    ),
                    Details(
                        label = context.getString(R.string.daily_investment_breakdown_gst_details_label),
                        value = "- ₹" + gstOnTotalAmountSaved.toInt()
                            .getFormattedAmount(maximumFractionDigits = 0),
                        color = com.jar.app.core_ui.R.color.color_D5CDF2
                    ),
                    Details(
                        label = context.getString(R.string.daily_investment_breakdown_appreciation_label),
                        value = "+ ₹" + (totalAmountSavedAfterAppreciation - totalInvestedAmountAfterTax).toInt()
                            .getFormattedAmount(maximumFractionDigits = 0),
                        color = com.jar.app.core_ui.R.color.color_D5CDF2
                    )
                ),
                gst = context.getString(R.string.daily_investment_breakdown_gst_label),
                warning = context.getString(R.string.daily_investment_breakdown_warning_label)
            )
        }
}