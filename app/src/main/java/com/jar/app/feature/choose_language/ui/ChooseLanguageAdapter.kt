package com.jar.app.feature.choose_language.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.databinding.CellChooseLanguageBinding
import com.jar.app.feature_onboarding.shared.domain.model.Language

class ChooseLanguageAdapter(
    private val onLanguageClick: (language: Language) -> Unit
) : ListAdapter<Language, ChooseLanguageViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Language>() {
            override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
                return oldItem.text == newItem.text && oldItem.isSelected == newItem.isSelected
            }

            override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseLanguageViewHolder {
        val binding = CellChooseLanguageBinding.inflate(LayoutInflater.from(parent.context))
        return ChooseLanguageViewHolder(binding, onLanguageClick)
    }

    override fun onBindViewHolder(holder: ChooseLanguageViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setLanguage(it)
        }
    }
}