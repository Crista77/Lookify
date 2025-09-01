package com.example.lookify.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lookify.data.database.Users
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.composables.*

@Composable
fun LoginScreen(state: LookifyState, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TitleAppBar(navController, state) }
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Titolo Log In
            Text(
                text = "Log In",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Messaggio di errore
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // Campo Username
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Username*",
                        color = Color.Red
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.Red,
                    unfocusedLabelColor = Color.Red,
                    cursorColor = Color.Red
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                enabled = !isLoading
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Password*",
                        color = Color.Red
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.Red,
                    unfocusedLabelColor = Color.Red,
                    cursorColor = Color.Red
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                enabled = !isLoading
            )

            // Pulsante Tuffati
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Inserisci username e password"
                        return@Button
                    }

                    isLoading = true
                    val loginResult = LoginCheck(
                        users = state.users,
                        username = username.trim(),
                        password = password
                    )

                    when (loginResult) {
                        is LoginResult.Success -> {
                            state.currentUserId = loginResult.user.id_user
                            navController.navigate("${LookifyRoute.Home}?currentUserId=${state.currentUserId}}") {
                                popUpTo(LookifyRoute.Login) { inclusive = true }
                            }
                        }
                        is LoginResult.Error -> {
                            errorMessage = loginResult.message
                            isLoading = false
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
                    .padding(bottom = 32.dp)
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

            // Link registrazione
            Text(
                text = "Non sei ancora registrato? Registrati",
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable {
                        if (!isLoading) {
                            navController.navigate(LookifyRoute.Registration)
                        }
                    }
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

// Sealed class per i risultati del login
sealed class LoginResult {
    data class Success(val user: Users) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

// Funzione di controllo login
fun LoginCheck(
    users: List<Users>,
    username: String,
    password: String
): LoginResult {
    val user = users.find {
        it.username.equals(username, ignoreCase = true)
    }

    return when {
        user == null -> {
            LoginResult.Error("Username non trovato")
        }
        user.password != password -> {
            LoginResult.Error("Password non corretta")
        }
        else -> {
            LoginResult.Success(user)
        }
    }
}