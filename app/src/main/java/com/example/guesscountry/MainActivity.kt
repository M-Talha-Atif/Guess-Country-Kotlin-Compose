package com.example.guesscountry

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.guesscountry.CountryFunctions.Companion.switchValueButton
import com.example.guesscountry.ui.theme.GuessCountryTheme

class MainActivity : ComponentActivity() {
    private var switchValue by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuessCountryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Button(onClick = {
                                // Launch GuessCountry activity
                                startActivity(Intent(this@MainActivity, GuessCountry::class.java))
                            }) {
                                Text("Guess the Country")
                            }
                            Button(onClick = {
                                startActivity(Intent(this@MainActivity, GuessHints::class.java))
                            }) {
                                Text("Guess-Hints")
                            }
                            Button(onClick = {       startActivity(Intent(this@MainActivity, GuessFlag::class.java))}) {
                                Text("Guess the Flag")

                            }
                            Button(onClick = {
                                startActivity(Intent(this@MainActivity, AdvanceLevel::class.java))
                            }) {
                                Text("Advanced Level")
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Switch Button")
                                Switch(
                                    checked = switchValue,
                                    onCheckedChange = { isChecked ->
                                        switchValue = isChecked
                                        switchValueButton = isChecked

                                    },
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("switchValue", switchValue )
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        switchValue = savedInstanceState.getBoolean("switchValue")
    }
}


