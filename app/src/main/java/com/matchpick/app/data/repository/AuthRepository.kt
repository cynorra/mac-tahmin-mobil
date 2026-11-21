package com.matchpick.app.data.repository

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val context: Context
) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun syncUserOnAuthSuccess(user: FirebaseUser, countryCode: String) {
        val userDocRef = firestore.collection("users").document(user.uid)
        val snapshot = userDocRef.get().await()

        if (!snapshot.exists()) {
            val pendingReferrer = extractPendingReferralCode()
            val userData = hashMapOf(
                "uid" to user.uid,
                "displayName" to (user.displayName ?: "Striker_${user.uid.take(4)}"),
                "email" to (user.email ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "country" to countryCode,
                "totalPoints" to 0L,
                "currentStreak" to 0,
                "bestStreak" to 0,
                "referredBy" to (pendingReferrer ?: ""),
                "createdAt" to FieldValue.serverTimestamp()
            )
            userDocRef.set(userData, SetOptions.merge()).await()

            // Referans ödülü istemciden verilemez (kurallar başkasının puanını yazdırmaz); backend işlemeli.
        }
    }

    /** Hesap silme: kullanicinin tahminlerini, gonderilerini ve profil belgesini siler. */
    suspend fun deleteUserData(uid: String) {
        val preds = firestore.collection("predictions").whereEqualTo("userId", uid).get().await()
        val posts = firestore.collection("feed_posts").whereEqualTo("authorId", uid).get().await()
        (preds.documents + posts.documents).chunked(400).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
        firestore.collection("users").document(uid).delete().await()
    }

    private fun extractPendingReferralCode(): String? {
        var refCode: String? = null
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    val response: ReferrerDetails = referrerClient.installReferrer
                    val referrerUrl = response.installReferrer
                    val decoded = URLDecoder.decode(referrerUrl, StandardCharsets.UTF_8.name())
                    // URL formatı: utm_source=google-play&ref_code=PRO2026
                    val params = decoded.split("&").associate {
                        val parts = it.split("=")
                        if (parts.size == 2) parts[0] to parts[1] else "" to ""
                    }
                    refCode = params["ref_code"]
                    referrerClient.endConnection()
                }
            }
            override fun onInstallReferrerServiceDisconnected() {}
        })
        return refCode
    }
}
