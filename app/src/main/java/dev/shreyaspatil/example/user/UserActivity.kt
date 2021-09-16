package dev.shreyaspatil.example.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import dev.shreyaspatil.example.data.model.User
import dev.shreyaspatil.example.ui.theme.ViewModelGoodPracticeExampleTheme

@AndroidEntryPoint
class UserActivity : ComponentActivity() {
	private val viewModel: UserViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ViewModelGoodPracticeExampleTheme {
				Surface(color = MaterialTheme.colors.background) {
					UserContent(viewModel)
				}
			}
		}
	}
}

@Composable
fun UserContent(viewModel: UserViewModel) {
	val sessionState by viewModel.sessionState.collectAsState()

	when (sessionState) {
		SessionState.Loading -> {
			Row {
				CircularProgressIndicator()
				Text(text = "Loading")
			}
		}
		SessionState.UserUnavailable -> {
			UserLoggedOutContent(
				onLogin = { user ->
					viewModel.setUserSession(user.name, user.email)
				}
			)
		}
		is SessionState.UserAvailable -> {
			val currentUser = (sessionState as? SessionState.UserAvailable)?.currentUser
			if (currentUser != null) {
				UserLoggedInContent(
					currentUser,
					onLoggedOut = {
						viewModel.logoutSession()
					}
				)
			}
		}
	}
}

@Composable
fun UserLoggedOutContent(onLogin: (LoginUser) -> Unit) {
	val name = dummyUsers.random()
	val email = "$name@example.com"
	Column {
		Text("Currently not logged in")
		Button(onClick = { onLogin(LoginUser(name, email)) }) {
			Text(text = "Randomly Login as $name")
		}
	}
}

@Composable
fun UserLoggedInContent(currentUser: User, onLoggedOut: () -> Unit) {
	Column {
		Text("Hello ${currentUser.name} (${currentUser.email})\nID: ${currentUser.id}")
		Button(onClick = onLoggedOut) {
			Text(text = "Logout")
		}
	}
}

data class LoginUser(val name: String, val email: String)

val dummyUsers = listOf("John", "Mark", "Harry", "Jerry", "Tom")