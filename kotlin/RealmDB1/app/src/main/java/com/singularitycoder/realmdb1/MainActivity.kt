package com.singularitycoder.realmdb1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.singularitycoder.realmdb1.databinding.ActivityMainBinding
import com.singularitycoder.realmdb1.databinding.ItemUserBinding
import io.realm.Realm
import io.realm.RealmResults

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialise()
        setUpClickListeners()
        readAllUsers()
    }

    private fun initialise() {
        realm = Realm.getDefaultInstance()
    }

    private fun setUpClickListeners() {
        binding.apply {
            btnCreate.setOnClickListener { createUser() }
            btnRead.setOnClickListener { readUser() }
            btnReadAll.setOnClickListener { readAllUsers() }
            btnUpdate.setOnClickListener { updateUser() }
            btnDelete.setOnClickListener { deleteUser() }
            btnDeleteAll.setOnClickListener { deleteAllUser() }
        }
    }

    private fun createUser() {
        try {
            val user = User().apply {
                id = binding.etId.editText?.text.toString().toInt()
                name = binding.etName.editText?.text.toString()
                email = binding.etEmail.editText?.text.toString()
            }
            realm.executeTransaction { realm -> realm.copyToRealm(user) }
            clearFields()
            readAllUsers()
            Log.d("MainActivity", "User Created")
        } catch (e: Exception) {
            Log.e("MainActivity", e.message.toString())
        }
    }

    private fun readAllUsers() {
        try {
            val users: List<User> = realm.where(User::class.java).findAll()
            binding.llAllUsers.removeAllViews()
            for (i in users.indices) {
                val itemBinding: ItemUserBinding = ItemUserBinding.inflate(LayoutInflater.from(this), binding.llAllUsers, false)
                itemBinding.apply {
                    tvId.text = "ID: " + users[i].id
                    tvName.text = users[i].name
                    tvEmail.text = users[i].email
                }
                binding.llAllUsers.addView(itemBinding.root)
            }
            Log.d("MainActivity", "User Read")
        } catch (e: Exception) {
            Log.e("MainActivity", e.message.toString())
        }
    }

    private fun readUser() {
        try {
            val id: Int = binding.etId.editText?.text.toString().toInt()
            val user = realm.where(User::class.java).equalTo("id", id).findFirst()
            binding.apply {
                etName.editText?.setText(user?.name)
                etEmail.editText?.setText(user?.email)
            }
            Log.d("MainActivity", "User Read")
        } catch (e: Exception) {
            Log.e("MainActivity", e.message.toString())
        }
    }

    private fun updateUser() {
        try {
            val user = User().apply {
                id = binding.etId.editText?.text.toString().toInt()
                name = binding.etName.editText?.text.toString()
                email = binding.etEmail.editText?.text.toString()
            }
            realm.executeTransaction { realm -> realm.copyToRealmOrUpdate(user) }
            clearFields()
            Log.d("MainActivity", "User Updated")
        } catch (e: Exception) {
            Log.e("MainActivity", e.message.toString())
        }
    }

    private fun deleteUser() {
        try {
            val id: Int = binding.etId.editText?.text.toString().toInt()
            val user = realm.where(User::class.java).equalTo("id", id).findFirst()
            realm.executeTransaction { user?.deleteFromRealm() }
            clearFields()
            Log.d("MainActivity", "User Deleted")
        } catch (e: Exception) {
            Log.e("MainActivity", e.message.toString())
        }
    }

    private fun deleteAllUser() {
        try {
            val users: RealmResults<User> = realm.where(User::class.java).findAll()
            realm.executeTransaction { users.deleteAllFromRealm() }
            binding.llAllUsers.removeAllViews()
            Log.d("MainActivity", "User Read")
        } catch (e: Exception) {
            Log.e("MainActivity", e.message.toString())
        }
    }

    private fun clearFields() {
        binding.apply {
            etId.editText?.setText("")
            etName.editText?.setText("")
            etEmail.editText?.setText("")
        }
    }
}