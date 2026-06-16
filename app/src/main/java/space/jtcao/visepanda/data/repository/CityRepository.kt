package space.jtcao.visepanda.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import space.jtcao.visepanda.data.api.ApiClient
import space.jtcao.visepanda.data.api.ApiConfig
import space.jtcao.visepanda.data.model.City
import space.jtcao.visepanda.data.model.CityDetail

/**
 * Repository for city and map data — fetched from the VisePanda API.
 *
 * Now uses Retrofit suspend functions — no more blocking URL.readText().
 */
class CityRepository {

    private val api = ApiClient.api

    /**
     * Fetch all cities as a flat list of (slug, City) pairs.
     * API returns: { cities: { slug: {...}, slug: {...} } }
     */
    suspend fun getCities(): List<Pair<String, City>> = withContext(Dispatchers.IO) {
        val response = api.getCities()
        response.cities.entries.map { (slug, city) -> slug to city }
    }

    /**
     * Fetch a single city's full detail.
     * API returns: { city: { ..., food: [...], hotels: {...}, tips: [...], estimate: {...}, map: {...} } }
     */
    suspend fun getCityDetail(city: String): CityDetail = withContext(Dispatchers.IO) {
        val response = api.getCityDetail(city)
        response.city
    }

    /** Get city image URL */
    fun getCityImageUrl(cityName: String): String {
        return "${ApiConfig.BASE_URL}/static/img/city-$cityName.jpg"
    }
}
