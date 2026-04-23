package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.interfaces.UserDataSource
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.ui.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDataSource: UserDataSource
) {
    fun getUserById(userId: String): Flow<Result<UserModel>> = flow {
        try {
            val userDto = userDataSource.getUserById(userId)
            if (userDto != null) {
                emit(Result.success(userDto.toDomain()))
            } else {
                emit(Result.failure(Exception("Usuario no encontrado")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun toggleFollow(currentUserId: String, targetUserId: String, isFollowing: Boolean): Result<Unit> {
        return try {
            userDataSource.toggleFollowUser(currentUserId, targetUserId, isFollowing)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
