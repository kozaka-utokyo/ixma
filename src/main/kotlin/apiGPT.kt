import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.HttpClient
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.Serializable

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

suspend fun formatByGpt(input:String):String{
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(HttpTimeout) { // タイムアウトの設定を追加
            requestTimeoutMillis = 600000  // 10分
            connectTimeoutMillis = 15000  // 15秒
            socketTimeoutMillis = 600000  // 10分
        }
    }

    val key = readApiKeyFromFile("api_key.txt")  // APIキーをファイルから読み込み
    val url = "https://api.openai.com/v1/chat/completions"

    val response: String = client.post(url) {
        contentType(ContentType.Application.Json)
        header("Authorization", "Bearer $key")
        body = CompletionRequest(
//            model = "gpt-3.5-turbo",
            model = "gpt-4",
            messages = listOf(Message(role = "user", content = input)),
            temperature = 0.7
        )
    }

    return response
}

suspend fun testFormatByGpt(){
//    val input = "Say this is a test!"
    val input = "文①に対して次のような階層化を行いました、文②で同様の階層化をできますか？\n" +
            "ーーーーー文①ーーーーーーーーーー\n" +
            "あさ鈴木先輩との予定に間に合うために起きた\n" +
            "大体10時ぐらい12時から鈴木先輩と話したインタビューした\n" +
            "一時半ぐらいまでインタビューした\n" +
            "でポストドクターの人名前金平さんと帰り道偶然あったんでいろいろ話してたでもあーでもじゃないな一番ためになるなと思ったのは対象者だけじゃなくてつまりユーザー候補になってる人にヒアリングするのではだけではなくてユーザーインターフェースの研究者だったり研究手法の研究者に話を聞きに行くのもいいと思うというアドバイスが良かったえっと金平さんとの話が盛り上がったので結局16時ぐらいに千歳に帰ってきた\n" +
            "ーーーーーーーーーーー階層化ーーーーーーーーー\n" +
            "・鈴木先輩、インタビュー\n" +
            "　・あさ鈴木先輩との予定に間に合うために起きた\n" +
            "　・大体10時ぐらい12時から鈴木先輩と話したインタビューした\n" +
            "　　一時半ぐらいまでインタビューした\n" +
            "・ポストドクター、金平さん、ヒアリング、ユーザーインターフェース、研究手法、アドバイス、千歳\n" +
            "　・でポストドクターの人名前金平さんと帰り道偶然あったんでいろいろ話してた\n" +
            "　・一番ためになるなと思ったアドバイスは、対象者だけじゃなく、つまりユーザー候補になってる人にヒアリングするのではだけではなく、ユーザーインターフェースの研究者だったり研究手法の研究者に話を聞きに行くのもいいと思うというもの\n" +
            "　・金平さんとの話が盛り上がったので結局16時ぐらいに千歳に帰ってきた\n" +
            "ーーーーー文②ーーーーーーーー\n" +
            "まちライブラリーに入ったところでただビットスターとの面接をすっぽかしてしまったことに気づいたスマート16時から17時までぐらいかけてbitstarえの謝罪メールと昨日やりそびれたプロジェクトプランニングじゃないやなんだっけスプリントプランニングの続きをやったインタビューが長引いたのでお昼を食べ損ねたんですよだからえっとこのタイミングでうどん食べた丸亀製麺丸亀製麺でわ好きなとろろ醤油うどんがなかったメニューらなくなっちゃったんですよだから代わりにとろ玉うどんを食べましたでこれが18時から19時違うわ17時から18時の間にうどんを食べました18時から18時半の間またもちらいぶらりーで作業して作業っていうのはスプリントランニングの続きとインタビュー結果の文字起こしこれはあれですねあのうノートpcを使ってメモを取りに行こうと思ったんだけどバッテリー切れてあの不本意ながら紙で持ってたのを共有できるようにデジタルに起こしてたねえその後電車乗って帰ってきてあそうえっと乗れると思った電車に乗れなくて結構待たされた家に着いたのがだいたい19時ちょうどでその後お風呂入って百プログラムのslackが盛り上がってたからそれに対して返信を考えたりしてあと一時間ぐらいリラックスしてたリラックスしたのは21時から22時の間22時からチームミーティングがあってそこでいろいろ話してたで結局3時間50分ぐらい盛り上がって終わったのが一時50分その後あのアドレナリンが結構出ててなかなか寝付けなかった結局4時ぐらいまで寝れなかったんじゃないかなそれまではえっとプロジェクトのスクラップボックスを整理したりしてた本当はこの日記もその時間に書けばよかったんだけどあまりに意識がプロジェクトの方に向いてできなかった"
    val response = formatByGpt(input)
    println(response)
}

suspend fun main (){
    testFormatByGpt()
}
