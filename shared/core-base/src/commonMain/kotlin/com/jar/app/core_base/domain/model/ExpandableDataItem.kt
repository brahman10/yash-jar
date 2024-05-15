package com.jar.app.core_base.domain.model

import kotlinx.serialization.SerialName

sealed class ExpandableDataItem : ExpandableViewHolderType, ExpandableIdViewHolder, IsExpandedDataType {
    class DefaultBannerWithBGIsExpandedDataType(
        @SerialName("question")
        val question: String,

        @SerialName("answer")
        val answer: String,

        override var isExpanded: Boolean = false,
        override var viewType: ExpandableRVViewTypes = ExpandableRVViewTypes.DEFAULT_BANNER_BG,
        override var id: String = question,
    ) : ExpandableDataItem()

    class CardHeaderIsExpandedDataType(
        @SerialName("question")
        val question: String,

        @SerialName("answer")
        val answer: String,
        override var isExpanded: Boolean = false,
        override var viewType: ExpandableRVViewTypes = ExpandableRVViewTypes.CARD_HEADER,
        override var id: String = question,
    ) : ExpandableDataItem()

    class LeftIconIsExpandedDataType(
        val resId: Int? = null,
        @SerialName("question")
        val question: String,
        @SerialName("imageUrl")
        val imageUrl: String? = null,

        @SerialName("answer")
        val answer: String,
        override var isExpanded: Boolean = false,
        override var viewType: ExpandableRVViewTypes = ExpandableRVViewTypes.LEFT_ICON_WITH_SEPERATOR,
        override var id: String = question,
    ) : ExpandableDataItem()
}

enum class ExpandableRVViewTypes {
    DEFAULT_BANNER_BG,
    CARD_HEADER,
    LEFT_ICON_WITH_SEPERATOR
}

internal interface ExpandableViewHolderType {
    var viewType: ExpandableRVViewTypes
}
internal interface ExpandableIdViewHolder {
    var id: String
}
internal interface IsExpandedDataType {
    var isExpanded: Boolean
}