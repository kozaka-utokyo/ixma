import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.PageRepository
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.RenderWindows
import ui.WindowController
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight

@Composable
fun InitialScreen(windowController: WindowController){
    val pages by remember { mutableStateOf(PageRepository.findAllLinks()) }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        recordingApp(windowController)
        Button(onClick = { windowController.openNewPageWindow("firstMemo") }) {
            Text("Open FirstMemo")
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(pages) { page ->
                Text(text = page.link,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold, // 太字
                    textDecoration = TextDecoration.Underline, // アンダーライン
                    modifier = Modifier.clickable {
                    // ここでクリックされたときの処理を書く
                    windowController.openNewPageWindow("${page.link}")
                }.padding(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Optional: You can add a spacer for better spacing
        Image(
            painter = painterResource("duck.jpg"),
            contentDescription = "Sample",
            modifier = Modifier.size(100.dp)
        )
    }
}