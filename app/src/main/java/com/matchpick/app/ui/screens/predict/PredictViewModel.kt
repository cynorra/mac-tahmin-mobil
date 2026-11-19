package com.matchpick.app.ui.screens.predict

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.matchpick.app.data.local.FixtureEntity
import com.matchpick.app.data.local.MockDataSeeder
import com.matchpick.app.data.repository.FixtureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.matchpick.app.core.analytics.AnalyticsTracker
import javax.inject.Inject

@HiltViewModel
class PredictViewModel @Inject constructor(
    private val repository: FixtureRepository,
    private val mockDataSeeder: MockDataSeeder,
    private val auth: FirebaseAuth,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictUiState())
    val uiState: StateFlow<PredictUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            mockDataSeeder.populateDemoFixturesIfEmpty()
        }
        loadFixtures("US")
        syncDailyData()
    }

    fun loadFixtures(region: String) {
        viewModelScope.launch {
            repository.getFixtures(region).collect { list ->
                _uiState.update { it.copy(fixtures = list, isLoading = false, selectedCountry = region) }
            }
        }
    }

    private fun syncDailyData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.refreshDailyFixtures()
        }
    }

    fun makePrediction(fixtureId: String, rawPick: String) {
        val pick = when (rawPick) {
            "HOME" -> "1"
            "DRAW" -> "X"
            "AWAY" -> "2"
            else -> rawPick
        }
        val uid = auth.currentUser?.uid ?: "demo_user"
        val matchedFixture = _uiState.value.fixtures.find { it.id == fixtureId }
        val odds = when (pick) {
            "1" -> matchedFixture?.oddsHome?.toDouble() ?: 2.10
            "X" -> matchedFixture?.oddsDraw?.toDouble() ?: 3.30
            "2" -> matchedFixture?.oddsAway?.toDouble() ?: 3.20
            else -> 2.10
        }

        analyticsTracker.logPredictionSubmitted(
            fixtureId = fixtureId,
            pick = pick,
            odds = odds,
            league = matchedFixture?.leagueName ?: _uiState.value.selectedCountry
        )
        analyticsTracker.logPointsEarned(10L, "prediction_submission")

        viewModelScope.launch {
            repository.savePrediction(fixtureId, pick, uid)
            _uiState.update { state ->
                val newStreak = state.streak + 1
                if (newStreak % 3 == 0) {
                    analyticsTracker.logStreakMilestone(newStreak)
                }
                state.copy(
                    streak = newStreak,
                    points = state.points + 10,
                    fixtures = state.fixtures.map {
                        if (it.id == fixtureId) it.copy(userPick = pick) else it
                    }
                )
            }
        }
    }
}

data class PredictUiState(
    val isLoading: Boolean = false,
    val fixtures: List<FixtureEntity> = emptyList(),
    val streak: Int = 3,
    val points: Long = 140,
    val selectedCountry: String = "US"
)
