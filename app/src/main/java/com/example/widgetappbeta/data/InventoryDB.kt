package com.example.widgetappbeta.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.widgetappbeta.model.Inventory


@Database(entities = [Inventory::class], version = 1)
abstract class InventoryDB : RoomDatabase(){

    abstract fun inventoryDao(): InventoryDao

    companion object{
        fun getDatabase(context: Context): InventoryDB{
            return Room.databaseBuilder(
                context.applicationContext,
                InventoryDB::class.java,
                "inventory_db").build()
        }
    }


}