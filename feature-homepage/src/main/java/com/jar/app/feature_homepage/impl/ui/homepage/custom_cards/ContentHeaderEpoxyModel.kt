package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import com.airbnb.epoxy.VisibilityState
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageContentHeaderBinding

internal class ContentHeaderEpoxyModel(
    private val libraryCardData: LibraryCardData
) :
    CustomViewBindingEpoxyModel<FeatureHomepageContentHeaderBinding>(
        R.layout.feature_homepage_content_header
    ) {

    private var binding: FeatureHomepageContentHeaderBinding? = null

    private var hasAnimatedOnce = false

    override fun getBinding(view: View): FeatureHomepageContentHeaderBinding {
        return FeatureHomepageContentHeaderBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageContentHeaderBinding) {
        this.binding = binding
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            if (binding != null && hasAnimatedOnce.not()) {
                binding?.lottieView?.playLottieWithUrlAndExceptionHandling(
                    context = binding?.root?.context!!,
                    url = libraryCardData.cardMeta?.infographic?.url!!
                )
                hasAnimatedOnce = true
            }
        }
    }
}