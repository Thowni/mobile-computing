package com.example.hw1

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Button(onClick = { navController.navigate("messages"){
            popUpTo("messages"){
                inclusive = true
            }
        } }) {
            Text("Go Back to messages")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row (verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)){
            Image(
                painter = painterResource(R.drawable.carp),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = "Profile Screen",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                )
        }
    }
}