package com.vyarth.ellipsify.model

data class Post(
    val id: String = "",
    val author: String = "",
    val content: String = "",
    val timestamp: Long = 0,
    val likes: Int = 0,
    val comments: List<String> = listOf()
)
