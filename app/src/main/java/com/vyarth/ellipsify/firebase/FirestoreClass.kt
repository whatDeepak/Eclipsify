package com.vyarth.ellipsify.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.AccountActivity
import com.vyarth.ellipsify.activities.IntroActivity
import com.vyarth.ellipsify.activities.MainActivity
import com.vyarth.ellipsify.activities.ProfileActivity
import com.vyarth.ellipsify.activities.SignInActivity
import com.vyarth.ellipsify.model.User
import com.vyarth.ellipsify.activities.SignUpActivity
import com.vyarth.ellipsify.activities.SplashActivity
import com.vyarth.ellipsify.fragments.HomeFragment
import com.vyarth.ellipsify.model.JournalList
import com.vyarth.ellipsify.utils.Constants
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    fun loadUserData(context: Context) {
        // Here we pass the collection name from which we want the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(context.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)

                // Here call a function of base activity or fragment for transferring the result to it.
                if (loggedInUser != null) {
                    // Use loggedInUser safely here
                    when (context) {
                        is MainActivity -> {
                            val navHostFragment = (context as MainActivity).supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                            if (navHostFragment is NavHostFragment) {
                                val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()
                                if (currentFragment is HomeFragment) {
                                    currentFragment.updateUserDetails(loggedInUser)
                                }
                            }
                        }
                        is ProfileActivity -> {
                            (context as ProfileActivity).updateUserDetails(loggedInUser)
                        }
                        is AccountActivity -> {
                            (context as AccountActivity).setUserDataInUI(loggedInUser)
                        }
                    }
                } else {
                    // Handle the case where the conversion to User failed or the document was null
                }
            }
            .addOnFailureListener { e ->
                //Here call a function of base activity or fragment for transferring the result to it.
                when (context) {
                    is SignInActivity -> {
                        (context as SignInActivity).hideProgressDialog()
                    }
                    is ProfileActivity -> {
                        (context as ProfileActivity).hideProgressDialog()
                    }
                    is FragmentActivity -> {
                        Log.e("UserData", "Bhaakkkkkkkkkkkkkkkkkkkkkkk")
                    }
                    is AccountActivity -> {
                        (context as AccountActivity).hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
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

    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Data updated successfully!")

                // Notify the success result.

                when (activity) {
                    is AccountActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AccountActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.", e
                )
            }
    }
    fun logoutUser(context: Context) {
        FirebaseAuth.getInstance().signOut()

        // After logout, navigate to the SplashActivity
        val intent = Intent(context, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }



    //Journal
    // Inside FirestoreClass
    fun saveJournalEntry(title: String, text: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Get the current user ID
        val userId = getCurrentUserID()

        // Get the current date
        val currentDate = Calendar.getInstance().time

        // Define the desired date format
        val dateFormat = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault())

        // Format the current date
        val formattedDate = dateFormat.format(currentDate)

        // Create a HashMap to store journal entry data
        val journalData = hashMapOf(
            "userId" to userId,
            "title" to title,
            "text" to text,
            "timestamp" to formattedDate
        )

        // Save the data to Firestore
        mFireStore.collection("journal_entries")
            .add(journalData)
            .addOnSuccessListener {
                // Entry saved successfully
                onSuccess.invoke()
            }
            .addOnFailureListener { e ->
                // Handle failure
                onFailure.invoke(e)
            }
    }

    fun getJournalEntriesForDate(date: String, onSuccess: (List<JournalList>) -> Unit, onFailure: (Exception) -> Unit) {
        val userID = getCurrentUserID()
        mFireStore.collection("journal_entries")
            .whereEqualTo("timestamp", date)
            .whereEqualTo("userId", userID)
            .get()
            .addOnSuccessListener { documents ->
                val journalEntries = mutableListOf<JournalList>()

                for (document in documents) {
                    val entry = document.toObject(JournalList::class.java)
                    journalEntries.add(entry)
                }

                onSuccess.invoke(journalEntries)
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    // Inside FirestoreClass
    fun getTotalJournalsCount(onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        val userID = getCurrentUserID()
        mFireStore.collection("journal_entries") // Replace with your actual collection name
            .whereEqualTo("userId", userID)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess.invoke(documents.size())
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    fun saveMoodJournalEntry(title: String, text: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Get the current user ID
        val userId = getCurrentUserID()

        // Get the current date
        val currentDate = Calendar.getInstance().time

        // Define the desired date format
        val dateFormat = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault())

        // Format the current date
        val formattedDate = dateFormat.format(currentDate)

        // Create a HashMap to store journal entry data
        val journalData = hashMapOf(
            "userId" to userId,
            "title" to title,
            "text" to text,
            "timestamp" to formattedDate
        )

        // Save the data to Firestore
        mFireStore.collection("mood_journal_entries")
            .add(journalData)
            .addOnSuccessListener {
                // Entry saved successfully
                onSuccess.invoke()
            }
            .addOnFailureListener { e ->
                // Handle failure
                onFailure.invoke(e)
            }
    }

    fun getMoodJournalEntriesForDate(date: String, onSuccess: (List<JournalList>) -> Unit, onFailure: (Exception) -> Unit) {
        val userID = getCurrentUserID()
        mFireStore.collection("mood_journal_entries")
            .whereEqualTo("timestamp", date)
            .whereEqualTo("userId", userID)
            .get()
            .addOnSuccessListener { documents ->
                val journalEntries = mutableListOf<JournalList>()

                for (document in documents) {
                    val entry = document.toObject(JournalList::class.java)
                    journalEntries.add(entry)
                }

                onSuccess.invoke(journalEntries)
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    // Inside FirestoreClass
    fun getMoodTotalJournalsCount(onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        val userID = getCurrentUserID()
        mFireStore.collection("mood_journal_entries") // Replace with your actual collection name
            .whereEqualTo("userId", userID)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess.invoke(documents.size())
            }
            .addOnFailureListener { e ->
                onFailure.invoke(e)
            }
    }

    //Quotes

    fun getDailyQuote(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val apiUrl = "https://quotesapi-3eo5.onrender.com/api/v1/${getCurrentDay()}"

        // You can use a networking library like Retrofit or any other method to fetch data from the API
        // I'll provide a simple example using AsyncTask for demonstration purposes
        FetchQuoteAsyncTask(onSuccess, onFailure).execute(apiUrl)
    }

    fun getCurrentDay(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    private class FetchQuoteAsyncTask(
        private val onSuccess: (String) -> Unit,
        private val onFailure: (Exception) -> Unit
    ) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {
            try {
                val url = URL(params[0])
                val connection = url.openConnection() as HttpURLConnection
                val reader = BufferedReader(InputStreamReader(connection.inputStream))

                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                connection.disconnect()

                return response.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (result.isNotEmpty()) {
                try {
                    val jsonObject = JSONObject(result)
                    val quote = jsonObject.getString("quote")
                    onSuccess.invoke(quote)
                } catch (e: JSONException) {
                    onFailure.invoke(e)
                }
            } else {
                onFailure.invoke(Exception("Empty response"))
            }
        }
    }

}