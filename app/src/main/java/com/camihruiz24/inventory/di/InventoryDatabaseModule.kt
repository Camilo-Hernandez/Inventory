package com.camihruiz24.inventory.di

import android.content.Context
import com.camihruiz24.inventory.data.InventoryDatabase
import com.camihruiz24.inventory.data.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class InventoryDatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): InventoryDatabase {
        return InventoryDatabase.getDatabase(context)
    }

    @Provides
    fun provideItemDao(inventoryDatabase: InventoryDatabase): ItemDao {
        return inventoryDatabase.getItemDao()
    }

}