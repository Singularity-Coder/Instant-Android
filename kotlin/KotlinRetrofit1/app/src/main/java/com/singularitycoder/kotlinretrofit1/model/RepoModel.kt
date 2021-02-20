package com.singularitycoder.kotlinretrofit1.model

import com.google.gson.annotations.SerializedName

data class RepoResponse(
    @SerializedName("items")
    val itemsList: List<Item> = ArrayList(),
)

data class Item(
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("owner")
    val owner: Owner = Owner(),
    @SerializedName("description")
    val description: String? = "",
)

data class Owner(
    @SerializedName("login")
    val login: String? = "",
    @SerializedName("avatar_url")
    val avatarUrl: String? = "",
)

data class RepoError(
    val message: String? = "",
)