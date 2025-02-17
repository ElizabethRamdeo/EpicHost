# Flask API for RecipeApp Data from MongoDB
# --------------------------------------
# How to Install Flask for app:
# python3 app.py
# --------------------------------------
# Run the following commands to install The App:
# pip install pymongo bcrypt
#
# --------------------------------------
# curl Commands to Test the API:
# --------------------------------------
# 1. To login for a particular user:
#    curl http://localhost:8888/login
#
# 2. To register for a customer or an admin with "secret" code
#    curl http://localhost:8888/register
#
# 3. To see the User Profile
#    curl http://localhost:8888/user/<user_id>  methods=['GET']
#
# 4. Update User Profile
#    curl http://localhost:8888/user/<user_id> methods=['PUT']
#
# 5. To create a new recipe:
# curl -X POST http://localhost:8888/create_recipe -H "Content-Type: application/json" -d '{  "recipeName": "Recipe Name",  "recipeDescription": "Description of the recipe",  "recipeCategory": "Category of the recipe", "yield": 0, "healthLabel": "Health label of the recipe",  "ingredients": "ingredients of the recipe",  "directions": "directions of the recipe",  "image_url": "imageurl", "groceryList": "Grocery list"}'
#
# 6. To retrieve all recipes:
# curl http://localhost:8888/retrieve_all_recipes
#
# 7. To retrieve a single recipe by ID:
# curl http://localhost:8888/retrieve_single_recipe/<recipe_id>
#
# 8. To delete a single recipe by ID:
# curl -X DELETE http://localhost:8888/delete_single_recipe/<recipe_id>
#
# 9. To update a recipe by ID:
# curl -X PUT http://localhost:8888/update_single_recipe/<recipe_id> -H "Content-Type: application/json" -d '{  "recipeName": "Updated Recipe Name",  "recipeDescription": "Updated Description",  "recipeCategory": "Updated Category",  "yield": 0, "healthLabel": "Updated Helth Label",  "Ingredients": "updated ingredients",  "directions": "updated directions",  "image_url": "updated imageurl", "groceryList": "updated grocery list"}'
#
# 10. To create an event:
#curl -X POST http://localhost:8888/event -H "Content-Type: application/json" -d '{  "user": "user_id_here", "eventName": "Event Name", "numGuests": 0, "budget": 0.00, "recipes": [    {"recipe_id": 0 ,recipeName": "Recipe Name", "yield": 0, "ingredients": "recipe ingredients", "healthLabel: "Health Label of Recipe", "directions": "Directions", "groceryList": "grocery list"}  ],  "event_date": "2024-11-29"}'
#
# 11. To see event's menu items
#   curl -X GET http://localhost:8888/menu/<event_id>
#
# 12. To Add a recipe to event menu
#   curl -X Post http://localhost:8888/menu/<event_id>
#
# 13. To remove a recipe  from the menu
#   curl -X Post http://localhost:8888/menu/<event_id>/<recipe_id>


from flask import Flask, jsonify, request, send_from_directory
from pymongo import MongoClient
import bcrypt
import os
from werkzeug.utils import secure_filename
from bson import ObjectId  # Import ObjectId from bson
from datetime import datetime

app = Flask(__name__)

# MongoDB Atlas connection string (replace <username>, <password>, and <cluster-url> to the ones you want to use)
client = MongoClient("mongodb+srv://<username>:<password>@<clusterurl>/")


# Access the database and collection
db = client.recipesfinder
users_collection = db.users
recipes_collection = db.recipes
events_collection = db.events


# Endpoint to register
@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()

    # Extract data from the request
    fullname = data.get('fullname')
    phone = data.get('phone')
    email = data.get('email')
    password = data.get('password')
    user_type = data.get('type')

    # Check if the email already exists
    if users_collection.find_one({"email": email}):
        return jsonify({"error": "Email already exists"}), 409

    # Hash the password
    hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())

    # Create user data for insertion
    user_data = {
        "fullname": fullname,
        "phone": phone,
        "email": email,
        "password": hashed_password,
        "type": user_type
    }

    # Insert the user into the database
    users_collection.insert_one(user_data)

    return jsonify({"message": "User registered successfully"}), 201


# Endpoint to login
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    email, password = data['email'], data['password']

    user = users_collection.find_one({"email": email})
    if user and bcrypt.checkpw(password.encode('utf-8'), user['password']):
        return jsonify({
            "message": "Login successful",
            "type": user["type"],
            "user_id": str(user["_id"])  # Include user ID in the response
        }), 200
    return jsonify({"error": "Invalid credentials"}), 401


