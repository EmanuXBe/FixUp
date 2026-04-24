package edu.javeriana.fixup.data.datasource.interfaces

import edu.javeriana.fixup.data.network.dto.UserDto

interface UserDataSource {
    suspend fun getUserById(userId: String): UserDto?
    suspend fun getUsersByIds(userIds: List<String>): List<UserDto>
    suspend fun toggleFollowUser(currentUserId: String, targetUserId: String, isFollowing: Boolean)
    suspend fun isFollowing(currentUserId: String, targetUserId: String): Boolean
    suspend fun getFollowersCount(userId: String): Long
    suspend fun getFollowingCount(userId: String): Long
    suspend fun getFollowersIds(userId: String): List<String>
    suspend fun getFollowingIds(userId: String): List<String>
}
