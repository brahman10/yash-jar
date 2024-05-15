package com.jar.app.feature_goal_based_saving.impl.utils

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.android.material.bottomsheet.BottomSheetBehavior

class KeyboardObserver(
    private val rootView: View,
    private val navController: NavController,
    onOpen: () -> Unit,
    onClose: () -> Unit
) : DefaultLifecycleObserver {
    private var isDialogFragmentShowing = false

    init {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            isDialogFragmentShowing = destination.label == "BackAlertBottomSheetFragment"
        }
    }

    private val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        private val rect = Rect()
        private var keyboardOpen = false

        override fun onGlobalLayout() {
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keyboardHeight = screenHeight - rect.bottom

            if (keyboardHeight > screenHeight * 0.15 && !isDialogFragmentShowing) {
                if (!keyboardOpen) {
                    keyboardOpen = true
                    onOpen.invoke()
                }
            } else {
                if (keyboardOpen) {
                    keyboardOpen = false
                    onClose.invoke()
                }
            }
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onPause(owner)
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
    }
}



