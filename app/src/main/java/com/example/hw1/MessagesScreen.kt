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

data class Message(val author: String, val body: String)

@Composable
fun MessagesScreen(navController: NavController) {
    Conversation(SampleData.conversationSample, navController)
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

@Composable
fun Conversation(messages: List<Message>, navController: NavController){
    LazyColumn {
        items(messages) { message ->
            MessageCard(message, navController)
        }
    }
}

