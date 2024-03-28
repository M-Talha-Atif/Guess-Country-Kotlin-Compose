package com.example.guesscountry


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.guesscountry.CountryFunctions.Companion.getFlagResourceId
import com.example.guesscountry.ui.theme.GuessCountryTheme
import kotlinx.coroutines.launch







class GuessCountry : ComponentActivity() {
    private lateinit var countryList: List<Country>
    private var currentCountry: Country? by mutableStateOf(null)
    private var selectedCountry: String by mutableStateOf("")
    private lateinit var flagImage: Painter // Declare flagImage as a global variable
    private var searchText by   mutableStateOf("")
    private var buttonText by mutableStateOf("Submit Guess")
    private var checkValueGuessed = "Wrong"
    private var snackBarVisible by mutableStateOf(false)

    private var random = 0
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countryList = CountryFunctions.loadCountries(context = this)
        random = (0..255).random()
        currentCountry = countryList[random]
        if (CountryFunctions.switchValueButton) {
            Toast.makeText(this@GuessCountry, "Timer ON", Toast.LENGTH_SHORT).show()
        }

        setContent {

            GuessCountryTheme {
                val coroutineScope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        flagImage = painterResource(
                            id = getFlagResourceId(
                                currentCountry!!.code,
                                this@GuessCountry
                            )
                        )
//                        var searchText by remember { mutableStateOf("") }
//                        val buttonText = remember { mutableStateOf("Submit Guess") }
//                        val timerValue = remember { mutableStateOf(10) } // Initial timer value
//                        val countdownActive =
//                            remember { mutableStateOf(true) } // Flag to track if countdown is active
                        if (currentCountry == null) {
                            // Show loading indicator while fetching random country
                            CircularProgressIndicator()
                        } else {

//                            if (CountryFunctions.switchValueButton)
//                                // Countdown timer UI
//                                Text(
//                                    text = "Timer: ${timerValue.value}",
//                                    style = MaterialTheme.typography.bodyMedium
//                                )
                                //     LaunchedEffect(Unit) {
                                // Start countdown coroutine when countdownActive is true
//                                    if (countdownActive.value) {
//                                        while (timerValue.value > 0) {
//
//                                            delay(1000) // Delay for 1 second
//                                            timerValue.value -= 1 // Decrement timer value
//
//                                        }
//
//                                            if (buttonText.value == "Next") {
//                                                snackbarVisible = false
//                                                buttonText.value = "Submit Guess"
//                                                // Reset selection for next guess
//                                                reset()
//
//                                            } else {
//                                                if (selectedCountry == "") {
//                                                    checkValueGuessed = "Wrong"
//                                                    snackbarVisible = true
//                                                } else {
//                                                    val isCorrect =
//                                                        selectedCountry == currentCountry!!.code
//                                                    if (isCorrect) {
//                                                        checkValueGuessed = "Correct"
//
//                                                        snackbarVisible = true
//
//
//                                                    } else {
//                                                        checkValueGuessed = "Wrong"
//                                                        snackbarVisible = true
//                                                    }
//
//
//                                                }
//                                                buttonText.value = "Next"
//                                                reset()
//                                            }
//                                    }
//
//
//                                        }
//
//
//                           }
                                //    flagImage = painterResource(id = getFlagResourceId(currentCountry!!.code, this@GuessCountry))
                                Image(
                                    painter = flagImage,
                                    contentDescription = "Flag of ${currentCountry!!.name}",
                                    modifier = Modifier.size(120.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                if (snackBarVisible ) {
                                    // CorrectSnackBar()
                                    Snackbar(
                                        action = {
                                            Button(
                                                onClick = { snackBarVisible = false },
                                                colors = ButtonDefaults.run { buttonColors(Color.Transparent) }
                                            ) {
                                                Text(text = "Dismiss")
                                            }
                                        },
                                        modifier = Modifier.padding(14.dp)
                                    )

                                    {
                                        Column {
                                            if (checkValueGuessed == "Wrong") {
                                                // Display wrong message in red color with black background
                                                Text(
                                                    text = "Wrong!",
                                                    color = Color.Red,
                                                    modifier = Modifier.background(Color.White)
                                                )
                                                Text(
                                                    text = "Correct country: ${currentCountry!!.name}",
                                                    color = Color.Blue,
                                                    modifier = Modifier.background(Color.White)
                                                )
                                            } else {
                                                // Display correct message in green color with blue country name
                                                Text(
                                                    text = "Correct",
                                                    color = Color.Green,
                                                    modifier = Modifier.background(Color.White)
                                                )
                                                Text(
                                                    text = currentCountry!!.name,
                                                    color = Color.Blue,
                                                    modifier = Modifier.background(Color.White)
                                                )
                                            }
                                        }
                                    }

                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Guess the Country:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                //     var searchText by remember { mutableStateOf("") }

                                TextField(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    label = { Text("Search Country") },
                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                )

                                LazyColumn(modifier = Modifier.weight(1f)) {
                                    val filteredCountries = if (searchText.isEmpty()) {
                                        countryList
                                    } else {
                                        countryList.filter {
                                            it.name.contains(
                                                searchText,
                                                ignoreCase = true
                                            )
                                        }
                                    }
                                    items(filteredCountries) { country ->
                                        val isSelected = selectedCountry == country.code
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedCountry = country.code },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedCountry = country.code }
                                            )
                                            Text(
                                                text = country.name,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                                //        val buttonText = remember { mutableStateOf("Submit Guess") }
                                Button(
                                    onClick = {
                                        // Check user's guess and display feedback
                                        coroutineScope.launch {
                                            if (buttonText == "Next") {
//                                                timerValue.value = 10
//                                                countdownActive.value = true
                                                snackBarVisible = false
                                                buttonText = "Submit Guess"
                                                // Reset selection for next guess
                                                reset()

                                            } else {
                                                if (selectedCountry == "") {
                                                    Toast.makeText(
                                                        this@GuessCountry,
                                                        "Please mark any country",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    val isCorrect =
                                                        selectedCountry == currentCountry!!.code
                                                    checkValueGuessed = if (isCorrect) {
                                                        "Correct"

                                                    } else {
                                                        "Wrong"
                                                    }
//                                                    countdownActive.value = false
//                                                    timerValue.value = 0
                                                    snackBarVisible  = true

                                                    // Change the label of the button to "Next"
                                                    buttonText = "Next"

                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = buttonText)
                                }

                            }
                        }
                    }
                }
            }
        }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentCountryIndex", countryList.indexOf(currentCountry))
        outState.putString("selectedCountry", selectedCountry)
        outState.putString("buttonText", buttonText)
        outState.putString("checkValueGuessed", checkValueGuessed)
        outState.putBoolean("snackBarVisible", snackBarVisible)
        // Save other relevant state variables
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        countryList = CountryFunctions.loadCountries(context = this@GuessCountry)
        val countryIndex = savedInstanceState.getInt("currentCountryIndex")
        if (countryIndex != -1) {
            currentCountry = countryList[countryIndex]
        }
        selectedCountry = savedInstanceState.getString("selectedCountry", "")
        buttonText = savedInstanceState.getString("buttonText", "Submit Guess") ?: "Submit Guess"
        checkValueGuessed = savedInstanceState.getString("checkValueGuessed", "Wrong") ?: "Wrong"
        snackBarVisible = savedInstanceState.getBoolean("snackBarVisible", false)
        // Restore other relevant state variables
    }

    private fun reset() {
        selectedCountry = ""
        // Fetch a new random country
        random = (0..255).random()
        currentCountry = countryList[random]
    }

    }






