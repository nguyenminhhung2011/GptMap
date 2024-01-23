package com.espressodev.gptmap.feature.favourite

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.EditableItemUiEvent
import com.espressodev.gptmap.core.model.Exceptions.RealmFailedToLoadFavouritesException
import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.FavouriteUiState
import com.espressodev.gptmap.core.model.ImageSummary
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val realmSyncService: RealmSyncService,
    logService: LogService,
    private val ioDispatcher: CoroutineDispatcher
) : GmViewModel(logService) {
    val favourites: StateFlow<Response<List<Favourite>>> =
        realmSyncService
            .getFavourites()
            .map<List<Favourite>, Response<List<Favourite>>> { favouritesList ->
                Response.Success(favouritesList)
            }
            .catch { exception ->
                logService.logNonFatalCrash(exception)
                emit(Response.Failure(RealmFailedToLoadFavouritesException()))
            }
            .flowOn(ioDispatcher)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                Response.Loading
            )

    private val _uiState =
        MutableStateFlow(FavouriteUiState(selectedItem = Favourite()))
    val uiState = _uiState.asStateFlow()

    private val favouriteId
        get() = uiState.value.selectedItem.favouriteId
    fun onEvent(event: EditableItemUiEvent) {
        when (event) {
            EditableItemUiEvent.OnCancelClick -> reset()
            EditableItemUiEvent.OnDeleteClick -> _uiState.update { it.copy(deleteDialogState = true) }
            EditableItemUiEvent.OnDeleteDialogConfirm -> onDeleteDialogConfirmClick()
            EditableItemUiEvent.OnDeleteDialogDismiss -> _uiState.update { it.copy(deleteDialogState = false) }
            EditableItemUiEvent.OnEditClick -> _uiState.update { it.copy(editDialogState = true) }
            is EditableItemUiEvent.OnEditDialogConfirm -> onEditDialogConfirmClick(event.text)
            EditableItemUiEvent.OnEditDialogDismiss -> _uiState.update { it.copy(editDialogState = false) }
            is EditableItemUiEvent.OnLongClickToItem<*> -> _uiState.update { it.copy(uiIsInEditMode = true, selectedItem = event.item as Favourite) }
            EditableItemUiEvent.Reset -> reset()
        }
    }

    private fun onDeleteDialogConfirmClick() = launchCatching {
        withContext(ioDispatcher) {
            realmSyncService.deleteFavourite(favouriteId).getOrThrow()
        }
        reset()
    }

    private fun onEditDialogConfirmClick(text: String) = launchCatching {
        withContext(ioDispatcher) {
            realmSyncService.updateFavouriteText(favouriteId, text).getOrThrow()
        }
        reset()
    }

    private fun reset() {
        _uiState.update { FavouriteUiState(selectedItem = Favourite()) }
    }
}
