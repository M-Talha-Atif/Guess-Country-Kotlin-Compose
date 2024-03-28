package com.example.guesscountry

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class CountryFunctions {
    companion object {
        var switchValueButton = true
        fun loadCountries(context: Context): List<Country> {
            val countries = mutableListOf<Country>()
            try {
                val inputStream = context.assets.open("countries.json")
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = StringBuilder()

                var line: String? = reader.readLine()
                while (line != null) {
                    jsonString.append(line)
                    line = reader.readLine()
                }

                val jsonObject = JSONObject(jsonString.toString())
                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = jsonObject.getString(key)
                    countries.add(Country(key, getCountryName(value)))
                }

                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return countries
        }
        fun getFlagResourceId(countryCode: String, context: Context): Int {
            val resourceName = countryCode.lowercase() // Assuming country codes match filenames
            return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        }
        private fun getCountryName(country : String) : String{
            var countryName = ""
            for (char in country){
                if(char.isLetter())
                    countryName += char

            }
            return countryName
        }
    }

}