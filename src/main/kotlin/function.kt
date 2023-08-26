// 数字かどうか判定
fun isNumeric(s: String): Boolean {
    return try {
        s.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

// 自然数かどうか判定
fun isPositiveNum(s: String): Boolean {
    if (isNumeric(s)) {
        if (s.substring(0, 1) == "0") {
            return false
        }
        for (c in s) {
            if (c !in '0'..'9') {
                return false
            }
        }
    } else {
        return false
    }
    return true
}

// 指定範囲内の自然数とcommaで成立しているか判定
fun isValidNumOrComma(maxNum: Int, s: String): Boolean {
    for (row in s.split(",").map { it.trim() }) {
        if (!isPositiveNum(row)) {
            return false
        } else {
            if (" " in s) {
                return false
            } else if (row.toInt() > maxNum) {
                return false
            }
        }
    }
    return true
}

//　
fun testIsPositiveNum() {
    val texts = listOf(
        "1", "2", "202", "02",
        "2,2", ",", "", " "
    )
    for (row in texts) {
        print(isPositiveNum(row))
        print(": '")
        print(row)
        println("'")
    }
}


inline fun <T> Iterable<T>.firstIndex(predicate: (T) -> Boolean): Int{
    return this.mapIndexed { index, item -> Pair(index, item) }
        .firstOrNull() { predicate(it.second) }
        ?.first.toString().toInt()
}

fun testIsValidNumOrComma(maxNum: Int) {
    val texts = listOf(
        "1", "2", "10", "100", "153",
        "2,2", "100,100", "1,3,4",
        "154", "202", "02", "153,154", "0", "-1",
        "153 ", ",2", "2,", ",0", "1.1",
        "", " ", " ,", ",", "a"
    )
    for (row in texts) {
        print(isValidNumOrComma(maxNum, row))
        print(": '")
        print(row)
        println("'")
    }
}

fun testFirstIndexOrNull() {
    val arr = arrayListOf("a", "b", "c")
    println(arr.firstIndex { it == "b" })
//    println(arr.firstIndex { it == "z" })
}

fun main() {
//    testIsPositiveNum()
//    testIsValidNumOrComma(153)
    testFirstIndexOrNull()
}