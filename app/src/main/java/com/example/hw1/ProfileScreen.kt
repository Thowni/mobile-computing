package com.example.hw1

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.material3.Button
import java.io.File
import java.io.FileOutputStream


@Composable
fun ProfileScreen(navController: NavController, activity: MainActivity) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var profileName by remember { mutableStateOf("") }
    var savedImageUri by remember { mutableStateOf<Uri?>(null) }

    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.ProfileDao()

    fun saveImageToInternalStorage(context: Context, uri: Uri): Uri {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_image.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return Uri.fromFile(file)
    }

    @Composable
    fun RequestNotificationPermission(context: Context) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }) {
                Text("Allow Notifications")
            }
        }
    }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null){
            savedImageUri = saveImageToInternalStorage(context, uri)
            scope.launch {
                dao
                    .insertProfile(Profile(id = 1, name = profileName, imageUri = savedImageUri.toString()))
            }
        }
    }

    LaunchedEffect(Unit) {
        val profile = dao.getProfile()
        profileName = profile?.name ?: "Default"
        savedImageUri = profile?.imageUri?.let { Uri.parse(it) }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate("messages"){
            popUpTo("messages"){
                inclusive = true
            }
        } }) {
            Text("Go Back to messages")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row (verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = savedImageUri ?: R.drawable.carp,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(CircleShape)
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = profileName,
                onValueChange = { newName ->
                    profileName = newName
                    scope.launch {
                        dao.insertProfile(
                            Profile(
                                id = 1,
                                name = newName,
                                imageUri = savedImageUri.toString()
                            )
                        )
                    }
                },
                label = { Text("Profile name") }
            )
        }
        RequestNotificationPermission(context)
    }
}