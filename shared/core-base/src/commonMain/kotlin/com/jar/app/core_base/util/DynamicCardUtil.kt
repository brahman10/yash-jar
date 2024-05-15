package com.jar.app.core_base.util

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.GridListCard
import com.jar.app.core_base.domain.model.card_library.HorizontalListCard
import com.jar.app.core_base.domain.model.card_library.LibraryCardData

object DynamicCardUtil {

    /**
     * sort and rearrange the dynamic cards
     */
    fun rearrangeDynamicCards(list: MutableList<DynamicCard>) {
        //First sort all the cards by order
        list.sortBy {
            it.getSortKey()
        }
        /**
         * Now filter all the cards which needs to be shown in horizontal lists & group them by group Id
         * (because all the the cards with same group will show up in a single horizontal recyclerview)
         **/
        val horizontalCardMap = list.filter {
            return@filter when (it.getCardType()) {
                in getCardTypeForHorizontalList() -> true
                else -> false
            }
        }
            .groupBy { (it as LibraryCardData).groupId }

        horizontalCardMap.forEach {
            val items = it.value

            //Now remove all these cards for this group from the original list
            list.minusAssign(items)

            //Now sort these cards by order among themselves
            val sortedItems = items.sortedBy {
                it.getSortKey()
            }

            sortedItems.forEachIndexed { index, dynamicCard ->
                dynamicCard.horizontalPosition = index
            }

            //Now find the least order from these cards
            val leastOrder = sortedItems[0].getSortKey()

            //Find the index where this group of these cards needs to be merged in the final list
            val index =
                list.indexOfFirst { it.getSortKey() >= leastOrder && it.getCardType() !in getCardTypeForHorizontalList() }

            val horizontalOrGridList =
                if (sortedItems[0].getCardType() != DynamicCardType.HOMEFEED_TYPE_THREE) {
                    HorizontalListCard(
                        cards = sortedItems,
                        order = leastOrder
                    )
                } else {
                    GridListCard(
                        cards = sortedItems,
                        order = leastOrder
                    )
                }

            if (index == -1) {
                list.add(
                    horizontalOrGridList
                ) //Means this should come at the top
            } else {
                list.add(
                    index, // Else add it on the corresponding position
                    horizontalOrGridList
                )
            }
        }
    }

    fun getCardTypeForHorizontalList(): List<DynamicCardType> {
        return listOf(
            DynamicCardType.SMALL,
            DynamicCardType.HOMEFEED_TYPE_ONE,
            DynamicCardType.HOMEFEED_TYPE_TWO,
            DynamicCardType.HOMEFEED_TYPE_THREE,
            DynamicCardType.HOMEFEED_TYPE_ELEVEN
        )
    }

    fun getCardTypeToUpdateUserInteraction(): List<DynamicCardType> {
        return listOf(
            DynamicCardType.HOMEFEED_TYPE_ONE,
            DynamicCardType.HOMEFEED_TYPE_TWO,
            DynamicCardType.HOMEFEED_TYPE_THREE,
            DynamicCardType.HOMEFEED_TYPE_FOUR,
            DynamicCardType.HOMEFEED_TYPE_FIVE,
            DynamicCardType.HOMEFEED_TYPE_SIX,
            DynamicCardType.NONE
        )
    }
}