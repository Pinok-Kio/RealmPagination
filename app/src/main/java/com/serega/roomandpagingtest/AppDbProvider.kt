package com.serega.roomandpagingtest

import androidx.room.Room
import android.content.Context

/**
 * @author S.A.Bobrischev
 *         Developed by Magora Team (magora-systems.com). 14.05.18.
 */
object AppDbProvider {
    private var database: AppDatabase? = null

    fun database(context: Context): AppDatabase {
        if (database == null) {
            database = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database").build()
        }
        return database!!
    }

}