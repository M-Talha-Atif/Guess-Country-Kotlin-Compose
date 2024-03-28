package com.example.guesscountry

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guesscountry.CountryFunctions.Companion.getFlagResourceId

class GuessFlag : ComponentActivity() {
    private lateinit var countryList: List<Country>
    private var selectedFlag by mutableStateOf<Country?>(null)
    private var buttonText by mutableStateOf("Submit")
    private var flagClicked by mutableStateOf(false)
    private var checkValueGuessed by mutableStateOf("Wrong")
    private var  snackBarVisible by mutableStateOf(false)
    // Default intialization value
    private var currentQuestion by  mutableStateOf(Question(Country("UK","UNITED-KINGDOM"),listOf(Country("UK","UNITED-KINGDOM"))))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ComeOut","Here")
        countryList = CountryFunctions.loadCountries(context = this)
        currentQuestion = generateQuestion()

        setContent {
          GuessFlagScreen()
        }
    }


    @Composable
    private fun GuessFlagScreen() {


        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "What country does this flag belong to?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                currentQuestion.flags.forEach { flag ->
                    Image(
                        painter = painterResource(id = getFlagResourceId(flag.code, this@GuessFlag)),
                        contentDescription = "Flag",
                        modifier = Modifier
                            .size(100.dp)
                            .clickable(enabled = !flagClicked) {
                                selectedFlag = flag
                                flagClicked = true
                            }
                    )
                }
            }
            Text(
                text = currentQuestion.correctAnswer.name,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(
                onClick = {
                    if(buttonText == "Next"){
                        snackBarVisible = false
                        buttonText = "Submit"
                        currentQuestion=generateQuestion()
                        flagClicked = false
                        selectedFlag = null // Reset selected flag
                    }
                    else {
                        if (selectedFlag != null) { // Check if a flag is selected
                            evaluateSelection(selectedFlag, currentQuestion.correctAnswer)
                            buttonText = "Next"

                        } else {
                            Toast.makeText(
                                this@GuessFlag,
                                "Please select a flag",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
              //  enabled = flagClicked // Disable button until a flag is clicked
            ) {
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
                        if (checkValueGuessed == "WRONG") {
                            // Display wrong message in red color with black background
                            Text(text = "Wrong!", color = Color.Red, modifier = Modifier.background(
                                Color.White))
                        } else {
                            // Display correct message in green color with blue country name
                            Text(text = "Correct!", color = Color.Green, modifier = Modifier.background(
                                Color.White))
                        }
                    }
                }

            }
        }
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val correctAnswerIndex = savedInstanceState.getInt("correctAnswerIndex", -1)
        val flag1Index = savedInstanceState.getInt("flag1", -1)
        val flag2Index = savedInstanceState.getInt("flag2", -1)
        val flag3Index = savedInstanceState.getInt("flag3", -1)


        snackBarVisible = savedInstanceState.getBoolean("snackBarVisible", false)
        buttonText = savedInstanceState.getString("buttonText", "Submit") ?: "Submit"
        flagClicked = savedInstanceState.getBoolean("flagClicked", false)

        checkValueGuessed = savedInstanceState.getString("checkValueGuessed", "WRONG")

        val correctAnswer = countryList[correctAnswerIndex]
        val flags = listOf(countryList[flag1Index],countryList[flag2Index],countryList[flag3Index])

        currentQuestion = Question(correctAnswer, flags)
        val selectIndex = savedInstanceState.getInt("selectedFlag", -1)
        if(selectIndex != -1)
             selectedFlag = countryList[selectIndex]
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("correctAnswerIndex", countryList.indexOf(currentQuestion.correctAnswer))
        outState.putInt("selectedFlag", countryList.indexOf(selectedFlag))
        outState.putString("checkValueGuessed", checkValueGuessed)
        outState.putInt("flag1", countryList.indexOf(currentQuestion.flags[0]) )
        outState.putInt("flag2", countryList.indexOf(currentQuestion.flags[1]) )
        outState.putInt("flag3", countryList.indexOf(currentQuestion.flags[2]) )
        outState.putString("buttonText", buttonText)
        outState.putBoolean("snackBarVisible", snackBarVisible)
        outState.putBoolean("flagClicked", flagClicked)
    }

//
    private fun generateQuestion(): Question {
       // countryList = CountryFunctions.loadCountries(context = this@GuessFlag)
        val correctAnswer = countryList.random()
        val wrongAnswers = List(2) { countryList.filter { it.code != correctAnswer.code }.random() }
        val shuffledFlags = wrongAnswers.shuffled() + listOf(correctAnswer)
        return Question(correctAnswer, shuffledFlags.subList(0, 3)) // Get first 3 flags
    }

    private fun evaluateSelection(selectedFlag: Country?, correctCountry: Country?) {
        checkValueGuessed  = if (selectedFlag == correctCountry) {
            "CORRECT"

        } else {
          "WRONG"
        }
        snackBarVisible  = true
     //   Toast.makeText(this@GuessFlag, message, Toast.LENGTH_SHORT).show()
    }

    data class Question(
        val correctAnswer: Country,
        val flags: List<Country>
    )
}
