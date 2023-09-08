import javax.sound.sampled.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.system.exitProcess
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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

@Composable
fun recordingApp() {
    var isRecording by remember { mutableStateOf(false) }
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
                        whisper()
                    }
                }) {
                    Text("Stop Recording and Transcribe")
                }
            }
        }
    }
}
fun readApiKeyFromFile(filename: String): String {
    return File(filename).readText().trim()
}

fun whisper() = runBlocking {
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
        })
    }
    println(response)
}

fun main() {
    application {
        recordingApp()
    }
}