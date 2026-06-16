package space.jtcao.vpworkbuddy.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import space.jtcao.vpworkbuddy.data.api.ApiClient
import space.jtcao.vpworkbuddy.data.model.AppConfig

/**
 * Repository for travel tools and app configuration.
 *
 * Now uses Retrofit — no more blocking URL.readText().
 */
class ToolsRepository {

    private val api = ApiClient.api

    /** Fetch all travel tools — returns map { name → description } */
    suspend fun getTools(): Map<String, String> = withContext(Dispatchers.IO) {
        val response = api.getTools()
        response.tools
    }

    /** Fetch app configuration */
    suspend fun getConfig(): AppConfig = withContext(Dispatchers.IO) {
        api.getConfig()
    }
}
