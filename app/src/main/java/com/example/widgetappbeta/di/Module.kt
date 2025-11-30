package com.example.widgetappbeta.di

import android.content.Context
import androidx.room.Room
import com.example.widgetappbeta.data.InventoryDB
import com.example.widgetappbeta.data.InventoryDao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun provideInventoryDB(@ApplicationContext context: Context): InventoryDB {
        return Room.databaseBuilder(
            context,
            InventoryDB::class.java,
            "inventoryDB"
        ).build()
    }


    @Singleton
    @Provides
    fun provideInventoryDao(inventoryDB:InventoryDB): InventoryDao {
        return inventoryDB.inventoryDao()
    }

}