package com.jar.app.core_ui.winnings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.core_ui.winnings.customAnimations.MysteryCardAnimation
import com.jar.app.core_ui.winnings.customAnimations.SpinsAnimation
import com.jar.app.core_ui.winnings.customAnimations.WeeklyMagicAnimation
import com.jar.app.core_base.domain.model.WinningsType
import com.jar.app.core_ui.databinding.MysteryCardViewHolderv2Binding
import com.jar.app.core_ui.winnings.viewholder.MysteryCardV2ViewHolder
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard

class WinningsRVAdapter(private val isForTransactionScreen: Boolean, private val callback: (deepLink: String, featureType: String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mData: MutableList<PostPaymentRewardCard> by lazy {
        mutableListOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        LayoutInflater.from(parent.context)
        return when (WinningsType.values()[viewType]) {
            WinningsType.SPINS -> {
                val spins = SpinsAnimation(parent.context, isForTransactionScreen)
                SpinningWin(spins)
            }
            WinningsType.MYSTERY_CARDS -> {
                val mysteryCard = MysteryCardAnimation(parent.context, isForTransactionScreen)
                MysteryCard(mysteryCard)
            }
            WinningsType.WEEKLY_MAGIC -> {
                val weeklyMagic = WeeklyMagicAnimation(parent.context, isForTransactionScreen)
                WeeklyMagic(weeklyMagic)
            }
            WinningsType.MYSTERY_CARD_HERO -> {
                val binding = MysteryCardViewHolderv2Binding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MysteryCardV2ViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is SpinningWin -> {
                holder.onResume()
            }
            is WeeklyMagic -> {
                holder.onResume()
            }
            else -> Unit
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is SpinningWin -> {
                holder.onPause()
            }
            is WeeklyMagic -> {
                holder.onPause()
            }
            else -> Unit
        }
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (mData[position].animationType?.let { WinningsType.getWinningsType(it) }) {
            WinningsType.SPINS -> {
                (holder as? SpinningWin)?.bind(mData[position], callback)
            }
            WinningsType.MYSTERY_CARDS -> {
                (holder as? MysteryCard)?.bind(mData[position], callback)
            }
            WinningsType.WEEKLY_MAGIC -> {
                (holder as? WeeklyMagic)?.bind(mData[position], callback)
            }
            WinningsType.MYSTERY_CARD_HERO -> {
                (holder as? MysteryCardV2ViewHolder)?.bind(mData[position], callback)
            }
            else -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int {
        val type = mData[position].animationType?.let { WinningsType.getWinningsType(it) }
        return type?.ordinal ?: -1
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        for (i in 0 until recyclerView.childCount) {
            val childView = recyclerView.getChildAt(i)
            when (val holder = recyclerView.getChildViewHolder(childView)) {
                is SpinningWin -> {
                    holder.onPause()
                }
                is MysteryCard -> {
                    // no-op
                }
                is WeeklyMagic -> {
                    holder.onPause()
                }
                else -> Unit
            }
        }
    }

    fun setData(postPaymentReward: List<PostPaymentRewardCard>?) {
        mData.clear()
        postPaymentReward?.let {
            mData.addAll(it)
        }
        notifyDataSetChanged()
    }
}