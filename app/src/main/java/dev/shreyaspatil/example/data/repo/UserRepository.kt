package dev.shreyaspatil.example.data.repo

import dev.shreyaspatil.example.data.model.User
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
interface UserRepository {
	suspend fun findById(id: Int): User
	suspend fun add(name: String, email: String): User
}

@Singleton
class InMemoryUserRepository @Inject constructor() : UserRepository {
	private val map = mutableMapOf<Int, User>()

	override suspend fun findById(id: Int): User {
		// Add sample delay to demonstrate processing
		delay(1000)

		return map[id] ?: error("User not exists with ID: $id")
	}

	override suspend fun add(name: String, email: String): User {
		// Add sample delay to demonstrate processing
		delay(1000)

		val id = Random.nextInt()
		val user = User(id, name, email)

		map[id] = user

		return user
	}

}