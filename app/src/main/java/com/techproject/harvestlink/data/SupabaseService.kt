package com.techproject.harvestlink.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import com.techproject.harvestlink.BuildConfig
import io.github.jan.supabase.realtime.Realtime

object SupabaseService {
    private val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    private val SUPABASE_API_KEY: String = BuildConfig.SUPABASE_API_KEY

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_API_KEY
        ) {
            defaultSerializer = KotlinXSerializer(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
            install(Realtime)
            install(Postgrest)
            install(Auth)
            install(Storage)
        }
    }
}