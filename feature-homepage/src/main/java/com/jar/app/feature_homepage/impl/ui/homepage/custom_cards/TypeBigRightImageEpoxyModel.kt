package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.databinding.FeatureHomepageTypeBigRightImageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference

class TypeBigRightImageEpoxyModel(
    private val uiScope: CoroutineScope,
    private val libraryCardViewData: LibraryCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null,
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageTypeBigRightImageBinding>(
        com.jar.app.feature_homepage.R.layout.feature_homepage_type_big_right_image
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
                DynamicCardEventKey.Footer to libraryCardViewData.cardMeta?.footer?.joinToString(separator = "\n")
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
    private var binding: FeatureHomepageTypeBigRightImageBinding? = null

    override fun getBinding(view: View): FeatureHomepageTypeBigRightImageBinding {
        return FeatureHomepageTypeBigRightImageBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageTypeBigRightImageBinding) {
        this.binding = binding
        bindItem()
    }

    private fun bindItem() {
        binding?.apply{
            root.setPlotlineViewTag(tag = libraryCardViewData.featureType)
            val contextRef = WeakReference(root.context)
            tvTitleTop.text = libraryCardViewData.cardMeta?.title?.convertToString(contextRef)
            libraryCardViewData.cardMeta?.cta?.text?.convertToString(contextRef)?.let {
                btnAction.setText(it)
            }
            tvDescriptionText.text = libraryCardViewData.cardMeta?.description?.convertToString(contextRef)
            Glide.with(root.context)
                .load(libraryCardViewData.cardMeta?.infographic?.url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(140.dp)
                .into(ivInfoGraphic)

            (libraryCardViewData.cardMeta?.description?.textList?.getOrNull(1)?.text ?: libraryCardViewData.cardMeta?.footer?.joinToString(separator = "\n"))?.let {
                tvSubDescriptionText.visibility = View.VISIBLE
                tvSubDescriptionText.text = it
            } ?: run {
                tvSubDescriptionText.visibility = View.GONE
            }

            libraryCardViewData.cardMeta?.textListFooter?.textList?.let { textList->

                footerLayout.visibility = View.VISIBLE
                addViewsToLinearLayout(textList = textList,context=root.context,footerLayout=footerLayout)
            }?:run{
                footerLayout.visibility = View.GONE
            }

            btnAction.setText(libraryCardViewData.cardMeta?.cta?.text?.textList?.first()?.text.orEmpty())
            val data = PrimaryActionData(
                type = PrimaryActionType.DEEPLINK,
                value = libraryCardViewData.cardMeta?.cta?.deepLink.orEmpty(),
                order = libraryCardViewData.getSortKey(),
                cardType = libraryCardViewData.getCardType(),
                featureType = libraryCardViewData.featureType
            )

            btnAction.setDebounceClickListener {
                onActionClick.invoke(data, eventData)
            }

            libraryCardViewData.cardMeta?.labelTop?.text?.let {
                tvLabelTop.isVisible = true
                tvLabelTop.text = it.convertToString(contextRef)
            } ?: kotlin.run {
                tvLabelTop.isVisible = false
            }
        }

    }
    private fun addViewsToLinearLayout(textList: List<TextData>, context: Context, footerLayout: LinearLayout) {
        footerLayout.removeAllViews()
        val requestOptions = RequestOptions.centerInsideTransform()
        textList.forEachIndexed { index, textData ->
            val horizontalLayout = LinearLayout(context)
            horizontalLayout.orientation = LinearLayout.HORIZONTAL
            horizontalLayout.gravity = Gravity.CENTER_VERTICAL

            val imageView = ImageView(context)
            Glide.with(context)
                .load(textData.textIcon)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(requestOptions)
                .into(imageView)


            val imageLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            imageLayoutParams.setMargins(0, 12.dp, 0, 12.dp)
            imageLayoutParams.marginEnd = 6.dp
            if(index!=0) {
                imageLayoutParams.marginStart = 16.dp
            }


            val textView = TextView(context)
            textView.text = textData.text
            textView.textSize = 10f

            horizontalLayout.addView(imageView, imageLayoutParams)
            horizontalLayout.addView(textView)
            footerLayout.addView(horizontalLayout)
        }
    }
}