package com.jar.app.feature_lending.impl.ui.personal_details.address.address_option

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.core.view.isVisible
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.CellPlacesPredictionBinding
import com.jar.app.feature_lending.shared.domain.model.PlacesPrediction
import kotlin.collections.ArrayList

class AutoCompletePlacesAdapter(
    context: Context,
    placesList: MutableList<PlacesPrediction>,
    private val onPlaceSelected: (placePrediction: PlacesPrediction) -> Unit
) : ArrayAdapter<PlacesPrediction>(context, 0, placesList) {

    private var placesListFull: List<PlacesPrediction> = ArrayList()

    override fun getFilter(): Filter {
        return placesFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = CellPlacesPredictionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        getItem(position)?.let {
            binding.tvPrimaryText.text = it.primaryText
            binding.tvSecondaryText.text = it.secondaryText
            binding.tvPrimaryText.isVisible = !it.showPlaceNotFound && !it.showGoogleAttribution
            binding.tvSecondaryText.isVisible = !it.showPlaceNotFound && !it.showGoogleAttribution
            binding.tvPlaceNotFound.isVisible = it.showPlaceNotFound
            binding.tvPoweredBy.isVisible = it.showGoogleAttribution
            binding.root.setDebounceClickListener { _ ->
                if (binding.tvPrimaryText.isVisible) {
                    onPlaceSelected.invoke(it)
                }
            }
        }
        return binding.root
    }

    private val placesFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val suggestions: MutableList<PlacesPrediction> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                suggestions.addAll(placesListFull)
            } else {
                val filterPattern =
                    constraint.toString().lowercase().trim { it <= ' ' }
                for (item in placesListFull) {
                    if (item.fullText.lowercase().contains(filterPattern)) {
                        suggestions.add(item)
                    }
                }
            }
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            clear()
            addAll(results.values as List<PlacesPrediction>)
            notifyDataSetChanged()
        }
    }

    init {
        placesListFull = ArrayList(placesList)
    }
}