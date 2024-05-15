package com.jar.app.feature_settings.impl.ui.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_settings.databinding.CellChooseAppLanguageBinding
import com.jar.app.feature_settings.domain.model.Language

internal class ChooseAppLanguageAdapter(
    private val onLanguageClick: (language: Language) -> Unit
) : ListAdapter<Language, ChooseAppLanguageViewHolder>(DIFF_UTIL) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseAppLanguageViewHolder {
        val binding = CellChooseAppLanguageBinding.inflate(LayoutInflater.from(parent.context))
        return ChooseAppLanguageViewHolder(binding, onLanguageClick)
    }

    override fun onBindViewHolder(holder: ChooseAppLanguageViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setLanguage(it)
        }
    }
}