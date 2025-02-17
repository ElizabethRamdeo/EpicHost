package com.example.epichostv2

data class Event(

    val eventId: Int,
    //changed this to User type
    var userId: String,
    val eventName: String,
    val numGuests: Int,
    val budget: Double? = null,
    var recipes: Array<Recipe> = emptyArray(),

    val transDateAndTime: String, //date of the event
)
