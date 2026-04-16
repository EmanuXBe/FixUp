package edu.javeriana.fixup.data.mapper

import edu.javeriana.fixup.data.network.dto.ReviewDto
import edu.javeriana.fixup.data.network.dto.UserDto
import edu.javeriana.fixup.data.network.dto.ServiceDto
import edu.javeriana.fixup.ui.model.ReviewModel
import edu.javeriana.fixup.ui.model.ServiceModel
import edu.javeriana.fixup.ui.model.UserModel

fun ReviewDto.toDomain(): ReviewModel {
    /**
     * Convierte un DTO de reseña a un modelo de dominio.
     * Esta decisión arquitectónica (Mapper) permite que los cambios en la API (como nuevos campos)
     * no afecten directamente a la lógica de la UI, manteniendo el desacoplamiento.
     */
    return ReviewModel(
        id = id ?: "",
        userId = userId ?: "",
        userName = userName ?: "Usuario anónimo",
        articleName = articleName ?: "",
        rating = rating?.toInt() ?: 0,
        comment = comment ?: "",
        userProfileImage = userProfileImage ?: "", // Mapeo de la nueva URL de imagen de perfil
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
        userId = userId,
        userName = userName,
        articleName = articleName,
        rating = rating.toDouble(),
        comment = comment,
        userProfileImage = userProfileImage,
        createdAt = date
    )
}
