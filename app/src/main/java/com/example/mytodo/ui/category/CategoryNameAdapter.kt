package com.example.mytodo.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.mytodo.R
import com.example.mytodo.model.Category
import com.example.mytodo.utils.CategoryListener
import com.example.mytodo.utils.gone
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class CategoryNameAdapter(
    context: Context,
    resource: Int,
    textViewResourceId: Int,
    val objects: List<Category>
) :
    ArrayAdapter<Category>(context, resource, textViewResourceId, objects) {
    lateinit var clickListener: CategoryListener
    fun clickListener(categoryListener: CategoryListener) {
        this.clickListener = categoryListener
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_category, parent, false)

        val imageView: MaterialButton = view.findViewById(R.id.icDots)
        val textView: MaterialTextView = view.findViewById(R.id.tvNames)
        val icMore: MaterialButton = view.findViewById(R.id.icMore)
        icMore.gone()
        val category = getItem(position)
        if (category != null) {
            // Set the image resource and text
            if (category.color == 0)
                imageView.setIconTintResource(R.color.colorPrimary)
            else
                imageView.setIconTintResource(category.color) // Replace with your image resource
            textView.text = category.name
        }
        textView.setOnClickListener {
            clickListener.clickListener(category!!, position, view)
        }
        return view
    }
    override fun getItem(position: Int): Category? {
        return objects[position]
    }
    override fun getCount(): Int {
        return objects.size
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        return view
    }
}