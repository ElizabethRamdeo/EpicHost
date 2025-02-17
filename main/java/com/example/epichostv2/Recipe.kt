package com.example.epichostv2

data class Recipe(
    val _id: String,
    val recipeName: String,
    val recipeDescription: String? = null,
    val recipeCategory: String? = null,
    val yield: Int? = null,
    val healthLabel: String? = null,
    val ingredients: String,
    val directions: String,
    val image_url: String? =null,
    var groceryList: String

)
