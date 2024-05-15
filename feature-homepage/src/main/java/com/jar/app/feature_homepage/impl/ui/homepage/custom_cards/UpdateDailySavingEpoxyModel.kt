package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.strikeThrough
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellUpdateDailySavingCardBinding
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.widget.button.CustomButtonV2
import com.jar.app.feature_homepage.shared.domain.model.update_daily_saving.UpdateDailySavingCardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class UpdateDailySavingEpoxyModel(
    private val updateDailySavingCardData: UpdateDailySavingCardData,
    private val uiScope: CoroutineScope,
    private val onCardShown: (cardEventData: CardEventData) -> Unit = {},
    private val onPrimaryCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    private val onSliderMoved: (amount: Int) -> Unit = { _ -> },
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureHomepageCellUpdateDailySavingCardBinding>(
        R.layout.feature_homepage_cell_update_daily_saving_card
    ) {

    private var visibilityState: Int? = null

    private var binding: FeatureHomepageCellUpdateDailySavingCardBinding? = null
    private var progress: Int = 0
    private val cardEventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to updateDailySavingCardData.cardType,
                DynamicCardEventKey.FeatureType to updateDailySavingCardData.featureType,
                DynamicCardEventKey.Data to updateDailySavingCardData.updateDailySavingData?.currentDailySavingAmount.toString()
            )
        )
    }

    override fun bindItem(binding: FeatureHomepageCellUpdateDailySavingCardBinding) {
        this.binding = binding
        binding.root.setPlotlineViewTag(tag = updateDailySavingCardData.featureType)
        binding.tvLowestValue.text = binding.root.context.getString(
            R.string.feature_homepage_rupee_x_in_double,
            updateDailySavingCardData.updateDailySavingData?.sliderMinValue
        )
        binding.tvHighestValue.text = binding.root.context.getString(
            R.string.feature_homepage_rupee_x_in_double,
            updateDailySavingCardData.updateDailySavingData?.sliderMaxValue
        )

        binding.seekBarDailySaving.max =
            updateDailySavingCardData.updateDailySavingData?.sliderMaxValue?.toInt().orZero() -
                    updateDailySavingCardData.updateDailySavingData?.sliderMinValue?.toInt().orZero()

        binding.seekBarDailySaving.progress =
            updateDailySavingCardData.updateDailySavingData?.recommendedDailySavingsAmount?.toInt().orZero()

        binding.seekBarDailySaving.post {
            progress =
                updateDailySavingCardData.updateDailySavingData?.recommendedDailySavingsAmount?.toInt()
                    .orZero()
            binding.seekBarDailySaving.progress =
                updateDailySavingCardData.updateDailySavingData?.recommendedDailySavingsAmount?.toInt()
                    .orZero() - updateDailySavingCardData.updateDailySavingData?.sliderMinValue?.toInt()
                    .orZero()

            binding.clRecommendedContainer.isVisible = true
            binding.clRecommendedContainer.x =
                binding.seekBarDailySaving.thumb?.bounds?.left?.toFloat().orZero() +
                        binding.seekBarDailySaving.thumb?.minimumWidth.orZero() +
                        binding.tvRsX.width.orZero() + 16
        }

        binding.seekBarDailySaving.incrementProgressBy(updateDailySavingCardData.updateDailySavingData?.sliderStepValue.orZero())

        binding.seekBarDailySaving.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progressValue: Int,
                fromUser: Boolean
            ) {
                progress =
                    progressValue + updateDailySavingCardData.updateDailySavingData?.sliderMinValue?.toInt()
                        .orZero()
                updateSeekBarProgress(
                    binding.root,
                    seekBar,
                    binding.tvSaveXRs,
                    binding.btnSaveXDaily,
                    binding.tvRsX
                )
                val span = buildSpannedString {
                    append(binding.root.context.getString(R.string.feature_home_page_by_saving))
                    append(" ")
                    strikeThrough {
                        append("\u20B9${updateDailySavingCardData.updateDailySavingData?.currentDailySavingAmount.orZero()}")
                    }
                    color(ContextCompat.getColor(binding.root.context, com.jar.app.core_ui.R.color.color_EBB46A)) {
                        append(" \u20B9${progress.orZero()} ")
                    }
                    append(binding.root.context.getString(R.string.feature_home_page_daily))
                }.toSpannable()

                binding.tvBySavingXDaily.text = span
                binding.clRecommendedContainer.isVisible = fromUser.not()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                onSliderMoved.invoke(progress)
            }
        })

        binding.btnSaveXDaily.setDebounceClickListener {
            val data = PrimaryActionData(
                type = PrimaryActionType.DEEPLINK,
                value = BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.UPDATE_DAILY_SAVING_MANDATE_SETUP + "/$progress" + "/${updateDailySavingCardData.updateDailySavingData?.currentDailySavingAmount.orZero()}/{${BaseConstants.DailySavingUpdateFlow.HOME}}",
                order = updateDailySavingCardData.getSortKey(),
                cardType = updateDailySavingCardData.getCardType(),
                featureType = updateDailySavingCardData.featureType
            )
            onPrimaryCtaClick.invoke(data, cardEventData)
        }
    }

    private fun updateSeekBarProgress(
        root: View,
        seekBarView: SeekBar,
        tvSaveXRs: AppCompatTextView,
        btnSaveXDaily: CustomButtonV2,
        tvRsX: AppCompatTextView
    ) {

        tvSaveXRs.text = root.context.getString(
            R.string.feature_home_page_save_rs_x,
            progress * 180 // 6 months
        )
        tvRsX.text = root.context.getString(
            com.jar.app.core_ui.R.string.feature_buy_gold_currency_sign_x_int,
            progress
        )
        btnSaveXDaily.setText(
            root.context.getString(
                R.string.feature_home_page_save_rs_x_daily,
                progress
            )
        )

        // Get the thumb bound and get its left value
        val x = seekBarView.thumb.bounds.left
        // set the left value to textview x value
        tvRsX.x = x.toFloat() + seekBarView.thumb.intrinsicWidth + 16

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

    override fun getBinding(view: View): FeatureHomepageCellUpdateDailySavingCardBinding {
        return FeatureHomepageCellUpdateDailySavingCardBinding.bind(view)
    }
}