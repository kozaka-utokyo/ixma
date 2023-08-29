package model

data class Page(
    val memos: List<Memo>
) {
    private fun memosByTags(): Map<String, MutableList<Memo>> {
        val memosByTags = mutableMapOf<String, MutableList<Memo>>()
        for (memo in this.memos) {
            if (memosByTags.containsKey(memo.tag)) {
                memosByTags[memo.tag]?.add(memo)
            } else {
                memosByTags[memo.tag] = mutableListOf(memo)
            }
        }
        return memosByTags
    }
    fun plainText():String{
        var text = ""
        for (memo in this.memos){
            text += "・${memo.tag}\n${memo.value}\n"
        }
        return text.dropLast(1)//末尾の改行文字を削除
    }
    fun plainSortedText(): String {
        var text = ""
        val memosByTags = this.memosByTags()
        for (tag in memosByTags.keys) {
            text = "$text・$tag\n"
            memosByTags[tag]?.let {
                for (memo in it) {
                    text += "${memo.value}\n"
                }
            }
        }
        return text.dropLast(1)//末尾の改行を削除
    }

}