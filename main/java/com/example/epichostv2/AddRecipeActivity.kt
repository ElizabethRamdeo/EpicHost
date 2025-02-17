package com.example.epichostv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService


    private lateinit var btnAdminSubmitAddRecipe: Button

    private lateinit var etAddRecipeName: EditText
    private lateinit var etAddRecipeDescription: EditText
    private lateinit var spinnerAddRecipeCategory: Spinner
    private lateinit var etAddRecipeYield: EditText
    private lateinit var etAddRecipeImageURL: EditText
    private lateinit var etAddRecipeHealthLabels: EditText
    private lateinit var etAddRecipeIngredients: EditText
    private lateinit var etAddRecipeDirections: EditText
    private lateinit var etAddRecipeGroceryList: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_recipe)

        btnAdminSubmitAddRecipe= findViewById(R.id.btnAdminSubmitAndAddRecipe)

        etAddRecipeName= findViewById(R.id.etAddRecipeName)
        etAddRecipeDescription= findViewById(R.id.etAddRecipeDescription)
        spinnerAddRecipeCategory= findViewById(R.id.spinnerAddRecipeCategory)
        etAddRecipeYield= findViewById(R.id.etAddRecipeYield)
        etAddRecipeImageURL= findViewById(R.id.etAddRecipeImageURL)
        etAddRecipeHealthLabels= findViewById(R.id.etAddRecipeHealthLabels)
        etAddRecipeIngredients= findViewById(R.id.etAddRecipeIngredients)
        etAddRecipeDirections= findViewById(R.id.etAddRecipeDirections)
        etAddRecipeGroceryList= findViewById(R.id.etAddRecipeGroceryList)

        setUpSpinner()

        // Submit product form
        btnAdminSubmitAddRecipe.setOnClickListener {
            if (validateAddRecipeForm()) {
                createRecipe()
                val intent = Intent(this, AdminHomeActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun setUpSpinner() {
        // Define the categories for the spinner
        val categories = listOf("Dinner", "Breakfast", "Side", "Dessert", "Appetizer", "Drinks")

        // Create an ArrayAdapter with the category list
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Attach the adapter to the spinner
        spinnerAddRecipeCategory.adapter = categoryAdapter
    }

    private fun createRecipe() {

            val recipe = Recipe(
                _id = null.toString(),
                recipeName = etAddRecipeName.text.toString(),
                recipeDescription = etAddRecipeDescription.text.toString(),
                recipeCategory = spinnerAddRecipeCategory.selectedItem?.toString(),
                yield = etAddRecipeYield.text.toString().toIntOrNull(),

                healthLabel = etAddRecipeHealthLabels.text.toString(),
                ingredients = etAddRecipeIngredients.text.toString(),
                directions = etAddRecipeDirections.text.toString(),
                image_url = etAddRecipeImageURL.text.toString(),
                groceryList = etAddRecipeGroceryList.text.toString()
            )

            // Initialize API service
            val baseUrl = readBaseUrl(this) // Assuming `readBaseUrl` fetches the backend base URL
            apiService = RetrofitClient.getRetrofitInstance(baseUrl).create(ApiService::class.java)

            // Call the API
            apiService.createRecipe(recipe).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddRecipeActivity, "Recipe created successfully", Toast.LENGTH_SHORT).show()
                        finish() // Close activity
                    } else {
                        Toast.makeText(this@AddRecipeActivity, "Failed to create recipe: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddRecipeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


    //check that all fields have inputs
    private fun validateAddRecipeForm(): Boolean {

        return when {
            etAddRecipeName.text.isEmpty() -> {
                etAddRecipeName.error = "Please enter recipe name"
                false
            }
            etAddRecipeDescription.text.isEmpty() -> {
                etAddRecipeDescription.error = "Please enter recipe description"
                false
            }
            spinnerAddRecipeCategory.selectedItem == null ||
                    spinnerAddRecipeCategory.selectedItem.toString().isEmpty() -> {

                Toast.makeText(this, "Please select a recipe category", Toast.LENGTH_SHORT).show()
                false
            }
            etAddRecipeYield.text.isEmpty() -> {
                etAddRecipeYield.error = "Please enter yield"
                false
            }
            etAddRecipeYield.text.toString().toIntOrNull() == null -> {
                etAddRecipeYield.error = "Please enter a valid number for yield"
                false
            }
            etAddRecipeImageURL.text.isEmpty() -> {
                etAddRecipeImageURL.error = "Please enter image URL"
                false
            }
            etAddRecipeHealthLabels.text.isEmpty() -> {
                etAddRecipeHealthLabels.error = "Please enter health labels"
                false
            }
            etAddRecipeIngredients.text.isEmpty() -> {
                etAddRecipeIngredients.error = "Please enter ingredients"
                false
            }
            etAddRecipeDirections.text.isEmpty() -> {
                etAddRecipeDirections.error = "Please enter directions"
                false
            }
            etAddRecipeGroceryList.text.isEmpty() -> {
                etAddRecipeGroceryList.error = "Please enter grocery list"
                false
            }
            else -> true
        }

    }
}