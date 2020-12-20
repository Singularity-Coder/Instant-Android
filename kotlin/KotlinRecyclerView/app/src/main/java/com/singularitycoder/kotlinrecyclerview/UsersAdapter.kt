package com.singularitycoder.kotlinrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(private val userList: ArrayList<User>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bindItems(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(user: User) {
            val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name) as TextView
            val tvUserProfession: TextView = itemView.findViewById(R.id.tv_user_profession) as TextView
            tvUserName.text = user.name
            tvUserProfession.text = user.profession
        }
    }
}