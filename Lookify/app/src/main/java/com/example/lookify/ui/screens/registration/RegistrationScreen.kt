package com.example.lookify.ui.screens.registration

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.lookify.data.database.Users
import com.example.lookify.data.repositories.UsersRepository
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.composables.*
import com.example.traveldiary.utils.rememberCameraLauncher

@Composable
fun RegistrationScreen(
    state: LookifyState,
    navController: NavController,
    onRegister: (Users) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confermaPassword by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var residenza by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Gestione permesso fotocamera
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

    // Usa il tuo CameraLauncher
    val cameraLauncher = rememberCameraLauncher { uri ->
        profileImageUri = uri
    }

    // Launcher per selezionare da galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    Scaffold(
        topBar = { TitleAppBar(navController, state) },
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 32.dp)
                .padding(vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Titolo Registrazione
            Text(
                text = "Registrazione",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Immagine profilo e pulsanti
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Cerchio per immagine profilo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        // Mostra l'immagine selezionata (ora sarà automaticamente circolare)
                        ImageWithPlaceholder(profileImageUri, Size.Md)
                    } else {
                        Icon(
                            Icons.Outlined.CameraAlt,
                            contentDescription = "Foto Profilo",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pulsanti per foto
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsante Scatta Foto
                    Button(
                        onClick = {
                            if (hasCameraPermission) {
                                try {
                                    println("DEBUG: Launching camera...")
                                    cameraLauncher.captureImage()
                                } catch (e: Exception) {
                                    println("DEBUG: Error launching camera: ${e.message}")
                                    e.printStackTrace()
                                }
                            } else {
                                println("DEBUG: Requesting camera permission...")
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Scatta Foto", fontSize = 14.sp)
                    }

                    // Pulsante Galleria
                    Button(
                        onClick = {
                            galleryLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Galleria", fontSize = 14.sp)
                    }
                }
            }

            // Messaggio di errore
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Campi di input
            RegisterTextField(
                value = nome,
                onValueChange = {
                    nome = it
                    errorMessage = ""
                },
                label = "Nome*",
                enabled = !isLoading
            )

            RegisterTextField(
                value = cognome,
                onValueChange = {
                    cognome = it
                    errorMessage = ""
                },
                label = "Cognome*",
                enabled = !isLoading
            )

            RegisterTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = ""
                },
                label = "Username*",
                enabled = !isLoading
            )

            RegisterTextField(
                value = residenza,
                onValueChange = {
                    residenza = it
                    errorMessage = ""
                },
                label = "Città di Residenza*",
                enabled = !isLoading
            )

            RegisterTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = "Password*",
                isPassword = true,
                enabled = !isLoading
            )

            RegisterTextField(
                value = confermaPassword,
                onValueChange = {
                    confermaPassword = it
                    errorMessage = ""
                },
                label = "Conferma Password*",
                isPassword = true,
                enabled = !isLoading
            )

            // Pulsante Tuffati
            Button(
                onClick = {
                    // Debug: controlla quanti utenti ci sono
                    println("DEBUG: Controllo registrazione per username: '$username'")
                    println("DEBUG: Utenti esistenti: ${state.users.size}")
                    state.users.forEach { user ->
                        println("DEBUG: User esistente: '${user.username}'")
                    }

                    val registerResult = RegisterCheck(
                        users = state.users,
                        nome = nome.trim(),
                        cognome = cognome.trim(),
                        username = username.trim(),
                        password = password,
                        confermaPassword = confermaPassword,
                        profileImageUri = profileImageUri,
                        residenza = residenza
                    )

                    when (registerResult) {
                        is RegisterResult.Success -> {
                            isLoading = true
                            // Salva il nuovo utente
                            onRegister(registerResult.user)
                            // Naviga al login
                            navController.navigate(LookifyRoute.Login) {
                                popUpTo(LookifyRoute.Registration) { inclusive = true }
                            }
                        }
                        is RegisterResult.Error -> {
                            errorMessage = registerResult.message
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Tuffati",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Link login
            Text(
                text = "Sei già registrato? Fai Log In",
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        if (!isLoading) {
                            navController.navigate(LookifyRoute.Login)
                        }
                    }
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = Color.Red
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Red,
            unfocusedBorderColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.Red,
            unfocusedLabelColor = Color.Red,
            cursorColor = Color.Red
        ),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        enabled = enabled
    )
}

// Sealed class per i risultati della registrazione
sealed class RegisterResult {
    data class Success(val user: Users) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

// Funzione di controllo registrazione
fun RegisterCheck(
    users: List<Users>,
    nome: String,
    cognome: String,
    username: String,
    password: String,
    confermaPassword: String,
    profileImageUri: Uri?,
    residenza: String
): RegisterResult {
    return when {
        nome.isBlank() -> RegisterResult.Error("Inserisci il nome")
        cognome.isBlank() -> RegisterResult.Error("Inserisci il cognome")
        username.isBlank() -> RegisterResult.Error("Inserisci lo username")
        username.length < 3 -> RegisterResult.Error("Username deve essere almeno 3 caratteri")
        password.isBlank() -> RegisterResult.Error("Inserisci la password")
        password.length < 6 -> RegisterResult.Error("Password deve essere almeno 6 caratteri")
        residenza.isBlank() -> RegisterResult.Error("Inserisci una residenza")
        confermaPassword.isBlank() -> RegisterResult.Error("Conferma la password")
        password != confermaPassword -> RegisterResult.Error("Le password non coincidono")
        users.any { it.username.equals(username, ignoreCase = true) } ->
            RegisterResult.Error("Username già esistente")
        else -> {
            // Crea nuovo utente (assumendo che Users abbia questi campi)
            val newUser = Users(
                nome = nome,
                cognome = cognome,
                username = username,
                password = password,
                immagine = profileImageUri?.toString(),
                residenza = residenza
            )
            RegisterResult.Success(newUser)
        }
    }
}