package dev.shreyaspatil.example.user

import dev.shreyaspatil.example.data.model.User

sealed class SessionState {
	object Loading : SessionState()
	data class UserAvailable(val currentUser: User) : SessionState()
	object UserUnavailable : SessionState()
}
