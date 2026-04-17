package edu.javeriana.fixup.data.mapper

import edu.javeriana.fixup.data.network.dto.ReviewDto
import edu.javeriana.fixup.data.network.dto.UserDto
import edu.javeriana.fixup.data.network.dto.ServiceDto
import edu.javeriana.fixup.ui.model.ReviewModel
import edu.javeriana.fixup.ui.model.ServiceModel
import edu.javeriana.fixup.ui.model.UserModel

fun ReviewDto.toDomain(): ReviewModel {
    return ReviewModel(
        id = id ?: 0,
        userId = user?.id ?: "",
        rating = rating ?: 0,
        comment = comment ?: "",
        date = date ?: "",
        authorName = authorName ?: user?.name ?: "Usuario desconocido",
        authorProfileImageUrl = authorProfileImageUrl ?: user?.profileImage ?: "",
        serviceTitle = service?.title ?: ""
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

fun ServiceDto.toDomain(): ServiceModel {
    return ServiceModel(
        id = id ?: 0,
        name = title ?: "",
        description = description ?: "",
        price = price ?: 0.0,
        category = category ?: "",
        providerId = providerId ?: "",
        imageUrl = imageUrl ?: "",
        rating = rating ?: 0.0
    )
}

fun ServiceModel.toDto(): ServiceDto {
    return ServiceDto(
        id = id,
        title = name,
        description = description,
        price = price,
        category = category,
        providerId = providerId,
        imageUrl = imageUrl,
        rating = rating
    )
}

fun ReviewModel.toDto(): ReviewDto {
    return ReviewDto(
        id = id,
        rating = rating,
        comment = comment,
        date = date,
        authorName = authorName,
        authorProfileImageUrl = authorProfileImageUrl,
        user = null,
        service = null
    )
}
