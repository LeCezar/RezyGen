import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import di.getSharedModules
import di.modelModule
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinApplication
import org.koin.dsl.koinApplication
import residencyexamgen.composeapp.generated.resources.Res
import residencyexamgen.composeapp.generated.resources.app_name
import theme.RezyGenTheme
import ui.conversation.ConversationScreen

@Composable
fun AiChatApp() {
    KoinApplication(
        application = {
            koinApplication {
                modules(getSharedModules() + modelModule)
            }
        }
    ) {
        RezyGenTheme {
            Scaffold(
                topBar = {
                    Box(Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.padding(20.dp),
                            text = stringResource(Res.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            ) {
                Box(Modifier.padding(it).fillMaxSize()) {
                    Navigator(
                        screen = ConversationScreen()
                    ) {
                        SlideTransition(
                            navigator = it,
                            content = { screen -> screen.Content() }
                        )
                    }
                }
            }
        }
    }
}