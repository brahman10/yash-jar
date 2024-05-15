package com.jar.app.base.ui

import android.app.Activity
import android.net.Uri
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jar.app.base.R
import com.jar.app.core_base.util.orFalse
import timber.log.Timber

interface BaseNavigation {

    fun getNavOptions(
        shouldAnimate: Boolean,
        @IdRes popUpToId: Int? = null,
        inclusive: Boolean? = null,
        saveState: Boolean? = null,
        showBackwardNavigationAnimation: Boolean = false
    ): NavOptions {
        val builder = NavOptions.Builder()
        if (shouldAnimate) {
            if (showBackwardNavigationAnimation) {
                builder.setEnterAnim(R.anim.slide_from_left)
                builder.setExitAnim(R.anim.slide_to_right)
                builder.setPopEnterAnim(R.anim.slide_from_right)
                builder.setPopExitAnim(R.anim.slide_to_left)
            } else {
                builder.setEnterAnim(R.anim.slide_from_right)
                builder.setExitAnim(R.anim.slide_to_left)
                builder.setPopEnterAnim(R.anim.slide_from_left)
                builder.setPopExitAnim(R.anim.slide_to_right)
            }
        }
        if (popUpToId != null) {
            builder.setPopUpTo(popUpToId, inclusive.orFalse(), saveState.orFalse())
        }

        return builder.build()
    }

    fun getNavOptionsMirrored(
        shouldAnimate: Boolean,
        @IdRes popUpToId: Int? = null,
        inclusive: Boolean? = null,
        saveState: Boolean? = null
    ): NavOptions {
        val builder = NavOptions.Builder()
        if (shouldAnimate) {
            builder.setEnterAnim(R.anim.slide_from_left)
            builder.setExitAnim(R.anim.slide_to_right)
            builder.setPopEnterAnim(R.anim.slide_from_right)
            builder.setPopExitAnim(R.anim.slide_to_left)
        }
        if (popUpToId != null) {
            builder.setPopUpTo(popUpToId, inclusive.orFalse(), saveState.orFalse())
        }

        return builder.build()
    }

    fun getBottomNavOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.slide_from_bottom)
            .setExitAnim(R.anim.stay)
            .setPopEnterAnim(R.anim.stay)
            .setPopExitAnim(R.anim.slide_to_bottom)
            .build()
    }
    fun getAnimNavOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.reveal_from_top)
            .setExitAnim(R.anim.stay)
            .setPopEnterAnim(R.anim.stay)
            .setPopExitAnim(R.anim.reveal_to_top)
            .build()
    }
    //HotFix with try-catch.. Getting a lot of action cannot be found from the current destination crashes
    fun Fragment.navigateTo(
        navDirections: NavDirections,
        shouldAnimate: Boolean = true,
        @IdRes popUpTo: Int? = null,
        inclusive: Boolean? = null,
        saveState: Boolean? = null,
        navOptions: NavOptions? = null
    ) {
        try {
            findNavController().navigate(
                navDirections,
                navOptions ?: getNavOptions(shouldAnimate, popUpTo, inclusive)
            )

        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun Fragment.navigateTo(
        uri: String,
        shouldAnimate: Boolean = true,
        @IdRes popUpTo: Int? = null,
        inclusive: Boolean? = null,
        saveState: Boolean? = null
    ) {
        findNavController().navigate(
            Uri.parse(uri),
            getNavOptions(shouldAnimate, popUpTo, inclusive)
        )
    }

    fun Fragment.popBackStack() {
        findNavController().popBackStack()
    }

    fun Fragment.popBackStack(id: Int, inclusive: Boolean) {
        findNavController().popBackStack(id, inclusive)
    }

    fun Activity.popBackStack(
        navController: NavController,
        id: Int,
        inclusive: Boolean
    ) {
        navController.popBackStack(id, inclusive)
    }

    fun Activity.popBackStack(
        navController: NavController,
    ) {
        navController.popBackStack()
    }

    //HotFix with try-catch.. Getting a lot of action cannot be found from the current destination crashes
    fun Activity.navigateTo(
        navController: NavController,
        navDirections: NavDirections,
        shouldAnimate: Boolean = true,
        @IdRes popUpTo: Int? = null,
        inclusive: Boolean? = null,
        saveState: Boolean? = null,
        overrideNavOptions: NavOptions? = null
    ) {
        try {
            navController.navigate(
                navDirections,
                overrideNavOptions ?: getNavOptions(shouldAnimate, popUpTo, inclusive)
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun Activity.navigateTo(
        navController: NavController,
        uri: String,
        shouldAnimate: Boolean = true,
        @IdRes popUpTo: Int? = null,
        inclusive: Boolean? = null,
        saveState: Boolean? = null,
        overrideNavOptions: NavOptions? = null
    ) {
        navController.navigate(
            Uri.parse(uri),
            overrideNavOptions ?: getNavOptions(shouldAnimate, popUpTo, inclusive)
        )
    }
}