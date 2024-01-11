package com.vyarth.ellipsify.model

data class Feedback(
    val rating: Int,
    val comment: String,
    val userId: String,
    val userName: String,
    // Add other properties as needed
)