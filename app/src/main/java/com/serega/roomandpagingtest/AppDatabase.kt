package com.serega.roomandpagingtest

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * @author S.A.Bobrischev
 *         Developed by Magora Team (magora-systems.com). 14.05.18.
 */
@Database(entities = arrayOf(Profile::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}