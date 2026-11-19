package com.matchpick.app.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDataSeeder @Inject constructor(
    private val fixtureDao: FixtureDao
) {
    suspend fun populateDemoFixturesIfEmpty() {
        val demoList = listOf(
            FixtureEntity(
                id = "mock_1",
                leagueId = "mls",
                leagueName = "MLS",
                countryCode = "US",
                homeTeamName = "Inter Miami",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9779.png",
                awayTeamName = "LA Galaxy",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/6541.png",
                matchTimestamp = (System.currentTimeMillis() / 1000) + 7200,
                officialPick = "1",
                officialConfidence = 78,
                userPick = null,
                isLocked = false
            ),
            FixtureEntity(
                id = "mock_2",
                leagueId = "superlig",
                leagueName = "Süper Lig",
                countryCode = "TR",
                homeTeamName = "Galatasaray",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
                awayTeamName = "Fenerbahçe",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8695.png",
                matchTimestamp = (System.currentTimeMillis() / 1000) + 14400,
                officialPick = "1",
                officialConfidence = 64,
                userPick = null,
                isLocked = false
            ),
            FixtureEntity(
                id = "mock_3",
                leagueId = "epl",
                leagueName = "Premier League",
                countryCode = "GLOBAL",
                homeTeamName = "Arsenal",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png",
                awayTeamName = "Chelsea",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8455.png",
                matchTimestamp = (System.currentTimeMillis() / 1000) + 21600,
                officialPick = "X",
                officialConfidence = 52,
                userPick = null,
                isLocked = false
            )
        )
        fixtureDao.insertFixtures(demoList)
    }
}
