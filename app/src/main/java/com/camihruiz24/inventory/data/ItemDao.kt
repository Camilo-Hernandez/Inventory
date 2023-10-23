package com.camihruiz24.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * List of SQL queries to make CRUD operations over the items table in the database
 */
@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignore because we only insert from one place. If a conflict appears, we ignore the new item
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM items WHERE id = :itemId")
    fun getItemById(itemId: Int): Flow<Item>

    @Query("SELECT * FROM items ORDER BY name DESC")
    fun getAllItems(): Flow<List<Item>>
}

