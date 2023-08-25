package com.example.fifthsemproject.di

import android.content.Context
import com.example.fifthsemproject.data.local.GPTDatabase
import com.example.fifthsemproject.data.remote.Gpt3ApiManager
import com.example.fifthsemproject.data.repositories.DataRepositoryImpl
import com.example.fifthsemproject.domain.repositories.DataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{

    @Binds
    @Singleton
    abstract fun provideDataRepo(
        repository: DataRepositoryImpl
    ): DataRepository



}