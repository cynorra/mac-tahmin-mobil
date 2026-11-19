package com.matchpick.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.matchpick.app.data.local.FixtureDao
import com.matchpick.app.data.local.FixtureEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FixtureRepository @Inject constructor(
    private val fixtureDao: FixtureDao,
    private val firestore: FirebaseFirestore
) {

    fun getFixtures(countryCode: String): Flow<List<FixtureEntity>> {
        return fixtureDao.getFixturesByRegion(countryCode).flowOn(Dispatchers.IO)
    }

    suspend fun savePrediction(fixtureId: String, pick: String, userId: String) = withContext(Dispatchers.IO) {
        // Önce yerel veritabanını güncelle (0 ms gecikme ve anında UI feedback)
        fixtureDao.updateUserPick(fixtureId, pick)

        // Firestore'a tahmini senkronize et
        val predictionData = hashMapOf(
            "fixtureId" to fixtureId,
            "userId" to userId,
            "pick" to pick,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        firestore.collection("predictions")
            .document("${fixtureId}_$userId")
            .set(predictionData)
            .await()
    }

    suspend fun refreshDailyFixtures(force: Boolean = false) = withContext(Dispatchers.IO) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())

        try {
            // Sadece tek doküman okuması: 1 read
            val document = firestore.collection("daily_bundles").document(today).get().await()
            if (document.exists()) {
                val fixturesRaw = document.get("fixtures") as? List<Map<String, Any>> ?: emptyList()
                val entities = fixturesRaw.map { raw ->
                    FixtureEntity(
                        id = raw["id"] as? String ?: "",
                        leagueId = raw["leagueId"] as? String ?: "",
                        leagueName = raw["leagueName"] as? String ?: "",
                        countryCode = raw["countryCode"] as? String ?: "GLOBAL",
                        homeTeamName = raw["homeTeamName"] as? String ?: "",
                        homeTeamLogo = raw["homeTeamLogo"] as? String ?: "",
                        awayTeamName = raw["awayTeamName"] as? String ?: "",
                        awayTeamLogo = raw["awayTeamLogo"] as? String ?: "",
                        matchTimestamp = (raw["matchTimestamp"] as? Number)?.toLong() ?: 0L,
                        officialPick = raw["officialPick"] as? String,
                        officialConfidence = (raw["officialConfidence"] as? Number)?.toInt() ?: 0,
                        userPick = null,
                        isLocked = (raw["isLocked"] as? Boolean) ?: false
                    )
                }
                fixtureDao.insertFixtures(entities)
            }
        } catch (_: Exception) {
            // Offline fallback: Hata anında Room'daki mevcut verilerle devam edilir
        }
    }
}
