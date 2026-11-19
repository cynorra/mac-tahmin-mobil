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
                "totalPoints" to if (pendingReferrer != null) 25L else 0L, // Hoş geldin bonusu
                "currentStreak" to 0,
                "bestStreak" to 0,
                "referredBy" to (pendingReferrer ?: ""),
                "createdAt" to FieldValue.serverTimestamp()
            )
            userDocRef.set(userData, SetOptions.merge()).await()

            // Eğer bir referans koduyla geldiyse davet edene puan kazandır
            if (!pendingReferrer.isNullOrEmpty()) {
                rewardReferrer(pendingReferrer, user.uid)
            }
        }
    }

    private suspend fun rewardReferrer(referrerCode: String, newUserId: String) {
        val query = firestore.collection("users")
            .whereEqualTo("uid", referrerCode)
            .limit(1)
            .get()
            .await()

        if (!query.isEmpty) {
            val referrerDoc = query.documents[0]
            referrerDoc.reference.update("totalPoints", FieldValue.increment(50L)).await()
            
            // Log referral
            firestore.collection("referrals").document("${referrerCode}_$newUserId").set(
                mapOf(
                    "referrer" to referrerCode,
                    "referred" to newUserId,
                    "reward" to 50L,
                    "timestamp" to FieldValue.serverTimestamp()
                )
            ).await()
        }
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
                    // URL formatı: utm_source=google-play&ref_code=EREN123
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
