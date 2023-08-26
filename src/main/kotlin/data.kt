import java.io.File
import java.io.InputStream
import java.nio.charset.Charset

fun testCorrespondences() {  // メインテストコード
    var count = 1
    run lit@{
        File("kotlin_sample/V3/").walk().forEach {
            if (it.name.endsWith(".csv") && it.name.startsWith("【ハッシュ化済み】").not()) {
                if (it.name == "order20220916_174559_test (1).csv") {
                    val sampleData = readCsv(it)
                    var correspondences = getCorrespondences(sampleData)
                    correspondences.forEach {
                        println(listOf(it.cc, it.outCol, it.sugNums))
                    }
                    if (count > 1) return@lit
                    count += 1
                } else {
//                    println(it.name)
                }
            }
        }
    }
}

fun getCorrespondences(sampleData: List<Map<String, String>>): List<correspondence> {  //　データ対応処理部分
    val correspondences = mutableListOf<correspondence>()
    val inCols = sampleData[0].keys
    // セル名、セルスタイル、自動選択候補、設定候補
    var outCol = "受付番号"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "申請日時"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "モデルID"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店管理ID"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "デバイス情報"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "IPアドレス"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "Cookie"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "イベント種別"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "目視審査締切日時"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "媒体種別"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "利用上限金額（与信枠）"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "決済ステータス"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "決済日時"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "決済金額"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "決済方法"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "クレジットカード番号ハッシュ"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "クレジットカード有効期限"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "クレジットカード支払回数"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "クレジットカードBINコード"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "オーソリ結果コード"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "オーソリ結果コメント"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "カード発行元"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "初回支払期限日"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者会員ID"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者生年月日"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者性別"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者名前ハッシュ"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者名前SHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者正規化済み名前ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者正規化済み名前SHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者苗字ハッシュ"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者苗字SHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者正規化済み苗字ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者正規化済み苗字SHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者氏名文字数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者氏名漢字数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者氏名ひらがな数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者氏名カタカナ数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者氏名アルファベット数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者氏名上記以外文字数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者氏名辞書存在"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者国コード"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者郵便番号"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者都道府県"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者市区町村"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者その他"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者固定電話番号"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者携帯電話番号"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者PCメールアカウントハッシュ"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者PCメールアカウントSHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者PCメールドメイン"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者携帯メールアカウントハッシュ"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者携帯メールアカウントSHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者携帯メールドメイン"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者会社名"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者部署名"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "購入者役職"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "顧客分類"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先会員ID"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先生年月日"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先性別"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先名前ハッシュ"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先名前SHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先正規化済み名前ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先正規化済み名前SHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先苗字ハッシュ"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先苗字SHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先正規化済み苗字ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先正規化済み苗字SHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先氏名文字数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先氏名漢字数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先氏名ひらがな数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先氏名カタカナ数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先氏名アルファベット数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先氏名上記以外文字数"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先氏名辞書存在"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先国コード"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先郵便番号"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先都道府県"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先市区町村"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先その他"
    correspondences.add(correspondence(outCol, "req", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先固定電話番号"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先携帯電話番号"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先メールアカウントハッシュ"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先メールアカウントSHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先メールドメイン"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先携帯メールアカウントハッシュ"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先携帯メールアカウントSHA2ハッシュ"
    correspondences.add(correspondence(outCol, "set", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先携帯メールドメイン"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先会社名"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先部署名"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送先役職"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送希望日時"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送希望有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "配送業者"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店店子ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "店子上限金額"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "利用者与信枠補正率"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "店子名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "店子URL"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "店子登録日"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "店子Webサイト識別ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "店子Webサイト識別パスワード"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "sub", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "加盟店商品ID"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "単価"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "数量"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "商品名"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    outCol = "在庫有無"
    correspondences.add(correspondence(outCol, "", getSugNums(outCol, inCols), getSetting(outCol)))
    return correspondences
}

fun getSugNums(outCol: String, inCols: Set<String>): MutableSet<Int> {
    // データのカラムと出力のカラムの名前から候補を探す
    val sugNums = mutableSetOf<Int>()
    var searchWords = mutableListOf<String>()
    if (outCol == "受付番号") {
        searchWords += listOf("注文番号", "受注番号", "ID", "ＩＤ")
    } else if (outCol in listOf("申請日時", "目視審査締切日時")) {
        searchWords += listOf("日", "時間")
    } else if (outCol == "デバイス情報") {
        searchWords += listOf("経路")
    } else if (outCol == "決済金額") {
        searchWords += listOf("額", "合計")
    } else if (outCol == "購入者会員ID") {
        searchWords += listOf("番号", "ID")
    } else if (outCol == "購入者名前ハッシュ") {
        searchWords += listOf("名", "購入者", "注文者")
    } else if (outCol == "購入者苗字ハッシュ") {
        searchWords += listOf("性", "苗", "購入者", "注文者")
    } else if (outCol == "購入者郵便番号") {
        searchWords += listOf("郵便番号")
    } else if (outCol == "購入者都道府県") {
        searchWords += listOf("都道府県", "住所")
    } else if (outCol == "購入者その他") {
        searchWords += listOf("住所", "市", "区", "建物", "町", "番地")
    } else if (outCol == "購入者PCメールアカウントハッシュ") {
        searchWords += listOf("メール")
    } else if (outCol == "購入者PCメールドメイン") {
        searchWords += listOf("メール")
    } else if (outCol == "購入者PCメールドメイン") {
        searchWords += listOf("郵便番号")
    } else if (outCol == "配送先都道府県") {
        searchWords += listOf("都道府県", "住所")
    } else if (outCol == "配送先その他") {
        searchWords += listOf("お届け先市区", "配送先住所", "配送先 市区", "送付先住所２", "市区", "住所")
    }
    for ((index, elem) in inCols.withIndex()) {
        searchWords.forEach { column ->
            if (Regex(column).containsMatchIn(elem)) {
                sugNums.add(index + 1)
            }
        }
    }
    return sugNums
}

fun getSetting(outCol: String): List<String> {
    // 設定候補
    var setting = mutableListOf<String>()
    if (outCol == "モデルID") {
        setting += getSettlementModels().keys
    } else if (outCol in listOf("申請日時", "目視審査締切日時", "初回支払期限日")) {
        setting += getDayFormats().keys
    } else {
        setting += listOf(
            ""
        )
    }
    return setting
}

class globalCc {
    // corのナンバリング
    var cc: Int = 0

    companion object {
        private var instance: globalCc? = null
        fun getInstance(): globalCc {
            if (instance == null) instance = globalCc()
            return instance!!
        }
    }
}

class correspondence(var outCol: String, var style: String, var sugNums: Set<Int>, var setting: List<String>) {
    // corのモデル部分
    var cc = 0

    init {
        val gc = globalCc.getInstance()
        gc.cc += 1
        cc = gc.cc
    }

    constructor(
        cc: Int,
        column: String,
        style: String,
        sugNums: Set<Int>,
        setting: List<String>,
    ) : this(column, style, sugNums, setting) {
        this.cc = cc
        this.style = style
        this.outCol = column
        this.sugNums = sugNums
        this.setting = setting
    }
}

fun getDayFormats(): Map<String, Regex> {
    // 日時のフォーマット
    return mapOf(
        // 自動検索
        "yyyy/MM/dd HH:mm:ss.ffffff" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{6}$"),
        "yyyy/MM/dd HH:mm:ss.fff" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}$"),
        "yyyy/MM/dd HH:mm:ss" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$"),
        "yyyy/MM/dd H:mm:ss" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]:[0-9]{2}:[0-9]{2}$"),
        "yyyy/MM/dd HH*mm" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$"),
        "yyyy/MM/dd H*mm" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]:[0-9]{2}$"),
        "yyyy/MM/dd H*m" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]:[0-9]$"),
        "yyyy/MM/dd HH" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}}$"),
        "yyyy/MM/dd" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]{2}$"),
        "yyyy/MM/d" to Regex("^[0-9]{4}/[0-9]{2}/[0-9]$"),
        "yyyy/MM" to Regex("^[0-9]{4}/[0-9]{2}$"),
        "yyyy-MM-dd HH:mm:ss.ffffff" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{6}$"),
        "yyyy-MM-dd HH:mm:ss.fff" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}$"),
        "yyyy-MM-dd HH:mm:ss" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$"),
        "yyyy-MM-dd H:mm:ss" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]:[0-9]{2}:[0-9]{2}$"),
        "yyyy-MM-dd HH*mm" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$"),
        "yyyy-MM-dd H*mm" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]:[0-9]{2}$"),
        "yyyy-MM-dd H*m" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]:[0-9]$"),
        "yyyy-MM-dd HH" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}}$"),
        "yyyy-MM-dd" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"),
        "yyyy-MM-d" to Regex("^[0-9]{4}-[0-9]{2}-[0-9]$"),
        "yyyy-MM" to Regex("^[0-9]{4}/[0-9]{2}$"),
        "yyyy*MM*dd*HH*mm*ss*ffffff" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*dd*HH*mm*ss*fff" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*dd*HH*mm*ss" to Regex("^[0-9]{4}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*dd*H*mm*ss" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*dd*HH*mm" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*dd*H*mm" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*dd*H*m" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*dd*HH" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*dd" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM*d" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
        "yyyy*MM" to Regex("^[0-9]{2}[^0-9]+[0-9]{2}[^0-9]+[0-9]{2}$"),
    )
}

fun getSettlementModels(): Map<String, String> {
    // モデルと決済方法の対応
    val settlementModel = mapOf(
        "ATB_01" to "01",
        "TNB_01" to "01",
        "CRD_01" to "02",
        "DBK_01" to "03",
        "RNT_01" to "02",
        "RULE_01" to "01",
        "FC_CRD_01" to "02",
        "" to "",
    )
    return settlementModel
}

fun readCsv(dataFile: File): List<Map<String, String>> {
    // データの読み込み
    val reader = dataFile.inputStream().bufferedReader(Charset.forName("Shift-jis"))
    var header = reader.readLine().split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*\$)".toRegex())
        .map { it.replace("\"", "") }
    return reader.lineSequence().filter { it.isNotBlank() }.map {
        val value = it.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*\$)".toRegex())
        header.zip(value.map { it.replace("\"", "") }).toMap()
    }.toList()
}

fun main() {
    testCorrespondences()
}
