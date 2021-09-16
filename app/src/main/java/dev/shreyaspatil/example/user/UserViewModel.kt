package dev.shreyaspatil.example.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shreyaspatil.example.data.repo.UserRepository
import dev.shreyaspatil.example.session.SessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UserViewModel inheriting core [ViewModel].
 */
@HiltViewModel
class UserViewModel(
	private val sessionManager: SessionManager,
	private val userRepository: UserRepository,
	private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

	@Inject
	constructor(sessionManager: SessionManager, userRepository: UserRepository) : this(
		sessionManager,
		userRepository,
		Dispatchers.Default
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
		viewModelScope.launch(defaultDispatcher) { updateSessionState() }
	}

	/**
	 * Creates a new user with [name] and [email] and sets active session of that created user.
	 */
	fun setUserSession(name: String, email: String) {
		_sessionState.update { SessionState.Loading }

		// Cancel existing ongoing job (if exists).
		job?.cancel()
		job = viewModelScope.launch(defaultDispatcher) {
			val user = userRepository.add(name, email)
			sessionManager.setUserSession(user)
			updateSessionState()
		}
	}

	/**
	 * Clears the active session
	 */
	fun logoutSession() {
		job?.cancel()
		job = viewModelScope.launch(defaultDispatcher) {
			sessionManager.clear()
			_sessionState.update { SessionState.UserUnavailable }
		}

	}

	private suspend fun updateSessionState() {
		// Set current state as loading
		_sessionState.update { SessionState.Loading }

		// Try retrieving current user and create session accordingly
		val user = sessionManager.getCurrentUser()

		val sessionState = if (user != null) {
			SessionState.UserAvailable(user)
		} else {
			SessionState.UserUnavailable
		}

		// Update session
		_sessionState.update { sessionState }
	}
}