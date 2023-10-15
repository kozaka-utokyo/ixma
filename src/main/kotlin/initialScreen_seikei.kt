import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import jdk.jfr.Recording
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File


data class TextAndListState(
    val textState: TextFieldValue,
    val stringList: List<String>,
    val updateTextState: (TextFieldValue) -> Unit,
    val addText: () -> Unit
)

object FileUtils {
    private val memoFile = File("src/main/kotlin/data/favorite_memo_list")

    fun saveMemos(memos: List<String>) {
        memoFile.writeText(memos.joinToString("\n"))
    }

    fun loadMemos(): List<String> {
        return if (memoFile.exists()) {
            memoFile.readLines()
        } else {
            emptyList()
        }
    }
}

@Composable
fun Message_display(): MutableState<String> {
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
    return messageToShow
}


//関数定義

@Composable
fun MemoApp(onAddButtonClick: () -> Unit) {//入力をする。(いれたものは、favorite_memo_listに入る。)
    var textState by remember { mutableStateOf(TextFieldValue()) }
    var memoList by remember { mutableStateOf(FileUtils.loadMemos()) }

    Column(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.9f),horizontalAlignment = Alignment.Start) {
        Box(
            modifier = Modifier.height(50.dp)
        ) {
            Column() {
                TextField(
                    value = textState,
                    onValueChange = { newValue -> textState = newValue },
                    placeholder = { Text("こちらに内容を入力...") },
                    singleLine = true,
                    modifier = Modifier.onKeyEvent { event ->
                        if (event.key == Key(0x0a)) { // 0x0A is the keycode for Enter
                            print("hello")
                            onAddButtonClick()
                            true

                        } else {
                            println("--------")
                            println(event.key)
                            println(event.type)
                            false


                        }
                    }
                    //modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(onClick = {
            memoList = memoList + textState.text
            FileUtils.saveMemos(memoList)
            textState = TextFieldValue()  // テキストフィールドをクリア

        }) {
            Text("追加")

        }
    }
}
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
fun Search(windowController: WindowController) {//入力をする。(いれたものは、favorite_memo_listに入る。)
    var textState by remember { mutableStateOf(TextFieldValue()) }
    var memoList by remember { mutableStateOf(FileUtils.loadMemos()) }

    Column(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.9f),horizontalAlignment = Alignment.Start) {
        Box(
            modifier = Modifier.height(50.dp)
        ) {
            Column() {
                TextField(
                    value = textState,
                    onValueChange = { newValue -> textState = newValue },
                    placeholder = { Text("こちらに内容を入力...") },
                    singleLine = true,
                    //modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(onClick = {
            memoList = memoList + textState.text
            FileUtils.saveMemos(memoList)
            windowController.openNewPageWindow(textState.text)
            textState = TextFieldValue()  // テキストフィールドをクリア

//            windowController.openNewPageWindow("${page}")
//            println("${page}")
        }) {
            Text("検索")
        }
    }
}


@Composable//リストを表示(LazyColumnで)
fun DisplayMemosFromFile(title: String, filePath: String, windowController: WindowController) {

    // ファイルから行を読み込み
    val memos: List<String> = try {
        File(filePath).readLines()
    } catch (e: Exception) {
        listOf("ファイルの読み込みに失敗しました: $e")
    }
    Column() {
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))


        Text(
            text = title,
            //modifier = Modifier.heightIn(min = 48.dp), // ここで高さを設定
            style = MaterialTheme.typography.button, fontSize = 16.sp
        )


        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
        LazyColumn(modifier = Modifier.fillMaxHeight(0.69f)) {

            items(memos) { page ->
                Text(
                    text = page, fontSize = 14.sp, color = Color(149, 149, 149),
                    //fontWeight = FontWeight.Bold, // 太字
                    textDecoration = TextDecoration.Underline, // アンダーライン
                    modifier = Modifier.clickable {
                        // ここでクリックされたときの処理を書く
                        windowController.openNewPageWindow("${page}")
                        println("${page}")

                    }.padding(vertical = 8.dp)

                )

            }

        }
    }
}


@Composable
fun rokuon(windowController: WindowController, isRecording: Boolean) {
    //var isRecording by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val recorder = Recorder()
    var isRecording_now = isRecording

    //録音部分
    if (!isRecording) {
        coroutineScope.launch {
            windowController.openNewPageWindow("firstMemo")
            delay(1) // 仮の遅延
            println("First async action executed!")
        }


        coroutineScope.launch {

            isRecording_now = true
            recorder.startRecording()


            delay(500)  // 仮の遅延
            println("Second async action executed!")
        }
    } else {
        isRecording_now = false
        recorder.stopRecording("output.wav")
        runBlocking {
            //whisper()
        }
    }

}

@Composable
fun InitialScreen(windowController: WindowController) {

    val coroutineScope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    val recorder = Recorder()
    val messageToShow = Message_display()
    val state = manageTextAndListState()



    Box(//第一のbox
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight(0.1f),
                //modifier = Modifier.padding( )
            ) {
//
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.Center,//これで、中央にいれるかを決定する。
                    verticalAlignment = Alignment.CenterVertically, // Rowの中の要素を垂直方向の中央に配置

                ) {
                    Button(
                        onClick = {
                            //録音部分(rokuonで処理できず)
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
                    )//buttonの内容
                    {
                        if (!isRecording) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "アイコンの説明")
                        } else {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "アイコンの説明")
                        }
                    }//buttonの操作


                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = "今日あったことをあひるちゃんに話そう",
                        //modifier = Modifier.heightIn(min = 48.dp), // ここで高さを設定
                        style = MaterialTheme.typography.button
                    )
                }
            }
            Divider(color = Color(120,120,120), thickness = 2.dp)//今日のふりかえりモード終了

            Row() {
                Box(
                    modifier = Modifier.fillMaxWidth(0.02f),
                )
                Box(
                    modifier = Modifier.fillMaxWidth(0.35f),
                ) {

                    Column() {
                        DisplayMemosFromFile(
                            "お気に入り",
                            "src/main/kotlin/data/favorite_memo_list",
                            windowController = windowController
                        )
                        MemoApp(state.addText)
                    }
                }


                Box(
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(0.539f),
                ) {
                    Column() {


                        DisplayMemosFromFile(
                            "日付",
                            "src/main/kotlin/data/data_memo",
                            windowController = windowController
                        )
                        Search(windowController = windowController)
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                Modifier.matchParentSize().padding(start = 500.dp, top = 200.dp, end = 0.dp, bottom = 200.dp)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource("e1543_1.png"),
                    contentDescription = "Sample",
                    modifier = Modifier.size(500.dp).align(Alignment.Center)
                )
            }
            Box(
                Modifier.matchParentSize().padding(start = 540.dp, top = 200.dp, end = 40.dp, bottom = 220.dp)

            ) {
                Text(
                    text = messageToShow.value, fontSize = 18.sp, modifier = Modifier.align(Alignment.Center)
                )

            }

            Box(
                Modifier.matchParentSize().padding(top = 380.dp, start = 500.dp, bottom = 10.dp)

            ) {
                Image(
                    painter = painterResource("duck.jpg"),
                    contentDescription = "Sample",
                    modifier = Modifier.fillMaxHeight().align(Alignment.BottomCenter)
                )
            }
        }
    }

}










