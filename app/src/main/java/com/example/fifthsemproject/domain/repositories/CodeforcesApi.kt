package com.example.fifthsemproject.domain.repositories

import com.example.fifthsemproject.domain.models.codeforces.CodeforcesUserInfoResponse
import com.example.fifthsemproject.domain.models.codeforces.RatingResponse
import com.example.fifthsemproject.domain.models.codeforces.SubmissionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface CodeforcesApiService {
    @GET("user.info")
    suspend fun getUserInfo(
        @Query("handles") handle: String
    ): Response<CodeforcesUserInfoResponse>

    @GET("user.status")
    suspend fun getUserStatus(
        @Query("handle") handle: String,
        @Query("from") from: Int,
        @Query("count") count: Int
    ): Response<SubmissionsResponse>

    @GET("user.rating")
    suspend fun getUserRating(@Query("handle") handle: String): Response<RatingResponse>

}