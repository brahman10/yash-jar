package com.jar.app.core_compose_ui.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

@Composable
@ExperimentalMaterialApi
fun rememberModalBottomSheetStateCustom(
    initialValue: ModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean = false,
    confirmStateChange: (ModalBottomSheetValue) -> Boolean = { true }
): ModalBottomSheetState = rememberModalBottomSheetState(
    initialValue = initialValue,
    animationSpec = animationSpec,
    skipHalfExpanded = skipHalfExpanded,
    confirmStateChange = confirmStateChange
)