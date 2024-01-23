package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmUser
import kotlinx.coroutines.flow.Flow

interface RealmSyncService {
    suspend fun saveUser(realmUser: RealmUser): Result<Boolean>

    suspend fun saveFavourite(realmFavourite: RealmFavourite): Result<Boolean>

    suspend fun saveImageAnalysis(realmImageAnalysis: RealmImageAnalysis): Result<Boolean>

    fun isUserInDatabase(): Result<Boolean>

    fun getFavourites(): Flow<List<Favourite>>

    fun getFavourite(id: String): Favourite

    fun getImageAnalyses(): Flow<List<ImageAnalysis>>

    suspend fun deleteImageAnalysis(imageAnalysisId: String): Result<Boolean>

    suspend fun updateImageAnalysisText(imageAnalysisId: String, text: String): Result<Boolean>

    suspend fun deleteFavourite(favouriteId: String): Result<Boolean>

    suspend fun updateFavouriteText(favouriteId: String, text: String): Result<Boolean>
}
