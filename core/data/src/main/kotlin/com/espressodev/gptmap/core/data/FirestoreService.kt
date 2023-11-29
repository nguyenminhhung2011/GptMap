package com.espressodev.gptmap.core.data

import com.espressodev.gptmap.core.model.User
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    suspend fun saveUser(user: User)
    suspend fun isUserInDatabase(userId: String): Result<Boolean>
    suspend fun getUser(userId: String): Result<User>
}