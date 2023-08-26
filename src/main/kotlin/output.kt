import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


fun testOutput() {
    val text = xml {
        "CsvConfigDto"(
            "xmlns:xsi" to "http://www.w3.org/2001/XMLSchema-instance",
            "xmlns:xsd" to "http://www.w3.org/2001/XMLSchema"
        ) {
            "列名有無" { true }
            "主キー" { 2 }
            "審査件数上限" { 5000 }
            "列名有無" {
                "列名有無" {
                    "EventItemDto" {
                        "列名" {
                            "型"("xmlns:xsi" to "ReplaceColumn") {
                                "取得位置" { 2 }
                                "出力フォーマット" { }
                            }
                            "モード" { "無視" }
                        }
                    }
                }

            }
        }
    }.toString()

    val file = File("setting.xml")
    file.writeText(text)
}

fun xml(block: NODE.() -> Unit): NODE = NODE(newDocument()).also { it.block() }

private fun newDocument(): Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()

class NODE(
    private val node: Node
) {
    private val document: Document = if (node is Document) node else node.ownerDocument

    operator fun String.invoke(vararg attr: Pair<String, String>, block: NODE.() -> Any = { Unit }) {
        val child = document.createElement(this).also { node.appendChild(it) }
        attr.forEach { child.setAttribute(it.first, it.second) }
        NODE(child).block().let { if (it is String) child.textContent = it }
    }

    override fun toString(): String {
        val sw = StringWriter()
        TransformerFactory.newInstance().newTransformer().also {
            it.setOutputProperty(OutputKeys.INDENT, "yes")
            it.transform(DOMSource(document), StreamResult(sw))
        }
        return sw.toString()
    }
}


