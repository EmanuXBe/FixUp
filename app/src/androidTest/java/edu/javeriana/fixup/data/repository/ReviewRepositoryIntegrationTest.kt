package edu.javeriana.fixup.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import edu.javeriana.fixup.data.datasource.impl.AuthDataSourceImpl
import edu.javeriana.fixup.data.datasource.impl.ReviewFirebaseDataSourceImpl
import edu.javeriana.fixup.data.fake.FakeFixUpApiService
import edu.javeriana.fixup.data.util.AppConstants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.firebase.auth.FirebaseAuth

@RunWith(AndroidJUnit4::class)
class ReviewRepositoryIntegrationTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: ReviewRepository

    private val reviewsCollection = "reviews"
    private val usersCollection = "users"

    @Before
    fun setUp() = runBlocking {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        configureFirebaseEmulatorIfNeeded()

        auth.signOut()
        cleanDatabase()

        val authDataSource = AuthDataSourceImpl(auth, firestore)
        val authRepository = AuthRepository(
            dataSource = authDataSource,
            apiService = FakeFixUpApiService()
        )

        val reviewDataSource = ReviewFirebaseDataSourceImpl(firestore)

        repository = ReviewRepository(
            reviewDataSource = reviewDataSource,
            authRepository = authRepository,
            apiService = FakeFixUpApiService()
        )
    }

    @After
    fun tearDown() = runBlocking {
        cleanDatabase()
        auth.signOut()
    }

    @Test
    fun createReview_whenUserIsAuthenticated_createsReviewSuccessfully() = runBlocking {
        val user = createAuthenticatedUser()

        val result = repository.createReview(
            serviceId = "service-001",
            serviceTitle = "Servicio de prueba",
            rating = 5,
            comment = "Excelente servicio"
        )

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())

        val snapshot = firestore.collection(reviewsCollection)
            .whereEqualTo("userId", user.uid)
            .whereEqualTo("serviceId", "service-001")
            .get()
            .await()

        assertEquals(1, snapshot.documents.size)
        assertEquals("Excelente servicio", snapshot.documents.first().getString("comment"))
        assertEquals(5, snapshot.documents.first().getLong("rating")?.toInt())
    }

    @Test
    fun createReview_whenUserIsNotAuthenticated_returnsFailure() = runBlocking {
        auth.signOut()

        val result = repository.createReview(
            serviceId = "service-002",
            serviceTitle = "Servicio sin usuario",
            rating = 4,
            comment = "No debería crearse"
        )

        assertTrue(result.isFailure)
        assertEquals("Usuario no autenticado", result.exceptionOrNull()?.message)
    }

    @Test
    fun getReviewsByUserId_returnsOnlyReviewsFromSelectedUser() = runBlocking {
        insertReview(
            reviewId = "review-user-1",
            userId = "user-a",
            serviceId = "service-001",
            rating = 5,
            comment = "Review del usuario A"
        )

        insertReview(
            reviewId = "review-user-2",
            userId = "user-b",
            serviceId = "service-001",
            rating = 3,
            comment = "Review del usuario B"
        )

        val result = withTimeout(5000) {
            repository.getReviewsByUserId("user-a").first()
        }

        assertTrue(result.isSuccess)

        val reviews = result.getOrThrow()
        assertEquals(1, reviews.size)
        assertEquals("user-a", reviews.first().userId)
        assertEquals("Review del usuario A", reviews.first().comment)
    }

    @Test
    fun getReviewsByServiceId_returnsOnlyReviewsFromSelectedService() = runBlocking {
        insertReview(
            reviewId = "review-service-1",
            userId = "user-a",
            serviceId = "service-a",
            rating = 5,
            comment = "Review del servicio A"
        )

        insertReview(
            reviewId = "review-service-2",
            userId = "user-a",
            serviceId = "service-b",
            rating = 2,
            comment = "Review del servicio B"
        )

        val result = withTimeout(5000) {
            repository.getReviewsByServiceId("service-a").first()
        }

        assertTrue(result.isSuccess)

        val reviews = result.getOrThrow()
        assertEquals(1, reviews.size)
        assertEquals("service-a", reviews.first().serviceId)
        assertEquals("Review del servicio A", reviews.first().comment)
    }

    @Test
    fun toggleLike_whenReviewIsNotLiked_addsLike() = runBlocking {
        val user = createAuthenticatedUser()

        insertReview(
            reviewId = "review-like-1",
            userId = "review-owner",
            serviceId = "service-like",
            rating = 5,
            comment = "Review para like"
        )

        val result = repository.toggleLike(
            reviewId = "review-like-1",
            isCurrentlyLiked = false
        )

        assertTrue(result.isSuccess)

        val likeDocument = firestore.collection(reviewsCollection)
            .document("review-like-1")
            .collection("likes")
            .document(user.uid)
            .get()
            .await()

        assertTrue(likeDocument.exists())
    }

    @Test
    fun toggleLike_whenReviewIsAlreadyLiked_removesLike() = runBlocking {
        val user = createAuthenticatedUser()

        insertReview(
            reviewId = "review-like-2",
            userId = "review-owner",
            serviceId = "service-like",
            rating = 5,
            comment = "Review para quitar like"
        )

        firestore.collection(reviewsCollection)
            .document("review-like-2")
            .collection("likes")
            .document(user.uid)
            .set(mapOf("timestamp" to System.currentTimeMillis()))
            .await()

        val result = repository.toggleLike(
            reviewId = "review-like-2",
            isCurrentlyLiked = true
        )

        assertTrue(result.isSuccess)

        val likeDocument = firestore.collection(reviewsCollection)
            .document("review-like-2")
            .collection("likes")
            .document(user.uid)
            .get()
            .await()

        assertFalse(likeDocument.exists())
    }

    @Test
    fun updateReview_updatesRatingAndComment() = runBlocking {
        insertReview(
            reviewId = "review-update-1",
            userId = "user-update",
            serviceId = "service-update",
            rating = 2,
            comment = "Comentario inicial"
        )

        val result = repository.updateReview(
            reviewId = "review-update-1",
            rating = 4,
            comment = "Comentario actualizado"
        )

        assertTrue(result.isSuccess)

        val document = firestore.collection(reviewsCollection)
            .document("review-update-1")
            .get()
            .await()

        assertEquals(4, document.getLong("rating")?.toInt())
        assertEquals("Comentario actualizado", document.getString("comment"))
    }

    @Test
    fun deleteReview_deletesReviewDocument() = runBlocking {
        insertReview(
            reviewId = "review-delete-1",
            userId = "user-delete",
            serviceId = "service-delete",
            rating = 1,
            comment = "Review para eliminar"
        )

        val result = repository.deleteReview("review-delete-1")

        assertTrue(result.isSuccess)

        val document = firestore.collection(reviewsCollection)
            .document("review-delete-1")
            .get()
            .await()

        assertFalse(document.exists())
    }

    private suspend fun createAuthenticatedUser() =
        auth.createUserWithEmailAndPassword(
            "test${System.currentTimeMillis()}@fixup.com",
            "123456"
        ).await().user.also {
            assertNotNull(it)
        }!!

    private suspend fun insertReview(
        reviewId: String,
        userId: String,
        serviceId: String,
        rating: Int,
        comment: String
    ) {
        val reviewData = mapOf(
            "userId" to userId,
            "serviceId" to serviceId,
            "rating" to rating,
            "comment" to comment,
            "authorName" to "Usuario prueba",
            "authorProfileImageUrl" to "",
            "serviceTitle" to "Servicio prueba"
        )

        firestore.collection(reviewsCollection)
            .document(reviewId)
            .set(reviewData)
            .await()
    }

    private suspend fun cleanDatabase() {
        val reviews = firestore.collection(reviewsCollection).get().await()

        reviews.documents.forEach { review ->
            val likes = review.reference.collection("likes").get().await()

            likes.documents.forEach { like ->
                like.reference.delete().await()
            }

            review.reference.delete().await()
        }

        val users = firestore.collection(usersCollection).get().await()

        users.documents.forEach { user ->
            user.reference.delete().await()
        }
    }

    private fun configureFirebaseEmulatorIfNeeded() {
        if (firebaseEmulatorConfigured) return

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()

        firestore.firestoreSettings = settings

        firestore.useEmulator(
            AppConstants.EMULATOR_HOST,
            AppConstants.FIRESTORE_PORT
        )

        auth.useEmulator(
            AppConstants.EMULATOR_HOST,
            AppConstants.AUTH_PORT
        )

        firebaseEmulatorConfigured = true
    }

    companion object {
        private var firebaseEmulatorConfigured = false
    }
}