package com.jar.app.feature_settings.impl.ui.language

import androidx.recyclerview.widget.RecyclerView
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_settings.R
import com.jar.app.feature_settings.databinding.CellChooseAppLanguageBinding
import com.jar.app.feature_settings.domain.model.Language

internal class ChooseAppLanguageViewHolder(
    private val binding: CellChooseAppLanguageBinding,
    private val onLanguageClick: (language: Language) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private var language: Language? = null

    init {
        binding.root.setDebounceClickListener {
            language?.let(onLanguageClick)
        }
    }

    fun setLanguage(language: Language) {
        this.language = language
        binding.tvInitial.text = if(language.code == "enm") "A" else  language.text[0].toString()
        binding.tvLanguageName.text = language.text
        binding.tvInitial.setBackgroundResource(if (language.isSelected) R.drawable.feature_settings_bg_language_selected_circle else R.drawable.feature_settings_bg_language_circle)
        binding.root.setBackgroundResource(if (language.isSelected) R.drawable.feature_settings_round_black_bg_with_white_border_16dp else com.jar.app.core_ui.R.drawable.core_ui_rounded_black_bg_16dp)
    }
}