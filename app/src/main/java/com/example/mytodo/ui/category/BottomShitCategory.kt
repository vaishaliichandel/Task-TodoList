package com.example.mytodo.ui.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodo.R
import com.example.mytodo.database.TaskDatabase
import com.example.mytodo.database.repository.TaskRepository
import com.example.mytodo.databinding.BottomShitCategoryBinding
import com.example.mytodo.databinding.DialogNewCategoryBinding
import com.example.mytodo.model.Category
import com.example.mytodo.utils.CategoryListener
import com.example.mytodo.utils.DataFilterListener
import com.example.mytodo.utils.gone
import com.example.mytodo.utils.visible
import com.example.mytodo.viewModelFectory.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Collections


class BottomShitCategory : BottomSheetDialogFragment() {
    private lateinit var binding: BottomShitCategoryBinding
    val categoryList = ArrayList<Category>()
    private lateinit var adapter: CategoryAdapter
    private lateinit var viewModel: CategoryViewModel
    private var dataInsertionListener: DataFilterListener? = null
    private fun onDataInserted(data: Category) {
        dataInsertionListener?.onDataFilter(data)
    }

    fun setListener(listener: DataFilterListener) {
        dataInsertionListener = listener
    }

    private fun insertData(data: Category) {
        onDataInserted(data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = TaskRepository(TaskDatabase.getInstance(requireContext()).taskDao())
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[CategoryViewModel::class.java]
        adapter = CategoryAdapter(categoryList)
        if (viewModel.getAllCate().isEmpty()) {
            viewModel.insertCate(Category(name = "Home", color = R.color.colorHome))
            viewModel.insertCate(Category(name = "Personal", color = R.color.colorPersonal))
            viewModel.insertCate(Category(name = "School", color = R.color.colorSchool))
            viewModel.insertCate(Category(name = "Work", color = R.color.colorWork))
        }
        binding.run {
            rvCategory.adapter = adapter
            bNewCate.setOnClickListener { newCateDialog(null, true, 0) }
        }
        categoryList.clear()
        categoryList.addAll(viewModel.getAllCate())
        val simpleCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
                0
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.getAdapterPosition()
                val toPosition = target.getAdapterPosition()
                Collections.swap(categoryList, fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvCategory)
        adapter.clickListener(object : CategoryListener {
            override fun clickListener(category: Category, adapterPosition: Int, view: View) {
                showMenu(view, category, adapterPosition)
            }

            override fun clickItemListener(category: Category, adapterPosition: Int, view: View) {
                insertData(category)
                dismiss()
            }
        })
    }

    private fun newCateDialog(category: Category?, isNewCate: Boolean, adapterPosition: Int) {
        val binding1 = DialogNewCategoryBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext()).setView(binding1.root)
            .setNegativeButton("Cancel") { _, _ ->
            }
            .setPositiveButton("Save") { _, _ ->
                val color: Int = when (binding1.radioGroup2.checkedRadioButtonId) {
                    R.id.rb1 -> R.color.colorHome
                    R.id.rb2 -> R.color.colorPersonal
                    R.id.rb3 -> R.color.colorSchool
                    R.id.rb4 -> R.color.colorWork
                    R.id.rb5 -> R.color.color6
                    R.id.rb6 -> R.color.color5
                    else -> R.color.colorPrimary
                }

                if (isNewCate) {
                    viewModel.insertCate(
                        Category(
                            name = binding1.editText.text.toString(),
                            color = color
                        )
                    )
                    categoryList.add(
                        Category(
                            name = binding1.editText.text.toString(),
                            color = color
                        )
                    )
                    adapter.notifyItemInserted(categoryList.size)
                } else {
                    viewModel.updateCate(
                        Category(
                            category?.id ?: 0,
                            binding1.editText.text.toString(),
                            color
                        )
                    )
                    categoryList[adapterPosition] = Category(
                        category?.id ?: 0,
                        binding1.editText.text.toString(),
                        color
                    )
                    adapter.notifyItemChanged(adapterPosition)
                }
            }
            .show()

        binding1.run {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    binding1.tvCount.text =
                        binding1.editText.text.count().toString().plus(" / ").plus("50")
                }
            })
            if (!isNewCate) {
                editText.requestFocus()
                editText.setText(category?.name)
                ic1.setIconTintResource(category?.color ?: R.color.colorPrimaryDark)
            }
        }
    }

    private fun showMenu(view: View, category: Category, adapterPosition: Int) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_item, null)
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, 150, height, true)
        popupWindow.showAsDropDown(view, 0, 0, Gravity.END)
        val tvEdit = popupView.findViewById<View>(R.id.tvEdit)
        val tvHide = popupView.findViewById<TextView>(R.id.tvHide)
        val tvShow = popupView.findViewById<TextView>(R.id.tvShow)
        val tvDelete = popupView.findViewById<View>(R.id.tvDelete)
        if (category.isHide == 1) {
            tvHide.gone()
            tvShow.visible()
        } else {
            tvHide.visible()
            tvShow.gone()
        }
        tvEdit.setOnClickListener {
            newCateDialog(category, false, adapterPosition)
            popupWindow.dismiss()
        }

        tvHide.setOnClickListener {
            categoryList[adapterPosition].isHide = 1
            adapter.notifyItemChanged(adapterPosition)
            view.post {
                tvShow.visible()
                tvHide.gone()
                popupWindow.dismiss()
            }
            viewModel.updateCate(
                Category(
                    category.id,
                    category.name,
                    category.color,
                    isHide = 1
                )
            )
        }

        tvShow.setOnClickListener {
            tvShow.gone()
            tvHide.visible()
            categoryList[adapterPosition].isHide = 0
            adapter.notifyItemChanged(adapterPosition)
            view.post {
                tvShow.visible()
                tvHide.gone()
                popupWindow.dismiss()
            }
            viewModel.updateCate(
                Category(
                    category.id,
                    category.name,
                    category.color,
                    isHide = 0
                )
            )
        }
        tvDelete.setOnClickListener {
            viewModel.deleteCate(category)
            categoryList.remove(category)
            adapter.notifyItemRemoved(adapterPosition)
            popupWindow.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomShitCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
}