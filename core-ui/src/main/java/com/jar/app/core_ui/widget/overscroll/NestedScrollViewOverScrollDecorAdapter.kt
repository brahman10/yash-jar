package com.jar.app.core_ui.widget.overscroll

import android.view.View
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter

class NestedScrollViewOverScrollDecorAdapter(private val view: View) : IOverScrollDecoratorAdapter {

    override fun getView(): View {
        return view
    }

    override fun isInAbsoluteStart(): Boolean {
        return !this.view.canScrollVertically(-1)
    }

    override fun isInAbsoluteEnd(): Boolean {
        return !this.view.canScrollVertically(1)
    }

}