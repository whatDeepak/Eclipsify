package com.vyarth.ellipsify.model

data class JournalList(
    val title: String? = null,
    val text: String? = null,
    val timestamp: String? = null,
    val category: String? = null,
){
    // Add a no-argument constructor
    constructor() : this("", "", "", "")
}