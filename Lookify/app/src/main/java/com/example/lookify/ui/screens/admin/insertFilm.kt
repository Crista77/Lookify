package com.example.lookify.ui.screens.admin

import android.content.Context
import android.content.Intent
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lookify.data.database.*
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.ui.composables.TitleAppBar
import com.example.lookify.ui.screens.login.LoginCheck

@Composable
fun InsertFilmScreen(
    state: LookifyState,
    navController: NavController,
    viewModel: LookifyViewModel,
) {
    var selectedType by remember { mutableStateOf("Film") }
    var nome by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("") }
    var numeroPersonaleCast by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    var durata by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var selectedImageName by remember { mutableStateOf<String?>(null) }

    val numCast = numeroPersonaleCast.toIntOrNull() ?: 0
    val actorNames = remember(numCast) { mutableStateMapOf<Int, String>() }

    val context = LocalContext.current

    val availablePlatforms = state.platforms

    LaunchedEffect(availablePlatforms) {
        if (availablePlatforms.isNotEmpty() && selectedPlatform.isEmpty()) {
            selectedPlatform = availablePlatforms.first().nome
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri.toString()
                val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                }
                selectedImageName = fileName?.substringBeforeLast('.') ?: "default_image"
            }
        }
    }

    Scaffold(
        topBar = { TitleAppBar(navController) }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Inserimento $selectedType",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Tipo Film/Serie TV
            item {
                DropdownField(
                    label = "Tipo",
                    value = selectedType,
                    options = listOf("Film", "Serie TV"),
                    onValueChange = { selectedType = it }
                )
            }

            // Nome
            item {
                InputField(
                    label = "Titolo",
                    value = nome,
                    onValueChange = { nome = it }
                )
            }

            // Piattaforma
            item {
                DropdownField(
                    label = "Piattaforma",
                    value = selectedPlatform,
                    options = availablePlatforms.map { it.nome },
                    onValueChange = { selectedPlatform = it }
                )
            }

            // Numero Personale Cast
            item {
                DropdownField(
                    label = "Numero Personale Cast",
                    value = numeroPersonaleCast,
                    options = (1..20).map { it.toString() },
                    onValueChange = { numeroPersonaleCast = it }
                )
            }

            // Campi attori dinamici
            if (numCast > 0) {
                items(numCast) { index ->
                    ActorFields(
                        actorNumber = index + 1,
                        actorName = actorNames[index] ?: "",
                        onNameChange = { actorNames[index] = it }
                    )
                }
            }

            // Descrizione
            item {
                InputField(
                    label = "Descrizione",
                    value = descrizione,
                    onValueChange = { descrizione = it },
                    maxLines = 3
                )
            }

            // Durata
            item {
                InputField(
                    label = "Durata",
                    value = durata,
                    onValueChange = { durata = it },
                    placeholder = "Minuti"
                )
            }

            // Categoria
            item {
                InputField(
                    label = "Categoria",
                    value = categoria,
                    onValueChange = { categoria = it }
                )
            }

            // Bottoni
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Carica Copertina
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "image/*"
                                }
                                filePickerLauncher.launch(intent)
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Carica Copertina",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }

                    // Invia
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val actorsList = actorNames.values
                                    .mapNotNull { fullName ->
                                        fullName.split(" ").takeIf { it.size >= 2 }?.let {
                                            Actors(nome = it.first(), cognome = it.drop(1).joinToString(" "))
                                        }
                                    }

                                if (selectedType == "Film") {
                                    val film = Film(
                                        id_film = 0,
                                        titolo = nome,
                                        numero_Cast = numCast,
                                        descrizione = descrizione,
                                        durata = durata.toIntOrNull() ?: 0,
                                        categoria = categoria,
                                        visibile = true,
                                        visualizzazioni = 0,
                                        imageUri = selectedImageUri
                                    )
                                    viewModel.insertFilm(context, film, selectedPlatform, actorsList, selectedImageUri, selectedImageName)
                                } else {
                                    val serie = SerieTV(
                                        id_serie = 0,
                                        titolo = nome,
                                        numero_Cast = numCast,
                                        descrizione = descrizione,
                                        durata = durata.toIntOrNull() ?: 0,
                                        categoria = categoria,
                                        visibile = true,
                                        visualizzazioni = 0,
                                        imageUri = selectedImageUri
                                    )
                                    viewModel.insertSerie(context, serie, selectedPlatform, actorsList, selectedImageUri, selectedImageName)
                                }
                                navController.navigate("${LookifyRoute.AdminUser}?userId=${state.currentUserId}")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Invia",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }

                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    maxLines: Int = 1
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Red
            ),
            maxLines = maxLines
        )
    }
}

@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = label, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (value.isEmpty()) "Seleziona..." else value,
                    color = if (value.isEmpty()) Color.Gray else Color.White,
                    fontSize = 16.sp
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color(0xFF2E2E2E))) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option, color = Color.White) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ActorFields(
    actorNumber: Int,
    actorName: String,
    onNameChange: (String) -> Unit
) {
    InputField(
        label = "Nome e Cognome Attore $actorNumber",
        value = actorName,
        onValueChange = onNameChange
    )
}
