package com.jar.app.core_ui.util

import androidx.fragment.app.Fragment
import com.jar.app.base.ui.BaseResources
import com.jar.app.core_ui.view_holder.BaseViewHolder
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.StringDesc

interface BaseResourcesViewHolderExtension : BaseResources {

    fun BaseViewHolder.getCustomString(stringResource: StringResource): String {
        return getCustomString(context = context, stringResource = stringResource)
    }

    fun BaseViewHolder.getCustomStringFormatted(
        stringResource: StringResource,
        vararg args: Any
    ): String {
        return getCustomStringFormatted(
            context = context,
            stringResource = stringResource,
            args = args
        )
    }

    fun BaseViewHolder.getCustomPlural(
        stringResource: PluralsResource,
        quantity: Int
    ): String {
        return StringDesc.Plural(stringResource, quantity).toString(context)
    }


}