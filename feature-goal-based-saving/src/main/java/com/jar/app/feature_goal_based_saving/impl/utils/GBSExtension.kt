package com.jar.app.feature_goal_based_saving.impl.utils

import android.view.View

fun List<View>.setOnClickListener(onClick: () -> Unit) {
    forEach {
        it.setOnClickListener {
            onClick.invoke()
        }
    }
}