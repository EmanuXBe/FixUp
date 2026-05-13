package edu.javeriana.fixup.ui.features.feed

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import edu.javeriana.fixup.data.datasource.impl.FeedFirestoreDataSourceImpl
import edu.javeriana.fixup.data.repository.FeedRepository
import edu.javeriana.fixup.data.util.AppConstants
import edu.javeriana.fixup.data.util.DataSeeder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ✅ REQUISITO IMPLEMENTADO: "Realizar 2 pruebas de integración para un viewmodel"
 *
 * Pruebas de integración del FeedViewModel conectado a los componentes reales
 * del emulador de Firebase.
 *
 * Validan la cadena completa:
 *   Firestore ← FeedDataSource ← FeedRepository ← FeedViewModel
 *
 * Prueba 1: con artículos en Firestore, el estado se puebla correctamente.
 * Prueba 2: con colección vacía, el estado tiene lista vacía e isConnected=true.
 */
@RunWith(AndroidJUnit4::class)
class FeedViewModelIntegrationTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: FeedRepository
    private lateinit var dataSeeder: DataSeeder

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
            storage   = FirebaseStorage.getInstance(),
            auth      = auth
        )
        repository  = FeedRepository(dataSource)
        dataSeeder  = DataSeeder(auth, firestore)
    }

    @After
    fun tearDown() = runBlocking {
        cleanArticles()
        auth.signOut()
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    /**
     * Integración 1/2: Firestore con artículos → publicaciones cargadas en el estado del ViewModel.
     */
    @Test
    fun feedViewModel_whenFirestoreHasArticles_publicationsLoadedIntoState() = runBlocking {
        insertArticle("vm-art-1", "Pintura Interior",  150_000.0)
        insertArticle("vm-art-2", "Pintura Exterior",  250_000.0)
        insertArticle("vm-art-3", "Instalación Pisos", 800_000.0)

        val viewModel = FeedViewModel(repository, dataSeeder)

        // Wait until publications appear in state (max 5 s for emulator round-trip)
        val finalState = withTimeout(5_000) {
            viewModel.uiState.first { it.publications.isNotEmpty() }
        }

        assertEquals("All 3 articles must be in state", 3, finalState.publications.size)
        assertFalse("isLoading must be false after fetch", finalState.isLoading)
        assertTrue("isConnected must be true on success",  finalState.isConnected)

        val titles = finalState.publications.map { it.title }
        assertTrue(titles.contains("Pintura Interior"))
        assertTrue(titles.contains("Instalación Pisos"))
    }

    /**
     * Integración 2/2: Firestore vacío → lista vacía en estado pero isConnected=true (sin fallo).
     */
    @Test
    fun feedViewModel_whenFirestoreIsEmpty_stateHasEmptyPublicationsAndIsConnected() = runBlocking {
        // No articles inserted → collection is empty

        val viewModel = FeedViewModel(repository, dataSeeder)

        // Wait until the publications coroutine finishes (isLoading goes false)
        // Since there are no publications to wait for, we poll briefly.
        withTimeout(5_000) {
            // categories coroutine always succeeds (hardcoded), triggering isConnected = true.
            // publications coroutine sets isLoading = false after fetching empty list.
            viewModel.uiState.first { !it.isLoading && it.isConnected }
        }

        val state = viewModel.uiState.value
        assertTrue("Publications must be empty",   state.publications.isEmpty())
        assertTrue("isConnected must remain true", state.isConnected)
        assertFalse("isLoading must be false",     state.isLoading)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private suspend fun insertArticle(id: String, title: String, price: Double) {
        firestore.collection(articlesCollection).document(id).set(
            mapOf(
                "title"       to title,
                "description" to "Descripción de integración",
                "price"       to price,
                "category"    to "General",
                "imageUrl"    to "https://picsum.photos/400/300",
                "authorId"    to "integration-author"
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
