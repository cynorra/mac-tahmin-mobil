package com.matchpick.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "daily_fixtures")
data class FixtureEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val leagueName: String,
    val countryCode: String, // "GLOBAL", "TR", "ES", "GB", etc.
    val homeTeamName: String,
    val homeTeamLogo: String,
    val awayTeamName: String,
    val awayTeamLogo: String,
    val stadiumName: String,
    val matchTimestamp: Long,
    val officialPick: String?,
    val officialConfidence: Int,
    val userPick: String?,
    val isLocked: Boolean,
    val oddsHome: Float = 2.10f,
    val oddsDraw: Float = 3.30f,
    val oddsAway: Float = 3.20f,
    val votesHomePercent: Int = 48,
    val votesDrawPercent: Int = 26,
    val votesAwayPercent: Int = 26,
    val aiAnalysis: String = ""
)

@Dao
interface FixtureDao {
    @Query("SELECT * FROM daily_fixtures ORDER BY matchTimestamp ASC")
    fun getAllFixtures(): Flow<List<FixtureEntity>>

    @Query("SELECT * FROM daily_fixtures WHERE leagueId = :leagueId ORDER BY matchTimestamp ASC")
    fun getFixturesByLeague(leagueId: String): Flow<List<FixtureEntity>>

    @Query("SELECT * FROM daily_fixtures WHERE countryCode = :code OR countryCode = 'GLOBAL' ORDER BY matchTimestamp ASC")
    fun getFixturesByRegion(code: String): Flow<List<FixtureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixtures(fixtures: List<FixtureEntity>)

    @Query("UPDATE daily_fixtures SET userPick = :pick WHERE id = :fixtureId")
    suspend fun updateUserPick(fixtureId: String, pick: String)

    @Query("SELECT COUNT(*) FROM daily_fixtures")
    suspend fun getFixtureCount(): Int

    @Query("SELECT * FROM daily_fixtures WHERE userPick IS NOT NULL")
    suspend fun getPickedFixtures(): List<FixtureEntity>

    @Query("SELECT COUNT(*) FROM daily_fixtures WHERE userPick IS NOT NULL")
    suspend fun getUserPredictionCount(): Int
}

@Database(entities = [FixtureEntity::class], version = 2, exportSchema = false)
abstract class MatchPickDatabase : RoomDatabase() {
    abstract fun fixtureDao(): FixtureDao
}
