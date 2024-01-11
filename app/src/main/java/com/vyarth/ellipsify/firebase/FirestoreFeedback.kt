package com.vyarth.ellipsify.firebase

// FirestoreFeedback.kt

import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.model.Feedback

object FirestoreFeedback {

    private val db = FirebaseFirestore.getInstance()
    private const val FEEDBACK_COLLECTION = "feedback"

    fun storeFeedback(feedback: Feedback) {
        db.collection(FEEDBACK_COLLECTION)
            .add(feedback)
            .addOnSuccessListener { documentReference ->
                // Feedback stored successfully
                println("Feedback stored with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // Handle any errors
                println("Error storing feedback: $e")
            }
    }
}
