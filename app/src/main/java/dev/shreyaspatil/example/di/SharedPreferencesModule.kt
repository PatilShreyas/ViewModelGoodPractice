package dev.shreyaspatil.example.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shreyaspatil.example.session.DefaultSessionManager
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

	@UserPreferences
	@Provides
	fun sessionPreferences(@ApplicationContext context: Context): SharedPreferences {
		return context.getSharedPreferences(
			DefaultSessionManager.UserPreferencesDetails.NAME,
			Context.MODE_PRIVATE
		)
	}
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UserPreferences