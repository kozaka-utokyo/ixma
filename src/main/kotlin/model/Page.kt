package model

import data.PageRepository


data class Page(
    val link: String,
    private val lines: List<Line>
) {
    fun getLink2(): String {
        return link
    }

    fun getLines(): List<Line> {
        return lines
    }

    private fun restore() {
        PageRepository.restorePage(this)
    }

    fun plainValue(): String {
        val texts: List<String> = lines.map { it.valueString }
        return texts.joinToString(separator = "\n")
    }

    fun editAllLineByEntireString(entireValue: String): Page {
        val newLines = entireValue.split("\n").map {
            Line(valueString = it)
        }
        PageRepository.restorePage(this)
        return Page(this.link, newLines)
    }

    fun editTitle(newTitle: String): Page {
        PageRepository.deletePage(this.getLink2())
        val newInstance = Page(newTitle, this.lines)
        PageRepository.restorePage(newInstance)
        return newInstance
    }
}