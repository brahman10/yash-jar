package com.jar.app.feature_goal_based_saving.impl.ui.userEntry

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_goal_based_saving.databinding.FilledItemBinding
import com.jar.app.feature_goal_based_saving.impl.extensions.getCommaFormattedString
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.GOAL_BASED_SAVING_STEPS
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

internal class UserEnteredFieldAdapter(
    val list: MutableList<UserEnteredFieldModel>,
    private val onEdit: (UserEnteredFieldModel) -> Unit
): RecyclerView.Adapter<UserEnteredFieldAdapter.UserEnteredFieldViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserEnteredFieldViewHolder {
        val binding = FilledItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserEnteredFieldViewHolder(binding, onEdit)
    }

    override fun onBindViewHolder(holder: UserEnteredFieldViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class UserEnteredFieldViewHolder(
        private val viewBinding: FilledItemBinding,
        private val onEdit: (UserEnteredFieldModel) -> Unit
    ): RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(userEnteredFieldModel: UserEnteredFieldModel) {
            viewBinding.tvTitle.text = userEnteredFieldModel.text
            when(userEnteredFieldModel.step) {
                GOAL_BASED_SAVING_STEPS.GOAL_NAME -> {
                    val prefix = SpannableString("I’m saving for ")
                    val suffix = SpannableString(userEnteredFieldModel.text)
                    suffix.setSpan(StyleSpan(Typeface.BOLD), 0, suffix.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    val spannableStringBuilder = SpannableStringBuilder().apply {
                        append(prefix)
                        append(suffix)
                    }
                    viewBinding.tvTitle.text = spannableStringBuilder
                }
                GOAL_BASED_SAVING_STEPS.GOAL_AMOUNT -> {
                    val prefix = SpannableString("I want to save ")
                    if (userEnteredFieldModel.text.isEmpty().not()) {
                        val cleanString = userEnteredFieldModel.text.replace("""[$,.]""".toRegex(), "")
                        val formattedAmount = cleanString.getCommaFormattedString().format(cleanString.toDouble())

                        val suffix = SpannableString("\u20B9$formattedAmount")
                        suffix.setSpan(StyleSpan(Typeface.BOLD), 0, suffix.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        val spannableStringBuilder = SpannableStringBuilder().apply {
                            append(prefix)
                            append(suffix)
                        }
                        viewBinding.tvTitle.text = spannableStringBuilder
                    }
                }
                else -> Unit
            }
            viewBinding.root.setOnClickListener {
                onEdit.invoke(userEnteredFieldModel)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

data class UserEnteredFieldModel(
    val step: GOAL_BASED_SAVING_STEPS
) {
    var text: String = ""
}