package com.sixbeeps.uniquity.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sixbeeps.uniquity.TextUtility
import com.sixbeeps.uniquity.ui.theme.UniquityTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    enum class Page(
        val label: String,
        val icon: ImageVector
    ) {
        TEST("Tester", Icons.Default.Home),
        FAVORITES("Favorites", Icons.Default.Star),
        SEARCH("Search", Icons.Default.Search),
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
        val startPage = Page.FAVORITES
        var currentPage by rememberSaveable { mutableIntStateOf(startPage.ordinal) }
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = Modifier.Companion.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
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
            AppNavHost(
                navController,
                startPage,
                snackbarHostState,
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize())
        }
    }

    @Composable
    fun AppNavHost(
        navController: NavHostController,
        startPage: Page,
        snackbarHostState: SnackbarHostState,
        modifier: Modifier = Modifier
    ) {
        val viewModelFactory = (LocalContext.current.applicationContext as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory

        NavHost(navController, startPage.name, modifier) {
            Page.entries.forEach { page ->
                composable(page.name) {
                    when (page) {
                        Page.TEST -> TestPageLayout(modifier)
                        Page.FAVORITES -> {
                            if (viewModelFactory == null) {
                                println("ViewModelFactory is null, are you running on a real device?")
                                return@composable
                            }

                            val viewModel: FavoritesViewModel = viewModel (factory = viewModelFactory)
                            FavoritesPageLayout(Modifier.fillMaxHeight(), viewModel)
                        }
                        Page.SEARCH -> {
                            if (viewModelFactory == null) {
                                println("ViewModelFactory is null, are you running on a real device?")
                                return@composable
                            }

                            val viewModel: SearchViewModel = viewModel (factory = viewModelFactory)
                            SearchPageLayout(Modifier.fillMaxHeight(), viewModel, snackbarHostState)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TestPageLayout(modifier: Modifier = Modifier) {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                10.dp,
                Alignment.Companion.CenterVertically
            ),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            MySimpleTextField()
            Button(
                { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) },
                content = {
                    Row(
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Settings, "Settings")
                        Text("Open Keyboard Settings")
                    }
                }
            )
        }
    }

    @Composable
    fun FavoritesPageLayout(
        modifier: Modifier = Modifier,
        viewModel: FavoritesViewModel
    ) {
        val favorites by viewModel.favorites.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.loadFavorites()
        }

        LazyColumn (
            modifier
                .fillMaxSize()
                .padding(5.dp, 0f.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            contentPadding = PaddingValues(0.dp, 15.dp, 0.dp, 15.dp)
        ) {
            if (favorites.isEmpty()) {
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(5.dp, 15.dp),

                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "No favorites yet!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "To favorite an item, press and hold on a character in the keyboard",
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(favorites.size) { index ->
                    Card (
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    ) {
                        val codepoint = favorites[index].codepoint
                        val scalar = codepoint.toInt(16)
                        val text = TextUtility.codepointToString(scalar)
                        Row (
                            Modifier
                                .fillMaxWidth()
                                .padding(15.dp, 5.dp)
                                .wrapContentHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            Text(
                                text,
                                fontSize = 48.sp
                            )
                            Column (
                                Modifier
                                    .weight(1f)
                                    .padding(end = 1.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    favorites[index].name ?: "(Unnamed)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    codepoint,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            IconButton(
                                onClick = {
                                    viewModel.removeFromFavorites(favorites[index].id!!)
                                }
                            ) {
                                Icon(Icons.Default.Delete, "Delete Favorite")
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SearchPageLayout(
        modifier: Modifier = Modifier,
        viewModel: SearchViewModel,
        snackbarHostState: SnackbarHostState
    ) {
        val results by viewModel.results.collectAsState()
        val loading by viewModel.loading.collectAsState()
        var query by rememberSaveable { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        LaunchedEffect(query) {
            viewModel.search(query)
        }

        Column (
            modifier
                .fillMaxSize()
                .padding(5.dp, 0f.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = query,
                onValueChange = { newValue -> query = newValue },
                label = { Text("Search for characters...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") }
            )
            LazyColumn (
                Modifier,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Companion.CenterHorizontally,
                contentPadding = PaddingValues(0.dp, 15.dp, 0.dp, 15.dp)
            ) {
                if (results.isEmpty()) {
                    if (loading) {
                        item {
                            CircularProgressIndicator()
                        }
                    } else if (query.isNotEmpty()) {
                        item {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(5.dp, 15.dp),

                                verticalArrangement = Arrangement.spacedBy(15.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    "No results",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        item {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(5.dp, 15.dp),

                                verticalArrangement = Arrangement.spacedBy(15.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    "Type something to search",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    items(results.size) { index ->
                        Card (
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                        ) {
                            val codepoint = results[index].codepoint
                            val scalar = codepoint.toInt(16)
                            val text = TextUtility.codepointToString(scalar)
                            Row (
                                Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp, 5.dp)
                                    .wrapContentHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                Text(
                                    text,
                                    fontSize = 48.sp
                                )
                                Column (
                                    Modifier
                                        .weight(1f)
                                        .padding(end = 1.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        results[index].name ?: "(Unnamed)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        codepoint,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.addToFavorites(codepoint) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (it) "Added $text to favorites" else "Failed to add $text to favorites"
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Star, "Favorite")
                                }
                            }
                        }
                    }
                }
            }
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