package com.example.ktor1.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ReqResUserList(
    var page: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    var total: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    var data: ArrayList<ReqResUser> = arrayListOf(),
    var support: Support? = Support(),
)

@Serializable
data class ReqResUser(
    var id: Int? = null,
    var email: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    var avatar: String? = null,
)

@Serializable
data class Support(
    var url: String? = null,
    var text: String? = null,
)

@Serializable
data class ReqResRequest(
    var name: String? = null,
    var job: String? = null,
)

@Serializable
data class ReqResResponse(
    var name: String? = null,
    var job: String? = null,
    var id: String? = null,
    var createdAt: String? = null,
)