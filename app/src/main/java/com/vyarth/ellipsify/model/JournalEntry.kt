package com.vyarth.ellipsify.model

data class JournalEntry(
    val title: String? = null,
    val text: String? = null,
    val timestamp: Long? = null,
    val category: String? = null
)