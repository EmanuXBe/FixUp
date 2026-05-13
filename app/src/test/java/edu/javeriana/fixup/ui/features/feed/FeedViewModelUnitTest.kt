package edu.javeriana.fixup.ui.features.feed

import edu.javeriana.fixup.data.repository.FeedRepository
import edu.javeriana.fixup.data.util.DataSeeder
import edu.javeriana.fixup.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for FeedViewModel using MockK mocks and UnconfinedTestDispatcher.
 *
 * Each test exercises a distinct ViewModel behavior or state transition, ensuring
 * the ViewModel reacts correctly to repository results without touching Firebase.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelUnitTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FeedRepository
    private lateinit var dataSeeder: DataSeeder

    // ─── Setup ────────────────────────────────────────────────────────────────

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        dataSeeder = mockk(relaxed = true)
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test
    fun initialState_isLoadingFalse_publicationsEmpty() {
        runTest {
            coEvery { repository.getPublications() } returns Result.success(emptyList())
            coEvery { repository.getCategories() } returns Result.success(emptyList())

            val vm = FeedViewModel(repository, dataSeeder)

            // With UnconfinedTestDispatcher the init block runs eagerly before this assertion
            assertFalse(vm.uiState.value.isLoading)
        }
    }

    @Test
    fun init_whenRepositorySucceeds_publicationsAreStoredInState() {
        runTest {
            val publications = listOf(
                PublicationCardModel("p1", "url1", "Título 1", "Desde $100000"),
                PublicationCardModel("p2", "url2", "Título 2", "Desde $200000")
            )
            coEvery { repository.getPublications() } returns Result.success(publications)
            coEvery { repository.getCategories() } returns Result.success(emptyList())

            val vm = FeedViewModel(repository, dataSeeder)
            advanceUntilIdle()

            assertEquals(2, vm.uiState.value.publications.size)
            assertEquals("p1", vm.uiState.value.publications[0].id)
            assertEquals("p2", vm.uiState.value.publications[1].id)
        }
    }

    @Test
    fun init_whenRepositorySucceeds_categoriesAreStoredInState() {
        runTest {
            val categories = listOf(
                CategoryItemModel(0, "Baños"),
                CategoryItemModel(0, "Cocina")
            )
            coEvery { repository.getCategories() } returns Result.success(categories)
            coEvery { repository.getPublications() } returns Result.success(emptyList())

            val vm = FeedViewModel(repository, dataSeeder)
            advanceUntilIdle()

            assertEquals(2, vm.uiState.value.categories.size)
            assertTrue(vm.uiState.value.categories.map { it.title }.contains("Baños"))
        }
    }

    @Test
    fun init_whenPublicationsRepositoryFails_isConnectedSetToFalse() {
        runTest {
            coEvery { repository.getCategories() } returns Result.success(emptyList())
            coEvery { repository.getPublications() } returns Result.failure(Exception("Firestore error"))

            val vm = FeedViewModel(repository, dataSeeder)
            advanceUntilIdle()

            assertFalse(
                "isConnected must be false when publications fetch fails",
                vm.uiState.value.isConnected
            )
        }
    }

    @Test
    fun init_whenCategoriesSucceed_isConnectedSetToTrue() {
        runTest {
            coEvery { repository.getCategories() } returns Result.success(emptyList())
            coEvery { repository.getPublications() } returns Result.success(emptyList())

            val vm = FeedViewModel(repository, dataSeeder)
            advanceUntilIdle()

            assertTrue(vm.uiState.value.isConnected)
        }
    }

    @Test
    fun onSearchQueryChanged_updatesSearchQueryInState() {
        runTest {
            coEvery { repository.getPublications() } returns Result.success(emptyList())
            coEvery { repository.getCategories() } returns Result.success(emptyList())

            val vm = FeedViewModel(repository, dataSeeder)
            vm.onSearchQueryChanged("cocina")

            assertEquals("cocina", vm.uiState.value.searchQuery)
        }
    }

    @Test
    fun onSearchQueryChanged_doesNotModifyPublicationsList() {
        runTest {
            val publications = listOf(
                PublicationCardModel("p1", "url1", "Cocina Moderna", "Desde $300000"),
                PublicationCardModel("p2", "url2", "Baño Remodelado", "Desde $400000")
            )
            coEvery { repository.getPublications() } returns Result.success(publications)
            coEvery { repository.getCategories() } returns Result.success(emptyList())

            val vm = FeedViewModel(repository, dataSeeder)
            advanceUntilIdle()
            vm.onSearchQueryChanged("baño")

            // The ViewModel does not filter — all publications remain in state
            assertEquals(2, vm.uiState.value.publications.size)
        }
    }

    @Test
    fun seedData_whenSeederFails_isLoadingReturnsToFalse() {
        runTest {
            coEvery { repository.getPublications() } returns Result.success(emptyList())
            coEvery { repository.getCategories() } returns Result.success(emptyList())
            coEvery { dataSeeder.seed(any()) } returns false

            val vm = FeedViewModel(repository, dataSeeder)
            vm.seedData()
            advanceUntilIdle()

            assertFalse(
                "isLoading must be false even when seeder returns false",
                vm.uiState.value.isLoading
            )
        }
    }
}
