package edu.javeriana.fixup.data.datasource.impl

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.fixup.data.datasource.interfaces.UserDataSource
import edu.javeriana.fixup.data.network.dto.UserDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserDataSource {

    override suspend fun getUserById(userId: String): UserDto? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                val followers = getFollowersIds(userId)
                val following = getFollowingIds(userId)
                UserDto(
                    id = document.id,
                    name = document.getString("name"),
                    email = document.getString("email"),
                    phone = document.getString("phone"),
                    address = document.getString("address"),
                    role = document.getString("role"),
                    profileImageUrl = document.getString("profileImageUrl"),
                    followers = followers,
                    following = following
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<UserDto> {
        if (userIds.isEmpty()) return emptyList()
        return userIds.mapNotNull { userId -> getUserById(userId) }
    }

    override suspend fun toggleFollowUser(currentUserId: String, targetUserId: String, isFollowing: Boolean) {
        val batch = firestore.batch()

        val followingRef = firestore.collection("users").document(currentUserId)
            .collection("following").document(targetUserId)
        val followersRef = firestore.collection("users").document(targetUserId)
            .collection("followers").document(currentUserId)

        if (isFollowing) {
            batch.delete(followingRef)
            batch.delete(followersRef)
        } else {
            val data = mapOf("timestamp" to com.google.firebase.Timestamp.now())
            batch.set(followingRef, data)
            batch.set(followersRef, data)
        }

        batch.commit().await()
    }

    override suspend fun isFollowing(currentUserId: String, targetUserId: String): Boolean {
        val doc = firestore.collection("users").document(currentUserId)
            .collection("following").document(targetUserId)
            .get()
            .await()
        return doc.exists()
    }

    override suspend fun getFollowersCount(userId: String): Long {
        return try {
            firestore.collection("users").document(userId)
                .collection("followers")
                .count()
                .get(AggregateSource.SERVER)
                .await()
                .count
        } catch (e: Exception) {
            0L
        }
    }

    override suspend fun getFollowingCount(userId: String): Long {
        return try {
            firestore.collection("users").document(userId)
                .collection("following")
                .count()
                .get(AggregateSource.SERVER)
                .await()
                .count
        } catch (e: Exception) {
            0L
        }
    }

    override suspend fun getFollowersIds(userId: String): List<String> {
        val snapshot = firestore.collection("users").document(userId)
            .collection("followers")
            .get()
            .await()
        return snapshot.documents.map { it.id }
    }

    override suspend fun getFollowingIds(userId: String): List<String> {
        val snapshot = firestore.collection("users").document(userId)
            .collection("following")
            .get()
            .await()
        return snapshot.documents.map { it.id }
    }

    override suspend fun updateFcmToken(userId: String, token: String) {
        firestore.collection("users").document(userId)
            .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
            .await()
    }
}
