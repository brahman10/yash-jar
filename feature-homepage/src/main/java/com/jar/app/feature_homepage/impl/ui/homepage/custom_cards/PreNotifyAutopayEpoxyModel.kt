package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellPreNotifyAutopayBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.shared.domain.model.PreNotifyAutopayCardData
import com.jar.app.feature_homepage.shared.util.EventKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference

internal class PreNotifyAutopayEpoxyModel(
    private val preNotifyAutopayCardData: PreNotifyAutopayCardData,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit = {},
    private val footerCtaClick: (preNotifyAutopayCardData: PreNotifyAutopayCardData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val neverShowAgainCtaClicked: (preNotifyAutopayCardData: PreNotifyAutopayCardData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val dismissCtaClick: (preNotifyAutopayCardData: PreNotifyAutopayCardData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    override var cardShownEventJob: Job? = null
) : HomeFeedCard, CustomViewBindingEpoxyModel<FeatureHomepageCellPreNotifyAutopayBinding>(
    R.layout.feature_homepage_cell_pre_notify_autopay
) {
    private var visibilityState: Int? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to preNotifyAutopayCardData.cardType,
                DynamicCardEventKey.FeatureType to preNotifyAutopayCardData.featureType,
                DynamicCardEventKey.Data to preNotifyAutopayCardData.preNotifyAutopay.amount.toString()
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageCellPreNotifyAutopayBinding) {
        binding.apply {
            val contextRef = WeakReference(root.context)
            root.setPlotlineViewTag(tag = preNotifyAutopayCardData.featureType)
            tvTitle.text = HtmlCompat.fromHtml(
                preNotifyAutopayCardData.preNotifyAutopay.title.orEmpty(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            Glide.with(root.context)
                .load(preNotifyAutopayCardData.iconUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(ivDailySavingIcon)
            Glide.with(root.context)
                .load(preNotifyAutopayCardData.backgroundUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        root.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
            tvDesc.text = preNotifyAutopayCardData.preNotifyAutopay.description
            preNotifyAutopayCardData.ctaText?.convertToString(contextRef)?.let {
                btnTrackYourSavings.setText(it)
            } ?: kotlin.run {
                btnTrackYourSavings.setText(
                    root.context.getString(R.string.feature_homepage_track_your_savings)
                )
            }
            if (!preNotifyAutopayCardData.preNotifyAutopay.footerText.isNullOrEmpty()) {
                btnFooterText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                btnFooterText.isVisible = true
                preNotifyAutopayCardData.preNotifyAutopay.footerText.let {
                    btnFooterText.text = it
                }
            }

            btnTrackYourSavings.setDebounceClickListener {
                cardEventData.map[EventKey.ButtonType] =
                    preNotifyAutopayCardData.ctaText?.convertToString(contextRef).toString()
                footerCtaClick.invoke(preNotifyAutopayCardData, cardEventData)
            }

            btnFooterText.setDebounceClickListener {
                cardEventData.map[EventKey.ButtonType] =
                    preNotifyAutopayCardData.preNotifyAutopay.footerText.orEmpty()
                neverShowAgainCtaClicked.invoke(preNotifyAutopayCardData, cardEventData)
            }

            ivCross.setDebounceClickListener {
                cardEventData.map[EventKey.ButtonType] to "Cross"
                dismissCtaClick.invoke(preNotifyAutopayCardData, cardEventData)
            }
        }
    }

    override fun getBinding(view: View): FeatureHomepageCellPreNotifyAutopayBinding {
        return FeatureHomepageCellPreNotifyAutopayBinding.bind(view)
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            this.visibilityState = visibilityState
            startShowEventJob(
                uiScope,
                isCardFullyVisible = {
                    this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                },
                onCardShownEvent = {
                    onCardShown.invoke(cardEventData)
                }
            )
        }
    }
}