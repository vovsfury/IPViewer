package com.example.ipviewer

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun IPViewerScreen() {
    var ipAddress by remember { mutableStateOf("↓Нажмите кнопку↓") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Вот это нужно

    suspend fun fetchIP(): String {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://functions.yandexcloud.net/d4e2bt6jba6cmiekqmsv")
                    .build()
                val response = client.newCall(request).execute()

                val json = response.body?.string() ?: return@withContext "Ошибка: пустой ответ"
                val ipRegex = Regex("\"myip\"\\s*:\\s*\"([^\"]+)\"")
                val match = ipRegex.find(json)
                match?.groupValues?.get(1) ?: "Ошибка: IP не найден"
            } catch (e: Exception) {
                "Ошибка: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = ipAddress, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                isLoading = true
                ipAddress = "Загрузка..."
                scope.launch {
                    ipAddress = fetchIP()
                    isLoading = false
                }
            },
            enabled = !isLoading
        ) {
            Text("Получить IP")
        }
    }
}