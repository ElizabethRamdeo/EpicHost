package com.example.epichostv2

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST("register")
    fun registerUser(@Body user: User): Call<Void>

    @POST("login")
    fun loginUser(@Body user: User): Call<LoginResponse>


    // Create recipe with the image URL
    @POST("create_recipe")
    fun createRecipe(@Body product: Recipe): Call<Void>

    // Getting all recipes
    @GET("retrieve_all_recipes")
    fun getAllRecipes(): Call<List<Recipe>>

    @GET("user/{user_id}")
    fun getUserProfile(@Path("user_id") userId: String): Call<UserProfile>

    @PUT("user/{user_id}")
    fun updateUserProfile(@Path("user_id") userId: String, @Body profile: UserProfile): Call<Void>

    @GET("menu/{event_id}")
    fun getMenuItems(@Path("event_id") eventId: String): Call<List<Recipe>>

    @POST("menu/{event_id}")
    fun addMenuItem(@Path("event_id") eventId: String, @Body recipe: Recipe): Call<Void>

    @DELETE("menu/{event_id}/{recipe_id}")
    fun removeMenuItem(@Path("event_id") eventId: String, @Path("recipe_id") recipeId: String): Call<Void>

    @DELETE("delete_single_recipe/{recipe_id}")
    fun deleteSingleRecipe(@Path("recipe_id") recipeId:String): Call<Void>

    @GET("retrieve_single_recipe/{recipe_id}")
    fun getSingleRecipeById(@Path("recipe_id") recipeId: String): Call<Recipe>

    @PUT("/update_single_recipe/{recipe_id}")
    fun updateSingleRecipe(
        @Path("recipe_id") recipeId: String,
        @Body recipe: Recipe
    ):Call<Void>

    @GET("retrieve_all_events")
    fun getAllEvents(): Call<List<Event>>

    @GET("retrieve_event_by_id/{event_id}")
    fun getEventById(@Path("event_id") transId: String): Call<Event>

    // Get user by user_id for event
    @GET("retrieve_user_by_id/{user_id}")
    fun getUserById(@Path("user_id") userId: String): Call<User>

}
data class LoginResponse(
    val message: String,
    val type: String,
    val user_id: String
)

