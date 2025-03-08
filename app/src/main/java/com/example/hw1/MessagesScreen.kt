package com.example.hw1

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import java.io.File
import java.io.IOException
import android.Manifest
import android.widget.Toast

data class Message(val author: String, val body: String, val videoUrl: String? = null)

@Composable
fun MessagesScreen(navController: NavController) {
    Conversation(SampleData.conversationSample, navController)
    AudioRecorderBar()
}

@Composable
fun AudioRecorderBar() {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }
    val audioFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recorded_audio.mp3")

    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (isGranted) {
                Toast.makeText(context, "Microphone permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xffeaffff))
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

                Button(
                    onClick = {
                        if (hasPermission) {
                            if (!isRecording) {
                                mediaRecorder = MediaRecorder().apply {
                                    setAudioSource(MediaRecorder.AudioSource.MIC)
                                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                    setOutputFile(audioFile.absolutePath)
                                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                    try {
                                        prepare()
                                        start()
                                        isRecording = true
                                    } catch (e: IOException) {
                                        Log.e("Microphone", "Recording failed", e)
                                    }
                                }
                            } else {
                                mediaRecorder?.apply {
                                    stop()
                                    release()
                                }
                                mediaRecorder = null
                                isRecording = false
                            }
                        } else {
                            Toast.makeText(context, "You haven't given microphone permissions", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(if (isRecording) Color.Red else Color.Blue)
                ) {
                    Text(if (isRecording) "Stop Recording" else "Record")
                }

                Button(
                    onClick = {
                        if (!isPlaying) {
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(audioFile.absolutePath)
                                prepare()
                                start()
                                isPlaying = true

                                setOnCompletionListener {
                                    isPlaying = false
                                    release()
                                }
                            }
                        }
                    },
                    enabled = audioFile.exists()
                ) {
                    Text("Play Recording")
                }
            }
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                colors = ButtonDefaults.buttonColors(if(!hasPermission) Color.Gray else Color.Green)
            ) {
                Text(if (!hasPermission) "Request Microphone Permission" else "Microphone permission already granted!")
            }
        }
    }
}

@Composable
fun MessageCard(msg: Message, navController: NavController) {
    val context = LocalContext.current

    var profileName by remember { mutableStateOf("") }
    var savedImageUri by remember { mutableStateOf<Uri?>(null) }

    val db = remember { AppDatabase.getDatabase(context) }
    val dao = db.ProfileDao()

    LaunchedEffect(Unit) {
        val profile = dao.getProfile()
        profileName = profile?.name ?: "Default"
        savedImageUri = profile?.imageUri?.let { Uri.parse(it) }
    }

    Row(modifier = Modifier.padding(all = 8.dp)) {
        AsyncImage(
            model = savedImageUri ?: R.drawable.carp,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { navController.navigate("second") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if(isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )

        Column (modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(text = profileName,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall)

            Spacer(modifier = Modifier.height(4.dp))

            if(msg.videoUrl != null){
                VideoPlayer(videoUrl = msg.videoUrl)
            } else {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp,
                    color = surfaceColor,
                    modifier = Modifier.animateContentSize().padding(1.dp)
                ) {
                    Text(text = msg.body,
                        modifier = Modifier.padding(all = 4.dp),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun Conversation(messages: List<Message>, navController: NavController){
    LazyColumn {
        items(messages) { message ->
            MessageCard(message, navController)
        }
    }
}

