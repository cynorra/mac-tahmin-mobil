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
        return fixtureDao.getAllFixtures().flowOn(Dispatchers.IO)
    }

    fun getFixturesByLeague(leagueId: String): Flow<List<FixtureEntity>> {
        return fixtureDao.getFixturesByLeague(leagueId).flowOn(Dispatchers.IO)
    }

    suspend fun getUserPredictionCount(): Int = withContext(Dispatchers.IO) {
        fixtureDao.getUserPredictionCount()
    }

    suspend fun savePrediction(fixtureId: String, pick: String, userId: String) = withContext(Dispatchers.IO) {
        // Önce yerel veritabanını güncelle (0 ms gecikme ve anında UI feedback)
        fixtureDao.updateUserPick(fixtureId, pick)

        // Firestore'a tahmini senkronize et (Network/offline hata güvenliği)
        try {
            val predictionData = hashMapOf(
                "fixtureId" to fixtureId,
                "userId" to userId,
                "pick" to pick,
                "status" to "PENDING",
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            firestore.collection("predictions")
                .document("${fixtureId}_$userId")
                .set(predictionData)
                .await()
        } catch (e: Exception) {
            android.util.Log.w("MatchPickBackend", "Firestore prediction sync postponed: ${e.message}")
        }
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
                        stadiumName = raw["stadiumName"] as? String ?: "",
                        matchTimestamp = (raw["matchTimestamp"] as? Number)?.toLong() ?: 0L,
                        officialPick = raw["officialPick"] as? String,
                        officialConfidence = (raw["officialConfidence"] as? Number)?.toInt() ?: 0,
                        userPick = null,
                        isLocked = (raw["isLocked"] as? Boolean) ?: false,
                        oddsHome = (raw["oddsHome"] as? Number)?.toFloat() ?: 2.10f,
                        oddsDraw = (raw["oddsDraw"] as? Number)?.toFloat() ?: 3.30f,
                        oddsAway = (raw["oddsAway"] as? Number)?.toFloat() ?: 3.20f,
                        votesHomePercent = (raw["votesHomePercent"] as? Number)?.toInt() ?: 45,
                        votesDrawPercent = (raw["votesDrawPercent"] as? Number)?.toInt() ?: 25,
                        votesAwayPercent = (raw["votesAwayPercent"] as? Number)?.toInt() ?: 30,
                        aiAnalysis = raw["aiAnalysis"] as? String ?: ""
                    )
                }
                // REPLACE kullanıcının tahminini siliyordu -> XP tekrar kazanılabiliyordu. Mevcut tahminleri koru.
                val picks = fixtureDao.getPickedFixtures().associate { it.id to it.userPick }
                if (entities.isNotEmpty()) {
                    val activeIds = entities.map { it.id }
                    fixtureDao.deleteFixturesNotIn(activeIds)
                    fixtureDao.insertFixtures(entities.map { e -> picks[e.id]?.let { e.copy(userPick = it) } ?: e })
                }
            }
        } catch (_: Exception) {
            // Offline fallback: Hata anında Room'daki mevcut verilerle devam edilir, ancak çok eski maçları temizle
            val cutoff = (System.currentTimeMillis() / 1000L) - 43200L // 12 saatten eski bitmiş maçları temizle
            try { fixtureDao.deleteExpiredFixtures(cutoff) } catch (_: Exception) {}
        }
    }
}
