package edu.javeriana.fixup.data.repository

import edu.javeriana.fixup.data.datasource.interfaces.FeedDataSource
import edu.javeriana.fixup.data.network.dto.PublicationDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
"Tener 8 pruebas con mocks del repositorio" + "Probar mapeos"
 */
class FeedRepositoryUnitTest {

    private lateinit var dataSource: FeedDataSource
    private lateinit var repository: FeedRepository

    // ─── Setup ────────────────────────────────────────────────────────────────

    @Before
    fun setUp() {
        dataSource = mockk()
        repository = FeedRepository(dataSource)
    }

    // ─── Tests ────────────────────────────────────────────────────────────────
    // Prueba 1/8: contrato Result.success cuando el datasource retorna datos
    @Test
    fun getPublications_whenDatasourceReturnsData_wrapsInResultSuccess() = runTest {
        val dtos = listOf(buildDto("1", "Cocina Premium", "Desde $500000"))
        coEvery { dataSource.getPublications() } returns dtos

        val result = repository.getPublications()

        assertTrue("Must return Result.success", result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
    }

    // Prueba 2/8: contrato Result.failure cuando el datasource lanza excepción
    @Test
    fun getPublications_whenDatasourceThrows_returnsResultFailure() = runTest {
        coEvery { dataSource.getPublications() } throws RuntimeException("Network error")

        val result = repository.getPublications()

        assertTrue("Must return Result.failure on exception", result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    // Prueba 3/8 — MAPEO: el id del DTO se preserva en el modelo de UI
    @Test
    fun getPublications_mapping_preservesId() = runTest {
        coEvery { dataSource.getPublications() } returns listOf(buildDto(id = "article-42"))

        val card = repository.getPublications().getOrThrow().first()

        assertEquals("article-42", card.id)
    }

    // Prueba 4/8 — MAPEO: el title del DTO se preserva en el modelo de UI
    @Test
    fun getPublications_mapping_preservesTitle() = runTest {
        coEvery { dataSource.getPublications() } returns listOf(buildDto(title = "Remodelación Integral"))

        val card = repository.getPublications().getOrThrow().first()

        assertEquals("Remodelación Integral", card.title)
    }

    // Prueba 5/8 — MAPEO: el priceText del DTO se preserva en el modelo de UI
    @Test
    fun getPublications_mapping_preservesPriceText() = runTest {
        coEvery { dataSource.getPublications() } returns listOf(buildDto(priceText = "Desde $1200000"))

        val card = repository.getPublications().getOrThrow().first()

        assertEquals("Desde \$1200000", card.price)
    }

    // Prueba 6/8 — MAPEO: el imageUrl del DTO se preserva en el modelo de UI
    @Test
    fun getPublications_mapping_preservesImageUrl() = runTest {
        val url = "https://example.com/img/photo.jpg"
        coEvery { dataSource.getPublications() } returns listOf(buildDto(imageUrl = url))

        val card = repository.getPublications().getOrThrow().first()

        assertEquals(url, card.imageUrl)
    }

    // Prueba 7/8 — MAPEO: el authorId del DTO se preserva en el modelo de UI
    @Test
    fun getPublications_mapping_preservesAuthorId() = runTest {
        coEvery { dataSource.getPublications() } returns listOf(buildDto(authorId = "user-firebase-uid"))

        val card = repository.getPublications().getOrThrow().first()

        assertEquals("user-firebase-uid", card.authorId)
    }

    // Prueba 8/8: delega la lista de IDs de seguidos al datasource sin transformación
    @Test
    fun getFollowingPublications_delegatesFollowingIdListToDataSource() = runTest {
        val followingIds = listOf("uid-1", "uid-2", "uid-3")
        coEvery { dataSource.getFollowingPublications(followingIds) } returns emptyList()

        repository.getFollowingPublications(followingIds)

        coVerify(exactly = 1) { dataSource.getFollowingPublications(followingIds) }
    }

    // ─── Builder ──────────────────────────────────────────────────────────────

    private fun buildDto(
        id: String = "default-id",
        title: String = "Default Title",
        priceText: String = "Desde \$0",
        imageUrl: String = "https://picsum.photos/400/300",
        authorId: String = "default-author",
        description: String = "Descripción",
        location: String = "Bogotá"
    ) = PublicationDto(
        id = id,
        title = title,
        priceText = priceText,
        imageUrl = imageUrl,
        authorId = authorId,
        description = description,
        location = location
    )
}
