package space.jtcao.visepanda.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import space.jtcao.visepanda.data.api.ApiClient
import space.jtcao.visepanda.data.model.MapMarker

/**
 * Repository for map data — coordinates of all cities in China.
 *
 * API: GET /api/map → { cities: [{name, name_cn, lat, lng, vibe, days}, ...] }
 * Now uses Retrofit — no more blocking URL.readText().
 */
class MapRepository {

    private val api = ApiClient.api

    /** Fetch all city markers with coordinates */
    suspend fun getMarkers(): List<MapMarker> = withContext(Dispatchers.IO) {
        val response = api.getMapData()
        response.cities
    }
}
