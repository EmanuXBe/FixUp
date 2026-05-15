package edu.javeriana.fixup.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.javeriana.fixup.MainActivity
import edu.javeriana.fixup.data.util.AppConstants
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 *  REQUISITO IMPLEMENTADO: Casos de uso E2E
 *
 * Flujo E2E 1 (e2eFlow1): Usuario nuevo se registra con contraseña inválida (1234) →
 *   verifica mensaje de error → corrige a 123456 → ingresa a la app → abre primera
 *   publicación → verifica información del detalle → da like a primer comentario →
 *   verifica aumento de likes → vuelve → reabre publicación → quita like → verifica
 *   disminución de likes.
 *
 * Flujo E2E 2 (e2eFlow2): Usuario registrado hace login → va al perfil del autor de
 *   una publicación → verifica información del usuario → da follow → verifica aumento
 *   de seguidores → vuelve al home → va a sección "Siguiendo" → verifica que aparece
 *   al menos una publicación del usuario seguido.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class E2EFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private val newUserEmail    = "e2e_new_${System.currentTimeMillis()}@fixup.com"
    private val newUserPassword = "123456"
    private val newUserCedula   = "12345678"

    private val existingUserEmail    = "e2e_existing@fixup.com"
    private val existingUserPassword = "123456"

    private val targetUserEmail    = "e2e_target@fixup.com"
    private val targetUserPassword = "123456"

    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setUp() {
        println("E2E: Starting setUp")
        hiltRule.inject()
        println("E2E: Hilt injected")

        dismissSystemDialogs()

        clearAuthEmulator()
        println("E2E: Auth emulator cleared")

        runBlocking {
            println("E2E: Starting runBlocking cleanup")
            auth.signOut()
            cleanTestData()
            println("E2E: Cleanup finished, creating test data")

            // Target user and publication
            val targetResult = auth.createUserWithEmailAndPassword(targetUserEmail, targetUserPassword).await()
            val targetUid = targetResult.user!!.uid
            println("E2E: Target user created: $targetUid")

            firestore.collection("users").document(targetUid).set(
                mapOf(
                    "name"  to "Target E2E User",
                    "email" to targetUserEmail,
                    "role"  to "Fixer"
                )
            ).await()

            firestore.collection("articles").document("e2e-target-article").set(
                mapOf(
                    "title"       to "Artículo del usuario seguido",
                    "description" to "Publicación de prueba E2E para following feed",
                    "price"       to 500_000.0,
                    "category"    to "Cocina",
                    "imageUrl"    to "https://picsum.photos/seed/e2e/400/300",
                    "authorId"    to targetUid
                )
            ).await()

            // Pre-create review BEFORE signing out (avoid breaking the Firestore GRPC stream)
            firestore.collection("reviews").document("e2e-review-1").set(
                mapOf(
                    "userId"       to targetUid,
                    "serviceId"    to "e2e-target-article",
                    "rating"       to 5,
                    "comment"      to "Excelente servicio de prueba E2E",
                    "authorName"   to "Target E2E User",
                    "serviceTitle" to "Artículo del usuario seguido"
                )
            ).await()
            println("E2E: Review created")

            // Pre-create existing user, then sign out last
            auth.createUserWithEmailAndPassword(existingUserEmail, existingUserPassword).await()
            println("E2E: Existing user created")
            auth.signOut()
        }

        println("E2E: Launching MainActivity")
        scenario = ActivityScenario.launch(MainActivity::class.java)
        println("E2E: MainActivity launched")
    }

    @After
    fun tearDown() {
        println("E2E: Starting tearDown")
        runBlocking {
            withTimeoutOrNull(15_000) {
                try {
                    cleanTestData()
                } catch (e: Exception) {
                    println("E2E: Error in tearDown cleanup: ${e.message}")
                }
            }
            auth.signOut()
        }
        scenario?.close()
        println("E2E: tearDown finished")
    }

    // ✅ REQUISITO E2E 1: Registro con contraseña inválida → corrección → like/unlike en review
    @Test
    fun e2eFlow1_register_invalidPassword_showsError_thenCorrect_likeAndUnlike() {
        println("E2E: Starting test 1")
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText("Iniciar sesión", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        println("E2E: On Login screen")
        
        composeRule.onNodeWithText("Registrarse", ignoreCase = true).performClick()
        composeRule.waitForIdle()
        println("E2E: On Register screen")

        composeRule.onNodeWithTag("email_field").performTextInput(newUserEmail)
        composeRule.onNodeWithTag("cedula_field").performTextInput(newUserCedula)
        composeRule.onNodeWithTag("password_field").performTextInput("1234")
        composeRule.onNodeWithText("Fixer", ignoreCase = true).performClick()
        composeRule.onNodeWithTag("register_button").performClick()
        composeRule.waitForIdle()

        // ✅ REQUISITO: "la primera vez escribe como contraseña 1234, por lo cual no se puede
        //   registrar. Verificar el mensaje de error."
        composeRule.onNodeWithText("La contraseña debe tener al menos 6 caracteres").assertIsDisplayed()
        println("E2E: Error message verified")

        composeRule.onNodeWithTag("password_field").performTextClearance()
        composeRule.onNodeWithTag("password_field").performTextInput(newUserPassword)
        composeRule.onNodeWithTag("register_button").performClick()
        composeRule.waitForIdle()

        println("E2E: Registration submitted, waiting for feed")
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodes(hasTestTag("publication_card")).fetchSemanticsNodes().isNotEmpty()
        }
        println("E2E: Feed loaded")

        // Scroll the grid to the specific E2E article (LazyVerticalGrid virtualizes off-screen items)
        composeRule.onNodeWithTag("feed_screen")
            .performScrollToNode(hasText("Artículo del usuario seguido", ignoreCase = true))
        composeRule.onNodeWithText("Artículo del usuario seguido", ignoreCase = true)
            .performClick()
        composeRule.waitForIdle()

        // ✅ REQUISITO: "Verificar que la información de detalle sea correcta"
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Artículo del usuario seguido", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Artículo del usuario seguido", ignoreCase = true).assertIsDisplayed()
        println("E2E: Información de detalle verificada correctamente")

        // Wait for the async getLikedUsers chain to complete (initial like_count = "0")
        // before clicking like, so the optimistic update won't be overwritten by the
        // snapshot's async callback completing afterwards.
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("like_count").and(hasText("0"))).fetchSemanticsNodes().isNotEmpty()
        }

        // Like
        println("E2E: Liking publication")
        composeRule.onNodeWithContentDescription("Me gusta", useUnmergedTree = true)
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        // ✅ REQUISITO: "Verificar que aumente la cantidad de likes"
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("like_count").and(hasText("1"))).fetchSemanticsNodes().isNotEmpty()
        }

        // ✅ REQUISITO: "El usuario va atrás, y vuelve a seleccionar la publicación"
        composeRule.onNodeWithContentDescription("Volver", useUnmergedTree = true).performClick()
        composeRule.waitForIdle()
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("publication_card")).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("feed_screen")
            .performScrollToNode(hasText("Artículo del usuario seguido", ignoreCase = true))
        composeRule.onNodeWithText("Artículo del usuario seguido", ignoreCase = true).performClick()
        composeRule.waitForIdle()

        // Wait for the snapshot to reload with the persisted like (getLikedUsers returns [userId])
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodes(hasTestTag("like_count").and(hasText("1"))).fetchSemanticsNodes().isNotEmpty()
        }

        // ✅ REQUISITO: "ahora quita el like"
        println("E2E: Unliking publication")
        composeRule.onNodeWithContentDescription("Quitar me gusta", useUnmergedTree = true)
            .performScrollTo()
            .performClick()
        composeRule.waitForIdle()

        // ✅ REQUISITO: "se verifica que la cantidad de likes disminuya"
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("like_count").and(hasText("0"))).fetchSemanticsNodes().isNotEmpty()
        }
        println("E2E: Test 1 finished successfully")
    }

    // ✅ REQUISITO E2E 2: Login → perfil de usuario → verificar info → follow → verificar
    //   seguidores → following feed muestra publicación del usuario seguido
    @Test
    fun e2eFlow2_login_followUser_verifyFollowingFeedShowsPublication() {
        println("E2E: Starting test 2")
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText("Iniciar sesión", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("email_field").performTextInput(existingUserEmail)
        composeRule.onNodeWithTag("password_field").performTextInput(existingUserPassword)
        composeRule.onNodeWithText("Continuar", ignoreCase = true).performClick()
        composeRule.waitForIdle()

        println("E2E: Login submitted, waiting for feed")
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("publication_card")).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("feed_screen")
            .performScrollToNode(hasText("Artículo del usuario seguido", ignoreCase = true))
        composeRule.onNodeWithText("Artículo del usuario seguido", ignoreCase = true)
            .performClick()
        composeRule.waitForIdle()

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("author_profile_button")).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("author_profile_button").performScrollTo().performClick()
        composeRule.waitForIdle()

        // ✅ REQUISITO: "Verificar que la información del usuario sea correcta"
        // Esperar a que el nombre del usuario aparezca en la pantalla de perfil
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("profile_header_name")).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("profile_header_name").assertTextEquals("Target E2E User")
        println("E2E: Información del perfil de usuario verificada correctamente")

        // Wait for profile state to fully stabilize — including the async getReviewsByUserId
        // snapshot initial emission that sets _uiState.user = originalUser. If we click
        // "Seguir" before this completes, the snapshot emission will revert the optimistic update.
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("followers_count").and(hasText("0"))).fetchSemanticsNodes().isNotEmpty()
        }

        // Follow
        println("E2E: Following user")
        composeRule.onNodeWithText("Seguir", ignoreCase = true).performClick()

        // ✅ REQUISITO: "Le da follow y se verifica que aumenta la cantidad de seguidores"
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText("Dejar de seguir", ignoreCase = true).fetchSemanticsNodes().isNotEmpty() &&
            composeRule.onAllNodes(hasTestTag("followers_count").and(hasText("1"))).fetchSemanticsNodes().isNotEmpty()
        }

        // ✅ REQUISITO: "El usuario vuelve al home"
        composeRule.onNodeWithContentDescription("Volver", useUnmergedTree = true).performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("Volver", useUnmergedTree = true).performClick()
        composeRule.waitForIdle()

        // ✅ REQUISITO: "va a la sección de publicaciones de seguidos"
        println("E2E: Checking following feed")
        composeRule.onNodeWithText("Siguiendo", ignoreCase = true).performClick()
        composeRule.waitForIdle()

        // ✅ REQUISITO: "se verifica que aparezca al menos una publicación del usuario que
        //   acabó de seguir"
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodes(hasTestTag("publication_card")).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Artículo del usuario seguido", ignoreCase = true).assertIsDisplayed()
        println("E2E: Test 2 finished successfully")
    }

    private fun dismissSystemDialogs() {
        try {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            val selectors = listOf("OK", "Got it", "Continue", "Close", "Dismiss")
            for (text in selectors) {
                val btn = device.findObject(UiSelector().textMatches("(?i)$text"))
                if (btn.exists()) {
                    btn.click()
                    break
                }
            }
        } catch (e: Exception) {
            println("E2E: Could not dismiss system dialog: ${e.message}")
        }
    }

    private fun clearAuthEmulator() {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder()
                .url("http://${AppConstants.EMULATOR_HOST}:9099/emulator/v1/projects/fixup-f2128/accounts")
                .addHeader("Authorization", "Bearer owner")
                .delete()
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Warning: Failed to clear Auth emulator: ${response.code}")
                }
            }
        } catch (e: Exception) {
            println("Warning: Error connecting to Auth emulator for cleanup: ${e.message}")
        }
    }

    private suspend fun cleanTestData() {
        // Delete specific test artifacts
        listOf("e2e-target-article").forEach { id ->
            firestore.collection("articles").document(id).delete().await()
        }
        listOf("e2e-review-1").forEach { id ->
            firestore.collection("reviews").document(id).delete().await()
        }
        
        // Clean up only the specific test users we know about
        val testEmails = listOf(newUserEmail, existingUserEmail, targetUserEmail)
        val users = firestore.collection("users")
            .whereIn("email", testEmails)
            .get().await()
            
        users.documents.forEach { userDoc ->
            val uid = userDoc.id
            // Delete sub-collections (simplified for test cleanup speed)
            // Ideally use a more robust recursive delete, but for E2E this is usually enough
            val followers = userDoc.reference.collection("followers").get().await()
            followers.documents.forEach { it.reference.delete().await() }
            
            val following = userDoc.reference.collection("following").get().await()
            following.documents.forEach { it.reference.delete().await() }
            
            userDoc.reference.delete().await()
            println("E2E: Deleted user $uid from Firestore")
        }
    }


    companion object
}
