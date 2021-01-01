package com.singularitycoder.kotlinretrofit1.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RepoResponse(
    @SerializedName("items")
    @Expose
    val itemsList: List<Item>,
)

data class Item(
    val name: String?,
    val owner: Owner,
    val description: String?,
)

data class Owner(
    val login: String?,
    @SerializedName("avatar_url")
    @Expose
    val avatarUrl: String?,
)

data class Error(
    val code: Int,
    val status: String,
    val message: String,
)