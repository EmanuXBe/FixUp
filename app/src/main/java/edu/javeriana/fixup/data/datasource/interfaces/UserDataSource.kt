package edu.javeriana.fixup.data.datasource.interfaces

import edu.javeriana.fixup.data.network.dto.UserDto

interface UserDataSource {
    suspend fun getUserById(userId: String): UserDto?
    suspend fun toggleFollowUser(currentUserId: String, targetUserId: String, isFollowing: Boolean)

    /** Retorna los datos completos de cada seguidor del usuario [userId] */
    suspend fun getFollowerUsers(userId: String): List<UserDto>

    /** Retorna los datos completos de cada usuario que sigue [userId] */
    suspend fun getFollowingUsers(userId: String): List<UserDto>
}