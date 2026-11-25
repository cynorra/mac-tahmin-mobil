package com.matchpick.app.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveFixtureSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fixtureDao: FixtureDao
) {
    /**
     * Seeds Room local database with real fixtures from assets/fixtures_live.json
     * populated by the sports API engine (TheSportsDB). Zero fake or mock fixtures.
     */
    suspend fun seedLiveFixturesIfEmpty() {
        val cutoff = (System.currentTimeMillis() / 1000L) - 43200L
        try {
            fixtureDao.deleteExpiredFixtures(cutoff)
        } catch (_: Exception) {}
        if (fixtureDao.getFixtureCount() > 0) return

        try {
            val jsonString = context.assets.open("fixtures_live.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<FixtureEntity>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    FixtureEntity(
                        id = obj.getString("id"),
                        leagueId = obj.optString("leagueId", "epl"),
                        leagueName = obj.optString("leagueName", "Premier League"),
                        countryCode = obj.optString("countryCode", "GB"),
                        homeTeamName = obj.getString("homeTeamName"),
                        homeTeamLogo = obj.optString("homeTeamLogo", ""),
                        awayTeamName = obj.getString("awayTeamName"),
                        awayTeamLogo = obj.optString("awayTeamLogo", ""),
                        stadiumName = obj.optString("stadiumName", ""),
                        matchTimestamp = obj.optLong("matchTimestamp", System.currentTimeMillis() / 1000 + 7200),
                        officialPick = if (obj.has("officialPick") && !obj.isNull("officialPick")) obj.getString("officialPick") else null,
                        officialConfidence = obj.optInt("officialConfidence", 75),
                        userPick = null,
                        isLocked = obj.optBoolean("isLocked", false),
                        oddsHome = obj.optDouble("oddsHome", 2.10).toFloat(),
                        oddsDraw = obj.optDouble("oddsDraw", 3.30).toFloat(),
                        oddsAway = obj.optDouble("oddsAway", 3.20).toFloat(),
                        votesHomePercent = obj.optInt("votesHomePercent", 45),
                        votesDrawPercent = obj.optInt("votesDrawPercent", 25),
                        votesAwayPercent = obj.optInt("votesAwayPercent", 30),
                        aiAnalysis = obj.optString("aiAnalysis", "")
                    )
                )
            }
            if (list.isNotEmpty()) {
                fixtureDao.insertFixtures(list)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
