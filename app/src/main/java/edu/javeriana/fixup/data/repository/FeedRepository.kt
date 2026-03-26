package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.CategoryDto
import edu.javeriana.fixup.data.datasource.FeedDataSource
import edu.javeriana.fixup.data.datasource.PublicationDto
import edu.javeriana.fixup.ui.features.feed.CategoryItemModel
import edu.javeriana.fixup.ui.features.feed.PublicationCardModel
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val dataSource: FeedDataSource
) {
    suspend fun getCategories(): Result<List<CategoryItemModel>> {
        return try {
            val dtos = dataSource.getCategories()
            Result.success(dtos.map { it.toUiModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPublications(): Result<List<PublicationCardModel>> {
        return try {
            val dtos = dataSource.getPublications()
            Result.success(dtos.map { it.toUiModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension functions for mapping (Mappers)
fun CategoryDto.toUiModel() = CategoryItemModel(
    imageRes = this.iconRes,
    title = this.name
)

fun PublicationDto.toUiModel() = PublicationCardModel(
    id = this.id,
    imageRes = this.imageRes,
    title = this.title,
    price = this.priceText
)
