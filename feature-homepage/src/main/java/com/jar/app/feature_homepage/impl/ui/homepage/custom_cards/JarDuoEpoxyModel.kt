package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.VisibilityState
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.setPlotlineViewTag
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.JarDuoData
import com.jar.app.feature_homepage.shared.util.HomeConstants
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoCellHomepageCardBinding
import com.jar.app.feature_jar_duo.shared.util.DuoConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal class JarDuoEpoxyModel(
    private val uiScope: CoroutineScope,
    private val jarDuoData: JarDuoData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard, CustomViewBindingEpoxyModel<FeatureDuoCellHomepageCardBinding>(
    R.layout.feature_duo_cell_homepage_card
) {

    private var job: Job? = null
    private var visibilityState: Int? = null

    private val eventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to jarDuoData.cardType,
                DynamicCardEventKey.FeatureType to jarDuoData.featureType
            )
        )
    }

    private var binding: FeatureDuoCellHomepageCardBinding? = null

    override fun bindItem(binding: FeatureDuoCellHomepageCardBinding) {
        this.binding = binding
        binding.root.setPlotlineViewTag(tag = jarDuoData.featureType)

        Glide.with(binding.root)
            .load("${BaseConstants.CDN_BASE_URL}${com.jar.app.feature_jar_duo.shared.util.DuoConstants.ImageEndpoints.HOME_CARD_ONE}")
            .into(binding.ivTab1image)

        Glide.with(binding.root)
            .load("${BaseConstants.CDN_BASE_URL}${com.jar.app.feature_jar_duo.shared.util.DuoConstants.ImageEndpoints.HOME_CARD_TWO}")
            .into(binding.ivTab2image)

        Glide.with(binding.root)
            .load("${BaseConstants.CDN_BASE_URL}${com.jar.app.feature_jar_duo.shared.util.DuoConstants.ImageEndpoints.HOME_CARD_THREE}")
            .into(binding.ivTab3image)

        Glide.with(binding.root)
            .load("${BaseConstants.CDN_BASE_URL}${com.jar.app.feature_jar_duo.shared.util.DuoConstants.ImageEndpoints.HOME_CARD_FOUR}")
            .into(binding.ivTab4image)

        Glide.with(binding.root)
            .load("${BaseConstants.CDN_BASE_URL}${HomeConstants.Urls.JAR_DUO_LOGO}")
            .into(binding.ivHeaderStartIcon)

        binding.ivHeaderStartIcon.isVisible = true
        binding.tvJarDuo.isVisible = true
        binding.ivLightingDecoration.isVisible = false
        binding.ivDuoLogo.isVisible = false

        binding.btnSave.setDebounceClickListener {
            onActionClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = "${BaseConstants.BASE_EXTERNAL_DEEPLINK}${BaseConstants.ExternalDeepLinks.JAR_DUO}",
                    order = jarDuoData.getSortKey(),
                    cardType = jarDuoData.getCardType(),
                    featureType = jarDuoData.featureType
                ),
                eventData
            )
        }

        binding.ivInfo.setDebounceClickListener {
            onActionClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = "${BaseConstants.BASE_EXTERNAL_DEEPLINK}${BaseConstants.ExternalDeepLinks.JAR_DUO_ONBOARDING}",
                    order = jarDuoData.getSortKey(),
                    cardType = jarDuoData.getCardType(),
                    featureType = jarDuoData.featureType
                ),
                eventData
            )
        }

    }

    private fun autoSlideToNext(view1: View, view2: View) {
        view1.slideToRevealNew(view2)
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            if (binding != null) {
                var counter = -1
                job?.cancel()
                job = uiScope.doRepeatingTask(repeatInterval = 3000) {
                    counter++
                    when (counter % 4) {
                        0 -> {
                            autoSlideToNext(binding!!.clTab1, binding!!.clTab2)
                            binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(1), true)
                        }
                        1 -> {
                            autoSlideToNext(binding!!.clTab2, binding!!.clTab3)
                            binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(2), true)
                        }
                        2 -> {
                            autoSlideToNext(binding!!.clTab3, binding!!.clTab4)
                            binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(3), true)
                        }
                        3 -> {
                            autoSlideToNext(binding!!.clTab4, binding!!.clTab1)
                            binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(0), true)
                        }
                    }
                }
            }
            this.visibilityState = visibilityState
            startShowEventJob(
                uiScope,
                isCardFullyVisible = {
                    this.visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE
                },
                onCardShownEvent = {
                    onCardShown.invoke(eventData)
                }
            )
        } else if (visibilityState == VisibilityState.INVISIBLE)
            job?.cancel()
    }

    override fun getBinding(view: View): FeatureDuoCellHomepageCardBinding {
        return FeatureDuoCellHomepageCardBinding.bind(view)
    }

}