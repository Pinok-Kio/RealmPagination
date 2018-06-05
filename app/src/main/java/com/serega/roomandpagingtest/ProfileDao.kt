package com.serega.roomandpagingtest

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * @author S.A.Bobrischev
 *         Developed by Magora Team (magora-systems.com). 13.05.18.
 */
@Dao
interface ProfileDao {

    @Query("SELECT (*) COUNT FROM ${Profile.TABLE_NAME}")
    fun getProfilesCount() : Int

    @Query("SELECT * FROM ${Profile.TABLE_NAME}")
    fun getAll(): List<Profile>

    @Query("SELECT * FROM ${Profile.TABLE_NAME} WHERE age > :minAge SORT BY DESC")
    fun getAllOlderThan(minAge: Int): List<Profile>

    @Insert
    fun insert(profiles: List<Profile>)

    @Delete
    fun delete(profile: Profile)

    @Delete
    fun clear()
}