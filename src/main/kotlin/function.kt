import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.HttpClient
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.Serializable
import io.ktor.client.features.logging.*
import io.ktor.client.request.forms.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import java.io.File

@Serializable
data class CompletionRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double? = null
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

suspend fun gpt() {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    val key = readApiKeyFromFile("api_key.txt")  // APIキーをファイルから読み込み
    val url = "https://api.openai.com/v1/chat/completions"

    val response: String = client.post(url) {
        contentType(ContentType.Application.Json)
        header("Authorization", "Bearer $key")
        body = CompletionRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(Message(role = "user", content = "Say this is a test!")),
            temperature = 0.7
        )
    }

    println(response)
}

//fun whisper() = runBlocking {
//    val client = HttpClient(CIO) {
//        install(JsonFeature) {
//            serializer = KotlinxSerializer()
//        }
//        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL
//        }
//    }
//
//    val file = File("test.mp3")
//    val apiKey = readApiKeyFromFile("api_key.txt") // APIキーをファイルから読み込む
//
//    val response = client.submitForm<String> {
//        url("https://api.openai.com/v1/audio/transcriptions")
//        method = HttpMethod.Post
//
//        headers {
//            append("Authorization", "Bearer $apiKey") // 読み込んだAPIキーを使用する
//        }
//
//        body = MultiPartFormDataContent(formData {
//            appendInput(
//                key = "file",
//                headers = Headers.build {
//                    append(HttpHeaders.ContentDisposition, "filename=test.mp3")
//                    append(HttpHeaders.ContentType, "audio/mpeg")
//                }
//            ) {
//                ByteReadPacket(file.readBytes())
//            }
//            append("model", "whisper-1")
//        })
//    }
//    println(response)
//}

//suspend fun main(){
//    whisper()
//}