fun output(
    correspondences: List<correspondence>,
    outputNumStrings: List<String>,
    outputSettingStrings: List<String>,
    examUpperNumString: String,
    isColumnSet: Boolean,
) {
    val outputColumns = correspondences.map { it.outCol }
    val dateColumns = listOf(
        "申請日時", "目視審査締切日時", "決済日時", "配送希望日時",
        "初回支払期限日", "購入者生年月日", "配送先生年月日", "店子登録日",
    )
    val timeColumns = listOf(
        "申請日時", "目視審査締切日時", "決済日時", "配送希望日時",
    )
    val stringColumns =
        listOf("モデルID", "イベント種別", "利用上限金額（与信枠）", "決済ステータス", "決済方法", "購入者国コード", "配送先国コード")
    val telColumns = listOf(
        "購入者郵便番号",
        "購入者固定電話番号",
        "購入者携帯電話番号",
        "配送先郵便番号",
        "配送先固定電話番号",
        "配送先携帯電話番号"
    )
    val margeStringColumns = listOf("購入者市区町村", "購入者その他", "配送先市区町村", "配送先その他")
    val nameCountColumns = listOf("購入者氏名文字数", "配送先氏名文字数")
    val kanjiCountColumns = listOf("購入者氏名漢字数", "配送先氏名漢字数")
    val hiraganaCountColumns = listOf("購入者氏名ひらがな数", "配送先氏名ひらがな数")
    val katakanaCountColumns = listOf("購入者氏名カタカナ数", "配送先氏名カタカナ数")
    val alphabetCountColumns = listOf("購入者氏名アルファベット数", "配送先氏名アルファベット数")
    val otherCountColumns = listOf("購入者氏名上記以外文字数", "配送先氏名上記以外文字数")
    val separateByDictionaryColumns = listOf("購入者氏名辞書存在", "配送先氏名辞書存在")
    val priceColumns = listOf("決済金額", "単価")
    val mode = "無視"
    val nullPosition = "-1"
    val zeroPosition = "0"
    val onePosition = "1"
    val separeteString = "@"
    val outputFormatTime = "yyyy/MM/dd HH:mm:ss"
    val outputFormatDate = "yyyy/MM/dd"
    val isHash = "true"
    val hashTypes = listOf("TYPE_A", "TYPE_B")
    val creditBinCode = "0-5"
    val text = xml {
        "CsvConfigDto"(
            "xmlns:xsi" to "http://www.w3.org/2001/XMLSchema-instance",
            "xmlns:xsd" to "http://www.w3.org/2001/XMLSchema",
        ) {
            "列名有無" { isColumnSet.toString() }
            "主キー" { outputNumStrings[0] }
            "審査件数上限" { examUpperNumString }
            "列情報" {
                repeat(outputColumns.size) {
                    "EventItemDto" {
                        "列名" { outputColumns[it] }
                        if (outputColumns[it] in dateColumns) {
                            "型"("xsi:type" to "DateColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                                "フォーマット" { if (outputSettingStrings[it] == "") nullPosition else outputSettingStrings[it] }
                                if (outputColumns[it] in timeColumns) {
                                    "出力フォーマット" { outputFormatTime }
                                } else {
                                    "出力フォーマット" { outputFormatDate }
                                }
                            }
                        } else if (outputColumns[it] in stringColumns) {
                            "型"("xsi:type" to "StringColumn") {
                                "表示値" { outputSettingStrings[it] }
                            }
                        } else if (outputColumns[it] in priceColumns) {
                            if (outputSettingStrings[it] == "") {
                                "型"("xsi:type" to "PriceColumn") {
                                    "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                                }
                            } else {
                                "型"("xsi:type" to "StringColumn") {
                                    "表示値" { if (outputSettingStrings[it] == "") nullPosition else outputSettingStrings[it] }
                                }
                            }
                        } else if (outputColumns[it] in telColumns) {
                            "型"("xsi:type" to "TelColumns") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else if (outputColumns[it] in margeStringColumns) {
                            "型"("xsi:type" to "MargeStringColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else if (outputColumns[it] in nameCountColumns) {
                            "型"("xsi:type" to "NameCountColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else if (outputColumns[it] in kanjiCountColumns) {
                            "型"("xsi:type" to "KanjiCountColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else if (outputColumns[it] in hiraganaCountColumns) {
                            "型"("xsi:type" to "HiraganaCountColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else if (outputColumns[it] in katakanaCountColumns) {
                            "型"("xsi:type" to "KatakanaCountColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else if (outputColumns[it] in alphabetCountColumns) {
                            "型"("xsi:type" to "AlphabetCountColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else if (outputColumns[it] in otherCountColumns) {
                            "型"("xsi:type" to "OtherCountColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else if (outputColumns[it] in separateByDictionaryColumns) {
                            "型"("xsi:type" to "SeparateByDictionaryColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                            }
                        } else {
                            "型"("xsi:type" to "ReplaceColumn") {
                                "取得位置" { if (outputNumStrings[it] == "") nullPosition else outputNumStrings[it] }
                                if (outputColumns[it] == "クレジットBINコード") {
                                    "出力フォーマット" { creditBinCode }
                                } else {
                                    "出力フォーマット" { nullPosition }
                                }
                            }
                            if ("ハッシュ" in outputColumns[it]) {
                                "ハッシュ" { isHash }
                                "HashAlgorithm" { if ("SHA2" in outputColumns[it]) hashTypes[1] else hashTypes[0] }
                            }
                            if ("正規化済み" in outputColumns[it]) {
                                "正規化"("xsi:type" to "NameNormalizer") {}
                            }
                            if ("メール" in outputColumns[it]) {
                                "分割"("xsi:type" to "StringDivider") {
                                    "取得位置" { if ("ドメイン" in outputColumns[it]) onePosition else zeroPosition }
                                    "分割文字列" { separeteString }
                                }
                            }
                        }
                        "モード" { mode }
                    }
                }
            }
        }
    }.toString().replace(" encoding=\"UTF-8\" standalone=\"no\"", "").replace(
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"",
        "// ",
    ).replace(
        "<出力フォーマット>-1</出力フォーマット>",
        "<出力フォーマット></出力フォーマット>",
    ).replace(
        "<フォーマット>-1</フォーマット>",
        "<フォーマット></フォーマット>",
    )
    val file = File("setting.xml")
    file.writeText(text)
}

fun main() {
    testOutput()
}