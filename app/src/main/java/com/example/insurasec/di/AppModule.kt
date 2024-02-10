package com.example.insurasec.di

import android.app.Application
import com.example.insurasec.repository.AuthRepo
import com.example.insurasec.repository.SecurityRepo
import com.example.insurasec.repository.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(context: Application): AuthRepo {
        return AuthRepo(
            context,
            FirebaseAuth.getInstance(),
            Firebase.firestore
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepo {
        return UserRepo()
    }

    @Provides
    @Singleton
    fun provideSecurityRepository(context: Application): SecurityRepo {
        return SecurityRepo(
            context,
            FirebaseAuth.getInstance(),
            Firebase.firestore
        )
    }
}