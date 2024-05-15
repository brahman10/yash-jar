package com.jar.app.feature_contacts_sync_common.impl.ui.contact_list


import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_contacts_sync_common.R
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncItemFooterBinding

internal class ContactsSyncListFooterAdapter(private val onClicked:()->Unit) :
    ListAdapter<String, ContactsSyncListFooterAdapter.ContactsSyncFooterViewHolder>(ITEM_CALLBACK) {
    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem.length == newItem.length
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsSyncFooterViewHolder {
        val binding =
            FeatureContactsSyncItemFooterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ContactsSyncFooterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsSyncFooterViewHolder, position: Int) {
        getItem(position)?.let {
            holder.onBind(it,onClicked)
        }
    }

    inner class ContactsSyncFooterViewHolder(private val binding: FeatureContactsSyncItemFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(footerText: String, onClicked: () -> Unit) {

            val text =  binding.root.resources.getString(R.string.didn_t_find)
                //"Didn't find the contact you are looking for?\nMay be you have already sent them an invite"
            val underlineText = binding.root.resources.getString(R.string.didn_t_find_underlined_text)
            val spannableString = SpannableString(text)


            // Find the start and end index of the clickable text
            val startIndex = text.indexOf(underlineText)
            val endIndex = startIndex + underlineText.length

            // Apply the ClickableSpan to the clickable text
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClicked()
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = binding.tvErrorMessage.currentTextColor // Set the text color to the default color
                }
            }
            spannableString.setSpan(
                clickableSpan,
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Apply the underline style to the clickable text
            spannableString.setSpan(
                UnderlineSpan(),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Set the spannable string to the text view
            binding.tvErrorMessage.text = spannableString
            binding.tvErrorMessage.movementMethod =
                LinkMovementMethod.getInstance() // Make the clickable text clickable


            //binding.tvErrorMessage.text = footerText

        }
    }
}


