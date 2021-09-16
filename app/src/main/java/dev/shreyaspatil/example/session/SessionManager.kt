package dev.shreyaspatil.example.session

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.shreyaspatil.example.data.model.User
import dev.shreyaspatil.example.di.UserPreferences
import dev.shreyaspatil.example.session.DefaultSessionManager.UserPreferencesDetails.Keys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface SessionManager {
	suspend fun getCurrentUser(): User?
	suspend fun setUserSession(user: User)
	suspend fun clear()
}

@Singleton
class DefaultSessionManager @Inject constructor(
	@UserPreferences private val userPreferences: SharedPreferences
) : SessionManager {

	override suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
		val id = userPreferences.getInt(Keys.ID, -1)
		val name = userPreferences.getString(Keys.NAME, null)
		val email = userPreferences.getString(Keys.EMAIL, null)

		if (id != -1 && name != null && email != null) {
			User(id, name, email)
		} else {
			null
		}
	}

	override suspend fun setUserSession(user: User) = withContext(Dispatchers.IO) {
		userPreferences.edit {
			putInt(Keys.ID, user.id)
			putString(Keys.NAME, user.name)
			putString(Keys.EMAIL, user.email)
		}
	}


	override suspend fun clear() = withContext(Dispatchers.IO) {
		userPreferences.edit { clear() }
	}

	/**
	 * Object holding user preference key details
	 */
	object UserPreferencesDetails {
		const val NAME = "user_pref"

		object Keys {
			const val ID = "user_id"
			const val NAME = "user_name"
			const val EMAIL = "user_email"
		}
	}
}