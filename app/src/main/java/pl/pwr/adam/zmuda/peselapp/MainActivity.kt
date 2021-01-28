package pl.pwr.adam.zmuda.peselapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                View()
            }
        }
    }

    @Composable
    fun View() {
        val peselInvalid = savedInstanceState { true }
        val date = savedInstanceState {""}
        val sex = savedInstanceState {""}
        val controlValueString = savedInstanceState {""}

        Column (
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ){
            PeselInput(onValueChange = {
                peselInvalid.value = !isPeselValid(it)

                if (!peselInvalid.value) {
                    date.value = getDateFromPesel(it)
                    sex.value = getSexFromPesel(it)
                    controlValueString.value = isChecksumCorrect(it)
                }
            })

            Column(modifier = Modifier.height(200.dp)) {
                if (peselInvalid.value)
                {
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(
                        "Pesel is in invalid format!",
                        color = Color.Red,
                        style = TextStyle(fontSize = TextUnit.Sp(22))
                    )
                }
                else
                {
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text("Birth date: ${date.value}", style = TextStyle(fontSize = TextUnit.Sp(22)))

                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text("Sex: ${sex.value}", style = TextStyle(fontSize = TextUnit.Sp(22)))

                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text("Check sum: ${controlValueString.value}", style = TextStyle(fontSize = TextUnit.Sp(22)))
                }
            }
        }
    }

    @Composable
    fun PeselInput(onValueChange: (String) -> Unit) {
        val pesel = savedInstanceState { "" }

        Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    "Pesel:",
                    style = TextStyle(fontSize = TextUnit.Sp(26)),
                    modifier = Modifier.padding(end=5.dp)
            )
            TextField(
                    value = pesel.value,
                    onValueChange = { newValue ->
                        pesel.value = newValue.take(11)
                        onValueChange(pesel.value)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = TextUnit.Sp(26)),
            )

        }
    }

    private fun isPeselValid(pesel : String) : Boolean {
        return Regex("[0-9]{11}").matches(pesel)
    }

    private fun getDateFromPesel(pesel : String) : String {
        val day = pesel.substring(4,6)
        val month = pesel.substring(2, 4).toInt()
        val yearSecondPart = pesel.take(2)
        val yearFirstPart = when(month) {
            in 81..92 -> "18"
            in 1..12 -> "19"
            in 21..32 -> "20"
            in 41..52 -> "21"
            in 61..72 -> "22"
            else -> null
        }

        if (yearFirstPart == null)
            return "Could not calculate date"

        return "${yearFirstPart}${yearSecondPart} - ${month} - ${day}"
    }

    private fun getSexFromPesel(pesel : String) : String {
        val number = pesel.substring(9, 10).toInt()

        if (number % 2 == 0)
            return "Female"
        else
            return "Male"
    }

    private fun isChecksumCorrect(pesel : String) : String {
        val weights = arrayOf(1, 3, 7, 9, 1, 3, 7, 9, 1, 3, 1)
        val numbers = pesel.toCharArray().map { s -> Character.getNumericValue(s) }

        val products = weights.withIndex().map { i -> i.value * numbers[i.index] }
        val m = products.sum() % 10

        if (m == 0)
            return "Correct"
        else
            return "Incorrect"
    }
}




