package com.jar.app.core_ui.util

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat

object CustomFontCache {

    private val cache = hashMapOf<String, Typeface>()

    fun getFont(ctx: Context, path: String?, fontCallback: ResourcesCompat.FontCallback) {
        if (path.isNullOrEmpty()) {
            return
        }

        cache[path]?.let {
            // Cache hit! Return the typeface.
            fontCallback.onFontRetrieved(it)
        } ?: run {
            // Cache miss! Create the typeface and store it.
            val newTypeface = Typeface.createFromAsset(ctx.assets, path)
            cache[path] = newTypeface
            fontCallback.onFontRetrieved(newTypeface)
        }
    }
}