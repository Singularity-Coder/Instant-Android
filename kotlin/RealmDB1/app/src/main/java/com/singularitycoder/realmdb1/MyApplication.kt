package com.singularitycoder.realmdb1

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initRealmDatabase()
    }

    private fun initRealmDatabase() {
        Realm.init(this)
        val usersRealmConfig = RealmConfiguration.Builder()
            .name("usersdb.realm")
            .schemaVersion(1)
            .migration(UsersRealmMigrations())
            .build()
        Realm.setDefaultConfiguration(usersRealmConfig)
        Realm.getInstance(usersRealmConfig)
    }

    override fun onTerminate() {
        Realm.getDefaultInstance().close()
        super.onTerminate()
    }
}