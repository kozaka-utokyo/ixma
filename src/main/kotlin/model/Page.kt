package model

import data.PageRepository
import model.Node

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
        val newPage = Page(this.link, newLines)
        PageRepository.restorePage(newPage)
        return newPage
    }

    fun editTitle(newTitle: String): Page {
        PageRepository.deletePage(this.getLink2())
        val newInstance = Page(newTitle, this.lines)
        PageRepository.restorePage(newInstance)
        return newInstance
    }
}