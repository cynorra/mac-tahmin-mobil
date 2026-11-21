package com.matchpick.app.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.matchpick.app.data.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SignInResult {
    data class Success(val userId: String) : SignInResult
    data class Failure(val error: String) : SignInResult
    data object Cancelled : SignInResult
}

@Singleton
class CredentialAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signInWithGoogle(activityContext: Context, webClientId: String): SignInResult =
        withContext(Dispatchers.IO) {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = activityContext,
                    request = request
                )

                val credential = result.credential
                val googleIdToken = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
                    .createFrom(credential.data).idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                val user = authResult.user
                if (user != null) {
                    // Kullanıcı profilini ve referansını Spark dostu biçimde senkronize et
                    authRepository.syncUserOnAuthSuccess(user, "US")
                    SignInResult.Success(user.uid)
                } else {
                    SignInResult.Failure("User payload is null")
                }
            } catch (e: GetCredentialException) {
                SignInResult.Failure(e.localizedMessage ?: "Credential retrieval failed")
            } catch (e: GoogleIdTokenParsingException) {
                SignInResult.Failure("Invalid Google ID token")
            } catch (e: Exception) {
                SignInResult.Failure(e.localizedMessage ?: "Unknown auth error")
            }
        }

    /** E-posta/sifre ile giris yapar; createAccount true ise hesap olusturur (misafir oturumu varsa yukseltir). */
    suspend fun signInWithEmail(email: String, password: String, createAccount: Boolean): SignInResult =
        withContext(Dispatchers.IO) {
            try {
                val cleanEmail = email.trim()
                val current = auth.currentUser
                val user = if (createAccount) {
                    if (current != null && current.isAnonymous) {
                        // Misafir oturumunu e-posta hesabina yukselt (uid korunur)
                        current.linkWithCredential(EmailAuthProvider.getCredential(cleanEmail, password)).await().user
                    } else {
                        auth.createUserWithEmailAndPassword(cleanEmail, password).await().user
                    }
                } else {
                    auth.signInWithEmailAndPassword(cleanEmail, password).await().user
                }
                if (user != null) {
                    authRepository.syncUserOnAuthSuccess(user, "US")
                    SignInResult.Success(user.uid)
                } else {
                    SignInResult.Failure("User payload is null")
                }
            } catch (e: Exception) {
                SignInResult.Failure(e.localizedMessage ?: "Unknown auth error")
            }
        }
}
