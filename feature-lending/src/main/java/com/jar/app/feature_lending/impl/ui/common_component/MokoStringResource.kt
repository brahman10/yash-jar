package com.jar.app.feature_lending.impl.ui.common_component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.icerock.moko.resources.StringResource

@Composable
fun mokoStringResource(res: StringResource) = stringResource(id = res.resourceId)