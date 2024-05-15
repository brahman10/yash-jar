package com.jar.feature_quests.impl.ui.rewards_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.feature_quests.impl.ui.splash_screen.percentageOffset
import com.jar.feature_quests.shared.domain.model.RewardItem
import com.jar.feature_quests.shared.domain.model.RewardsResponse


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RewardsScreenCoinBackground(
    modifier: Modifier,
    data: RewardsResponse?,
    content: @Composable () -> Unit
) {
    Box(modifier) {
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopStart)
                .percentageOffset(0, -2)
                .fillMaxWidth(0.1f),
            model = data?.allRewardsViewItems?.bgCoin1,
            contentDescription = ""
        )
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .percentageOffset(-5, -3)
                .fillMaxWidth(0.15f),
            model = data?.allRewardsViewItems?.bgCoin2,
            contentDescription = ""
        )
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .percentageOffset(0, -3)
                .fillMaxWidth(0.1f),
            model = data?.allRewardsViewItems?.bgCoin3,
            contentDescription = ""
        )
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopStart)
                .percentageOffset(10, 7)
                .fillMaxWidth(0.1f),
            model = data?.allRewardsViewItems?.bgCoin4,
            contentDescription = ""
        )
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .percentageOffset(12, 5)
                .fillMaxWidth(0.1f),
            model = data?.allRewardsViewItems?.bgCoin5,
            contentDescription = ""
        )
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .percentageOffset(0, 5)
                .fillMaxWidth(0.1f),
            model = data?.allRewardsViewItems?.bgCoin6,
            contentDescription = ""
        )
        content()
    }
}


@Composable
fun RenderList(rewardsList: List<RewardItem>, onClick: (rewardItem: RewardItem, index: Int) -> Unit) {
    Column {
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = 8.dp),
            columns = GridCells.Fixed(2), // Fixed number of columns
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(8.dp),
            content = {
                itemsIndexed(rewardsList) { index, it ->
                    RewardViewItem(it, onClick, index)
                }
            }
        )
    }
}

