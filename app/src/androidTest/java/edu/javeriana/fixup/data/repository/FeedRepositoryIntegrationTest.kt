package edu.javeriana.fixup.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.datasource.impl.FeedFirestoreDataSourceImpl
import edu.javeriana.fixup.data.util.AppConstants
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for FeedRepository backed by a real FeedFirestoreDataSourceImpl
 * connected to the Firebase local emulator.
 *
 * These tests verify the full pipeline from Firestore documents → DTOs → UI models,
 * including the PublicationDto.toUiModel() mapping defined in FeedRepository.kt.
 */
@RunWith(AndroidJUnit4::class)
class FeedRepositoryIntegrationTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: FeedRepository

    private val articlesCollection = "articles"

    // ─── Setup / Teardown ────────────────────────────────────────────────────

    @Before
    fun setUp() = runBlocking {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        configureEmulatorIfNeeded()

        auth.signOut()
        cleanArticles()

        val dataSource = FeedFirestoreDataSourceImpl(
            firestore = firestore,
            storage = FirebaseStorage.getInstance(),
            auth = auth
        )
        repository = FeedRepository(dataSource)
    }

    @After
    fun tearDown() = runBlocking {
        cleanArticles()
        auth.signOut()
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test
    fun getPublications_whenFirestoreHasDocuments_returnsResultSuccess() = runBlocking {
        insertArticle("art-1", "Electricidad General", 300_000.0)
        insertArticle("art-2", "Remodelación de Baño", 600_000.0)

        val result = repository.getPublications()

        assertTrue("Result must be success", result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
    }

    @Test
    fun getPublications_whenFirestoreIsEmpty_returnsSuccessWithEmptyList() = runBlocking {
        val result = repository.getPublications()

        assertTrue(result.isSuccess)
        assertTrue("Publications list must be empty", result.getOrThrow().isEmpty())
    }

    @Test
    fun getPublications_mapsAllFieldsCorrectly_toPublicationCardModel() = runBlocking {
        insertArticle(
            id       = "art-map",
            title    = "Instalación de Pisos",
            price    = 850_000.0,
            authorId = "author-xyz",
            category = "Pisos"
        )

        val result = repository.getPublications()
        val card = result.getOrThrow().first()

        // Verify mapping from PublicationDto → PublicationCardModel
        assertEquals("art-map",            card.id)
        assertEquals("Instalación de Pisos", card.title)
        assertTrue("priceText must contain price value", card.price.contains("850000"))
        assertEquals("author-xyz",         card.authorId)
        assertEquals("Pisos",              card.location)
    }

    @Test
    fun getPublications_priceMappedWithDesdePrefix() = runBlocking {
        insertArticle("art-price", "Pintura Interior", 200_000.0)

        val card = repository.getPublications().getOrThrow().first()

        assertTrue(
            "Price text should start with 'Desde \$'",
            card.price.startsWith("Desde \$")
        )
    }

    @Test
    fun getPublicationById_whenDocumentExists_returnsSuccessWithCard() = runBlocking {
        insertArticle("art-detail", "Remodelación de Cocina", 1_200_000.0)

        val result = repository.getPublicationById("art-detail")

        assertTrue(result.isSuccess)
        val card = result.getOrThrow()
        assertEquals("art-detail",           card.id)
        assertEquals("Remodelación de Cocina", card.title)
    }

    @Test
    fun getPublicationById_whenDocumentNotFound_returnsFailure() = runBlocking {
        val result = repository.getPublicationById("does-not-exist")

        assertTrue("Result must be failure for missing document", result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun getFollowingPublications_whenFollowingIdsIsEmpty_returnsSuccessEmptyList() = runBlocking {
        insertArticle("art-following", "Artículo existente", 100_000.0)

        val result = repository.getFollowingPublications(emptyList())

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun getCategories_alwaysReturnsFourHardcodedCategories() = runBlocking {
        val result = repository.getCategories()

        assertTrue(result.isSuccess)
        val categories = result.getOrThrow()
        assertEquals("Should always return 4 categories", 4, categories.size)

        val names = categories.map { it.title }
        assertTrue(names.contains("Baños"))
        assertTrue(names.contains("Iluminación"))
        assertTrue(names.contains("Cocina"))
        assertTrue(names.contains("Exterior"))
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private suspend fun insertArticle(
        id: String,
        title: String,
        price: Double,
        authorId: String = "test-author",
        category: String = "General"
    ) {
        firestore.collection(articlesCollection).document(id).set(
            mapOf(
                "title"       to title,
                "description" to "Descripción de prueba para '$title'",
                "price"       to price,
                "category"    to category,
                "imageUrl"    to "https://picsum.photos/seed/$id/400/300",
                "authorId"    to authorId
            )
        ).await()
    }

    private suspend fun cleanArticles() {
        firestore.collection(articlesCollection).get().await()
            .documents.forEach { it.reference.delete().await() }
    }

    private fun configureEmulatorIfNeeded() {
        if (emulatorConfigured) return
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        firestore.firestoreSettings = settings
        firestore.useEmulator(AppConstants.EMULATOR_HOST, AppConstants.FIRESTORE_PORT)
        auth.useEmulator(AppConstants.EMULATOR_HOST, AppConstants.AUTH_PORT)
        emulatorConfigured = true
    }

    companion object {
        private var emulatorConfigured = false
    }
}
