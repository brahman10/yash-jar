package com.jar.app.feature_gold_sip.shared.util

import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MonthGenerator {

    suspend fun getMonthList(
        recommendedMonthDate: Int
    ): List<WeekOrMonthData> =
        withContext(Dispatchers.Default) {
            val list = ArrayList<WeekOrMonthData>()
            for (i in 1 until 31)
                list.add(WeekOrMonthData(null, i, i == recommendedMonthDate))
            return@withContext list
        }
}