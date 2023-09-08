package model

import data.PageRepository


data class Page(
    private val link: String,
    private val lines: List<Line>
) {
    fun getLink(): String {
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
        return Page(this.link, newLines)
    }

    fun editTitle(newTitle: String): Page {
        PageRepository.deletePage(this.getLink())
        val newInstance = Page(newTitle, this.lines)
        PageRepository.restorePage(newInstance)
        return newInstance
    }
}