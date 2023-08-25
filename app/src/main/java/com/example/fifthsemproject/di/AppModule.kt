package com.example.fifthsemproject.di

import android.content.Context
import com.example.fifthsemproject.data.local.GPTDatabase
import com.example.fifthsemproject.data.remote.Gpt3ApiManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideGPTDatabase(
        @ApplicationContext context: Context
    ): GPTDatabase = GPTDatabase(context)

}