package com.sixbeeps.uniquity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sixbeeps.uniquity.data.AppDatabase
import com.sixbeeps.uniquity.ui.theme.UniquityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            UniquityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MySimpleTextField(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun MySimpleTextField(modifier: Modifier = Modifier) {
        // State to hold the text input
        var text by remember { mutableStateOf("") }

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText -> text = newText },
                label = { Text("Enter text") }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        UniquityTheme {
            MySimpleTextField()
        }
    }
}