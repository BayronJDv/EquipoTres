package com.example.widgetappbeta.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.widgetappbeta.model.Inventory


@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveInventory(inventory: Inventory)

    @Query("SELECT * FROM inventory")
    suspend fun getListInventory(): MutableList<Inventory>

    @Delete
    suspend fun deleteInventory(inventory: Inventory)

    @Update
    suspend fun updateInventory(inventory: Inventory)


}