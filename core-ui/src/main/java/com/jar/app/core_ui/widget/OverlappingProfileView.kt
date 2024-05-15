package com.jar.app.core_ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.jar.app.base.util.dp
import com.jar.app.core_ui.R

class OverlappingProfileView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val avatarSize = 22.dp
    private val cornerRadius = avatarSize / 2
    private val recyclerView = RecyclerView(context, attrs)
    private val adapter: OverlappingProfileAdapter
    val typedArray =
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.OverlappingProfileView,
            0,
            0
        )

    companion object {
        const val DEFAULT_STROKE_COLOR = "#272239"
    }

    init {
        removeAllViews()
        val id = generateViewId()
        recyclerView.id = id
        addView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        //dummy
        val dummy = ArrayList<String>()
        dummy.add("https://i.pravatar.cc/150?img=60")
        dummy.add("https://i.pravatar.cc/150?img=52")
        dummy.add("https://i.pravatar.cc/150?img=47")
        dummy.add("https://i.pravatar.cc/150?img=43")
        dummy.add("https://i.pravatar.cc/150?img=56")
        adapter = OverlappingProfileAdapter(dummy)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(OverlapRecyclerViewDecoration(4, 50))
        clipChildren = false
    }

    fun submitProfilePics(profilePics: List<String>) {
        adapter.submitProfilePics(profilePics)
        postInvalidate()
    }

    inner class OverlappingProfileAdapter(
        private val profilePics: ArrayList<String>
    ) : RecyclerView.Adapter<OverlappingProfileAdapter.ProfileViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
            val customStrokeColor = typedArray.getColor(R.styleable.OverlappingProfileView_customStrokeColor, Color.parseColor(DEFAULT_STROKE_COLOR))

            val imageView =
                ShapeableImageView(parent.context, null, com.jar.app.core_ui.R.style.circleImageView).apply {
                    strokeColor = ColorStateList.valueOf(customStrokeColor)
                    strokeWidth = 2.dp.toFloat()
                    layoutParams = LayoutParams(avatarSize, avatarSize)
                    shapeAppearanceModel = ShapeAppearanceModel.builder()
                        .setAllCornerSizes(cornerRadius.toFloat())
                        .build()
                }
            return ProfileViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
            Glide.with(holder.itemView.context)
                .load(profilePics[position])
                .into(holder.itemView as ShapeableImageView)
        }

        override fun getItemCount(): Int = profilePics.size

        @SuppressLint("NotifyDataSetChanged")
        fun submitProfilePics(profilePics: List<String>) {
            this.profilePics.clear()
            this.profilePics.addAll(profilePics)
            notifyDataSetChanged()
        }

        inner class ProfileViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView)
    }
}