package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.*
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeBigRightImageBinding
import kotlinx.coroutines.*

internal class BigRightImageOldBtnEpoxyModel(
    private val libraryCardViewData: LibraryCardData,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : CustomViewBindingEpoxyModel<FeatureHomepageCellTypeBigRightImageBinding>(R.layout.feature_homepage_cell_type_big_right_image),
    HomeFeedCard {

    private val eventData by lazy {
        CardEventData(
            map = mutableMapOf(
                DynamicCardEventKey.CardType to libraryCardViewData.cardType,
                DynamicCardEventKey.FeatureType to libraryCardViewData.featureType,
                DynamicCardEventKey.Data to libraryCardViewData.state.toString(),
                DynamicCardEventKey.CardVerticalPosition to libraryCardViewData.verticalPosition.toString(),
                DynamicCardEventKey.CardHorizontalPosition to libraryCardViewData.horizontalPosition.toString(),
                DynamicCardEventKey.CardTitle to libraryCardViewData.cardMeta?.title?.textList?.getOrNull(
                    0
                )?.text.orEmpty(),
                DynamicCardEventKey.CardDescription to libraryCardViewData.cardMeta?.description?.textList?.getOrNull(
                    0
                )?.text.orEmpty(),
                DynamicCardEventKey.TopLabelTitle to libraryCardViewData.cardMeta?.labelTop?.text?.convertToRawString()
                    .toString(),
                DynamicCardEventKey.BottomLabelTitle to libraryCardViewData.cardMeta?.labelBottom?.text?.convertToRawString()
                    .toString(),
            ),
            fromSection = libraryCardViewData.header?.convertToRawString(),
            fromCard = libraryCardViewData.featureType
        )
    }

    private var visibilityState: Int? = null

    override fun getBinding(view: View): FeatureHomepageCellTypeBigRightImageBinding {
        return FeatureHomepageCellTypeBigRightImageBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellTypeBigRightImageBinding) {
        binding.root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
        binding.tvTitle.text = libraryCardViewData.cardMeta?.title?.textList?.getOrNull(0)?.text.orEmpty()
        binding.tvDescription.text = libraryCardViewData.cardMeta?.description?.textList?.getOrNull(0)?.text.orEmpty()
        binding.tvDescription2.text = libraryCardViewData.cardMeta?.footer?.getOrNull(0).orEmpty()
        binding.btnAction.setText(libraryCardViewData.cardMeta?.cta?.text?.textList?.getOrNull(0)?.text.orEmpty())

        val data = PrimaryActionData(
            type = PrimaryActionType.DEEPLINK,
            value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
            order = libraryCardViewData.getSortKey(),
            cardType = libraryCardViewData.getCardType(),
            featureType = libraryCardViewData.featureType
        )

        libraryCardViewData.cardMeta?.infographic?.url?.let {
            Glide.with(binding.root).load(it)
                .fitCenter()
                .into(binding.ivImage)
        } ?: run {
            Glide.with(binding.root)
                .load("${BaseConstants.CDN_BASE_URL}/Images/Gold_Redemption/gold-diamond-card.png")
                .fitCenter()
                .into(binding.ivImage)
        }

        binding.btnAction.setDebounceClickListener {
            onActionClick.invoke(data, eventData)
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)

        this.visibilityState = visibilityState

        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {

            startShowEventJob(
                uiScope,
                isCardFullyVisible = {
                    this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                },
                onCardShownEvent = {
                    onCardShown.invoke(eventData)
                }
            )
        }
    }
}