package com.jar.app.core_ui.util

import android.content.res.Resources
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.snackBarWithGenericFallback
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import java.lang.ref.WeakReference

internal val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun <T> LiveData<RestClientResult<ApiResponseWrapper<T>>>.observeNetworkResponse(
    lifeCycleOwner: LifecycleOwner,
    view: WeakReference<View>,
    onLoading: () -> Unit = {},
    onSuccess: (t: T) -> Unit = {},
    onError: (message: String) -> Unit = {},
    onSuccessWithNullData: () -> Unit = {},
    onErrorMessageCodeAndData: (message: String, errorCode: String?, t: T?) -> Unit = { _, _, _ -> },
    translationY: Float = -64.dp.toFloat(),
    suppressError: Boolean = false //set it to true if you want to suppress Snackbar from here and handle error by yourself
) {
    this.observe(lifeCycleOwner) {
        when (it.status) {
            RestClientResult.Status.LOADING -> {
                onLoading.invoke()
            }

            RestClientResult.Status.SUCCESS -> {
                if (it.data?.success.orFalse()) {
                    it.data?.data?.let(onSuccess) ?: run { onSuccessWithNullData.invoke() }
                } else {
                    onError.invoke(it.data?.errorMessage.orEmpty())
                    onErrorMessageCodeAndData.invoke(
                        it.data?.errorMessage.orEmpty(),
                        it.data?.errorCode?.toString(),
                        it.data?.data
                    )
                    if (suppressError.not()) {
                        view.get()?.let { rootView ->
                            it.data?.errorMessage.snackBarWithGenericFallback(
                                rootView,
                                translationY = translationY
                            )
                        }
                    }
                }
            }

            RestClientResult.Status.ERROR -> {
                onError.invoke(it.message.orEmpty())
                onErrorMessageCodeAndData.invoke(
                    it.message.orEmpty(),
                    it.errorCode,
                    it.data?.data
                )
                if (suppressError.not()) {
                    view.get()?.let { rootView ->
                        it.message.snackBarWithGenericFallback(
                            rootView,
                            translationY = translationY
                        )
                    }
                }
            }

            RestClientResult.Status.NONE -> {
                // Do nothing in this case
            }
        }
    }
}

fun <T> LiveData<RestClientResult<T>>.observeNetworkResponseUnwrapped(
    lifeCycleOwner: LifecycleOwner,
    viewRef: WeakReference<View>,
    onLoading: () -> Unit = {},
    onSuccess: (t: T) -> Unit,
    onSuccessWithNullData: () -> Unit = {},
    onError: (message: String?, errorCode: String?) -> Unit = { _, _ -> },
    translationY: Float = -64.dp.toFloat(),
    suppressError: Boolean = false//set it to true if you want to suppress Snackbar from here and handle error by yourself
) {
    this.observe(lifeCycleOwner) {
        when (it.status) {
            RestClientResult.Status.LOADING -> {
                onLoading.invoke()
            }

            RestClientResult.Status.SUCCESS -> {
                it.data?.let {
                    onSuccess.invoke(it)
                } ?: run {
                    onSuccessWithNullData.invoke()
                }
            }

            RestClientResult.Status.ERROR -> {
                onError.invoke(it.message, it.errorCode)
                if (suppressError.not())
                    it.message.snackBarWithGenericFallback(
                        viewRef.get()!!,
                        translationY = translationY
                    )
            }

            RestClientResult.Status.NONE -> {
                // Do nothing in this case
            }
        }
    }
}

fun <T> RestClientResult<T>.unfoldNetworkResponseUnwrapped(
    onLoading: () -> Unit = {},
    onSuccess: (t: T) -> Unit,
    onSuccessWithNullData: () -> Unit = {},
    onError: (message: String?, errorCode: String?) -> Unit = { _, _ -> },
) {
    when (this.status) {
        RestClientResult.Status.LOADING -> {
            onLoading.invoke()
        }

        RestClientResult.Status.SUCCESS -> {
            this.data?.let {
                onSuccess.invoke(it)
            } ?: run {
                onSuccessWithNullData.invoke()
            }
        }

        RestClientResult.Status.ERROR -> {
            onError.invoke(this.message, this.errorCode)
        }

        RestClientResult.Status.NONE -> {
            // Do nothing in this case
        }
    }
}

fun <T> RestClientResult<ApiResponseWrapper<T>>.unfoldNetworkResponse(
    onLoading: () -> Unit = {},
    onSuccess: (t: T) -> Unit = {},
    onError: (message: String) -> Unit = {},
    onSuccessWithNullData: () -> Unit = {},
    onErrorMessageCodeAndData: (message: String, errorCode: String?, t: T?) -> Unit = { _, _, _ -> },
) {
    when (this.status) {
        RestClientResult.Status.LOADING -> {
            onLoading.invoke()
        }

        RestClientResult.Status.SUCCESS -> {
            if (this.data?.success.orFalse()) {
                this.data?.data?.let(onSuccess) ?: run { onSuccessWithNullData.invoke() }
            } else {
                onError.invoke(this.data?.errorMessage.orEmpty())
                onErrorMessageCodeAndData.invoke(
                    this.data?.errorMessage.orEmpty(),
                    this.data?.errorCode?.toString(),
                    this.data?.data
                )
            }
        }

        RestClientResult.Status.ERROR -> {
            onError.invoke(this.message.orEmpty())
            onErrorMessageCodeAndData.invoke(
                this.message.orEmpty(),
                this.errorCode,
                this.data?.data
            )
        }

        RestClientResult.Status.NONE -> {
            // Do nothing in this case
        }
    }
}