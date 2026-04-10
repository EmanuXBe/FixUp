package edu.javeriana.fixup.data.mapper

import edu.javeriana.fixup.data.network.dto.ReviewDto
import edu.javeriana.fixup.data.network.dto.UserDto
import edu.javeriana.fixup.ui.model.ReviewModel
import edu.javeriana.fixup.ui.model.UserModel

fun ReviewDto.toDomain(): ReviewModel {
    return ReviewModel(
        id = id ?: "",
        userId = userId ?: "",
        userName = userName ?: "",
        rating = rating ?: 0.0,
        comment = comment ?: "",
        date = createdAt ?: ""
    )
}

fun UserDto.toDomain(): UserModel {
    return UserModel(
        id = id ?: "",
        name = name ?: "",
        email = email ?: "",
        phone = phone ?: "",
        address = address ?: "",
        role = role ?: "",
        profileImageUrl = profileImageUrl
    )
}

fun ReviewModel.toDto(): ReviewDto {
    return ReviewDto(
        id = id,
        userId = userId,
        userName = userName,
        rating = rating,
        comment = comment,
        createdAt = date
    )
}
