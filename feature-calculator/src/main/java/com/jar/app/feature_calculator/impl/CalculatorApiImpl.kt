package com.jar.app.feature_calculator.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_calculator.api.CalculatorApi
import com.jar.app.feature_calculator.shared.domain.model.CalculatorType
import dagger.Lazy
import javax.inject.Inject

internal class CalculatorApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
) : CalculatorApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openCalculatorScreen(calculatorType: CalculatorType, fromScreen: String?, fromSection: String?) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/calculatorFragment/${calculatorType.name}"),
            getNavOptions(shouldAnimate = true)
        )
    }
}