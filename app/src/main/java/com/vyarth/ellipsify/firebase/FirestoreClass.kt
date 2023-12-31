package com.vyarth.ellipsify.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.vyarth.ellipsify.activities.IntroActivity
import com.vyarth.ellipsify.activities.MainActivity
import com.vyarth.ellipsify.activities.SignInActivity
import com.vyarth.ellipsify.model.User
import com.vyarth.ellipsify.activities.SignUpActivity
import com.vyarth.ellipsify.utils.Constants

class FirestoreClass {

    // Create a instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the firestore database.
     */
    fun registerUser(activity: Activity, userInfo: User) {

        val currentUserID = getCurrentUserID()

        // Check if the user already exists based on email
        mFireStore.collection(Constants.USERS)
            .whereEqualTo("email", userInfo.email) // Assuming "email" is the field representing the email in your User model
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No user with the same email found, proceed with creating a new document
                    mFireStore.collection(Constants.USERS)
                        .document(currentUserID)
                        .set(userInfo, SetOptions.merge())
                        .addOnSuccessListener {
                            if (activity is IntroActivity) {
                                (activity as IntroActivity).userRegisteredSuccessIntro()
                            }
                            if (activity is SignUpActivity) {
                                (activity as SignUpActivity).userRegisteredSuccessSignUp()
                            }
                        }
                        .addOnFailureListener { e ->
                            handleRegistrationFailure(activity, e)
                        }
                } else {
                    // User with the same email already exists, update the existing document
                    val existingUserId = documents.documents[0].id
                    mFireStore.collection(Constants.USERS)
                        .document(existingUserId)
                        .set(userInfo, SetOptions.merge())
                        .addOnSuccessListener {
                            if (activity is IntroActivity) {
                                (activity as IntroActivity).userRegisteredSuccessIntro()
                            }
                            if (activity is SignUpActivity) {
                                (activity as SignUpActivity).userRegisteredSuccessSignUp()
                            }
                        }
                        .addOnFailureListener { e ->
                            handleRegistrationFailure(activity, e)
                        }
                }
            }
            .addOnFailureListener { e ->
                handleRegistrationFailure(activity, e)
            }
    }

    private fun handleRegistrationFailure(activity: Activity, e: Exception) {
        if (activity is IntroActivity) {
            (activity as IntroActivity).hideProgressDialog()
        }
        if (activity is SignUpActivity) {
            (activity as SignUpActivity).hideProgressDialog()
        }

        Log.e(
            activity.javaClass.simpleName,
            "Error writing document",
            e
        )
    }


    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */
    fun signInUser(activity: SignInActivity) {
        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null)
                    activity.signInSuccess(loggedInUser)

//                // Here call a function of base activity for transferring the result to it.
//                if (loggedInUser != null) {
//                    // Use loggedInUser safely here
//                    when (activity) {
//                        is SignInActivity -> {
//                            activity.signInSuccess(loggedInUser)
//                        }
//                        is MainActivity -> {
//                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
//                        }
//                        is MyProfileActivity -> {
//                            activity.setUserDataInUI(loggedInUser)
//                        }
//                    }
//                } else {
//                    // Handle the case where the conversion to User failed or the document was null
//                }

            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
//                when (activity) {
//                    is SignInActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                    is MainActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                    is MyProfileActivity -> {
//                        activity.hideProgressDialog()
//                    }
//                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }

    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserData(onSuccess: (User) -> Unit, onFailure: () -> Unit) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null) {
                    onSuccess(loggedInUser)
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

}