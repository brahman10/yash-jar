package com.jar.app.base.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

interface BaseResources {

    fun AppCompatActivity.getCustomString(stringResource: StringResource): String {
        return getCustomString(context = this, stringResource = stringResource)
    }

    fun AppCompatActivity.getCustomStringFormatted(
        stringResource: StringResource,
        vararg args: Any
    ): String {
        return getCustomStringFormatted(
            context = this,
            stringResource = stringResource,
            args = args
        )
    }

    fun Fragment.getCustomString(stringResource: StringResource): String {
        return getCustomString(context = requireContext(), stringResource = stringResource)
    }

    fun Fragment.getCustomStringFormatted(
        stringResource: StringResource,
        vararg args: Any
    ): String {
        return getCustomStringFormatted(
            context = requireContext(),
            stringResource = stringResource,
            args = args
        )
    }

    fun Fragment.getCustomPlural(
        stringResource: PluralsResource,
        quantity: Int
    ): String {
        return StringDesc.Plural(stringResource, quantity).toString(requireContext())
    }

    fun getCustomString(
        context: Context,
        stringResource: StringResource
    ): String {
        return StringDesc.Resource(stringResource).toString(context)
    }

    fun getCustomStringFormatted(
        context: Context,
        stringResource: StringResource,
        vararg args: Any
    ): String {
        return StringDesc.ResourceFormatted(stringResource, *args).toString(context)
    }

    fun getCustomPlural(
        context: Context,
        stringResource: PluralsResource,
        quantity: Int
    ): String {
        return StringDesc.Plural(stringResource, quantity).toString(context)
    }

    fun getCustomPluralFormatted(
        context: Context,
        stringResource: PluralsResource,
        quantity: Int,
    ): String {
        return StringDesc.PluralFormatted(stringResource, quantity,quantity).toString(context)
    }

    fun getCustomLocalizedString(
        context: Context,
        stringResource: StringResource,
        currentLangCode: String,
        lang: String = "en"
    ): String {
        /** Set language to get specific language string **/
        StringDesc.localeType = StringDesc.LocaleType.Custom(lang)
        val string =  StringDesc.Resource(stringResource).toString(context)
        /** Reset to app language **/
        StringDesc.localeType = StringDesc.LocaleType.Custom(currentLangCode)
        return string
    }
}