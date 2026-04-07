package com.jmr.coasterappwatch.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "favorites_prefs")

@Singleton
class FavoriteManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PARK_FAVORITES = stringSetPreferencesKey("park_favorites")
    private val RIDE_FAVORITES = stringSetPreferencesKey("ride_favorites")
    private val LAST_PARK_ID = stringSetPreferencesKey("last_park_id")

    val favoriteParks: Flow<Set<String>> = context.dataStore.data.map { it[PARK_FAVORITES] ?: emptySet() }
    val favoriteRides: Flow<Set<String>> = context.dataStore.data.map { it[RIDE_FAVORITES] ?: emptySet() }
    val lastParkId: Flow<String?> = context.dataStore.data.map { it[LAST_PARK_ID]?.firstOrNull() }

    suspend fun saveLastPark(id: String) {
        context.dataStore.edit { it[LAST_PARK_ID] = setOf(id) }
    }

    suspend fun toggleParkFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PARK_FAVORITES] ?: emptySet()
            prefs[PARK_FAVORITES] = if (current.contains(id)) current - id else current + id
        }
    }

    suspend fun toggleRideFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[RIDE_FAVORITES] ?: emptySet()
            prefs[RIDE_FAVORITES] = if (current.contains(id)) current - id else current + id
        }
    }

    suspend fun clearLastPark() {
        context.dataStore.edit { it.remove(LAST_PARK_ID) }
    }
}