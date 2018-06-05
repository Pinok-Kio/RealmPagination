package com.serega.roomandpagingtest

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * @author S.A.Bobrischev
 *         Developed by Magora Team (magora-systems.com). 02.06.18.
 */
@RealmClass
open class ProfileModel() : RealmModel {
    @PrimaryKey var uuid: String? = null
    var name: String? = null
    var age: Int = 0
}