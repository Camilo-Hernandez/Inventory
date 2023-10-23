package com.camihruiz24.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [Item::class],
    version = 1,
    exportSchema = false,
)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun getItemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase =
            /**
             * Multiple threads can potentially ask for a database instance at the same time,
             * which results in two databases instead of one. This issue is known as a race
             * condition. Wrapping the code to get the database inside a synchronized block means
             * that only one thread of execution at a time can enter this block of code, which
             * makes sure the database only gets initialized once.
             */
            Instance ?: synchronized(this) { // This is the companion object
                Room.databaseBuilder(
                    context = context,
                    klass = InventoryDatabase::class.java,
                    name = "item_database"
                )
                    /**
                     * Por lo general, deberías brindar un objeto de migración con una estrategia
                     * para cuando cambie el esquema. Un objeto de migración es un objeto que
                     * define cómo tomas todas las filas con el esquema anterior y las conviertes
                     * en filas en el esquema nuevo para que no se pierdan datos. La migración se
                     * encuentra fuera del alcance de este codelab, pero el término se refiere a
                     * cuando se cambia el esquema y debes trasladar tu fecha sin perder los datos.
                     * Como esta es una app de ejemplo, una alternativa simple es destruir y volver
                     * a compilar la base de datos, lo que significa que se pierden los datos de
                     * inventario. Por ejemplo, si cambias algo en la clase de entidad, como agregar
                     * un parámetro nuevo, puedes permitir que la app borre y vuelva a inicializar la base de datos.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        Instance = it
                    }
            }
    }
}