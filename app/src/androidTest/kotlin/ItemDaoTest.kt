package com.camihruiz24.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.camihruiz24.inventory.data.InventoryDatabase
import com.camihruiz24.inventory.data.Item
import com.camihruiz24.inventory.data.ItemDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class ItemDaoTest {

    private var item1 = Item(1, "Apples", 10.0, 20)
    private var item2 = Item(2, "Bananas", 15.0, 97)

    private lateinit var itemDao: ItemDao
    private lateinit var fakeInventoryDatabase: InventoryDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()

        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        fakeInventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        itemDao = fakeInventoryDatabase.getItemDao()

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        fakeInventoryDatabase.close()
    }

    /**
     * Agrega funciones de utilidad para agregar un elemento y, luego, dos elementos a la base
     * de datos. Más adelante, usarás estas funciones en la prueba. Márcalas como suspend para
     * que puedan ejecutarse en una corrutina.
     */

    private suspend fun addOneItemToDb() {
        itemDao.insert(item1)
    }

    private suspend fun addTwoItemsToDb() {
        itemDao.insert(item1)
        itemDao.insert(item2)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems: List<Item> = itemDao.getAllItems().first()
        assertEquals(allItems[0], item1)
    }


    @Test
    @Throws(Exception::class)
    fun daoInsert_inserts2ItemsIntoDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = itemDao.getAllItems().first()
        assertEquals(allItems[0], item2)
        assertEquals(allItems[1], item1)
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updatesItemsInDB() = runBlocking {
        addTwoItemsToDb()
        itemDao.update(Item(1, "Apples", 15.0, 25))
        itemDao.update(Item(2, "Bananas", 5.0, 50))
        val allItems = itemDao.getAllItems().first()
        assertNotEquals(allItems[0], item2)
        assertNotEquals(allItems[1], item1)
        assertEquals(allItems[0], Item(2, "Bananas", 5.0, 50))
        assertEquals(allItems[1], Item(1, "Apples", 15.0, 25))
    }

    @Test
    fun daoDeletesItem_deletesItemInDB() = runBlocking {
        daoInsert_insertsItemIntoDB()
        itemDao.delete(item1)
        assertTrue(itemDao.getAllItems().first().isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoGetItem_returnsItemFromDB() = runBlocking {
        addTwoItemsToDb()
        val item: Flow<Item> = itemDao.getItemById(1)
        assertEquals(item1, item.first())
    }

}

