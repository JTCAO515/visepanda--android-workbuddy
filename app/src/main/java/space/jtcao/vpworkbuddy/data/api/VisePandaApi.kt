package space.jtcao.vpworkbuddy.data.api

import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import space.jtcao.vpworkbuddy.data.model.AppConfig
import space.jtcao.vpworkbuddy.data.model.CitiesResponse
import space.jtcao.vpworkbuddy.data.model.CityDetailResponse
import space.jtcao.vpworkbuddy.data.model.MapApiResponse
import space.jtcao.vpworkbuddy.data.model.ToolsResponse

/**
 * VisePanda REST API — Retrofit interface.
 *
 * All endpoints served from Vercel (WSGI Python backend).
 * Response types now match actual API response shapes:
 *   - /api/cities → { cities: { slug: City, ... } }
 *   - /api/map    → { cities: [MapMarker, ...] }
 *   - /api/tools  → { tools: { name: desc, ... } }
 */
interface VisePandaApi {

    /** List all 36 cities — returns map { slug → City } */
    @GET("/api/cities")
    suspend fun getCities(): CitiesResponse

    /** Get city detail with attractions, food, hotels, tips, estimates */
    @GET("/api/cities/{city}")
    suspend fun getCityDetail(@Path("city") city: String): CityDetailResponse

    /** Get full China map data with all city markers */
    @GET("/api/map")
    suspend fun getMapData(): MapApiResponse

    /** List all travel tools */
    @GET("/api/tools")
    suspend fun getTools(): ToolsResponse

    /** Get app configuration */
    @GET("/api/config")
    suspend fun getConfig(): AppConfig

    /**
     * Chat endpoint — returns SSE stream.
     * NOTE: This is NOT called directly via Retrofit.
     * SSE streaming uses SseClient (OkHttp raw) instead.
     */
    @POST("/api/chat")
    suspend fun chat(
        @Body body: JsonObject
    ): retrofit2.Response<okhttp3.ResponseBody>
}
