package dev.shreyaspatil.example.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shreyaspatil.example.session.DefaultSessionManager
import dev.shreyaspatil.example.session.SessionManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SessionManagerModule {

	@Binds
	@Singleton
	fun sessionManager(manager: DefaultSessionManager): SessionManager
}