# Get User Profile
@app.route('/user/<user_id>', methods=['GET'])
def get_user_profile(user_id):
    user = users_collection.find_one({"_id": ObjectId(user_id)})
    if user:
        return jsonify({
            "fullname": user["fullname"],
            "email": user["email"],
            "phone": user["phone"]
        }), 200
    else:
        return jsonify({"error": "User not found"}), 404


# Update User Profile
@app.route('/user/<user_id>', methods=['PUT'])
def update_user_profile(user_id):
    data = request.get_json()
    updated_data = {
        "fullname": data.get("fullname"),
        "email": data.get("email"),
        "phone": data.get("phone")
    }

    result = users_collection.update_one({"_id": ObjectId(user_id)}, {"$set": updated_data})
    if result.matched_count > 0:
        return jsonify({"message": "Profile updated successfully"}), 200
    else:
        return jsonify({"error": "User not found"}), 404


# Endpoint to create recipe
@app.route('/create_recipe', methods=['POST'])
def create_recipe():
    data = request.get_json()
    recipe_name = data['recipeName']
    recipe_description = data.get('recipeDescription', None)
    recipe_category = data.get('recipeCategory', None)
    recipe_yield = data.get('yield',0)
    recipe_health_label = data.get('healthLabel', None)
    recipe_ingredients= data.get('ingredients', None)
    recipe_directions = data.get('directions', None)
    image_url = data.get('image_url', None)
    recipe_grocery_list= data.get('groceryList', None)

    # Create the recipe data dictionary without the recipeID
    recipe_data = {
        "recipeName": recipe_name,
        "recipeDescription": recipe_description,
        "recipeCategory": recipe_category,
        "yield": recipe_yield,
        "healthLabel": recipe_health_label,
        "ingredients": recipe_ingredients,
        "directions": recipe_directions,
        "image_url": image_url,  # Store the image URL here
        "groceryList": recipe_grocery_list
    }

    # Insert the recipe into MongoDB
    recipe = db.recipes.insert_one(recipe_data)

    # Return the inserted recipe's _id (MongoDB autogenerated)
    return jsonify({"message": "Recipe created successfully", "recipe_id": str(recipe.inserted_id)}), 201


# Endpoint to retrieve all recipes
@app.route('/retrieve_all_recipes', methods=['GET'])
def retrieve_all_recipes():
    # Fetch all recipes from the MongoDB database
    recipes = recipes_collection.find()
    recipes_list = []

    # Convert MongoDB cursor to a list of dictionaries
    for recipe in recipes:
        recipe['_id'] = str(recipe['_id'])  # Convert ObjectId to string
        recipe['image_url'] = request.host_url + recipe['image_url'] #Append the full image URL using request.host_url
        recipes_list.append(recipe)

    return jsonify(recipes_list), 200

# Endpoint to retrieve a single product by its ID
@app.route('/retrieve_single_recipe/<recipe_id>', methods=['GET'])
def retrieve_single_recipe(recipe_id):
    try:
        # Convert the string to an ObjectId
        recipe = recipes_collection.find_one({"_id": ObjectId(recipe_id)})

        if recipe:
            # Convert the ObjectId to string for the response
            recipe['_id'] = str(recipe['_id'])
            recipe['image_url'] = request.host_url + recipe['image_url']
            return jsonify(recipe), 200
        else:
            return jsonify({"error": "Recipe not found"}), 404
    except Exception as e:
        return jsonify({"error": f"Invalid ID format: {str(e)}"}), 400

# Endpoint to delete a single recipe by its ID
@app.route('/delete_single_recipe/<recipe_id>', methods=['DELETE'])
def delete_single_recipe(recipe_id):
    try:
        # Convert the string recipe_id to ObjectId
        result = recipes_collection.delete_one({"_id": ObjectId(recipe_id)})

        if result.deleted_count > 0:
            return jsonify({"message": "Recipe deleted successfully"}), 200
        else:
            return jsonify({"error": "Recipe not found"}), 404
    except Exception as e:
        return jsonify({"error": f"Invalid ID format: {str(e)}"}), 400

