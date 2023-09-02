import java.io.File

fun main() {
    val filePath = "test.mp3"
    val file = File(filePath)

    // ファイルが存在するか確認
    if (!file.exists()) {
        println("$filePath は存在しません。")
        return
    }

    // ファイルのサイズを表示
    println("$filePath のサイズ: ${file.length()} バイト")

    // ファイルを読み込み、最初の10バイトを表示
    val bytes = file.readBytes().take(10).toByteArray()
    println("最初の10バイト: ${bytes.joinToString(", ") { it.toString() }}")
}