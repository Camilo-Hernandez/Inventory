package com.camihruiz24.inventory.di

import com.camihruiz24.inventory.data.ItemsRepository
import com.camihruiz24.inventory.data.OfflineItemsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: OfflineItemsRepository
    ): ItemsRepository

}

