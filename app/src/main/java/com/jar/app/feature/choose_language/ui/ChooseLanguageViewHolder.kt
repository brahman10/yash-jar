package com.jar.app.feature.choose_language.ui

import androidx.recyclerview.widget.RecyclerView
import com.jar.app.R
import com.jar.app.databinding.CellChooseLanguageBinding
import com.jar.app.feature_onboarding.shared.domain.model.Language
import com.jar.app.core_ui.extension.setDebounceClickListener

class ChooseLanguageViewHolder(
    private val binding: CellChooseLanguageBinding,
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
        binding.tvInitial.setBackgroundResource(if (language.isSelected) R.drawable.bg_language_selected_circle else R.drawable.bg_language_circle)
        binding.root.setBackgroundResource(if (language.isSelected) R.drawable.round_black_bg_with_white_border_16dp else com.jar.app.core_ui.R.drawable.round_black_bg_16dp)
    }
}