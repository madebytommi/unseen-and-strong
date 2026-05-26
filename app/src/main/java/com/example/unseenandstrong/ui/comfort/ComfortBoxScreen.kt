package com.example.unseenandstrong.ui.comfort

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unseenandstrong.R
import com.example.unseenandstrong.ui.theme.DeepFogGrey
import com.example.unseenandstrong.ui.theme.LavenderPurple
import com.example.unseenandstrong.ui.theme.NightLavender
import com.example.unseenandstrong.ui.theme.PaleCloudWhite
import com.example.unseenandstrong.ui.theme.SoftBlushPink
import com.example.unseenandstrong.ui.theme.SoftCloudGrey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private enum class ComfortTone(val label: String) {
    GENTLE("Gentle"),
    DIRECT("Direct"),
    UPLIFTING("Uplifting")
}

private data class ComfortPhoto(
    val uri: String,
    val title: String
)

@Composable
fun ComfortBoxScreen(
    isFlareDay: Boolean = false
) {
    val backgroundColor = if (isFlareDay) NightLavender else SoftCloudGrey
    val contrastTextColor = if (isFlareDay) PaleCloudWhite else DeepFogGrey
    val reminderCardColor = if (isFlareDay) NightLavender.copy(alpha = 0.82f) else SoftCloudGrey.copy(alpha = 0.8f)
    val strategyCardColor = if (isFlareDay) LavenderPurple.copy(alpha = 0.22f) else LavenderPurple.copy(alpha = 0.1f)

    val context = LocalContext.current
    val reminders = stringArrayResource(id = R.array.gentle_reminders)
    val strategies = stringArrayResource(id = R.array.offline_coping_strategies)
    var selectedTone by rememberSaveable { mutableStateOf(ComfortTone.GENTLE) }
    var comfortPhotos by remember { mutableStateOf(loadComfortPhotos(context)) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val nextPhoto = ComfortPhoto(
                uri = uri.toString(),
                title = "Comfort photo ${comfortPhotos.size + 1}"
            )
            comfortPhotos = comfortPhotos + nextPhoto
            saveComfortPhotos(context, comfortPhotos)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(if (isFlareDay) 18.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isFlareDay) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = LavenderPurple.copy(alpha = 0.28f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Flare Day Mode is active. This space is simplified: comfort first, no pressure, no extra tasks.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = contrastTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Text(
                text = "Comfort Box",
                style = MaterialTheme.typography.headlineSmall,
                color = contrastTextColor,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = comfortMessageFor(selectedTone),
                style = MaterialTheme.typography.bodyLarge,
                color = contrastTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ComfortTone.entries.forEach { tone ->
                    FilterChip(
                        selected = selectedTone == tone,
                        onClick = { selectedTone = tone },
                        label = { Text(tone.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = SoftCloudGrey,
                            labelColor = DeepFogGrey,
                            selectedContainerColor = LavenderPurple.copy(alpha = 0.55f),
                            selectedLabelColor = contrastTextColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (!isFlareDay) {
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=UfcAVejs1Ac")
                        )
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlushPink,
                        contentColor = DeepFogGrey
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "I'm Struggling - YouTube",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://open.spotify.com/")
                        )
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LavenderPurple,
                        contentColor = contrastTextColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "I'm Struggling - Spotify",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = "Comfort Photos",
                style = MaterialTheme.typography.headlineSmall,
                color = contrastTextColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            if (!isFlareDay) {
                Button(
                    onClick = { photoPickerLauncher.launch(arrayOf("image/*")) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlushPink,
                        contentColor = DeepFogGrey
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Photo, contentDescription = null)
                    Text(
                        text = "Add a local comfort photo",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (comfortPhotos.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = reminderCardColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No comfort photos yet. Add a calming image, pet photo, memory, or anything that helps you feel held.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contrastTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                comfortPhotos.forEach { photo ->
                    ComfortPhotoCard(
                        photo = photo,
                        textColor = contrastTextColor,
                        cardColor = reminderCardColor,
                        onDelete = {
                            comfortPhotos = comfortPhotos.filterNot { it.uri == photo.uri }
                            saveComfortPhotos(context, comfortPhotos)
                        }
                    )
                }
            }

            Text(
                text = "Gentle Reminders",
                style = MaterialTheme.typography.headlineSmall,
                color = contrastTextColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            reminders.forEach { reminder ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = reminderCardColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = toneAdjustedReminder(selectedTone, reminder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = contrastTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (!isFlareDay) {
                Text(
                    text = "Offline Coping Strategies",
                    style = MaterialTheme.typography.headlineSmall,
                    color = contrastTextColor,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                strategies.forEach { strategy ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = strategyCardColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strategy,
                                style = MaterialTheme.typography.bodyLarge,
                                color = contrastTextColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComfortPhotoCard(
    photo: ComfortPhoto,
    textColor: androidx.compose.ui.graphics.Color,
    cardColor: androidx.compose.ui.graphics.Color,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var imageBitmap by remember(photo.uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(photo.uri) {
        imageBitmap = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(Uri.parse(photo.uri))?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove comfort photo",
                        tint = textColor
                    )
                }
            }

            imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = photo.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            } ?: Text(
                text = "Photo saved. If it does not preview, choose it again from your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}

private fun comfortMessageFor(tone: ComfortTone): String = when (tone) {
    ComfortTone.GENTLE -> "You do not have to push through this moment. Let this be soft."
    ComfortTone.DIRECT -> "Pause. Lower the demand. Do the next kind thing for your body."
    ComfortTone.UPLIFTING -> "You have survived hard moments before. You are still here, and that matters."
}

private fun toneAdjustedReminder(tone: ComfortTone, reminder: String): String = when (tone) {
    ComfortTone.GENTLE -> reminder
    ComfortTone.DIRECT -> when (reminder) {
        "You are safe. This will pass." -> "You are safe right now. Focus on the next minute."
        "Your feelings are valid." -> "Your symptoms and feelings are real. Treat them like data, not failure."
        "Take it one moment at a time." -> "Pick one small next step. Nothing more is required."
        else -> reminder
    }
    ComfortTone.UPLIFTING -> when (reminder) {
        "You are safe. This will pass." -> "This moment is heavy, but it is not the whole story."
        "Your feelings are valid." -> "You are allowed to need care and still be strong."
        "Take it one moment at a time." -> "One breath, one sip, one tiny step. That counts."
        else -> reminder
    }
}

private fun loadComfortPhotos(context: Context): List<ComfortPhoto> {
    val storedPhotos = context
        .getSharedPreferences(COMFORT_PREFS, Context.MODE_PRIVATE)
        .getStringSet(COMFORT_PHOTO_SET, emptySet())
        .orEmpty()

    return storedPhotos
        .mapNotNull { stored ->
            val parts = stored.split(COMFORT_PHOTO_SEPARATOR, limit = 2)
            if (parts.size == 2) {
                ComfortPhoto(uri = parts[0], title = parts[1])
            } else {
                null
            }
        }
        .sortedBy { it.title }
}

private fun saveComfortPhotos(context: Context, photos: List<ComfortPhoto>) {
    val storedPhotos = photos
        .map { photo -> "${photo.uri}$COMFORT_PHOTO_SEPARATOR${photo.title}" }
        .toSet()

    context
        .getSharedPreferences(COMFORT_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putStringSet(COMFORT_PHOTO_SET, storedPhotos)
        .apply()
}

private const val COMFORT_PREFS = "comfort_box"
private const val COMFORT_PHOTO_SET = "comfort_photos"
private const val COMFORT_PHOTO_SEPARATOR = "||"

@Preview(showBackground = true)
@Composable
fun ComfortBoxScreenPreview() {
    ComfortBoxScreen()
}

@Preview(showBackground = true)
@Composable
fun ComfortBoxScreenFlareDayPreview() {
    ComfortBoxScreen(isFlareDay = true)
}
