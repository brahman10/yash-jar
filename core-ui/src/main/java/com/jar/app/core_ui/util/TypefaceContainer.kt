package com.jar.app.core_ui.util

import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip

data class TypefaceContainer(
    var typeFaceUrl: String? = null,
    @FontRes var typeFaceResource: Int = 0
) {

    /**
     * Applies typeface to a given TextView object.
     * If there is no typeface (either URL or resource) set, this method is a no-op.
     *
     * @param textView The [AppCompatTextView] where the Typeface will be applied
     */
    fun applyTo(textView: AppCompatTextView?) {
        if (textView == null || textView.context == null) {
            return
        }
        if (typeFaceUrl == null && typeFaceResource == 0) {
            return
        }

        // Callback to font retrieval
        val callback = object : ResourcesCompat.FontCallback() {
            override fun onFontRetrievalFailed(reason: Int) {
                // Don't be panic, just do nothing.
            }
            override fun onFontRetrieved(typeface: Typeface) {
                textView.typeface = typeface
            }
        }

        // We give priority to the FontRes here.
        if (typeFaceResource != 0) {
            ResourcesCompat.getFont(textView.context, typeFaceResource, callback, null)
        } else {
            CustomFontCache.getFont(textView.context, typeFaceUrl, callback)
        }
    }

    fun applyTo(chip: Chip?) {
        if (chip == null || chip.context == null) {
            return
        }
        if (typeFaceUrl == null && typeFaceResource == 0) {
            return
        }

        // Callback to font retrieval
        val callback = object : ResourcesCompat.FontCallback() {
            override fun onFontRetrievalFailed(reason: Int) {
                // Don't be panic, just do nothing.
            }
            override fun onFontRetrieved(typeface: Typeface) {
                chip.typeface = typeface
            }
        }

        // We give priority to the FontRes here.
        if (typeFaceResource != 0) {
            ResourcesCompat.getFont(chip.context, typeFaceResource, callback, null)
        } else {
            CustomFontCache.getFont(chip.context, typeFaceUrl, callback)
        }
    }
}