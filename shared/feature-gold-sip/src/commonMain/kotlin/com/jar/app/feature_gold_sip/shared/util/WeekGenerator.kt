package com.jar.app.feature_gold_sip.shared.util

import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeekGenerator {

    suspend fun getWeekList(
        recommendedWeekDay: Int
    ): List<WeekOrMonthData> =
        withContext(Dispatchers.Default) {
            val list = ArrayList<WeekOrMonthData>()
            list.add(
                WeekOrMonthData(
                    StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_sunday),
                    1, recommendedWeekDay == 1
                )
            )
            list.add(
                WeekOrMonthData(
                    StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_monday),
                    2, recommendedWeekDay == 2
                )
            )
            list.add(
                WeekOrMonthData(
                    StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_tuesday),
                    3, recommendedWeekDay == 3
                )
            )
            list.add(
                WeekOrMonthData(
                    StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_wednesday),
                    4, recommendedWeekDay == 4
                )
            )
            list.add(
                WeekOrMonthData(
                    StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_thursday),
                    5, recommendedWeekDay == 5
                )
            )
            list.add(
                WeekOrMonthData(
                    StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_friday),
                    6, recommendedWeekDay == 6
                )
            )
            list.add(
                WeekOrMonthData(
                    StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_saturday),
                    7, recommendedWeekDay == 7
                )
            )
            return@withContext list
        }

    fun getWeekFromDay(
        day: Int
    ): ResourceStringDesc {
        return when (day) {
            1 -> StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_sunday)
            2 -> StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_monday)
            3 -> StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_tuesday)
            4 -> StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_wednesday)
            5 -> StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_thursday)
            6 -> StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_friday)
            7 -> StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_saturday)
            else -> {
                StringDesc.Resource(GoldSipMR.strings.feature_gold_sip_sunday)
            }
        }
    }
}