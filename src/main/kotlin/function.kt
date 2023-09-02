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

// 数字かどうか判定
fun isNumeric(s: String): Boolean {
    return try {
        s.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

// 自然数かどうか判定
fun isPositiveNum(s: String): Boolean {
    if (isNumeric(s)) {
        if (s.substring(0, 1) == "0") {
            return false
        }
        for (c in s) {
            if (c !in '0'..'9') {
                return false
            }
        }
    } else {
        return false
    }
    return true
}

// 指定範囲内の自然数とcommaで成立しているか判定
fun isValidNumOrComma(maxNum: Int, s: String): Boolean {
    for (row in s.split(",").map { it.trim() }) {
        if (!isPositiveNum(row)) {
            return false
        } else {
            if (" " in s) {
                return false
            } else if (row.toInt() > maxNum) {
                return false
            }
        }
    }
    return true
}

//　
fun testIsPositiveNum() {
    val texts = listOf(
        "1", "2", "202", "02",
        "2,2", ",", "", " "
    )
    for (row in texts) {
        print(isPositiveNum(row))
        print(": '")
        print(row)
        println("'")
    }
}


inline fun <T> Iterable<T>.firstIndex(predicate: (T) -> Boolean): Int{
    return this.mapIndexed { index, item -> Pair(index, item) }
        .firstOrNull() { predicate(it.second) }
        ?.first.toString().toInt()
}

fun testIsValidNumOrComma(maxNum: Int) {
    val texts = listOf(
        "1", "2", "10", "100", "153",
        "2,2", "100,100", "1,3,4",
        "154", "202", "02", "153,154", "0", "-1",
        "153 ", ",2", "2,", ",0", "1.1",
        "", " ", " ,", ",", "a"
    )
    for (row in texts) {
        print(isValidNumOrComma(maxNum, row))
        print(": '")
        print(row)
        println("'")
    }
}

fun testFirstIndexOrNull() {
    val arr = arrayListOf("a", "b", "c")
    println(arr.firstIndex { it == "b" })
//    println(arr.firstIndex { it == "z" })
}


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

    val file = File("test.mp3")
    val apiKey = readApiKeyFromFile("api_key.txt") // APIキーをファイルから読み込む

    val response = client.submitForm<String> {
        url("https://api.openai.com/v1/audio/transcriptions")
        method = HttpMethod.Post

        headers {
            append("Authorization", "Bearer $apiKey") // 読み込んだAPIキーを使用する
        }

        body = MultiPartFormDataContent(formData {
            appendInput(
                key = "file",
                headers = Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=test.mp3")
                    append(HttpHeaders.ContentType, "audio/mpeg")
                }
            ) {
                ByteReadPacket(file.readBytes())
            }
            append("model", "whisper-1")
        })
    }
    println(response)
}
fun readApiKeyFromFile(filename: String): String {
    return File(filename).readText().trim()
}

suspend fun main(){
    gpt()
//    whisper()
}