package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jar.app.base.util.dp
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomeCellTypeTenBinding
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellTypeSevenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference

internal class TypeTenEpoxyModel(
    private val uiScope: CoroutineScope,
    private val libraryCardViewData: LibraryCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null,
): HomeFeedCard, CustomViewBindingEpoxyModel<FeatureHomeCellTypeTenBinding>(R.layout.feature_home_cell_type_ten) {

    private var visibilityState: Int? = null
    private var binding: FeatureHomeCellTypeTenBinding? = null

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
                DynamicCardEventKey.Footer to libraryCardViewData.cardMeta?.textListFooter?.textList?.getOrNull(0)?.text.orEmpty()
            ),
            fromSection = libraryCardViewData.header?.convertToRawString(),
            fromCard = libraryCardViewData.featureType
        )
    }

    override fun getBinding(view: View): FeatureHomeCellTypeTenBinding {
        return FeatureHomeCellTypeTenBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomeCellTypeTenBinding) {
        this.binding = binding
        setupTypeSevenHomefeedCard()
    }

    private fun setupTypeSevenHomefeedCard() {
        binding?.apply {
            val contextRef = WeakReference(root.context)

            tvTitle.text = libraryCardViewData.cardMeta?.title?.convertToString(contextRef)
            Glide.with(root.context).load(libraryCardViewData.cardMeta?.title?.icon).into(ivIcon)
            tvDescription.text = libraryCardViewData.cardMeta?.description?.convertToString(contextRef)
            btnPrimary.setText(libraryCardViewData.cardMeta?.cta?.text?.convertToString(contextRef)!!)
            libraryCardViewData.cardMeta?.cardBackground?.let {
                val background = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(
                        Color.parseColor(it.startColor),
                        Color.parseColor(it.endColor),
                    )
                )
                background.cornerRadius = it.getCornerRadius(8f).dp
                root.background = background
            }

            val data = PrimaryActionData(
                type = PrimaryActionType.DEEPLINK,
                value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
                order = libraryCardViewData.getSortKey(),
                cardType = libraryCardViewData.getCardType(),
                featureType = libraryCardViewData.featureType
            )
            btnPrimary.setDebounceClickListener {
                onActionClick.invoke(data, eventData)
            }
            root.setDebounceClickListener {
                onActionClick.invoke(data, eventData)
            }
        }
    }

    private fun addImagesToFooterLayout(textList: List<String>, context: Context, footerLayout: LinearLayoutCompat) {
        footerLayout.removeAllViews()
        val requestOptions = RequestOptions.centerInsideTransform()
        textList.forEach {
            val imageView = ImageView(context)
            Glide.with(context)
                .load(it)
                .apply(requestOptions)
                .into(imageView)

            val imageLayoutParams = LinearLayoutCompat.LayoutParams(
                27.dp,
                27.dp
            )
            imageLayoutParams.marginEnd = 8.dp
            imageView.layoutParams = imageLayoutParams

            footerLayout.addView(imageView)
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