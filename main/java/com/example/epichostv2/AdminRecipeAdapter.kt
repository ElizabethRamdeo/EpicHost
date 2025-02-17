package com.example.epichostv2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class AdminRecipeAdapter(
    private var adminRecipeList: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<AdminRecipeAdapter.AdminRecipeViewHolder>() {


    class AdminRecipeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val itemRecipeName: TextView = view.findViewById(R.id.item_admin_recipe_name)
        val itemRecipeImage: ImageView = view.findViewById(R.id.item_admin_recipe_image)
        val itemRecipeId: TextView = view.findViewById(R.id.item_admin_recipe_id)
        val itemRecipeCategory: TextView = view.findViewById(R.id.item_admin_recipe_category)
        val itemRecipeHealthLabels: TextView = view.findViewById(R.id.item_admin_recipe_health_labels)

        fun bind(itemRecipe: Recipe, onItemClick: (Recipe) -> Unit) {
            // Log the recipe name being bound
            Log.d("Adapter", "Binding recipe: ${itemRecipe.recipeName}")

            itemRecipeName.text = itemRecipe.recipeName

            // Handle image URL formatting and loading
            val formattedImageUrl = itemRecipe.image_url?.removePrefix("insertprefixhere")
                ?: "default_image_path"

            Glide.with(itemView)
                .load(formattedImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(itemRecipeImage)

            itemRecipeId.text = itemRecipe._id
            itemRecipeCategory.text = itemRecipe.recipeCategory ?: "Unknown"
            itemRecipeHealthLabels.text = itemRecipe.healthLabel ?: "Unknown"

            itemView.setOnClickListener { onItemClick(itemRecipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_recipes, parent, false)
        return AdminRecipeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adminRecipeList.size

    }

    override fun onBindViewHolder(holder: AdminRecipeViewHolder, position: Int) {
        holder.bind(adminRecipeList[position], onItemClick)


    }

    fun updateList(newList: List<Recipe>) {
        adminRecipeList = newList
        notifyDataSetChanged()
    }
}


