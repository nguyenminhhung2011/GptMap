package com.espressodev.gptmap.feature.favourite

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class FavouriteViewModel @Inject constructor(logService: LogService): GmViewModel(logService) {

}