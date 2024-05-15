package com.jar.app.core_ui.dynamic_cards

import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyController
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.DynamicCardType
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_ui.dynamic_cards.base.CustomLinearCarousalModel
import com.jar.app.core_ui.dynamic_cards.card_library.*
import com.jar.app.core_ui.dynamic_cards.model.*
import kotlinx.coroutines.CoroutineScope

class DynamicEpoxyController(
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit = {},
    private val onPrimaryCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onEndIconClick: (staticInfoData: StaticInfoData, cardEventData: CardEventData) -> Unit = { _, _ -> }
) : EpoxyController() {

    var cards: MutableList<DynamicCard>? = null
        set(value) {
            field = value
            cancelPendingModelBuild()
            requestModelBuild()
        }

    override fun buildModels() {
        if (cards == null) {
            ShimmerEpoxyModel()
                .id("shimmer_loader")
                .addTo(this)
        } else {
            val list = cards!!
            var currentIndex = 0

            //Can't use forEach loop as it requires custom steps based on conditions
            while (currentIndex < list.size) {
                val it = list[currentIndex]

                when (it.getCardType()) {
                    /** If [LARGE]: add it as usual & move forward **/
                    DynamicCardType.LARGE -> {
                        LargeCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick,
                            onEndIconClick
                        )
                            .id(it.featureType)
                            .addTo(this)
                        currentIndex++
                    }
                    DynamicCardType.MEDIUM -> {
                        /** If [MEDIUM]: add it as usual & move forward **/
                        MediumCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick,
                            onEndIconClick
                        )
                            .id(it.featureType)
                            .addTo(this)
                        currentIndex++
                    }
                    DynamicCardType.TICKET -> {
                        /** If [TICKET]: add it as usual & move forward **/
                        TicketCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.featureType)
                            .addTo(this)
                        currentIndex++
                    }
                    DynamicCardType.VIDEO -> {
//                        /** If [CardType.VIDEO]: add it as usual & move forward **/
//                        VideoCardEpoxyModel(it as LibraryCardData, onCardShown, onPrimaryCtaClick)
//                            .id(it.featureType)
//                            .addTo(this)
//                        currentIndex++
                    }
                    DynamicCardType.STRIP_ONE -> {
                        /** If [STRIP_ONE]: add it as usual & move forward **/
                        StripOneCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.featureType)
                            .addTo(this)
                        currentIndex++
                    }
                    DynamicCardType.STRIP_TWO -> {
                        /** If [STRIP_TWO]: add it as usual & move forward **/
                        StripTwoCardEpoxyModel(
                            uiScope,
                            it as LibraryCardData,
                            onCardShown,
                            onPrimaryCtaClick
                        )
                            .id(it.featureType)
                            .addTo(this)
                        currentIndex++
                    }
                    DynamicCardType.NONE -> {} //ignore for now
                    DynamicCardType.SMALL -> {
                        /**
                         * As per the requirement all the small cards with the same group should show up in an horizontal list.
                         * Loop until we are finding the small cards & add them in a list
                         * If you are wondering how come we are getting all the small cards with same group type in a sorted order
                         * then please check the list manipulation & sorting logic in [HomeFragmentViewModel]
                         * **/

                        val smallCards = mutableListOf<DynamicCard>()

                        /**
                         * In case we get continuous section of small cards, we need groupId to halt the below while loop
                         * For ex - [S1 - [SMALL,SMALL,SMALL] & [S2 - [SMALL, SMALL]]]
                         * */
                        val firstElementGroupId = (list[currentIndex] as LibraryCardData).groupId

                        while (currentIndex < list.size &&
                            list[currentIndex].getCardType() == DynamicCardType.SMALL &&
                            (list[currentIndex] as LibraryCardData).groupId == firstElementGroupId
                        ) {
                            smallCards.add(list[currentIndex])
                            currentIndex++
                        }

                        //Convert the [smallCards] to SmallCardEpoxyModel
                        val smallCardModel = smallCards.map {
                            SmallCardEpoxyModel(
                                uiScope,
                                it as LibraryCardData,
                                onCardShown,
                                onPrimaryCtaClick
                            )
                                .id(it.featureType)
                        }

                        //Generating unique ID for a small card group
                        val id =
                            smallCardModel.joinToString { (it as SmallCardEpoxyModel).libraryCardViewData.featureType }

                        //Now finally add the list to carousal model
                        CustomLinearCarousalModel()
                            .id(id)
                            .initialPrefetchItemCount(smallCardModel.size)
                            .padding(
                                Carousel.Padding.dp(
                                    0, 16
                                )
                            )
                            .models(smallCardModel)
                            .numViewsToShowOnScreen(1.9f)
                            .addTo(this)
                    }

                    else -> {
                        // Do Nothing..
                    }
                }
            }
        }
    }
}