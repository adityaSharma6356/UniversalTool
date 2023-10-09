package com.example.fifthsemproject.di

import android.content.Context
import com.example.fifthsemproject.data.local.GPTDatabase
import com.example.fifthsemproject.data.remote.Gpt3ApiManager
import com.example.fifthsemproject.domain.repositories.CodeforcesApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideGPTDatabase(
        @ApplicationContext context: Context
    ): GPTDatabase = GPTDatabase(context)

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://codeforces.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCodeforcesApiService(retrofit: Retrofit): CodeforcesApiService {
        return retrofit.create(CodeforcesApiService::class.java)
    }


}