package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.core_ui.util.convertToString
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageHealthInsuranceImageCarouselBinding
import com.jar.app.feature_homepage.shared.domain.model.ImageCardCarouselData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

internal class ImageCarouselEpoxyModel(
    private val uiScope: CoroutineScope,
    private val imageCarouselData: ImageCardCarouselData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit = { },
    private val onPrimaryCtaClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit = { _, _ -> },
    override var cardShownEventJob: Job? = null
) : CustomViewBindingEpoxyModel<FeatureHomepageHealthInsuranceImageCarouselBinding>(R.layout.feature_homepage_health_insurance_image_carousel), HomeFeedCard {

    private var viewBinding: FeatureHomepageHealthInsuranceImageCarouselBinding? = null

    private var autoScrollJob: Job? = null

    private val autoScrollListener = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            autoScrollJob?.cancel()
            autoScrollJob = uiScope.launch(Dispatchers.Main.immediate) {
                delay(5000)
                viewBinding?.imageCarouselViewpager?.currentItem = position + 1
            }
        }
    }

    override fun onViewDetachedFromWindow(view: View) {
        super.onViewDetachedFromWindow(view)
        viewBinding?.imageCarouselViewpager?.unregisterOnPageChangeCallback(autoScrollListener)
        autoScrollJob?.cancel()
    }

    override fun getBinding(view: View): FeatureHomepageHealthInsuranceImageCarouselBinding {
        return FeatureHomepageHealthInsuranceImageCarouselBinding.bind(view)
    }

    override fun bindItem(binding: FeatureHomepageHealthInsuranceImageCarouselBinding) {
        viewBinding = binding
        imageCarouselData.data?.let { imageCardData ->
            binding.imageCarouselViewpager.apply {
                val onCardClick: () -> Unit = {
                    EventBus.getDefault().post(HandleDeepLinkEvent(deepLink = imageCardData.deeplink))
                }
                val mAdapter = imageCardData?.images?.let { ImageCarouselCardAdapter(it, onCardClick) }
                adapter = mAdapter
                registerOnPageChangeCallback(autoScrollListener)
            }
            binding.tvTopLabel.visibility = View.GONE
        }
        imageCarouselData.data.label.text?.let {
            binding.tvTopLabel.visibility = View.VISIBLE
            val contextRef = WeakReference(binding.root.context)
            binding.tvTopLabel.text = imageCarouselData.data.label.text.convertToString(contextRef)
        }
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
    }
}