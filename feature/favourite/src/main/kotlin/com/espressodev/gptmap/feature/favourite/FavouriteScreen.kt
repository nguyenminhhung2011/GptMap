package com.espressodev.gptmap.feature.favourite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.Content
import com.espressodev.gptmap.core.model.Favourite
import java.time.LocalDateTime
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun FavouriteRoute(
    popUp: () -> Unit,
    navigateToMap: (String) -> Unit,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val favourites by viewModel.favourites.collectAsStateWithLifecycle()
    FavouriteScreen(
        popUp = popUp,
        onCardClick = navigateToMap,
        favourites = favourites
    )
}


@Composable
fun FavouriteScreen(popUp: () -> Unit, onCardClick: (String) -> Unit, favourites: List<Favourite>) {
    Scaffold(
        topBar = {
            GmTopAppBar(
                title = AppText.favourite,
                icon = GmIcons.FavouriteFilled,
                onBackClick = popUp
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(HIGH_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(HIGH_PADDING)
        ) {
            items(favourites, key = { key -> key.id }) { favourite ->
                FavouriteCard(favourite, onClick = { onCardClick(favourite.favouriteId) })
            }
        }
    }
}


@Composable
fun FavouriteCard(favourite: Favourite, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = favourite.placeholderImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HIGH_PADDING),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
                ) {
                    Icon(
                        imageVector = GmIcons.LocationCityOutlined,
                        contentDescription = null,
                        modifier = Modifier.size(HIGH_PADDING),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = favourite.placeholderTitle,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomEnd) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
                    ) {
                        Icon(
                            imageVector = GmIcons.MyLocationOutlined,
                            contentDescription = null,
                            modifier = Modifier.size(HIGH_PADDING),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = favourite.placeholderCoordinates,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FavouritePreview() {
    GptmapTheme {
        FavouriteCard(
            favourite = Favourite(
                id = "definitiones",
                userId = "tritani",
                favouriteId = "ubique",
                title = "assueverit",
                placeholderImageUrl = "http://www.bing.com/search?q=finibus",
                locationImages = listOf(),
                content = Content(
                    latitude = 4.5,
                    longitude = 6.7,
                    city = "Laqwisch",
                    district = "pri",
                    country = "Togo",
                    poeticDescription = "sem",
                    normalDescription = "molestie"
                ),
                date = LocalDateTime.now()
            ),
            {}
        )
    }
}
