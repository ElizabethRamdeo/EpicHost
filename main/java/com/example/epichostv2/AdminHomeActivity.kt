package com.example.epichostv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AdminHomeActivity : AppCompatActivity() {

    private lateinit var btnAdminAddRecipes: Button
    private lateinit var spinnerAdminRecipeCategories: Spinner
    private lateinit var recyclerViewAdminRecipes: RecyclerView
    private lateinit var apiService: ApiService
    private lateinit var adminRecipeAdapter: AdminRecipeAdapter
    private var userId: String? = null

    private var adminRecipesList = mutableListOf<Recipe>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)
        spinnerAdminRecipeCategories= findViewById(R.id.spinnerAdminRecipeCategory)
        recyclerViewAdminRecipes = findViewById<RecyclerView>(R.id.recyclerViewAdminRecipes)
        btnAdminAddRecipes = findViewById(R.id.btnAdminAddRecipe)

        val baseUrl = readBaseUrl(this)
        apiService = RetrofitClient.getRetrofitInstance(baseUrl).create(ApiService::class.java)

        btnAdminAddRecipes.setOnClickListener {

            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivity(intent)
        }


//        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
//        userId = sharedPreferences.getString("user_id", null)

          setUpAdminRecipesRecyclerView()
          setUpCategorySpinner()
          getRecipes()
    }

    private fun setUpCategorySpinner() {

        val categories = listOf("All Categories","Dinner", "Breakfast", "Side", "Dessert", "Appetizer", "Drinks")

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAdminRecipeCategories.adapter = categoryAdapter

        // Spinner Listener
        spinnerAdminRecipeCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterRecipesByCategory(categories[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getRecipes() {
        apiService.getAllRecipes().enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    Log.d("AdminHomeActivity", "Raw Response: ${response.body()}")

                    val recipes = response.body()
                    if (recipes != null) {
                        adminRecipesList.clear()
                        adminRecipesList.addAll(recipes)
                        Log.d("AdminHomeActivity", "Parsed Recipes: $adminRecipesList")
                        adminRecipeAdapter.notifyDataSetChanged()
                    } else {
                        Log.e("AdminHomeActivity", "Response body is null.")
                    }
                } else {
                    Log.e("AdminHomeActivity", "API Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                Log.e("AdminHomeActivity", "API Failure: ${t.message}", t)
            }
        })
    }



    private fun filterRecipesByCategory(category: String) {
        if (category == "All Categories") {
            adminRecipeAdapter.updateList(adminRecipesList)
        } else {
            val filteredList = adminRecipesList.filter { it.recipeCategory == category }
            adminRecipeAdapter.updateList(filteredList)
        }
    }

    private fun setUpAdminRecipesRecyclerView() {
        adminRecipeAdapter = AdminRecipeAdapter(adminRecipesList) { itemRecipe ->
            // go to Recipe Detail Activity
            val intent = Intent(this, AdminRecipeDetailActivity::class.java)
            intent.putExtra("recipe_id", itemRecipe._id)
            startActivity(intent)
        }
        recyclerViewAdminRecipes.layoutManager = LinearLayoutManager(this)
        recyclerViewAdminRecipes.adapter = adminRecipeAdapter
    }
}