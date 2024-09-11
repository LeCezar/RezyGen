import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import residencyexamgen.composeapp.generated.resources.Res
import residencyexamgen.composeapp.generated.resources.compose_multiplatform
import theme.RezyGenTheme


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun App() {
    RezyGenTheme {
        BottomSheetNavigator { bottomNavigator ->
            LaunchedEffect(Unit) {
                bottomNavigator.show(FirstScreenDest("3"))
            }
            Navigator(
                screen = FirstScreenDest("1"),
                disposeBehavior = NavigatorDisposeBehavior()
            ) { nav ->
                LaunchedEffect(nav.lastItem) {
                    println("Current Screen: ${nav.lastItem}")
                    println("Current stack: ${nav.items.joinToString(separator = " -> ")}")
                }
                Scaffold(
                    topBar = {
                        Row(Modifier.fillMaxWidth().height(56.dp)) {
                            Image(
                                modifier = Modifier.size(48.dp).align(Alignment.CenterVertically)
                                    .clickable {
                                        nav.pop()
                                    },
                                painter = painterResource(Res.drawable.compose_multiplatform),
                                contentDescription = null
                            )
                        }
                    },
                    content = {
                        Box(Modifier.padding(it)) {
                            SlideTransition(
                                navigator = nav,
                            ) { currentScreen ->
                                currentScreen.Content()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FooScreen() {
    var showContent by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { "Greeting" }
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }
    }
}

class FirstScreenDest(private val userId: String) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        FirstScreen(model = rememberScreenModel(tag = userId) { FirstScreenModel(userId) })
    }
}

sealed class State {
    object Loading : State()
    data class Success(val userName: String) : State()
}

class FirstScreenModel(private val userId: String) : ScreenModel {
    val state = MutableStateFlow<State>(State.Loading)

    fun loadUser() {
        screenModelScope.launch {
            delay(1000)
            state.update { State.Success("UserName: Gigel#$userId") }
        }
    }

    override fun onDispose() {
        println("FirstScreenModel disposed")
    }
}

@Composable
fun FirstScreen(model: FirstScreenModel) {
    val localNavigator = LocalNavigator.currentOrThrow
    val state by model.state.collectAsState()

    LaunchedEffect(model) {
        model.loadUser()
    }

    Column(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(top = 36.dp).align(Alignment.CenterHorizontally)
                .clickable {
                    localNavigator.push(SecondScreenDest())
                },
            text = "First Screen | User: ${
                when (state) {
                    is State.Loading -> "Loading..."
                    is State.Success -> (state as State.Success).userName
                }
            }",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

class SecondScreenDest : Screen {
    @Composable
    override fun Content() {
        SecondScreen()
    }
}

@Composable
fun SecondScreen() {
    val localNavigator = LocalNavigator.currentOrThrow

    Column(Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(top = 36.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    localNavigator.push(FirstScreenDest("2"))
                },
            text = "SecondScreen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}