package com.example.mytodo.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.databinding.ListItemCategoryBinding
import com.example.mytodo.model.Category
import com.example.mytodo.utils.CategoryListener

class CategoryAdapter(val categoryList: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    lateinit var clickListener: CategoryListener

    fun clickListener(categoryListener: CategoryListener) {
        this.clickListener = categoryListener
    }

    class CategoryViewHolder(val binding: ListItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(category: Category, categoryListener: CategoryListener) {
            binding.run {
                tvNames.text = category.name
                icHide.isVisible = category.isHide == 1
                if (category.color == 0)
                    icDots.setIconTintResource(R.color.colorPrimary)
                else
                    icDots.setIconTintResource(category.color)
                icMore.setOnClickListener {
                    categoryListener.clickListener(category, adapterPosition, it)
                }
                llItem.setOnClickListener {
                    categoryListener.clickItemListener(category, adapterPosition, it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CategoryViewHolder(ListItemCategoryBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindData(categoryList[position], clickListener)
    }
}