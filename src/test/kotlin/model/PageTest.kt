package model

import org.junit.Test
import org.junit.Assert.*

class PageTest {


    @Test
    fun plainText() {
        val page = Page(
            listOf(
                Memo(tag="tag1",value="cat"),
                Memo(tag="tag2",value="dog"),
                Memo(tag="tag3",value="apple")
            )
        )

        val plainText = page.plainText()
        val expectedText = """
            ・tag1
            cat
            ・tag2
            dog
            ・tag3
            apple
        """.trimIndent()

        assertEquals(expectedText,plainText)
    }

    @Test
    fun plainSortedText() {
        val page = Page(
            listOf(
                Memo(tag="tag1",value="cat"),
                Memo(tag="tag2",value="dog"),
                Memo(tag="tag1",value="cat2")
            )
        )
        val plainSortedText = page.plainSortedText()
        val expectedText = """
            ・tag1
            cat
            cat2
            ・tag2
            dog
        """.trimIndent()
        assertEquals(expectedText,plainSortedText)
    }
}