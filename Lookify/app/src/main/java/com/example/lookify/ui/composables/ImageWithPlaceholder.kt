package com.example.lookify.ui.composables

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

enum class Size { Sm, Md, Lg }

@Composable
fun ImageWithPlaceholder(uri: Uri?, size: Size) {
    val dimension = when (size) {
        Size.Sm -> 72.dp
        Size.Md -> 96.dp
        Size.Lg -> 128.dp
    }

    val padding = when (size) {
        Size.Sm -> 20.dp
        Size.Md -> 28.dp
        Size.Lg -> 66.dp
    }

    Log.d("ImageDebug", "Carico immagine da URI: $uri")

    if (uri != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri.toString())
                .crossfade(true)
                .build(),
            contentDescription = "Immagine profilo",
            contentScale = ContentScale.Crop, // Cambiato da Fit a Crop per riempire il cerchio
            modifier = Modifier
                .size(dimension)
                .clip(CircleShape) // Aggiunto per rendere l'immagine circolare
        )
    } else {
        Image(
            imageVector = Icons.Outlined.Image,
            contentDescription = "Immagine mancante",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier
                .size(dimension)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(padding)
        )
    }
}