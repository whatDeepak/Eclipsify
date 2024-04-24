package com.vyarth.ellipsify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R

class CategoriesAdapter(
    private val context: Context,
    private val categories: List<String>
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private var selectedCategoryIndex: Int = 0
    private var selectedPosition: Int = 0
    private var onItemClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onItemClickListener?.invoke(category)
        }
        holder.itemView.isSelected = selectedPosition == position
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.textCategoryName)

        fun bind(category: String) {
            categoryName.text = category
            if (selectedPosition == adapterPosition) {
                itemView.setBackgroundResource(R.drawable.category_selected_background)
                categoryName.setTextColor(context.resources.getColor(android.R.color.white))
            } else {
                itemView.setBackgroundResource(R.drawable.category_background)
                categoryName.setTextColor(context.resources.getColor(android.R.color.black))
            }
        }
    }

    override fun getItemCount() = categories.size

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

}
