package com.example.lookify.ui.screens.registration

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.traveldiary.utils.rememberCameraLauncher
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TestCameraScreen() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Controllo permesso fotocamera
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher per richiedere il permesso
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            println("DEBUG: Camera permission granted!")
        } else {
            println("DEBUG: Camera permission denied!")
        }
    }

    val cameraLauncher = rememberCameraLauncher { uri ->
        imageUri = uri
        println("DEBUG: Photo captured, URI: $uri")
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Test Camera", fontSize = 24.sp, color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (hasCameraPermission) "Permission: ✅ Granted" else "Permission: ❌ Denied",
            color = if (hasCameraPermission) Color.Green else Color.Red,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Pulsante per richiedere permesso
        if (!hasCameraPermission) {
            Button(
                onClick = {
                    println("DEBUG: Requesting camera permission...")
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            ) {
                Text("Request Permission")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Pulsante per testare la fotocamera
        Button (
            onClick = {
                if (hasCameraPermission) {
                    try {
                        println("DEBUG: Button clicked, launching camera...")
                        cameraLauncher.captureImage()
                    } catch (e: Exception) {
                        println("DEBUG: Error launching camera: ${e.message}")
                        e.printStackTrace()
                    }
                } else {
                    println("DEBUG: No camera permission, requesting...")
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            enabled = hasCameraPermission
        ) {
            Text(if (hasCameraPermission) "Test Camera" else "Need Permission")
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (imageUri != null) {
            Text("Photo captured: $imageUri", color = Color.Green)
        } else {
            Text("No photo yet", color = Color.Gray)
        }
    }
}