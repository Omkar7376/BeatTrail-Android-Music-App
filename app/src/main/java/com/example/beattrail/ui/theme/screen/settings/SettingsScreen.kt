package com.example.beattrail.ui.theme.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beattrail.datastore.ThemeMode
import com.example.beattrail.ui.theme.ThemeViewModel

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel) {
    val selectedTheme by themeViewModel.appTheme.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White) },
                backgroundColor = Color.Gray,
                contentColor = Color.White
            )
        },
    ){padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)) {

            Text("Choose Theme", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))

            ThemeMode.values().forEach { theme ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable{themeViewModel.setTheme(theme)}
                    .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedTheme == theme,
                        onClick = { themeViewModel.setTheme(theme) },
                        colors = RadioButtonDefaults.colors(MaterialTheme.colorScheme.onSurface)
                    )
                    Text(text = theme.name.lowercase().replaceFirstChar { it.uppercase()}, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}