package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jar.app.base.util.dp
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.*
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.databinding.FeatureHomepageLendingProgressCardBinding
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.shared.domain.model.LendingProgressCardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference


internal class LendingProgressCardEpoxyModel(
    private val uiScope: CoroutineScope,
    private val lendingProgressCardData: LendingProgressCardData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard, CustomViewBindingEpoxyModel<FeatureHomepageLendingProgressCardBinding>(
    com.jar.app.feature_homepage.R.layout.feature_homepage_lending_progress_card
) {

    private var visibilityState: Int? = null

    private var binding: FeatureHomepageLendingProgressCardBinding? = null

    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to lendingProgressCardData.cardType,
                DynamicCardEventKey.FeatureType to lendingProgressCardData.featureType,
                DynamicCardEventKey.Data to lendingProgressCardData.progress.toString()
            )
        )
    }

    private val titleIconListener by lazy {
        object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                binding?.root?.context?.let { context ->
                    binding?.tvTitle?.text = lendingProgressCardData.cardData.title?.convertToString(WeakReference(context))
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resource ?: return false
                binding ?: return false
                binding?.root?.context?.let { context ->
                    val spannable = lendingProgressCardData.cardData.title?.convertToString(WeakReference(context))
                    resource.setBounds(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)
                    val span = ImageSpan(resource, ImageSpan.ALIGN_BASELINE)

                    val builder = SpannableStringBuilder()
                    builder.append(spannable)
                        .append("  ", span, 0)

                    binding?.tvTitle?.text = builder
                }
                return false
            }
        }
    }

    private val descIconListener by lazy {
        object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                binding?.root?.context?.let { context ->
                    binding?.tvDescription?.text = lendingProgressCardData.cardData.description?.convertToString(WeakReference(context))
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resource ?: return false
                binding ?: return false
                binding?.root?.context?.let { context ->
                    val spannable = lendingProgressCardData.cardData.description?.convertToString(WeakReference(context))

                    resource.setBounds(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)
                    val span = ImageSpan(resource, ImageSpan.ALIGN_BASELINE)

                    val builder = SpannableStringBuilder()
                    builder.append(spannable)
                        .append(" ", span, 0)

                    binding?.tvDescription?.text = builder
                }
                return false
            }
        }
    }

    override fun bindItem(binding: FeatureHomepageLendingProgressCardBinding) {
        this.binding = binding
        val contextRef = WeakReference(binding.root.context)
        binding.root.setPlotlineViewTag(tag = lendingProgressCardData.featureType)

        //Top label
        lendingProgressCardData.cardData.labelTop?.text?.let {
            binding.tvTopLabel.isVisible = true
            binding.tvTopLabel.text = it.convertToString(contextRef)
        } ?: kotlin.run {
            binding.tvTopLabel.isVisible = false
        }

        //Card background
        lendingProgressCardData.cardData.cardBackground?.let {
            val background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor(it.startColor),
                    Color.parseColor(it.endColor),
                )
            )
            background.cornerRadius = it.getCornerRadius(default = 8f).dp.orZero()
            binding.clCard.background = background
        }

        //Progress
        binding.progressLending.progress = lendingProgressCardData.progress
        binding.tvProgress.text = "${lendingProgressCardData.progress}%"

        //cta text
        lendingProgressCardData.cardData.cta?.text?.let {
            binding.btnAction.setText(it.convertToString(contextRef))
        }

        //title and Desc icons
        lendingProgressCardData.cardData.let { cardData ->
            cardData.title?.icon?.let { iconUrl ->
                binding.tvTitle.text = cardData.title?.convertToString(contextRef)
                Glide.with(binding.root.context).load(iconUrl).listener(titleIconListener).submit()
            } ?: kotlin.run {
                binding.tvTitle.text = cardData.title?.convertToString(contextRef)
            }

            cardData.description?.icon?.let { iconUrl ->
                binding.tvDescription.text = cardData.description?.convertToString(contextRef)
                Glide.with(binding.root.context).load(iconUrl).listener(descIconListener).submit()
            } ?: kotlin.run {
                binding.tvDescription.text = cardData.description?.convertToString(contextRef)
            }
        }

        //infographic
        lendingProgressCardData.cardData.infographic?.url?.let {
            when (lendingProgressCardData.cardData.infographic?.getInfographicType()) {
                InfographicType.IMAGE -> {
                    Glide.with(binding.root).load(it)
                        .into(binding.ivSuccess)
                    binding.ivSuccess.isVisible = true
                    binding.progressLending.visibility = View.INVISIBLE
                    binding.tvProgress.isVisible = false
                }
                else -> {
                    //Do Nothing
                }
            }
        }


        //Button click
        binding.clCard.setDebounceClickListener {
            binding.btnAction.performClick()
        }

        binding.btnAction.setDebounceClickListener {
            onCtaClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.LENDING_ONBOARDING,
                    order = lendingProgressCardData.getSortKey(),
                    cardType = lendingProgressCardData.getCardType(),
                    featureType = lendingProgressCardData.featureType
                ),
                cardEventData
            )
        }
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

    override fun getBinding(view: View): FeatureHomepageLendingProgressCardBinding {
        return FeatureHomepageLendingProgressCardBinding.bind(view)
    }
}