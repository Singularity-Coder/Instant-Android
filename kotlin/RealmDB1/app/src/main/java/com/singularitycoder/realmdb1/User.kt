package com.singularitycoder.realmdb1

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {
    @PrimaryKey
    var id = 0
    var name: String? = null
    var email: String? = null
}