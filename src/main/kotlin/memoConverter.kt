import model.Memo

public fun convertToMemos(inputStr: String): List<Memo> {
    val memos: MutableList<Memo> = mutableListOf()
    val lines: List<String> = inputStr.split("\n")

    var tag: String = ""
    var value: String = ""


    for (line in lines) {
        if (line.isEmpty()) {
            value += "\n"
            continue
        }

        //tagを検出したとき
        if (line[0] == '~' && line.length > 1) {

            //tagもvalueも空の場合はMemo生成せず、tagのみ登録
            if (tag == "" && value.isEmpty()) {
                tag = line.substring(1)
                continue
            }
            memos += Memo(tag, value)
            value = ""
            tag = line.substring(1)
        } else { //tag以外の行
            value = "$value$line\n"
        }
    }
    //入力末端のMemoを処理
    if (tag.isNotEmpty()) {
        memos += Memo(tag, value)
    }
    return memos
}

