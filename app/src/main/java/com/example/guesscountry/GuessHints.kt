package com.example.guesscountry

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.guesscountry.CountryFunctions.Companion.getFlagResourceId

import com.example.guesscountry.ui.theme.GuessCountryTheme


class GuessHints : ComponentActivity() {
    private lateinit var countryList: List<Country>
    private lateinit var currentCountry: Country
    private lateinit var flagImage: Painter
    private var checkValueGuessed = "Wrong"
    private var userInput by mutableStateOf("")
    private var dashes by mutableStateOf("")
    private var incorrectGuesses by mutableStateOf(0)
    private var buttonText by mutableStateOf("Submit")
    private var snackBarVisible by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countryList = CountryFunctions.loadCountries(context = this)
        randomizeCountry()
        dashes = "-".repeat(currentCountry.name.length)
        setContent {
            GuessCountryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                        Text(text = "Guess the Country:")
                        flagImage = painterResource(
                            id = getFlagResourceId(
                                currentCountry.code,
                                this@GuessHints
                            )
                        )
                        Image(
                            painter = flagImage,
                            contentDescription = "Flag of ${currentCountry.name}",
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = dashes)
                        TextField(
                            value = userInput,
                            onValueChange = { newValue ->
                                userInput = if (newValue.length > 1) newValue.take(1) else newValue
                            },
                            label = { Text("Enter a character") },
                            modifier = Modifier.padding(16.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
                        )



                        Button(onClick = {
                            if(userInput=="" && buttonText != "Next"){
                                  Toast.makeText(this@GuessHints,"Enter Input Character Kindly!!",Toast.LENGTH_SHORT).show()
                            }
                            else {
                                if (buttonText == "Next") {
                                    buttonText = "Submit Guess"
                                    snackBarVisible  = false
                                    randomizeCountry()
                                    dashes = "-".repeat(currentCountry.name.length)
                                    incorrectGuesses = 0
                                    userInput = ""
                                } else {

                                    if (currentCountry.name.lowercase()
                                            .contains(userInput.lowercase())
                                    ) {

                                        // Replace dashes with correct characters
                                        val updatedDashes = StringBuilder(dashes)
                                        Log.d("dash", userInput)
                                        Log.d("dash", currentCountry.name)
                                        currentCountry.name.lowercase()
                                            .forEachIndexed { index, char ->
                                                val guess = userInput.lowercase()[0]
                                                if (char == guess) {
                                                    updatedDashes[index] = char
                                                    Log.d("dashInside", userInput)

                                                }
                                            }

                                        dashes = updatedDashes.toString()

                                        // Check if all characters are guessed
                                        if (!updatedDashes.contains('-')) {
                                            // All characters guessed correctly
                                            // Display correct message and change button text to "Next"
                                            // var message = "Correct : ${currentCountry.name}"
                                            checkValueGuessed = "Correct"
                                            snackBarVisible  = true
                                            buttonText = "Next"


                                        }
                                    } else {

                                        incorrectGuesses++


                                        // Handle incorrect guess logic
                                        if (incorrectGuesses >= 3) {
                                            checkValueGuessed = "Wrong"
                                            snackBarVisible = true
                                            // Display wrong message and change button text to "Next"
                                            buttonText= "Next"

                                        }
                                    }
                                    userInput = ""
                                }
                            }
                        }) {
                            Text(text = buttonText)
                        }
                        if ( snackBarVisible ) {
                            // CorrectSnackBar()
                            Snackbar(
                                action = {
                                    Button(
                                        onClick = {  snackBarVisible = false },
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
                                            Text(text = "Wrong!", color = Color.Red, modifier = Modifier.background(Color.White))
                                            Text(text = "Correct country: ${currentCountry.name}", color = Color.Blue, modifier = Modifier.background(Color.White))
                                        } else {
                                            // Display correct message in green color with blue country name
                                            Text(text = "Correct", color = Color.Green, modifier = Modifier.background(Color.White))
                                            Text(text = currentCountry.name, color = Color.Blue, modifier = Modifier.background(Color.White))
                                        }
                                    }
                                }

                        }

                    }
                }
            }
        }
//        // Restore state if savedInstanceState is not null
//        savedInstanceState?.let { savedState ->
//
//            // You need to restore other state variables as well
//        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("userInput", userInput)
        outState.putString("dashes", dashes)
        outState.putInt("incorrectGuesses", incorrectGuesses)
        outState.putString("buttonText", buttonText)
        outState.putString("currentCountry", buttonText)
        outState.putInt("currentCountryIndex", countryList.indexOf(currentCountry))
        outState.putBoolean("SnackBarVisible",  snackBarVisible )
        outState.putString("checkValueGuessed", checkValueGuessed)

    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val countryIndex = savedInstanceState.getInt("currentCountryIndex", -1)
        currentCountry = if (countryIndex != -1 && countryIndex < countryList.size) {
            countryList[countryIndex]
        } else {
            countryList.firstOrNull() ?: Country("", "")
        }
        checkValueGuessed = savedInstanceState.getString("checkValueGuessed", "WRONG")
        userInput = savedInstanceState.getString("userInput") ?: ""
        dashes = savedInstanceState.getString("dashes") ?: ""
        incorrectGuesses = savedInstanceState.getInt("incorrectGuesses", 0)
        buttonText = savedInstanceState.getString("buttonText") ?: "Submit"
        snackBarVisible = savedInstanceState.getBoolean("SnackBarVisible", false)

    }
    private fun randomizeCountry() {
        val randomIndex = (countryList.indices).random()
        currentCountry = countryList[randomIndex]
    }



}

