package com.matchpick.app.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDataSeeder @Inject constructor(
    private val fixtureDao: FixtureDao
) {
    suspend fun populateDemoFixturesIfEmpty() {
        val now = System.currentTimeMillis() / 1000

        val realFixtures = listOf(
            FixtureEntity(
                id = "laliga_rm_barca",
                leagueId = "laliga",
                leagueName = "La Liga EA Sports",
                countryCode = "GLOBAL",
                homeTeamName = "Real Madrid",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8633.png",
                awayTeamName = "FC Barcelona",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8634.png",
                stadiumName = "Santiago Bernabéu",
                matchTimestamp = now + 7200, // 2 saat sonra
                officialPick = "1",
                officialConfidence = 82,
                userPick = null,
                isLocked = false,
                oddsHome = 2.05f,
                oddsDraw = 3.60f,
                oddsAway = 3.30f,
                votesHomePercent = 52,
                votesDrawPercent = 22,
                votesAwayPercent = 26,
                aiAnalysis = "El Clásico matchup. Real Madrid has won 4 of their last 5 home derbies, averaging 2.4 goals at Bernabéu."
            ),
            FixtureEntity(
                id = "epl_ars_mci",
                leagueId = "epl",
                leagueName = "Premier League",
                countryCode = "GLOBAL",
                homeTeamName = "Arsenal",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9825.png",
                awayTeamName = "Manchester City",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8456.png",
                stadiumName = "Emirates Stadium",
                matchTimestamp = now + 14400, // 4 saat sonra
                officialPick = "X",
                officialConfidence = 78,
                userPick = null,
                isLocked = false,
                oddsHome = 2.45f,
                oddsDraw = 3.40f,
                oddsAway = 2.75f,
                votesHomePercent = 44,
                votesDrawPercent = 28,
                votesAwayPercent = 28,
                aiAnalysis = "Title clash showdown. Arsenal holds the league's top home defense, while Man City leads in xG per 90."
            ),
            FixtureEntity(
                id = "superlig_gs_fb",
                leagueId = "superlig",
                leagueName = "Trendyol Süper Lig",
                countryCode = "TR",
                homeTeamName = "Galatasaray",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8637.png",
                awayTeamName = "Fenerbahçe",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8695.png",
                stadiumName = "RAMS Park",
                matchTimestamp = now + 21600, // 6 saat sonra
                officialPick = "1",
                officialConfidence = 74,
                userPick = null,
                isLocked = false,
                oddsHome = 2.15f,
                oddsDraw = 3.35f,
                oddsAway = 3.10f,
                votesHomePercent = 55,
                votesDrawPercent = 21,
                votesAwayPercent = 24,
                aiAnalysis = "Intercontinental Derby. Galatasaray is unbeaten in 18 home fixtures with high pressing in early periods."
            ),
            FixtureEntity(
                id = "epl_liv_che",
                leagueId = "epl",
                leagueName = "Premier League",
                countryCode = "GLOBAL",
                homeTeamName = "Liverpool",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8650.png",
                awayTeamName = "Chelsea",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8455.png",
                stadiumName = "Anfield",
                matchTimestamp = now + 86400, // Yarın
                officialPick = "1",
                officialConfidence = 79,
                userPick = null,
                isLocked = false,
                oddsHome = 1.80f,
                oddsDraw = 3.80f,
                oddsAway = 4.20f,
                votesHomePercent = 61,
                votesDrawPercent = 21,
                votesAwayPercent = 18,
                aiAnalysis = "High pressing encounter. Liverpool scored 14 goals in their last 5 home games, while Chelsea counter-attacks remain lethal."
            ),
            FixtureEntity(
                id = "bundesliga_bay_dor",
                leagueId = "bundesliga",
                leagueName = "Bundesliga",
                countryCode = "GLOBAL",
                homeTeamName = "Bayern München",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9823.png",
                awayTeamName = "Borussia Dortmund",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9789.png",
                stadiumName = "Allianz Arena",
                matchTimestamp = now + 93600, // Yarın
                officialPick = "1",
                officialConfidence = 85,
                userPick = null,
                isLocked = false,
                oddsHome = 1.65f,
                oddsDraw = 4.20f,
                oddsAway = 4.80f,
                votesHomePercent = 66,
                votesDrawPercent = 18,
                votesAwayPercent = 16,
                aiAnalysis = "Der Klassiker spectacle. The last 7 meetings have produced an average of 4.3 goals per game."
            ),
            FixtureEntity(
                id = "seriea_int_juv",
                leagueId = "seriea",
                leagueName = "Serie A Enilive",
                countryCode = "GLOBAL",
                homeTeamName = "Inter Milan",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8636.png",
                awayTeamName = "Juventus",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/9885.png",
                stadiumName = "San Siro",
                matchTimestamp = now + 100800,
                officialPick = "X",
                officialConfidence = 73,
                userPick = null,
                isLocked = false,
                oddsHome = 2.10f,
                oddsDraw = 3.20f,
                oddsAway = 3.50f,
                votesHomePercent = 50,
                votesDrawPercent = 30,
                votesAwayPercent = 20,
                aiAnalysis = "Derby d'Italia tactical battle. Both sides boast the lowest goals conceded in the first 45 minutes."
            ),
            FixtureEntity(
                id = "superlig_bjk_ts",
                leagueId = "superlig",
                leagueName = "Trendyol Süper Lig",
                countryCode = "TR",
                homeTeamName = "Beşiktaş",
                homeTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/10188.png",
                awayTeamName = "Trabzonspor",
                awayTeamLogo = "https://images.fotmob.com/image_resources/logo/teamlogo/8696.png",
                stadiumName = "Tüpraş Stadyumu",
                matchTimestamp = now + 108000,
                officialPick = "1",
                officialConfidence = 76,
                userPick = null,
                isLocked = false,
                oddsHome = 2.25f,
                oddsDraw = 3.30f,
                oddsAway = 3.00f,
                votesHomePercent = 46,
                votesDrawPercent = 27,
                votesAwayPercent = 27,
                aiAnalysis = "Intense Süper Lig classic. In their last 6 head-to-head fixtures, both teams found the back of the net."
            )
        )

        fixtureDao.insertFixtures(realFixtures)
    }
}
