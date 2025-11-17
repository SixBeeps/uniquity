package com.sixbeeps.uniquity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sixbeeps.uniquity.ui.theme.UniquityTheme

class MainActivity : ComponentActivity() {
    enum class Page(
        val label: String,
        val icon: ImageVector
    ) {
        TEST("Tester", Icons.Default.Home),
        FAVORITES("Favorites", Icons.Default.Star)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            UniquityTheme {
                MainAppLayout()
            }
        }
    }

    @Composable
    fun MainAppLayout() {
        val navController = rememberNavController()
        val startPage = Page.TEST
        var currentPage by rememberSaveable { mutableIntStateOf(startPage.ordinal) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar (windowInsets = NavigationBarDefaults.windowInsets) {
                    Page.entries.forEachIndexed { index, page ->
                        NavigationBarItem(
                            selected = currentPage == index,
                            onClick = {
                                currentPage = index
                                navController.navigate(page.name)
                            },
                            label = { Text(page.name) },
                            icon = {
                                Icon(page.icon, page.label)
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            AppNavHost(navController, startPage, Modifier.padding(innerPadding))
        }
    }

    @Composable
    fun AppNavHost(
        navController: NavHostController,
        startPage: Page,
        modifier: Modifier = Modifier
    ) {
        NavHost(navController, startPage.name, modifier) {
            Page.entries.forEach { page ->
                composable(page.name) {
                    when (page) {
                        Page.TEST -> TestPageLayout(modifier)
                        Page.FAVORITES -> FavoritesPageLayout(modifier)
                    }
                }
            }
        }
    }

    @Composable
    fun TestPageLayout(modifier: Modifier = Modifier) {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dp(10f), Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MySimpleTextField()
            Button(
                { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) },
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dp(10f))
                    ) {
                        Icon(Icons.Default.Settings, "Settings")
                        Text("Open Keyboard Settings")
                    }
                }
            )
        }
    }

    @Composable
    fun FavoritesPageLayout(modifier: Modifier = Modifier) {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dp(10f), Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TODO: Implement favorites page")
        }
    }

    @Composable
    fun MySimpleTextField() {
        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            onValueChange = { newText -> text = newText },
            label = { Text("Try typing here...") }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        UniquityTheme {
            MainAppLayout()
        }
    }
}