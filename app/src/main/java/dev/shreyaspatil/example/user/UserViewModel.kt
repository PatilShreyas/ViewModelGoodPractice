package dev.shreyaspatil.example.user

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shreyaspatil.example.data.model.User
import dev.shreyaspatil.example.data.repo.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel inheriting [AndroidViewModel] for accessing framework level dependencies.
 */
@HiltViewModel
class UserViewModel @Inject constructor(
	application: Application,
	private val userRepository: UserRepository
) : AndroidViewModel(application) {

	/**
	 * A [SharedPreferences] for storing user preferences.
	 */
	private val userPreferences = application.getSharedPreferences(
		UserPreferences.NAME,
		Context.MODE_PRIVATE
	)

	/**
	 * A cancellable [Job] for setting session.
	 * Useful for cancellation of job after calling [setUserSession].
	 */
	private var job: Job? = null

	private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)

	/**
	 * State holder of a [SessionState]
	 */
	val sessionState: StateFlow<SessionState> = _sessionState

	init {
		viewModelScope.launch { updateSessionState() }
	}

	/**
	 * Creates a new user with [name] and [email] and sets active session of that created user.
	 */
	fun setUserSession(name: String, email: String) {
		_sessionState.update { SessionState.Loading }

		// Cancel existing ongoing job (if exists).
		job?.cancel()
		job = viewModelScope.launch {
			val user = userRepository.add(name, email)
			saveUserSession(user)
			updateSessionState()
		}
	}

	/**
	 * Clears the active session
	 */
	fun logoutSession() {
		job?.cancel()
		job = viewModelScope.launch {
			clearUserSession()
			_sessionState.update { SessionState.UserUnavailable }
		}

	}


	private suspend fun updateSessionState() {
		// Set current state as loading
		_sessionState.update { SessionState.Loading }

		// Try retrieving current user and create session accordingly
		val user = getCurrentUserSession()

		val sessionState = if (user != null) {
			SessionState.UserAvailable(user)
		} else {
			SessionState.UserUnavailable
		}

		// Update session
		_sessionState.update { sessionState }
	}

	private suspend fun saveUserSession(user: User) = withContext(Dispatchers.IO) {
		userPreferences.edit {
			putInt(UserPreferences.Keys.ID, user.id)
			putString(UserPreferences.Keys.NAME, user.name)
			putString(UserPreferences.Keys.EMAIL, user.email)
		}
	}

	private suspend fun clearUserSession() = withContext(Dispatchers.IO) {
		userPreferences.edit { clear() }
	}

	private suspend fun getCurrentUserSession(): User? = withContext(Dispatchers.IO) {
		val id = userPreferences.getInt(UserPreferences.Keys.ID, -1)
		val name = userPreferences.getString(UserPreferences.Keys.NAME, null)
		val email = userPreferences.getString(UserPreferences.Keys.EMAIL, null)

		if (id != -1 && name != null && email != null) {
			User(id, name, email)
		} else {
			null
		}
	}

	/**
	 * Object holding user preference key details
	 */
	private object UserPreferences {
		const val NAME = "user_pref"

		object Keys {
			const val ID = "user_id"
			const val NAME = "user_name"
			const val EMAIL = "user_email"
		}
	}
}