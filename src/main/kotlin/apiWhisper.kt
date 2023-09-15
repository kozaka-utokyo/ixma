import javax.sound.sampled.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.system.exitProcess
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import data.PageRepository
import java.io.File
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.HttpClient
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.forms.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import model.Page
import ui.WindowController
import java.time.LocalDate
import java.time.LocalDateTime

class Recorder {

    private val audioFormat = AudioFormat(44100.0f, 16, 2, true, true)
    private val byteArrayOutputStream = ByteArrayOutputStream()
    private var audioInputStream: AudioInputStream? = null
    private var targetDataLine: TargetDataLine? = null

    fun startRecording() {
        val dataLineInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)
        targetDataLine = AudioSystem.getLine(dataLineInfo) as TargetDataLine?
        targetDataLine!!.open(audioFormat)
        targetDataLine!!.start()

        val captureThread = Thread {
            val audioBuffer = ByteArray(targetDataLine!!.bufferSize / 5)
            var bytesRead = 0

            audioInputStream = AudioInputStream(targetDataLine)
            while (true) {
                bytesRead = audioInputStream!!.read(audioBuffer)
                if (bytesRead > 0) {
                    byteArrayOutputStream.write(audioBuffer, 0, bytesRead)
                } else {
                    break
                }
            }
            byteArrayOutputStream.close()
        }
        captureThread.start()
    }

    fun stopRecording(outputFilePath: String) {
        targetDataLine!!.stop()
        targetDataLine!!.close()

        val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        val audioInputStream = AudioInputStream(byteArrayInputStream, audioFormat, byteArrayOutputStream.size().toLong() / audioFormat.frameSize)

        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, File(outputFilePath))
    }
}


fun readApiKeyFromFile(filename: String): String {
    return File(filename).readText().trim()
}

fun recordByWhisper() : String = runBlocking {
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    val file = File("output.wav") // ここをMP3からWAVに変更
    val apiKey = readApiKeyFromFile("api_key.txt")

    val response = client.submitForm<String> {
        url("https://api.openai.com/v1/audio/transcriptions")
        method = HttpMethod.Post

        headers {
            append("Authorization", "Bearer $apiKey")
        }

        body = MultiPartFormDataContent(formData {
            appendInput(
                key = "file",
                headers = Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=output.wav") // ここもMP3からWAVに変更
                    append(HttpHeaders.ContentType, "audio/wav")
                }
            ) {
                ByteReadPacket(file.readBytes())
            }
            append("model", "whisper-1")
            append("language", "ja")
        })
    }
    return@runBlocking response // 結果を返す
}

@Composable
fun recordingApp(windowController: WindowController= WindowController()) {
    var isRecording by remember { mutableStateOf(false) }
    var transcriptionResult by remember { mutableStateOf<String?>(null) } // 結果を表示するための変数
    val recorder = Recorder()

    Window(onCloseRequest = { exitProcess(0) }) {
        Column {
            if (!isRecording) {
                Button(onClick = {
                    isRecording = true
                    recorder.startRecording()
                }) {
                    Text("Start Recording")
                }
            } else {
                Button(onClick = {
                    isRecording = false
                    recorder.stopRecording("output.wav")
                    runBlocking {
                        transcriptionResult = recordByWhisper() // 結果を取得
                        var page = Page(link= LocalDate.now().toString(), lines = listOf())
                        transcriptionResult?.let { page = page.editAllLineByEntireString(it) }
                        PageRepository.restorePage(page)
                        windowController.openNewPageWindow(page.link)
                    }

                }) {
                    Text("Stop Recording and Transcribe")
                }
            }
            // 結果を表示
            transcriptionResult?.let {
                Text("Transcription: $it")
            }
        }
    }
}

fun main() {
    application {
        recordingApp()
    }
}