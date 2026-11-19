package com.matchpick.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "daily_fixtures")
data class FixtureEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val leagueName: String,
    val countryCode: String, // "US", "TR", "GLOBAL"
    val homeTeamName: String,
    val homeTeamLogo: String,
    val awayTeamName: String,
    val awayTeamLogo: String,
    val matchTimestamp: Long,
    val officialPick: String?,
    val officialConfidence: Int,
    val userPick: String?,
    val isLocked: Boolean
)

@Dao
interface FixtureDao {
    @Query("SELECT * FROM daily_fixtures ORDER BY matchTimestamp ASC")
    fun getAllFixtures(): Flow<List<FixtureEntity>>

    @Query("SELECT * FROM daily_fixtures WHERE countryCode = :code OR countryCode = 'GLOBAL' ORDER BY matchTimestamp ASC")
    fun getFixturesByRegion(code: String): Flow<List<FixtureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixtures(fixtures: List<FixtureEntity>)

    @Query("UPDATE daily_fixtures SET userPick = :pick WHERE id = :fixtureId")
    suspend fun updateUserPick(fixtureId: String, pick: String)
}

@Database(entities = [FixtureEntity::class], version = 1, exportSchema = false)
abstract class MatchPickDatabase : RoomDatabase() {
    abstract fun fixtureDao(): FixtureDao
}
