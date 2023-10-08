import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.PageRepository
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material.TextField
import androidx.compose.ui.input.key.*
import ui.WindowController
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.event.KeyEvent

@Composable
fun InitialScreen(windowController: WindowController) {
    val pages by remember { mutableStateOf(PageRepository.findAllLinks()) }
    val coroutineScope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    val recorder = Recorder()

    val positions = remember { mutableStateListOf<Offset>() }
    val density = LocalDensity.current

    val ahiruchan: String = "最近のプロジェクトの進行はいかがですか。"
    val ahiruchan_2: String = "気になるものがあれば押してね。"
    val ahiruchan_3: String = "研究頑張ろう。"


    val messageToShow = remember { mutableStateOf(ahiruchan) }

    // 10秒後にメッセージを更新する
    LaunchedEffect(key1 = Unit) {
        delay(10000)  // 10秒待機
        messageToShow.value = ahiruchan_2
        delay(10000)  // 10秒待機
        messageToShow.value = ahiruchan_3
    }

    Box(
        contentAlignment = Alignment.TopStart,
        //modifier = Modifier.padding( )
    ) {

        Column(
            modifier = Modifier.fillMaxSize().background(Color.White),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Divider(color = Color.Gray, thickness = 0.5.dp)
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically // Rowの中の要素を垂直方向の中央に配置
            ) {
                Spacer(modifier = Modifier.width(40.dp))

                Button(
                    onClick = {
                        //録音部分
                        if (!isRecording) {
                            coroutineScope.launch {
                                windowController.openNewPageWindow("firstMemo")
                                delay(1) // 仮の遅延
                                println("First async action executed!")
                            }


                            coroutineScope.launch {

                                isRecording = true
                                recorder.startRecording()


                                delay(500)  // 仮の遅延
                                println("Second async action executed!")
                            }
                        } else {
                            isRecording = false
                            recorder.stopRecording("output.wav")
                            runBlocking {
                                //whisper()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,     // ボタンの背景色
                        contentColor = Color.Black  // ボタン内のテキストやアイコンの色
                    )
                ) {
                    if (!isRecording) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "アイコンの説明")
                    } else {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "アイコンの説明")
                    }
                }
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = "今日あったことをあひるちゃんに話そう",
                    //modifier = Modifier.heightIn(min = 48.dp), // ここで高さを設定
                    style = MaterialTheme.typography.button
                )
            }
            Divider(color = Color.Gray, thickness = 2.dp)

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically // Rowの中の要素を垂直方向の中央に配置
            ) {
                Spacer(modifier = Modifier.width(15.dp))

            }

            Box(
                modifier = Modifier.padding(start = 50.dp, top = 10.dp, end = 400.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically, // Rowの中の要素を垂直方向の中央に配置,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column() {

                        Text(
                            text = "お気に入り",
                            //modifier = Modifier.heightIn(min = 48.dp), // ここで高さを設定
                            style = MaterialTheme.typography.button,
                            fontSize = 16.sp
                        )
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            val dummy_okiniiri = listOf("タンパク質A", "タンパク質B", "DNA-1", "mouse", "PCR")
                            items(dummy_okiniiri) { page ->
                                Text(
                                    text = page,
                                    fontSize = 14.sp,
                                    color = Color(149, 149, 149),
                                    //fontWeight = FontWeight.Bold, // 太字
                                    textDecoration = TextDecoration.Underline, // アンダーライン
                                    modifier = Modifier.clickable {
                                        // ここでクリックされたときの処理を書く
                                        windowController.openNewPageWindow("${page}")

                                    }.padding(vertical = 8.dp)

                                )

                            }

                        }
                        val state = manageTextAndListState()

                        TextBoxUI(state.textState, state.updateTextState, state.addText)

                        state.stringList.forEach { item ->
                            Text(item)
                        }

                    }


                    Column() {

                        Text(
                            text = "日付",
                            //modifier = Modifier.heightIn(min = 48.dp), // ここで高さを設定
                            style = MaterialTheme.typography.button,
                            fontSize = 16.sp
                        )
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            val dummy_hizuke = listOf(
                                "2023-09-11(月)",
                                "2023-09-12(火)",
                                "2023-09-13(水)",
                                "2023-09-14(木)",
                                "2023-09-15(金)",
                                "2023-09-16(土)",
                                "2023-09-17(日)"
                            )
                            items(dummy_hizuke) { page ->
                                Text(
                                    text = page,
                                    fontSize = 14.sp,
                                    color = Color(149, 149, 149),
                                    //fontWeight = FontWeight.Bold, // 太字
                                    textDecoration = TextDecoration.Underline, // アンダーライン
                                    modifier = Modifier.clickable {
                                        // ここでクリックされたときの処理を書く
                                        windowController.openNewPageWindow("${page}")

                                    }.padding(vertical = 8.dp)

                                )

                            }
                        }
                    }
                }

            }










            Spacer(modifier = Modifier.height(16.dp)) // Optional: You can add a spacer for better spacing


        }
    }

    Box {
        Box(modifier = Modifier.fillMaxSize())

        Box(
            Modifier.matchParentSize()
                .padding(start = 500.dp, top = 200.dp, end = 0.dp, bottom = 200.dp)
                .background(Color.White)
        ) {
            Image(
                painter = painterResource("e1543_1.png"),
                contentDescription = "Sample",
                modifier = Modifier.size(500.dp).align(Alignment.Center)
            )
        }
        Box(
            Modifier.matchParentSize()
                .padding(start = 540.dp, top = 200.dp, end = 40.dp, bottom = 220.dp)

        ) {
            Text(
                text = messageToShow.value,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )

        }

        Box(
            Modifier.matchParentSize()
                .padding(top = 380.dp, start = 500.dp, bottom = 10.dp)

        ) {
            Image(
                painter = painterResource("duck.jpg"),
                contentDescription = "Sample",
                modifier = Modifier.fillMaxHeight().align(Alignment.BottomCenter)
            )
        }


    }


}

data class TextAndListState(
    val textState: TextFieldValue,
    val stringList: List<String>,
    val updateTextState: (TextFieldValue) -> Unit,
    val addText: () -> Unit
)


@Composable
fun manageTextAndListState(): TextAndListState {

    var textState by remember { mutableStateOf(TextFieldValue()) }
    var stringList by remember { mutableStateOf(listOf<String>()) }

    fun updateTextState(newValue: TextFieldValue) {
        textState = newValue
    }

    fun addText() {
        stringList = stringList + textState.text
        textState = TextFieldValue()
    }
    return TextAndListState(textState, stringList, ::updateTextState, ::addText)
}





@Composable
fun TextBoxUI(
    textState: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onAddButtonClick: () -> Unit
) {
    Column {
        TextField(
            textState,
            onValueChange = onTextChange,
            label = { Text("テキストを入力してください") },
            modifier = Modifier.onKeyEvent {
                event ->
                if (event.key == Key(0x0a) ) { // 0x0A is the keycode for Enter
                    print("Hello")
                    onAddButtonClick()
                    true

                } else {
                    println("--------")
                    println(event.key)
                    println(event.type)
                    false


                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onAddButtonClick) {
            Text("追加")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

}














