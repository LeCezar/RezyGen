package theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RezyGenTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}