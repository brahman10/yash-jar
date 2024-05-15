package com.jar.app.feature_jar_duo.impl.ui.duo_group_detail.v2


import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.util.asInitials
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.jarProgressBarWithDrawable.JarProgressBarWithDrawable
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoScorecardItemBinding
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoBottomObjectUsersDetailsV2s
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle
import kotlin.math.max

internal class DuoGroupScoreAdapter(
    private val isSamplePage: Boolean,
    private val onClick: (data: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle) -> Unit,
) :
    ListAdapter<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2, DuoGroupScoreAdapter.DuoGroupScoreViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2>() {
            override fun areItemsTheSame(
                oldItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2,
                newItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2,
                newItem: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = DuoGroupScoreViewHolder(
        FeatureDuoScorecardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onClick
    )

    override fun onBindViewHolder(holder: DuoGroupScoreViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setScoreInfo(it)
        }
    }

    inner class DuoGroupScoreViewHolder(
        private val binding: FeatureDuoScorecardItemBinding,
        private val onClick: (data: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle) -> Unit,
    ) :
        BaseViewHolder(binding.root) {
        fun setScoreInfo(scoreInfo: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupBottomObjectV2) {
            Glide.with(context).load(scoreInfo.iconLink).apply(RequestOptions().override(35))
                .centerCrop().into(binding.optionIcon)
            binding.tvTitle.text = scoreInfo.header
            if (scoreInfo.buttonDisplayText?.isNotEmpty() == true) {
                binding.buttonText.text = scoreInfo.buttonDisplayText
            } else {
                binding.btnLayout.visibility = View.GONE
            }
            if (isSamplePage) binding.btnLayout.isEnabled = false

            binding.btnLayout.setDebounceClickListener {
                onClick(
                    com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoClickHandle(
                        deepLink = scoreInfo.deepLink,
                        optionName = scoreInfo.buttonDisplayText
                    )
                )
            }
            val user = scoreInfo.scores[0]
            val userScore = user.score

            val friend = scoreInfo.scores[1]
            val friendScore = friend.score

            val maxScore = max(userScore, friendScore)

            val userProgress: Int = try {
                ((userScore.toFloat() / maxScore.toFloat()) * 80).toInt()
            } catch (e: ArithmeticException) {
                0
            }
            val friendProgress = try {
                ((friendScore.toFloat() / maxScore.toFloat()) * 80).toInt()
            } catch (e: ArithmeticException) {
                0
            }
            binding.tvUserScore.text = "$userScore"
            binding.tvFriendScore.text = "$friendScore"


            when {
                userScore > friendScore -> {
                    val scoreDifference = userScore - friendScore
                    binding.tvUsrScoreDifference.apply {
                        visibility = View.VISIBLE
                        setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(
                                context,
                                R.drawable.feature_duo_ic_score_up_arrow
                            ), null
                        )
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_58DDC8
                            )
                        )
                        text = scoreDifference.toString()
                    }

                }
                userScore < friendScore -> {
                    val scoreDifference = friendScore - userScore
                    binding.tvUsrScoreDifference.apply {
                        visibility = View.VISIBLE
                        setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(
                                context,
                                R.drawable.feature_duo_ic_score_down_arrow
                            ), null
                        )
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                com.jar.app.core_ui.R.color.color_EB6A6E
                            )
                        )
                        text = scoreDifference.toString()
                    }

                }
                else -> {
                    binding.tvUsrScoreDifference.visibility = View.GONE
                }
            }
            setProgressBarIcon(user, binding.userProgress)
            setProgressBarIcon(friend, binding.friendProgress)
            binding.userProgress.setAnimate(true)
            binding.friendProgress.setAnimate(true)
            binding.userProgress.progress = if (userProgress > 8) userProgress else 0
            binding.friendProgress.progress =  if (friendProgress > 8) friendProgress else 0
            binding.userProgress.setUserInitials(user.userName.uppercase().asInitials())
            binding.friendProgress.setUserInitials(friend.userName.uppercase().asInitials())


        }

        private fun setProgressBarIcon(
            data: com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoBottomObjectUsersDetailsV2s,
            userProgress: JarProgressBarWithDrawable
        ) {
            if (data.image?.isNotBlank() == true) {
                Glide.with(context)
                    .load(data.image)
                    //.apply(RequestOptions.overrideOf(30,30))
                    .circleCrop()
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            userProgress.setProgressImage(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            } else {
                binding.userProgress.setUserInitials(data.userName.uppercase().asInitials())
            }
        }
    }


}