package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.util.Log
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeNineCardBinding
import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.jar.app.base.util.orFalse
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData

internal class TypeNineEpoxyModel(
    internal val libraryCardViewData: LibraryCardData,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellTypeNineCardBinding>(R.layout.feature_homepage_cell_type_nine_card) {

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

    private var job: Job? = null

    private var shimmerLayout: ShimmerFrameLayout? = null

    override fun getBinding(view: View): FeatureHomepageCellTypeNineCardBinding {
        return FeatureHomepageCellTypeNineCardBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellTypeNineCardBinding) {
        when (libraryCardViewData.cardMeta?.infographic?.getInfographicType()) {
            InfographicType.GIF,
            InfographicType.IMAGE -> {
                Glide.with(binding.root).load(libraryCardViewData.cardMeta?.infographic!!.url)
                    .into(binding.ivCardImage)
            }
            else -> {
                //Do Nothing
            }
        }
        binding.tvHeader.text = libraryCardViewData.cardMeta?.labelTop?.text?.textList?.getOrNull(0)?.text.toString().orEmpty()
        binding.tvTittle.text = libraryCardViewData.cardMeta?.title?.textList?.getOrNull(0)?.text.toString().orEmpty()
        binding.tvDescription.text = libraryCardViewData.cardMeta?.description?.textList?.getOrNull(0)?.text.toString().orEmpty()
        binding.btnSaveNow.setText(libraryCardViewData.cardMeta?.cta?.text?.textList?.getOrNull(0)?.text.toString().orEmpty())

        val data = PrimaryActionData(
            type = libraryCardViewData.cardMeta?.cta?.getPrimaryActionType()!!,
            value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
            order = libraryCardViewData.getSortKey(),
            cardType = libraryCardViewData.getCardType(),
            featureType = libraryCardViewData.featureType
        )

        binding.root.setDebounceClickListener {
            onActionClick.invoke(data, eventData)
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            onCardShown.invoke(eventData)

            if (libraryCardViewData.cardMeta?.shouldRunShimmer.orFalse())
                startShimmer()
        } else if (visibilityState == VisibilityState.INVISIBLE) {
            stopShimmer()
        } else if (visibilityState == VisibilityState.PARTIAL_IMPRESSION_INVISIBLE) {
            stopShimmer()
        }
    }

    private fun startShimmer() {
        job?.cancel()
        job = uiScope.launch {
            delay(1000)
            if (isActive) {
                shimmerLayout?.showShimmer(false)
                shimmerLayout?.startShimmer()
            }
        }
    }

    private fun stopShimmer() {
        job?.cancel()
        shimmerLayout?.stopShimmer()
        shimmerLayout?.hideShimmer()
        shimmerLayout?.clearAnimation()
    }

}