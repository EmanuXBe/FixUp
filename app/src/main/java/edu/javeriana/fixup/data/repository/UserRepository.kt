package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.interfaces.UserDataSource
import edu.javeriana.fixup.data.mapper.toDomain
import edu.javeriana.fixup.ui.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import edu.javeriana.fixup.ui.model.FollowUser
import edu.javeriana.fixup.data.network.dto.toFollowUser

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

    /**
     * Retorna la lista de seguidores con sus datos completos.
     * Usa el UserDataSource que ya tenés inyectado.
     */
    suspend fun getFollowers(userId: String): Result<List<FollowUser>> =
        runCatching {
            userDataSource.getFollowerUsers(userId)
                .map { it.toFollowUser() }
        }

    /**
     * Retorna la lista de usuarios que sigue [userId] con sus datos completos.
     */
    suspend fun getFollowing(userId: String): Result<List<FollowUser>> =
        runCatching {
            userDataSource.getFollowingUsers(userId)
                .map { it.toFollowUser() }
        }

    /** Obtiene los conteos de seguidores y siguiendo sin traer datos de cada usuario */
    suspend fun getFollowCounts(userId: String): Result<Pair<Int, Int>> =
        runCatching {
            val user = userDataSource.getUserById(userId)
            val followers = user?.followers?.size ?: 0
            val following = user?.following?.size ?: 0
            Pair(followers, following)
        }
}
