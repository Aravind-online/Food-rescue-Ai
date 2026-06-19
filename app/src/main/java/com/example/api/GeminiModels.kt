package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String // inline base64 image data
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "temperature") val temperature: Double? = null,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content?
)

// --- Response Structure from Gemini for Freshness Estimation ---
@JsonClass(generateAdapter = true)
data class FreshnessAnalysis(
    @Json(name = "freshnessScore") val freshnessScore: Int, // 0-100
    @Json(name = "predictedSafeHours") val predictedSafeHours: Int, // hours remaining
    @Json(name = "freshnessReport") val freshnessReport: String, // descriptive report
    @Json(name = "compostAdvice") val compostAdvice: String // counseling on what to do if it spoils
)

// --- Response Structure from Gemini for Prep / Waste Analytics ---
@JsonClass(generateAdapter = true)
data class WastePrediction(
    @Json(name = "recommendedQty") val recommendedQty: Int, // Recommended prep quantity
    @Json(name = "predictedSurplus") val predictedSurplus: Int, // predicted surplus left
    @Json(name = "reasoning") val reasoning: String, // dynamic explanation
    @Json(name = "discountStrategy") val discountStrategy: String // dynamic discount recommendation
)
