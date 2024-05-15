package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.databinding.FeatureHomepageTypeBigRightImageWithTwoCtaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference


class TypeBigRightImageWithTwoCtaEpoxyModel(
    private val uiScope: CoroutineScope,
    private val libraryCardViewData: LibraryCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null,
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageTypeBigRightImageWithTwoCtaBinding>(
        com.jar.app.feature_homepage.R.layout.feature_homepage_type_big_right_image_with_two_cta
    ) {

    private var visibilityState: Int? = null
    private val eventData by lazy {
        CardEventData(
            map = mutableMapOf(
                DynamicCardEventKey.CardType to libraryCardViewData.cardType,
                DynamicCardEventKey.FeatureType to libraryCardViewData.featureType,
                DynamicCardEventKey.Data to libraryCardViewData.state.toString(),
                DynamicCardEventKey.CardVerticalPosition to libraryCardViewData.verticalPosition.toString(),
                DynamicCardEventKey.CardHorizontalPosition to libraryCardViewData.horizontalPosition.toString(),
                DynamicCardEventKey.CardTitle to libraryCardViewData.cardMeta?.title?.textList?.getOrNull(0)?.text.orEmpty(),
                DynamicCardEventKey.CardDescription to libraryCardViewData.cardMeta?.description?.textList?.getOrNull(0)?.text.orEmpty(),
                DynamicCardEventKey.TopLabelTitle to libraryCardViewData.cardMeta?.labelTop?.text?.convertToRawString()
                    .toString(),
                DynamicCardEventKey.BottomLabelTitle to libraryCardViewData.cardMeta?.labelBottom?.text?.convertToRawString()
                    .toString(),
            ),
            fromSection = libraryCardViewData.header?.convertToRawString(),
            fromCard = libraryCardViewData.featureType
        )
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

    private var job: Job? = null
    private var binding: FeatureHomepageTypeBigRightImageWithTwoCtaBinding? = null

    override fun getBinding(view: View): FeatureHomepageTypeBigRightImageWithTwoCtaBinding {
        return FeatureHomepageTypeBigRightImageWithTwoCtaBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageTypeBigRightImageWithTwoCtaBinding) {
        this.binding = binding
        setupSpendsTrackerHomePageCard()
    }

    private fun setupSpendsTrackerHomePageCard() {
        binding?.apply{
            root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
            val contextRef = WeakReference(root.context)
            ivStartIcon.isVisible = libraryCardViewData.cardMeta?.startIcon != null
            Glide.with(root.context)
                .load(libraryCardViewData.cardMeta?.startIcon.orEmpty())
                .into(ivStartIcon)
            tvTitleTop.text= libraryCardViewData.cardMeta?.title?.convertToString(contextRef)
            tvDescriptionText.text = libraryCardViewData.cardMeta?.description?.convertToString(contextRef)
            Glide.with(root.context)
                .load(libraryCardViewData.cardMeta?.infographic?.url)
                .override(140.dp)
                .into(ivInfoGraphic)
            btnPrimaryCta.setText(libraryCardViewData.cardMeta?.cta?.text?.textList?.first()?.text.orEmpty())
            val primaryData = PrimaryActionData(
                type = PrimaryActionType.DEEPLINK,
                value = libraryCardViewData.cardMeta?.cta?.text?.textList?.first()?.deepLink.orEmpty(),
                order = libraryCardViewData.getSortKey(),
                cardType = libraryCardViewData.getCardType(),
                featureType = libraryCardViewData.featureType
            )
            btnPrimaryCta.setDebounceClickListener {
                onActionClick.invoke(primaryData, eventData)
            }

            btnSecondaryCta.isVisible = libraryCardViewData.cardMeta?.cta?.text?.textList?.getOrNull(1) != null
            btnSecondaryCta.setText(libraryCardViewData.cardMeta?.cta?.text?.textList?.getOrNull(1)?.text.orEmpty())
            val params: LinearLayoutCompat.LayoutParams = LinearLayoutCompat.LayoutParams(
                0.dp, LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            )
            params.weight = if (libraryCardViewData.cardMeta?.cta?.text?.textList?.getOrNull(1) != null) 1.0f else 2.0f
            btnPrimaryCta.layoutParams = params
            val secondaryData = PrimaryActionData(
                type = PrimaryActionType.DEEPLINK,
                value = libraryCardViewData.cardMeta?.cta?.text?.textList?.getOrNull(1)?.deepLink.orEmpty(),
                order = libraryCardViewData.getSortKey(),
                cardType = libraryCardViewData.getCardType(),
                featureType = libraryCardViewData.featureType
            )
            btnSecondaryCta.setDebounceClickListener {
                onActionClick.invoke(secondaryData, eventData)
            }

            libraryCardViewData.cardMeta?.labelTop?.text?.let {
                tvLabelTop.isVisible = true
                tvLabelTop.text = it.convertToString(contextRef)
            } ?: kotlin.run {
                tvLabelTop.isVisible = false
            }
        }

    }
}