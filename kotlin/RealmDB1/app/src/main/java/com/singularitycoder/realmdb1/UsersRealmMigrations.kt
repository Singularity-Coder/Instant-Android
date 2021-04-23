package com.singularitycoder.realmdb1

import io.realm.DynamicRealm
import io.realm.RealmMigration

class UsersRealmMigrations : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        when (oldVersion) {
            1L -> {
                val userSchema = schema["User"]
                userSchema?.addField("email", String::class.javaPrimitiveType)
            }
        }
    }
}