# Endpoint to update a single recipe by its ID
@app.route('/update_single_recipe/<recipe_id>', methods=['PUT'])
def update_single_recipe(recipe_id):
    data = request.get_json()

    try:
        # Fetch the existing recipe from the database by ObjectId
        recipe = recipes_collection.find_one({"_id": ObjectId(recipe_id)})

        if not recipe:
            return jsonify({"error": "Recipe not found"}), 404

        # Prepare the updated recipe data
        updated_data = {
            "recipeName": data.get('recipeName', recipe['recipeName']),
            "recipeDescription": data.get('recipeDescription', recipe['recipeDescription']),
            "recipeCategory": data.get('recipeCategory', recipe['recipeCategory']),
            "yield": data.get('yield', recipe['yield']),
            "healthLabel": data.get('healthLabel', recipe['healthLabel']),
            "ingredients": data.get('ingredients', recipe['ingredients']),
            "directions": data.get('directions', recipe['directions']),
            "image_url": data.get('image_url', recipe['image_url']),
            "groceryList": data.get('groceryList', recipe['groceryList'])
        }

        

        # Update the recipe in the MongoDB database
        result = recipes_collection.update_one({"_id": ObjectId(recipe_id)}, {"$set": updated_data})

        if result.matched_count > 0:
            return jsonify({"message": "Recipe updated successfully"}), 200
        else:
            return jsonify({"error": "Failed to update recipe"}), 400
    except Exception as e:
        return jsonify({"error": f"Invalid ID format: {str(e)}"}), 400



# Endpoint to create event
@app.route('/create_event', methods=['POST'])
def create_event():
    data = request.get_json()

    user_id = data.get('user_id')
    event_name = data.get('eventName')
    number_of_guests = data.get('numGuests', 0)  
    event_budget = data.get('budget', 0.00)  
    recipes = data.get('recipes', [])
    event_date_str = data.get('event_date') 

       # Validate required fields
    if not user_id or not event_name or not event_date_str:
        return jsonify({"error": "Missing required fields: user_id, eventName, or event_date"}), 400

    # Parse and validate the event_date
    try:
        event_date = datetime.strptime(event_date_str, '%Y-%m-%d')  # Adjust format if needed
    except ValueError:
        return jsonify({"error": "Invalid date format. Use YYYY-MM-DD."}), 400

   
    event_data = {
        "user_id": user_id,
        "eventName": event_name,
        "numGuests": number_of_guests,
        "budget": event_budget,
        "recipes": recipes,
        "event_date": event_date
        
    }
    events_collection.insert_one(event_data)

    return jsonify({"message": "Event created"}), 201


# Endpoint to add a recipe to the menu
@app.route('/menu/<event_id>', methods=['POST'])
def add_to_menu(event_id):
    data = request.get_json()
    recipe_id = data['recipe_id']

    event = events_collection.find_one({"_id": ObjectId(event_id)})
    if not event:
        return jsonify({"error": "Event not found"}), 404

    # Add the recipe to the event's menu
    menu = event.get("menu", [])
    if recipe_id not in menu:
        menu.append(recipe_id)
        events_collection.update_one({"_id": ObjectId(event_id)}, {"$set": {"menu": menu}})
        return jsonify({"message": "Recipe added to menu"}), 200
    else:
        return jsonify({"message": "Recipe is already in menu"}), 400

# Endpoint to get the event's menu
@app.route('/menu/<event_id>', methods=['GET'])
def get_menu(event_id):
    event = events_collection.find_one({"_id": ObjectId(event_id)})
    if event:
        menu_recipe_ids = event.get("menu", [])
        recipes = recipes_collection.find({"_id": {"$in": [ObjectId(recipe_id) for recipe_id in menu_recipe_ids]}})
        recipe_list = [recipe for recipe in recipes]
        return jsonify(recipe_list), 200
    else:
        return jsonify({"error": "Event not found"}), 404

# Endpoint to remove a recipe from the menu
@app.route('/menu/<event_id>/<recipe_id>', methods=['DELETE'])
def remove_from_menu(event_id, recipe_id):
    event = events_collection.find_one({"_id": ObjectId(event_id)})
    if not event:
        return jsonify({"error": "Event not found"}), 404

    menu = event.get("menu", [])
    if recipe_id in menu:
        menu.remove(recipe_id)
        events_collection.update_one({"_id": ObjectId(event_id)}, {"$set": {"menu": menu}})
        return jsonify({"message": "Recipe removed from menu"}), 200
    else:
        return jsonify({"error": "Recipe not found in menu"}), 404




if __name__ == '__main__':
    # Run the application on all available IPs on port 8888
    app.run(host='0.0.0.0', port=8888)