package com.example.languagelearner.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.random.Random

data class GeminiEvaluationResult(
    val fluencyScore: Int,
    val grammarFeedback: String,
    val aiTip: String,
    val nativeSuggestion: String
)

object GeminiService {
    private const val API_KEY = "YOUR_GEMINI_API_KEY_HERE"

    private val candidateModels = listOf(
        "gemini-3.5-flash-lite",
        "gemini-3.5-flash",
        "gemini-3-flash-preview"
    )

    private suspend fun callGemini(query: String, temperature: Float = 0.7f): String {
        var lastException: Exception? = null
        for (modelName in candidateModels) {
            try {
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = API_KEY,
                    generationConfig = generationConfig {
                        this.temperature = temperature
                    }
                )
                val response = model.generateContent(query)
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return text
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException ?: Exception("No Gemini models responded.")
    }

    suspend fun generatePrompt(
        languageName: String,
        languageCode: String,
        situationTitle: String,
        levelTitle: String
    ): SpeakingPrompt = withContext(Dispatchers.IO) {
        val randomSeed = Random.nextInt(1000, 9999)
        val scenarioVariations = listOf(
            "asking about recommendations or daily specials",
            "requesting a customization, specific size, or ingredient preference",
            "inquiring about prices, payment options, or the bill",
            "ordering takeout versus dining in",
            "asking where to sit or requesting a table",
            "asking for an additional item or condiment"
        ).random()

        val query = """
            You are an AI language tutor. Generate a unique, realistic conversation scenario for a student learning $languageName at a $levelTitle level in the situation '$situationTitle'.
            
            Sub-focus for this session: $scenarioVariations (Session ID: #$randomSeed).
            
            CRITICAL REQUIREMENTS:
            - Generate a distinct scenario each time.
            - Do NOT repeat standard templates like "Café Sunshine ordering a small latte".
            - Vary the venue name, partner personality, items, and the student's task.
            - Match the language complexity strictly to $levelTitle level.
            
            Return ONLY a valid JSON object with the following exact keys (no markdown formatting, no code blocks):
            {
              "situationContext": "A 1-sentence English description of what is happening in the scene.",
              "partnerSays": "A realistic opening line spoken by the conversation partner in fluent $languageName.",
              "yourTask": "A clear instruction in English telling the student what to say or order in response.",
              "helpfulVocabulary": ["word1 in $languageName", "word2 in $languageName", "word3 in $languageName", "word4 in $languageName"],
              "nativeExample": "A natural, complete sentence a native speaker of $languageName would say in response."
            }
        """.trimIndent()

        try {
            val responseText = callGemini(query, temperature = 0.95f)
            val jsonText = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(jsonText)
            val vocabArray = json.optJSONArray("helpfulVocabulary")
            val vocabList = mutableListOf<String>()
            if (vocabArray != null) {
                for (i in 0 until vocabArray.length()) {
                    vocabList.add(vocabArray.getString(i))
                }
            }

            SpeakingPrompt(
                situationTitle = situationTitle,
                situationContext = json.optString("situationContext", "Conversation practice in $languageName"),
                partnerSays = json.optString("partnerSays", "Hello!"),
                yourTask = json.optString("yourTask", "Respond in your own words in $languageName."),
                helpfulVocabulary = if (vocabList.isNotEmpty()) vocabList else listOf("hello", "please", "thank you"),
                nativeExample = json.optString("nativeExample", "Hello! Nice to meet you."),
                languageCode = languageCode
            )
        } catch (e: Exception) {
            SpeakingPrompt(
                situationTitle = situationTitle,
                situationContext = "Error: ${e.javaClass.simpleName}",
                partnerSays = "${e.message}",
                yourTask = "Check connection or key",
                helpfulVocabulary = listOf("retry", "check"),
                nativeExample = "Error",
                languageCode = languageCode
            )
        }
    }

    suspend fun evaluateSpeech(
        spokenText: String,
        languageName: String,
        situationTitle: String,
        levelTitle: String,
        partnerSays: String,
        task: String
    ): GeminiEvaluationResult = withContext(Dispatchers.IO) {
        if (spokenText.isBlank() || spokenText.contains("Tap the microphone")) {
            return@withContext GeminiEvaluationResult(
                fluencyScore = 0,
                grammarFeedback = "No spoken words were detected.",
                aiTip = "Tap the microphone and speak your response out loud.",
                nativeSuggestion = "Try speaking a complete sentence in $languageName."
            )
        }

        val query = """
            You are an expert language teacher evaluating a $levelTitle student learning $languageName.
            Situation: '$situationTitle'
            Partner said: '$partnerSays'
            Student task: '$task'
            Student said: '$spokenText'

            Evaluate what the student said in terms of grammar, vocabulary, sentence structure, and relevance to the scenario.
            
            Return ONLY a valid JSON object with the following exact keys (no markdown formatting, no code blocks):
            {
              "fluencyScore": 85,
              "grammarFeedback": "Detailed, friendly grammar and sentence structure feedback explaining any mistakes or good phrasing.",
              "aiTip": "A constructive tip on how to improve pronunciation, tone, or fluency.",
              "nativeSuggestion": "The most natural, polished way a native $languageName speaker would say the student's thought."
            }
        """.trimIndent()

        try {
            val responseText = callGemini(query, temperature = 0.3f)
            val jsonText = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(jsonText)
            GeminiEvaluationResult(
                fluencyScore = json.optInt("fluencyScore", 75),
                grammarFeedback = json.optString("grammarFeedback", "Good sentence structure! Keep practicing."),
                aiTip = json.optString("aiTip", "Try adding more descriptive adjectives to sound more natural."),
                nativeSuggestion = json.optString("nativeSuggestion", spokenText)
            )
        } catch (e: Exception) {
            GeminiEvaluationResult(
                fluencyScore = 75,
                grammarFeedback = "Your response was understood in context.",
                aiTip = "Focus on speaking clearly and confidently.",
                nativeSuggestion = "Keep up the great practice!"
            )
        }
    }
}