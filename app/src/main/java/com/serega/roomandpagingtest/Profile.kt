package com.serega.roomandpagingtest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author S.A.Bobrischev
 *         Developed by Magora Team (magora-systems.com). 13.05.18.
 */
@Entity(tableName = Profile.TABLE_NAME)
data class Profile(
        @PrimaryKey var uuid: String,
        var name: String,
        @ColumnInfo(name = "age") var age: Int
) {
    companion object {
        const val TABLE_NAME = "table_profile"
    }
}

