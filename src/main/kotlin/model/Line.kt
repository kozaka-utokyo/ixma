package model

data class Line(
    var valueString: String
) {
    fun getValue(): String {
        return valueString
    }

    fun edit(newValue: String) {
        valueString = newValue
    }

    fun dividedStringsByLinks(): List<SubString> {
        val pattern = "\\[([^\\[\\]]*)\\]"
        val regex = Regex(pattern)

        val subStrings = mutableListOf<SubString>()
        var lastIndex = 0

        for (matchResult in regex.findAll(valueString)) {
            // Add the portion of the string before the match as a non-link SubString
            val nonLinkText = valueString.substring(lastIndex, matchResult.range.first)
            if (nonLinkText.isNotEmpty()) {
                subStrings.add(SubString(nonLinkText))
            }

            // Add the matched text as a link SubString
            val linkText = matchResult.groupValues[1]
            subStrings.add(SubString(linkText, isLinkText = true))

            lastIndex = matchResult.range.last + 1
        }

        // Add any remaining non-link portion of the string
        if (lastIndex < valueString.length) {
            val nonLinkText = valueString.substring(lastIndex)
            subStrings.add(SubString(nonLinkText))
        }

        // Print the resulting SubString list
        return subStrings
    }

    private fun dividedStringsByLinks2(): List<SubString> {//正規表現を使わない実装
        val result = mutableListOf<SubString>()
        val buffer = StringBuilder()
        var isInsideBrackets = false

        for (i in valueString.indices) {
            val char = valueString[i]

            when (char) {
                '[' -> {
                    if (!isInsideBrackets) {
                        if (buffer.isNotEmpty()) {
                            result.add(SubString(buffer.toString(), false))
                            buffer.clear()
                        }
                        isInsideBrackets = true
                    } else {
                        buffer.append(char)
                    }
                }
                ']' -> {
                    if (isInsideBrackets) {
                        result.add(SubString(buffer.toString(), true))
                        buffer.clear()
                        isInsideBrackets = false
                    } else {
                        buffer.append(char)
                    }
                }
                else -> buffer.append(char)
            }
        }

        // 最後の部分を追加
        if (buffer.isNotEmpty()) {
            result.add(SubString(buffer.toString(), isInsideBrackets))
        }

        return result
    }
}

data class SubString(
    val text: String,
    val isLinkText: Boolean = false
)