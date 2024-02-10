package com.vyarth.ellipsify.model

import com.vyarth.ellipsify.R

data class Emotion (
    val mood: String,
    val backgroundColor: Int,
    val imageResId: Int,
    val selectedbgcolor: Int,
    var isSelected: Boolean = false // New property for selection state
    )

