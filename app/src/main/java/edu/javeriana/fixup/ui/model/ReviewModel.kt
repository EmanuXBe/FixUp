package edu.javeriana.fixup.ui.model

import com.google.gson.annotations.SerializedName

data class ReviewModel(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("comment")
    val comment: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("service_id")
    val serviceId: Int = 0,
    @SerializedName("User")
    val user: UserInfo? = null,
    @SerializedName("Service")
    val service: ServiceInfo? = null
) {
    val displayName: String get() = user?.username?.ifEmpty { "Usuario $userId" } ?: "Usuario $userId"
    val idAsString: String get() = id.toString()
}

data class UserInfo(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("username") val username: String = "",
    @SerializedName("email") val email: String = ""
)

data class ServiceInfo(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("categoria") val categoria: String? = null
)