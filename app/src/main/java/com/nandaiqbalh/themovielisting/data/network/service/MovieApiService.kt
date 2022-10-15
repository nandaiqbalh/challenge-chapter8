package com.nandaiqbalh.themovielisting.data.network.service

import com.nandaiqbalh.themovielisting.BuildConfig
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.nandaiqbalh.themovielisting.data.network.model.detail.DetailMovie
import com.nandaiqbalh.themovielisting.data.network.model.popular.Popular
import com.nandaiqbalh.themovielisting.data.network.model.toprated.TopRated
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MovieApiService {
    @GET(ApiEndPoints.POPULAR_END_POINT)
    suspend fun getPopular(
        @Query("language") language: String = LANGUAGE_US,
        @Query("page") page: Int = PAGE
    ): Popular

    @GET(ApiEndPoints.TOP_RATED_END_POINT)
    suspend fun getTopRated(
        @Query("language") language: String = LANGUAGE_US,
        @Query("page") page: Int = PAGE
    ): TopRated

    @GET(ApiEndPoints.DETAIL_END_POINT)
    suspend fun getDetail(
        @Path("movie_id") id: Int,
        @Query("language") language: String = LANGUAGE_US
    ): DetailMovie

    companion object {
        private const val LANGUAGE_US = "en-US"
        private const val PAGE = 1

        @JvmStatic
        operator fun invoke(chuckerInterceptor: ChuckerInterceptor): MovieApiService {
            val authInterceptor = Interceptor {
                val originRequest = it.request()
                val oldUrl = originRequest.url
                val newUrl = oldUrl.newBuilder().apply {
                    addQueryParameter("api_key", BuildConfig.API_KEY)
                }.build()
                it.proceed(originRequest.newBuilder().url(newUrl).build())
            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(chuckerInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(MovieApiService::class.java)
        }
    }
}