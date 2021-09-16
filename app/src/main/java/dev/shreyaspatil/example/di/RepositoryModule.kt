package dev.shreyaspatil.example.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shreyaspatil.example.data.repo.InMemoryUserRepository
import dev.shreyaspatil.example.data.repo.UserRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
	@Singleton
	@Binds
	fun userRepository(repository: InMemoryUserRepository): UserRepository
}