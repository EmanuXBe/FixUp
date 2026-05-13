package edu.javeriana.fixup.data.datasource

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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for FeedFirestoreDataSourceImpl using the Firebase local emulator.
 *
 * Prerequisites: Firebase Emulator Suite running on localhost:
 *   - Firestore → port 8080
 *   - Auth     → port 9099
 *
 * Start emulators: firebase emulators:start
 * Run tests from Android emulator where 10.0.2.2 maps to host localhost.
 */
@RunWith(AndroidJUnit4::class)
class FeedDataSourceIntegrationTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var dataSource: FeedFirestoreDataSourceImpl

    private val articlesCollection = "articles"

    // ─── Setup / Teardown ────────────────────────────────────────────────────

    @Before
    fun setUp() = runBlocking {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        configureEmulatorIfNeeded()

        auth.signOut()
        cleanArticles()

        dataSource = FeedFirestoreDataSourceImpl(
            firestore = firestore,
            storage = FirebaseStorage.getInstance(),
            auth = auth
        )
    }

    @After
    fun tearDown() = runBlocking {
        cleanArticles()
        auth.signOut()
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test
    fun getPublications_whenCollectionIsEmpty_returnsEmptyList() = runBlocking {
        val result = dataSource.getPublications()

        assertTrue("Expected empty list", result.isEmpty())
    }

    @Test
    fun getPublications_whenDocumentsHaveTitleField_returnsAllDocuments() = runBlocking {
        insertArticle("art-1", title = "Remodelación de Cocina", price = 500_000.0)
        insertArticle("art-2", title = "Pintura Exterior",      price = 250_000.0)
        insertArticle("art-3", title = "Instalación de Pisos",  price = 800_000.0)

        val result = dataSource.getPublications()

        assertEquals("Should return all 3 articles", 3, result.size)
        val titles = result.map { it.title }
        assertTrue(titles.contains("Remodelación de Cocina"))
        assertTrue(titles.contains("Pintura Exterior"))
        assertTrue(titles.contains("Instalación de Pisos"))
    }

    @Test
    fun getPublications_whenDocumentHasTituloInsteadOfTitle_mapsTituloCorrectly() = runBlocking {
        // Documents with "titulo" (Spanish field name) instead of "title"
        insertArticleWithCustomFields(
            id = "art-titulo",
            data = mapOf(
                "titulo"      to "Servicio con titulo español",
                "description" to "Descripción",
                "price"       to 100_000.0,
                "category"    to "Baños",
                "authorId"    to "user-a"
            )
        )

        val result = dataSource.getPublications()

        assertEquals(1, result.size)
        assertEquals("Servicio con titulo español", result.first().title)
    }

    @Test
    fun getPublications_whenDocumentHasPriceStoredAsLong_convertsToDouble() = runBlocking {
        // Firestore may store numeric fields as Long when set via Kotlin Int
        insertArticleWithCustomFields(
            id = "art-long-price",
            data = mapOf(
                "title"    to "Artículo precio entero",
                "price"    to 350_000L,          // Long, not Double
                "category" to "Cocina",
                "authorId" to "user-b"
            )
        )

        val result = dataSource.getPublications()

        assertEquals(1, result.size)
        // priceText should include the numeric value without crashing
        assertTrue(result.first().priceText.contains("350000"))
    }

    @Test
    fun getPublications_whenDocumentHasNoTitle_usesDefaultSinTitulo() = runBlocking {
        // Document missing all title-like fields
        insertArticleWithCustomFields(
            id = "art-no-title",
            data = mapOf(
                "description" to "Sin título ni name ni titulo",
                "price"       to 0.0,
                "category"    to "Exterior",
                "authorId"    to "user-c"
            )
        )

        val result = dataSource.getPublications()

        assertEquals(1, result.size)
        assertEquals("Sin título", result.first().title)
    }

    @Test
    fun getPublicationById_whenDocumentExists_returnsMappedDto() = runBlocking {
        insertArticle("art-by-id", title = "Detalle de Artículo", price = 700_000.0)

        val dto = dataSource.getPublicationById("art-by-id")

        assertNotNull(dto)
        assertEquals("art-by-id", dto.id)
        assertEquals("Detalle de Artículo", dto.title)
        assertTrue(dto.priceText.contains("700000"))
    }

    @Test
    fun getPublicationById_whenDocumentDoesNotExist_throwsException() = runBlocking {
        var caughtException: Exception? = null

        try {
            dataSource.getPublicationById("nonexistent-id")
        } catch (e: Exception) {
            caughtException = e
        }

        assertNotNull("Expected an exception for a missing document", caughtException)
        assertTrue(caughtException!!.message?.contains("no encontrado") == true)
    }

    @Test
    fun getFollowingPublications_whenFollowingIdsIsEmpty_returnsEmptyList() = runBlocking {
        insertArticle("art-follow-1", title = "Artículo de seguido", price = 100_000.0)

        val result = dataSource.getFollowingPublications(emptyList())

        assertTrue("getFollowingPublications with empty ids must return empty", result.isEmpty())
    }

    @Test
    fun getFollowingPublications_whenFollowingIdsProvided_returnsOnlyMatchingArticles() = runBlocking {
        insertArticle("art-match-1", title = "Artículo del autor seguido",  price = 100_000.0, authorId = "followed-user")
        insertArticle("art-match-2", title = "Artículo de otro autor",      price = 200_000.0, authorId = "other-user")

        val result = dataSource.getFollowingPublications(listOf("followed-user"))

        assertEquals(1, result.size)
        assertEquals("Artículo del autor seguido", result.first().title)
        assertEquals("followed-user", result.first().authorId)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private suspend fun insertArticle(
        id: String,
        title: String,
        price: Double,
        authorId: String = "test-author",
        category: String = "Cocina"
    ) {
        firestore.collection(articlesCollection).document(id).set(
            mapOf(
                "title"       to title,
                "description" to "Descripción de prueba",
                "price"       to price,
                "category"    to category,
                "imageUrl"    to "https://picsum.photos/400/300",
                "authorId"    to authorId
            )
        ).await()
    }

    private suspend fun insertArticleWithCustomFields(id: String, data: Map<String, Any>) {
        firestore.collection(articlesCollection).document(id).set(data).await()
    }

    private suspend fun cleanArticles() {
        val docs = firestore.collection(articlesCollection).get().await()
        docs.documents.forEach { it.reference.delete().await() }
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
