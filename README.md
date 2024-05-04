# Eclipsify - Your Mental Wellness Companion

Eclipsify is a mobile application designed to support individuals in their mental health journey. With a range of integrated features, users can track their progress, access resources, and connect with mental health professionals conveniently.

# Download the App

Check out our app 'Eclipsify' - made with ❤ by vyarth : https://drive.google.com/file/d/1-unQ5RzGlF2QlU5A7nweNzdLDkyoleRU/view 


[![Eclipsify Demo Video](https://img.youtube.com/vi/Hl4lUfwXDMQ/0.jpg)](https://www.youtube.com/watch?v=Hl4lUfwXDMQ)



## Features

- **Daily Mood Tracker:** Log your mood daily to monitor your emotional well-being over time.
- **Daily Inspirational Quote:** Receive a daily dose of inspiration to uplift your spirits.
- **Stats Section:** Analyze your progress and emotional patterns with streak tracking and mood trend analysis.
- **Profile Management:** Personalize your profile, provide feedback, and logout securely.
- **Explore Section:**
  - **Meditation:** Guided meditation practices for tranquility and mindfulness.
  - **Breathing Exercises:** Haptic feedback-assisted breathing exercises for clarity of mind.
  - **Daily Affirmations:** Maintain a positive mindset with daily affirmations.
  - **Articles:** Access curated articles on mental health topics and coping strategies.
  - **Sleep Serenity:** Bedtime stories and calming music to enhance sleep quality.
  - **Music Therapy:** Healing sounds and nature melodies for relaxation and rejuvenation.
- **Journal Section:**
  - **Daily Journal:** Cultivate a daily writing habit to reflect on thoughts and experiences.
  - **Mood Journal:** Track mood fluctuations over time for self-awareness and well-being.
- **Personal Mental Health Assistant:** Chatbot powered by Google AI for personalized support and guidance.
- **Discover Experts:** Find local mental health professionals nearby, facilitated by Google Maps integration.
- **Community Section:** Engage with like-minded individuals, share thoughts, and provide support.


## How to Run

1. Clone the repository to your local machine.
   ```properties
   git clone https://github.com/whatDeepak/Eclipsify.git
   
2. Ensure you have Kotlin and Android Studio installed on your development environment.
3. Open the project in Android Studio.
4. Add your API keys to `local.properties` as follows:
   
   ```properties
   geminiApiKey=your_gemini_api_key_here
   mapsApiKey=your_maps_api_key_here
   placesApiKey=your_places_api_key_here

Feel free to replace `your_gemini_api_key_here`, `your_maps_api_key_here`, and `your_places_api_key_here` with your actual API keys.

5. Add the following <meta-data> tag inside the <application> element of your AndroidManifest.xml:
   
   ```properties
   <application>
    ...
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="${mapsApiKey}" />
    ...
    </application>

6. Sync the project. Build and run the application on your preferred mobile device or emulator.
7. Register or log in to access the app's features.
8. Explore the various sections and features available to support your mental wellness journey.

## Contributors

- [Deepak Kumar](https://github.com/whatDeepak)

## License

This project is licensed under the [MIT License](LICENSE).

