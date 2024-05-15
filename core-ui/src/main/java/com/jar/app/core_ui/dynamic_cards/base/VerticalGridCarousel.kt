package com.jar.app.core_ui.dynamic_cards.base

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelView

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class VerticalGridCarousel(context: Context?) : Carousel(context) {
    override fun createLayoutManager(): LayoutManager {
        return GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
    }
}