package com.vyarth.ellipsify.model

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@[Module InstallIn(SingletonComponent::class)]
object GeminiModule {
    /**
     * Harassment	        Negative or harmful comments targeting identity and/or protected attributes.
     * Hate speech	        Content that is rude, disrespectful, or profane.
     * Sexually explicit	Contains references to sexual acts or other lewd content.
     * Dangerous	        Promotes, facilitates, or encourages harmful acts.
     * */
    private val harassment = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
    private val hateSpeech = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)

    private val config = generationConfig {
        temperature = 0.99f
        topK = 50
        topP = 0.99f
        maxOutputTokens=500
    }

    @[Provides Singleton GeminiPro]
    fun provideGemini(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-pro",
            apiKey = API_KEY,
            safetySettings = listOf(
                harassment, hateSpeech
            ),
            generationConfig = config
        )
    }

    private const val API_KEY = "AIzaSyAWZcz4ok3JWFCLgavHbPGCjNc2pvsPB2A"
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiPro

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeminiProVision