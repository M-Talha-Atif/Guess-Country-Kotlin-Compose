package com.example.guesscountry
// Libraries to be used
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.guesscountry.CountryFunctions.Companion.getFlagResourceId
import com.example.guesscountry.ui.theme.GuessCountryTheme

class AdvanceLevel : ComponentActivity() {
    private var correctCountries = mutableListOf<Country>()
    private var correctAnswers = mutableListOf<String>()
    private var score by mutableStateOf(0)
    private var attemptsRemaining by mutableStateOf(3)
    private lateinit var countryList: List<Country>
    private var buttonText by mutableStateOf("Submit")
    private var question  by  mutableStateOf(
        Question(
            listOf(Country("UK", "UNITED-KINGDOM")),
            listOf(Country("UK", "UNITED-KINGDOM"))
        )
    )
    private var guesses by mutableStateOf(List(3) { "" })
    private var checkValueGuessed = "Wrong"

    private var enabling by  mutableStateOf(List(3) { mutableStateOf(true) })
    private val colors by   mutableStateOf(List(3) { mutableStateOf(Color.Black) })
    private val colorsIndex by   mutableStateOf(List(3) { mutableStateOf(0) })
    private var correctAnswersCount = 0
    private var  snackBarVisible  by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countryList = CountryFunctions.loadCountries(context = this@AdvanceLevel)
        question = generateQuestion()

        setContent {
            GuessCountryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdvanceLevelScreen()
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AdvanceLevelScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(text = "Score: $score", modifier = Modifier.padding(end = 16.dp))
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


        //    var question by remember { mutableStateOf(generateQuestion()) }
            correctCountries = question.correctFlags.toMutableList()
           // val (guesses, setGuesses) = remember { mutableStateOf(List(3) { "" }) }
        //    val enabling = remember { List(3) { mutableStateOf(true) } }
         //   val colors = remember { List(3) { mutableStateOf(Color.White) } }

            question.flags.forEachIndexed { index, flag ->
                Image(
                    painter = painterResource(
                        id = getFlagResourceId(
                            flag.code,
                            this@AdvanceLevel
                        )
                    ),
                    contentDescription = flag.name,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { /* handle flag click if needed */ }
                )
                Spacer(modifier = Modifier.width(16.dp))

                TextField(
                    value = guesses[index],
                    onValueChange = { newValue ->
                        guesses = guesses.toMutableList().apply { set(index, newValue) }
                    },
                    enabled = enabling[index].value,
                    modifier = Modifier.width(100.dp),
                    colors = TextFieldDefaults.textFieldColors(textColor = colors[index].value)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.DarkGray, thickness = 1.dp)
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (buttonText == "Submit") {

                        guesses.forEachIndexed { index, guess ->
                            if (guess.equals(correctCountries[index].name, ignoreCase = true) &&  enabling[index].value) {
                                correctAnswers.add(guess)
                                enabling[index].value = false
                                colors[index].value = Color.Green
                                colorsIndex[index].value = 1
                                correctAnswersCount++
                                score += correctAnswersCount
                            } else {
                                colors[index].value = Color.Red
                                colorsIndex[index].value = 2
                            }
                        }


                        if (correctAnswersCount < 3 && attemptsRemaining > 0) {
                            attemptsRemaining--
                            if(attemptsRemaining==0){
                                buttonText = "Next"
                                checkValueGuessed = "WRONG"
                                snackBarVisible  = true

                            }
                        } else if(correctAnswersCount == 3){
                            buttonText = "Next"
                            checkValueGuessed = "CORRECT"
                            snackBarVisible  = true
                        }
                    } else {
                        snackBarVisible  = false
                        question = generateQuestion()
                        reset()
                        enabling.forEach {
                            it.value = true
                        }
                        colors.forEach {
                            it.value = Color.Black
                        }

                        buttonText = "Submit"
                    }
                }
            ) {
                Text(text = buttonText)
            }

            Spacer(modifier = Modifier.height(10.dp))
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
                        if (checkValueGuessed == "WRONG" && correctAnswersCount<3) {
                            // Display wrong message in red color with black background
                            Text(text = "Wrong!", color = Color.Red, modifier = Modifier.background(Color.White))

                            // Display correct country names in blue color with white background
                            correctAnswers.forEach { correctAnswer ->
                                Text(text = "Correct country: $correctAnswer", color = Color.Blue, modifier = Modifier.background(Color.White))
                            }
                        } else {
                            // Display correct message in green color with blue country name
                            Text(text = "Correct", color = Color.Green, modifier = Modifier.background(Color.White))

                        }
                    }
                }

            }

        }
    }
}

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray("guesses", guesses.toTypedArray())
        outState.putBooleanArray("enabling", enabling.map { it.value }.toBooleanArray())
        outState.putInt("score", score)
        outState.putBoolean("snackBarVisible",  snackBarVisible )
        outState.putInt("attemptsRemaining", attemptsRemaining)
        outState.putString("buttonText", buttonText)
        outState.putString("checkValueGuessed", checkValueGuessed)
        outState.putInt("correctAnswersCount", correctAnswersCount)
        outState.putInt("flag1", countryList.indexOf(question.flags[0]) )
        outState.putInt("flag2", countryList.indexOf(question.flags[1]) )
        outState.putInt("flag3", countryList.indexOf(question.flags[2]) )
        outState.putInt("correctFlag1", countryList.indexOf(question.correctFlags[0]) )
        outState.putInt("correctFlag2", countryList.indexOf(question.correctFlags[1]) )
        outState.putInt("correctFlag3", countryList.indexOf(question.correctFlags[2]) )

        outState.putInt("textBox1", colorsIndex[0].value )
        outState.putInt("textBox2", colorsIndex[1].value )
        outState.putInt("textBox3", colorsIndex[2].value )

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the saved state here if needed
       // countryList = CountryFunctions.loadCountries(context = this@AdvanceLevel)
        val savedGuesses = savedInstanceState.getStringArray("guesses")
        savedGuesses?.let { guesses = it.toList() }

        val savedEnabling = savedInstanceState.getBooleanArray("enabling")
        savedEnabling?.forEachIndexed { index, value ->
            enabling[index].value = value
            when(colorsIndex[index].value){
              1 ->  colors[index].value = Color.Red
              2 ->  colors[index].value = Color.Green
              else ->  colors[index].value = Color.Black
            }
        }

        score = savedInstanceState.getInt("score", 0)
        checkValueGuessed = savedInstanceState.getString("checkValueGuessed", "WRONG")
        snackBarVisible = savedInstanceState.getBoolean("snackBarVisible", false)
        attemptsRemaining = savedInstanceState.getInt("attemptsRemaining", 3)
        buttonText = savedInstanceState.getString("buttonText", "Submit")
        correctAnswersCount = savedInstanceState.getInt("correctAnswersCount", 0)

        val flag1Index = savedInstanceState.getInt("flag1", -1)
        val flag2Index = savedInstanceState.getInt("flag2", -1)
        val flag3Index = savedInstanceState.getInt("flag3", -1)

        val correctFlag1Index = savedInstanceState.getInt("correctFlag1", -1)
        val correctFlag2Index = savedInstanceState.getInt("correctFlag2", -1)
        val correctFlag3Index = savedInstanceState.getInt("correctFlag3", -1)
        val flags = listOf(countryList[flag1Index],countryList[flag2Index],countryList[flag3Index])
        val correctFlags = listOf(countryList[correctFlag1Index],countryList[correctFlag2Index],countryList[correctFlag3Index])
        question = Question(correctFlags,flags)
    }


    private fun reset() {
        correctAnswers.clear()
        correctAnswersCount = 0
        attemptsRemaining = 3
    }

    private fun generateQuestion(): Question {
        val correctAnswers = mutableListOf<Country>()
        val selectedCountries = mutableSetOf<Int>()
        while (correctAnswers.size < 3) {
            val randomIndex = (0 until 255).random()
            if (!selectedCountries.contains(randomIndex)) {
                selectedCountries.add(randomIndex)
                correctAnswers.add(countryList[randomIndex])
            }
        }
        correctAnswers.shuffle()
        return Question(correctAnswers, correctAnswers.map { it.copy() })
    }

    data class Question(
        val correctFlags: List<Country>,
        val flags: List<Country>
    )
}
