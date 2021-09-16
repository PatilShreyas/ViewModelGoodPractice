package dev.shreyaspatil.example.user

import dev.shreyaspatil.example.data.model.User
import dev.shreyaspatil.example.data.repo.UserRepository
import dev.shreyaspatil.example.session.SessionManager
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
class UserViewModelTest : BehaviorSpec({
	val testDispatcher = TestCoroutineDispatcher()

	val sessionManager: SessionManager = mockk(relaxUnitFun = true)
	val userRepository: UserRepository = mockk()
	val viewModel = UserViewModel(sessionManager, userRepository, testDispatcher)

	Given("A name and email") {
		val name = "John Doe"
		val email = "john@example.com"

		val expectedUser = User(1, name, email)

		coEvery { userRepository.add(name, email) } returns expectedUser
		coEvery { sessionManager.getCurrentUser() } returns expectedUser

		When("A user session is set (logged in)") {
			viewModel.setUserSession(name, email)

			Then("User should be get created") {
				coVerify { userRepository.add(name, email) }
			}

			Then("Session should be get saved") {
				coVerify { sessionManager.setUserSession(expectedUser) }
			}

			Then("State should be get updated") {
				viewModel.sessionState.value shouldBe SessionState.UserAvailable(expectedUser)
			}
		}
	}

	Given("A logged in user") {
		val user = User(1, "John doe", "john@example.com")

		coEvery { sessionManager.getCurrentUser() } returns user

		When("A user session is logged out") {
			viewModel.logoutSession()

			Then("Session should be get cleared") {
				coVerify { sessionManager.clear() }
			}

			Then("State should be get updated") {
				viewModel.sessionState.value shouldBe SessionState.UserUnavailable
			}
		}
	}

	afterTest {
		testDispatcher.cleanupTestCoroutines()
	}
})