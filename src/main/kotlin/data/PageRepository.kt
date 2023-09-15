package data

import model.Line
import model.Page
import java.io.File

object PageRepository {
    fun findAllLinks(): List<Page> {
        val directoryPath = this.directory()
        val directory = File(directoryPath)

        val pages = mutableListOf<Page>()

        if (directory.exists() && directory.isDirectory) {
            val filesInDirectory = directory.listFiles()

            if (filesInDirectory != null) {
                filesInDirectory.filter { it.isFile && it.name.endsWith(".txt") }.forEach {
                    val link = it.name.removeSuffix(".txt")
                    val page = findByLink(link)
                    pages.add(page)
                }
            } else {
                println("ディレクトリ内のファイルをリストアップできませんでした。")
            }
        } else {
            println("指定したディレクトリが存在しないかディレクトリではありません。")
        }

        return pages
    }

    fun findByLink(link: String): Page {
        //TODO エラーハンドリング必要

        val fileName = "${directory()}$link.txt" // 読み込むファイルの名前
        val file = File(fileName)
        var text = ""

        try {
            text = file.readText()
            println("ファイル内容：\n$text")
        } catch (e: Exception) {
            println("ファイルの読み込み中にエラーが発生しました: ${e.message}")
        }

        val lines:List<Line> = text.split("\n").map { Line(it) }//こんなコードは本来ドメイン層でやるべきこと Pageクラスのコンストラクタの自由度がないからこんなことになっている 本当はPageに直接文字列を渡したい
        return Page(link = link, lines = lines)
    }

    fun isExistLink(link: String): Boolean {
        //TODO エラーハンドリングするjava
        val directoryPath = this.directory()
        val fileName = "$link.txt" // チェックしたいファイル名

        val directory = File(directoryPath)

        if (directory.exists() && directory.isDirectory) {
            val filesInDirectory = directory.listFiles()
            if (filesInDirectory != null) {
                val matchingFiles = filesInDirectory.filter { it.isFile && it.name == fileName }

                return matchingFiles.isNotEmpty()
            } else {
                println("ディレクトリ内のファイルをリストアップできませんでした。")
            }
        } else {
            println("指定したディレクトリが存在しないかディレクトリではありません。")
        }
        return false //バグの温床　エラーが発生した場合、嘘の真偽値をreturnsする　エラーハンドリングを省略したのでこんなことになっている
    }

    fun restorePage(page: Page) {
        val directory = directory()
        val fileName = "${directory}${page.getLink2()}.txt"
        val content:String = page.plainValue()

        try {
            val file = File(fileName)
            file.writeText(content)
            println("ファイルに書き込みました: $fileName")
        } catch (e: Exception) {
            println("ファイルの書き込み中にエラーが発生しました: ${e.message}")
        }
    }

    fun deletePage(link: String) {
        val filePath = "${this.directory()}/$link.txt" // 削除したいファイルのパス

        val file = File(filePath)

        if (file.exists() && file.isFile) {
            try {
                file.delete()
                println("ファイルを削除しました: $filePath")
            } catch (e: Exception) {
                println("ファイルの削除中にエラーが発生しました: ${e.message}")
            }
        } else {
            println("指定したファイルが存在しないかファイルではありません。")
        }
    }
    fun findPagesBySubString(substring: String):List<Page>{
        val pages = mutableListOf<Page>()
        val directory = File(directory())

        directory.walk().forEach {
            if (it.isFile && it.extension == "txt") {
                val link = it.nameWithoutExtension
                val page = findByLink(link)
                if (page.plainValue().contains(substring)) {
                    pages.add(page)
                }
            }
        }
        return pages
    }

    private fun directory(): String {
        val currentDirectory = System.getProperty("user.dir")
        return "$currentDirectory/localfiles/"
    }
}