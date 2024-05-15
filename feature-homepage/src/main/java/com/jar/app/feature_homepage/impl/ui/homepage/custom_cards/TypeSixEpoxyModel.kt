package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_base.domain.model.card_library.StaticInfoType
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.*
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeSixBinding
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

internal class TypeSixEpoxyModel(
    private val libraryCardViewData: LibraryCardData,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    private val onEndIconClick: (staticInfoData: StaticInfoData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : CustomViewBindingEpoxyModel<FeatureHomepageCellTypeSixBinding>(R.layout.feature_homepage_cell_type_six),
    HomeFeedCard {

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

    private var visibilityState: Int? = null

    private var job: Job? = null

    private var root: View? = null

    override fun getBinding(view: View): FeatureHomepageCellTypeSixBinding {
        return FeatureHomepageCellTypeSixBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageCellTypeSixBinding) {
        val contextRef = WeakReference(binding.root.context)
        root = binding.root
        binding.root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
        binding.desc.text = libraryCardViewData.cardMeta?.description?.convertToString(contextRef)
        Glide.with(binding.root).load(libraryCardViewData.cardMeta?.infographic?.url)
            .into(binding.imageView)
        binding.rightFooter.setText(libraryCardViewData.cardMeta?.cta?.text?.convertToString(contextRef))
        val data = PrimaryActionData(
            type = PrimaryActionType.DEEPLINK,
            value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
            order = libraryCardViewData.getSortKey(),
            cardType = libraryCardViewData.getCardType(),
            featureType = libraryCardViewData.featureType
        )

        binding.root.setDebounceClickListener {
            onActionClick.invoke(data, eventData)
        }
        binding.ivEndIcon.setDebounceClickListener {
            val endIconClickData = StaticInfoData(
                type = StaticInfoType.CUSTOM_ACTION_DISMISS_REFER_EARN.name,
                value = libraryCardViewData.getSortKey().toString(),
            )
            onEndIconClick.invoke(endIconClickData, eventData)
            startDismissAnimation()
        }
    }

    private fun startDismissAnimation() {
        job?.cancel()
        job = uiScope.launch(Dispatchers.Main) {
            if (isActive) {
                val animation: Animation = AnimationUtils.loadAnimation(root?.context, R.anim.recycler_view_exit)
                root?.startAnimation(animation)
            }
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        this.visibilityState = visibilityState

        if (visibilityState == VisibilityState.INVISIBLE) {
            stopAnimation()
        } else if (visibilityState == VisibilityState.PARTIAL_IMPRESSION_INVISIBLE) {
            stopAnimation()
        } else if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            onCardShown.invoke(eventData)
        }
    }

    private fun stopAnimation() {
        job?.cancel()
        root?.clearAnimation()
    }
}