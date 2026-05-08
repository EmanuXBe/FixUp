package edu.javeriana.fixup.data.util

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import net.datafaker.Faker
import java.util.UUID
import javax.inject.Inject

class FirebaseSeeder @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val faker = Faker()

    suspend fun seedData(currentUserId: String? = null): Result<Unit> {
        return try {
            seedUsers(5)
            seedArticles(10)
            seedReviews(15, currentUserId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedUsers(count: Int) {
        val usersCollection = firestore.collection("users")
        repeat(count) {
            val userId = UUID.randomUUID().toString()
            val userMap = hashMapOf(
                "name" to faker.name().fullName(),
                "email" to faker.internet().emailAddress(),
                "phone" to faker.phoneNumber().phoneNumber(),
                "address" to faker.address().fullAddress(),
                "role" to "Cliente",
                "profileImageUrl" to "https://picsum.photos/200/200?random=${faker.number().numberBetween(1, 1000)}"
            )
            usersCollection.document(userId).set(userMap).await()
        }
    }

    private suspend fun seedArticles(count: Int) {
        val articlesCollection = firestore.collection("articles")
        repeat(count) {
            val articleMap = hashMapOf(
                "title" to faker.commerce().productName(),
                "description" to faker.lorem().paragraph(),
                "price" to faker.number().randomDouble(2, 50, 5000),
                "imageUrl" to "https://picsum.photos/400/300?random=${faker.number().numberBetween(1, 1000)}",
                "category" to faker.commerce().department(),
                "authorId" to UUID.randomUUID().toString()
            )
            articlesCollection.add(articleMap).await()
        }
    }

    private suspend fun seedReviews(count: Int, currentUserId: String?) {
        val reviewsCollection = firestore.collection("reviews")
        repeat(count) {
            val isForCurrentUser = currentUserId != null && it < 5 // Seed 5 reviews for current user
            val userId = if (isForCurrentUser) currentUserId else UUID.randomUUID().toString()
            val authorName = if (isForCurrentUser) "Mi Usuario Debug" else faker.name().fullName()
            
            val reviewMap = hashMapOf(
                "userId" to userId,
                "serviceId" to UUID.randomUUID().toString(),
                "rating" to faker.number().numberBetween(1, 6),
                "comment" to faker.lorem().sentence(),
                "authorName" to authorName,
                "authorProfileImageUrl" to "https://picsum.photos/100/100?random=${faker.number().numberBetween(1, 1000)}",
                "serviceTitle" to faker.commerce().productName(),
                "date" to "2023-10-27"
            )
            reviewsCollection.add(reviewMap).await()
        }
    }
}
