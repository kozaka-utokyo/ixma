package model


data class Page(
    val title:String,
    val lines:List<Line>
) {
    fun restore(){
        //TODO
    }

    fun plainValue(): String {
        val texts:List<String> = this.lines.map { it.value }
        return texts.joinToString(separator = "\n")
    }
}