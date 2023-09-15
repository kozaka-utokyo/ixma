package model

data class Node(val text: String, val level: Int, var parent: Node? = null, var children: List<Node> = mutableListOf()){
    fun concatenateTreeText(): String {
        val builder = StringBuilder()
        concatenateNodeText(this, builder)
        return builder.toString()
    }

    private fun concatenateNodeText(node: Node, builder: StringBuilder, indent: String = "") {
        builder.append("$indent${node.text}\n")
        for (child in node.children) {
            concatenateNodeText(child, builder, "$indent  ")
        }
    }
    fun findNodesContainingWord(targetWord: String): List<Node> {
        return findNodesContainingWord(this,targetWord)
    }

    fun concatenateLines(): List<Line> {
        val lines = mutableListOf<Line>()
        concatenateNodeLines(this, lines, "")
        return lines
    }

    private fun concatenateNodeLines(node: Node, lines: MutableList<Line>, indent: String = "") {
        val line = Line("$indent${node.text}")
        lines.add(line)
        for (child in node.children) {
            concatenateNodeLines(child, lines, "$indent  ")
        }
    }
}
fun buildTree(lines: List<String>): Node {
    val root = Node("", -1, parent = null)
    var parentNode = root
    val stack = mutableListOf<Node>()

    for (line in lines) {
        val level = getIndentLevel(line)
        val text = line.trim()
        val currentNode = Node(text, level, parent = parentNode)

        while (stack.isNotEmpty() && level <= stack.last().level) {
            stack.removeAt(stack.size - 1)
        }

        if (level > parentNode.level) {
            stack.add(parentNode)
        }

        parentNode = currentNode

        if (root != parentNode) {
            if (stack.isNotEmpty()) {
                stack.last().addChild(currentNode)
            } else {
                root.addChild(currentNode)
            }
        }
    }

    return root
}

fun Node.addChild(child: Node) {
    child.parent = this
    this.children += child
}


fun getIndentLevel(line: String): Int {

    return line.takeWhile { it == '　' }.count()
}

fun printTree(node: Node, indent: String = "") {
    println(indent + node.text)
    for (child in node.children) {
        printTree(child, "$indent  ")
    }
}

fun findNodesContainingWord(root: Node, targetWord: String): List<Node> {
    val result = mutableListOf<Node>()

    fun search(node: Node) {
        if (node.text.contains(targetWord)) {
            result.add(node)
        }

        for (child in node.children) {
            search(child)
        }
    }
    search(root)
    return result.map{ extractAncestors(it) }
}

fun extractAncestors(node: Node): Node {
    // 新しいノードを生成し、元のノードの情報と子ノードをコピー
    var newNode = Node(node.text, node.level)
    newNode.children += node.children

    // 先祖ノードを抽出
    var ancestor = node.parent
    while (ancestor != null) {
        val ancestorCopy = Node(ancestor.text, ancestor.level)
        ancestorCopy.children += newNode
        newNode.parent = ancestorCopy
        ancestor = ancestor.parent
        newNode = ancestorCopy
    }

    return newNode
}


fun main() {
    val lines = listOf(
        "プロジェクトA",
        "　食べ物",
        "　　うどん",
        "　　　釜玉",
        "　　　とろろ醤油",
        "　　　　丸亀製麺のとろろがまじでおいしいのよネ",
        "　　天ぷら",
        "　　　鶏天",
        "　　　えび天",
        "　飲み物",
        "　　コーラ",
        "　　お茶",
        "プロジェクトB",
        "　犬",
        "　猫"
    )
    val tree = buildTree(lines)
    val n = findNodesContainingWord(tree,"飲み物")
    val m = extractAncestors(n[0])
    printTree(m)

}




